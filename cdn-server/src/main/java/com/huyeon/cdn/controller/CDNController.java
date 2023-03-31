package com.huyeon.cdn.controller;

import com.huyeon.cdn.dto.CKEditor5Res;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CDNController {
    private final ObjectStorage objectStorage;

    @Value("${oci.bucketName}")
    private String BUCKET_NAME;

    @Value("${oci.namespace}")
    private String NAMESPACE;

    @Value("${cdn.origin}")
    private String ORIGIN;

    private final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif", "webp");
    private final String CONTENT_PREFIX = "Contents/CONMOTO_CONTENTS_";

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<CKEditor5Res>> uploadFile(
            ServerWebExchange exchange,
            @RequestPart("upload") FilePart filePart
    ) {
        if (notAllowedDomain(exchange)) {
            CKEditor5Res response = new CKEditor5Res(false, "Not Allowed Domain.");
            return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(response));
        }

        String extension = FilenameUtils.getExtension(filePart.filename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            CKEditor5Res response = new CKEditor5Res(false, "It's Unacceptable extension.\nWe allow 'jpg', 'jpeg', 'png', 'gif', 'webp'");
            return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response));
        }

        // Get the input stream from the file part
        Mono<InputStream> inputStreamMono = DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                })
                .map(ByteArrayInputStream::new);

        // Upload the file to Object Storage
        return inputStreamMono.flatMap(inputStream -> {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucketName(BUCKET_NAME)
                    .namespaceName(NAMESPACE)
                    .objectName(CONTENT_PREFIX + filePart.filename())
                    .contentLength(filePart.headers().getContentLength())
                    .putObjectBody(inputStream)
                    .build();

            CompletableFuture<PutObjectResponse> future = CompletableFuture.supplyAsync(() ->
                    objectStorage.putObject(putObjectRequest)
            );

            return Mono.fromFuture(future)
                    .map(response -> {
                        CKEditor5Res body = new CKEditor5Res(true, ORIGIN + "/contents/" + filePart.filename());
                        return ResponseEntity.ok().body(body);
                    });
        });
    }

    @GetMapping("/images/{fileName:.+}")
    public Mono<ResponseEntity<Resource>> getImages(@PathVariable String fileName) {
        final String IMAGE_PREFIX = "Images/CONMOTO_IMAGES_";
        return getResource(IMAGE_PREFIX + fileName);
    }

    @GetMapping("/contents/{fileName:.+}")
    public Mono<ResponseEntity<Resource>> getContents(@PathVariable String fileName) {
        return getResource(CONTENT_PREFIX + fileName);
    }

    @GetMapping("/files/{fileName:.+}")
    public Mono<ResponseEntity<Resource>> getFiles(ServerWebExchange exchange, @PathVariable String fileName) {
        if (notAllowedDomain(exchange)) {
            return forbiddenMessageResource();
        }

        String extension = FilenameUtils.getExtension(fileName);
        final String FILE_PREFIX = "Files/" + extension + "/CONMOTO_FE_FILES_";
        return getResource(FILE_PREFIX + fileName);
    }

    private boolean notAllowedDomain(ServerWebExchange exchange) {
        String domain = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getHostName();
        return domain != null && !domain.endsWith("conmoto.site") && !domain.endsWith("0:0:0:0:0:0:1");
    }

    private Mono<ResponseEntity<Resource>> forbiddenMessageResource() {
        ByteArrayResource resource = new ByteArrayResource("Not Allowed Domain".getBytes());
        return Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource));
    }

    private Mono<ResponseEntity<Resource>> getResource(@PathVariable String fileName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucketName(BUCKET_NAME)
                .namespaceName(NAMESPACE)
                .objectName(fileName)
                .build();

        CompletableFuture<GetObjectResponse> responseFuture = CompletableFuture.supplyAsync(() ->
                objectStorage.getObject(getObjectRequest));

        return Mono.fromFuture(responseFuture)
                .publishOn(Schedulers.boundedElastic())
                .map(response -> {
                    try {
                        InputStream inputStream = response.getInputStream();
                        ByteArrayResource resource = new ByteArrayResource(inputStream.readAllBytes());
                        return ResponseEntity.ok()
                                .cacheControl(setCacheOneDayRevalidateOneWeek())
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                                .contentLength(response.getContentLength())
                                .contentType(MediaType.parseMediaType(response.getContentType()))
                                .body(resource);
                    } catch (IOException e) {
                        throw new RuntimeException("Error occurred while reading the file", e);
                    }
                });
    }

    private CacheControl setCacheOneDayRevalidateOneWeek() {
        return CacheControl
                .maxAge(1, TimeUnit.DAYS)
                .cachePrivate()
                .staleWhileRevalidate(7, TimeUnit.DAYS);
    }
}
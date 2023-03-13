package com.huyeon.cdn.controller;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CDNController {
    private final ObjectStorage objectStorage;

    @Value("${oci.bucketName}")
    private String BUCKET_NAME;

    @Value("${oci.namespace}")
    private String NAMESPACE;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> uploadFile(@RequestPart("file") FilePart filePart) {
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
                    .objectName(filePart.filename())
                    .contentLength(filePart.headers().getContentLength())
                    .putObjectBody(inputStream)
                    .build();

            CompletableFuture<PutObjectResponse> future = CompletableFuture.supplyAsync(() ->
                    objectStorage.putObject(putObjectRequest)
            );

            return Mono.fromFuture(future)
                    .map(response -> "File uploaded successfully with ETag: " + response.getETag());
        });
    }

    @GetMapping("/download/{fileName:.+}")
    public Mono<ResponseEntity<Resource>> downloadFile(@PathVariable String fileName) {
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
                                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                                .contentLength(response.getContentLength())
                                .contentType(MediaType.parseMediaType(response.getContentType()))
                                .body(resource);
                    } catch (IOException e) {
                        throw new RuntimeException("Error occurred while reading the file", e);
                    }
                });
    }
}
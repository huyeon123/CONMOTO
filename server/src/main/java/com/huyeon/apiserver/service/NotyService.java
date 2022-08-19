package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.NotyDto;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.NotyType;
import com.huyeon.apiserver.repository.EmitterRepository;
import com.huyeon.apiserver.repository.EmitterRepositoryImpl;
import com.huyeon.apiserver.repository.NotyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotyService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000L * 60L;

    private final NotyRepository notyRepository;
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();

    public SseEmitter subscribe(String userEmail, String lastEventId) {
        String emitterId = makeTimeIncludeId(userEmail);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        //성공 or 시간만료 시 자동으로 삭제
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(userEmail);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userEmail=" + userEmail + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, userEmail, emitterId, emitter);
        }

        return emitter;
    }

    private String makeTimeIncludeId(String userEmail) {
        return userEmail + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String emitterId, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(userEmail);
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public void publish(Noty noty) {
        Noty newNoty = notyRepository.save(noty);

        newNoty.getReceivers().forEach(email -> {
            String eventId = makeTimeIncludeId(email);
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(email);
            emitters.forEach((key, emitter) -> {
                emitterRepository.saveEventCache(key, newNoty);
                sendNotification(emitter, key, eventId, new NotyDto(newNoty));
            });
        });
    }

    public Noty createNotyToMember(String sender, String receiver, NotyType notificationType, String message, String url) {
        return Noty.builder()
                .senderName(sender)
                .receivers(List.of(receiver))
                .type(notificationType)
                .message(message)
                .redirectUrl(url)
                .build();
    }

    public Noty createNotyToMembers(String sender, List<String> receivers, NotyType notificationType, String message, String url) {
        return Noty.builder()
                .senderName(sender)
                .receivers(receivers)
                .type(notificationType)
                .message(message)
                .redirectUrl(url)
                .build();
    }

    public Noty createNotyToGroup(String sender, Groups group, NotyType notificationType, String message, String url) {
        return Noty.builder()
                .senderName(sender)
                .receiveGroup(group)
                .type(notificationType)
                .message(message)
                .redirectUrl(url)
                .build();
    }
}

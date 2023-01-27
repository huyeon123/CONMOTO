package com.huyeon.superspace.domain.noty.service;

import com.huyeon.superspace.domain.noty.dto.EmitterAdaptor;
import com.huyeon.superspace.domain.noty.dto.NotyDto;
import com.huyeon.superspace.global.dto.NotyEventDto;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.domain.noty.repository.EmitterRepository;
import com.huyeon.superspace.domain.noty.repository.NotyReceiverRepository;
import com.huyeon.superspace.domain.noty.repository.NotyRepository;
import com.huyeon.superspace.global.exception.PermissionDeniedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotyService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60L * 60L;

    private final NotyRepository notyRepository;
    private final NotyReceiverRepository receiverRepository;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(EmitterAdaptor ea) {
        String emitterId = makeTypeIncludeId(ea.getUserEmail());
        SseEmitter emitter = getEmitter(emitterId);

        ea.setEmitterId(emitterId);
        ea.setEmitter(emitter);

        if (hasLostData(ea.getLastEventId())) {
            sendLostData(ea);
        }

        return emitter;
    }

    private String makeTypeIncludeId(String userEmail) {
        return userEmail + "_" + System.currentTimeMillis();
    }

    private SseEmitter getEmitter(String emitterId) {
        SseEmitter emitter = findOrMakeEmitter(emitterId);
        setOnEvent(emitter, emitterId);
        return emitter;
    }

    private SseEmitter findOrMakeEmitter(String emitterId) {
        Optional<SseEmitter> emitterOpt = emitterRepository.findEmitterById(emitterId);
        return emitterOpt.orElse(
                emitterRepository.save(
                        emitterId, new SseEmitter(DEFAULT_TIMEOUT)
                )
        );
    }

    private void setOnEvent(SseEmitter emitter, String emitterId) {
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
    }

    public List<NotyDto> findUnreadEvent(String userEmail) {
        List<ReceivedNoty> receivedNotices = receiverRepository.findAllByUserEmail(userEmail);
        return receivedNotices.stream()
                .filter(ReceivedNoty::isUnread)
                .map(NotyDto::new)
                .collect(Collectors.toList());
    }

    private String makeTimeIncludeId(String userEmail) {
        return userEmail + "_" + System.currentTimeMillis();
    }

    private void sendNotification(EmitterAdaptor ea) {
        try {
            ea.getEmitter()
                    .send(SseEmitter.event()
                            .id(ea.getEventId())
                            .data(ea.getData()));
        } catch (IOException exception) {
            emitterRepository.deleteById(ea.getEmitterId());
        }
    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(EmitterAdaptor ea) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(ea.getUserEmail());
        eventCaches.entrySet().stream()
                .filter(entry -> ea.getLastEventId().compareTo(entry.getKey()) < 0)
                .forEach(entry -> {
                    ea.setEventId(entry.getKey());
                    ea.setData(entry.getValue());
                    sendNotification(ea);
                });
    }

    @Transactional
    public void publish(NotyEventDto noty) {
        Noty newNoty = notyRepository.save(noty.getNoty());
        receiverRepository.saveAll(noty.getReceivers());

        noty.getReceivers().forEach(receiver -> {
            String userEmail = receiver.getUserEmail();
            String eventId = makeTimeIncludeId(userEmail);
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(userEmail);
            emitters.forEach((key, emitter) -> {
                emitterRepository.saveEventCache(key, newNoty);

                EmitterAdaptor ea = EmitterAdaptor.builder()
                        .emitter(emitter)
                        .emitterId(key)
                        .eventId(eventId)
                        .data(new NotyDto(receiver))
                        .build();

                sendNotification(ea);
            });
        });
    }

    public List<NotyDto> findAllByUser(String userEmail, int page) {
        List<ReceivedNoty> received =
                receiverRepository.findAllByUserEmailOrderByIdDesc(userEmail, PageRequest.of(page, 10));
        return received.stream()
                .map(NotyDto::new)
                .collect(Collectors.toList());
    }

    public void setReadNoty(List<Long> idList, String userEmail) {
        idList.forEach(id -> {
            ReceivedNoty receiver = receiverRepository.findById(id).orElseThrow();
            if (receiver.getUserEmail().equals(userEmail)) {
                receiver.setRead(true);
                receiverRepository.save(receiver);
            } else throw new PermissionDeniedException("잘못된 알림에 대한 접근입니다!");
        });
    }
}

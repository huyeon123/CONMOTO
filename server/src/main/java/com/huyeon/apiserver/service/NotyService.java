package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.EmitterAdaptor;
import com.huyeon.apiserver.model.dto.NotyDto;
import com.huyeon.apiserver.model.dto.NotyEventDto;
import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.ReceivedNoty;
import com.huyeon.apiserver.repository.EmitterRepository;
import com.huyeon.apiserver.repository.EmitterRepositoryImpl;
import com.huyeon.apiserver.repository.NotyReceiverRepository;
import com.huyeon.apiserver.repository.NotyRepository;
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
@Transactional
@RequiredArgsConstructor
public class NotyService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60L * 60L;

    private final NotyRepository notyRepository;
    private final NotyReceiverRepository receiverRepository;
    private final EmitterRepository emitterRepository = new EmitterRepositoryImpl();

    public SseEmitter subscribe(EmitterAdaptor ea, String notyType) {
        String emitterId = makeTypeIncludeId(ea.getUserEmail(), notyType);
        SseEmitter emitter = getEmitter(emitterId);

        ea.setEmitterId(emitterId);
        ea.setEmitter(emitter);

        sendUnreadEvent(ea);

        if (hasLostData(ea.getLastEventId())) {
            sendLostData(ea);
        }

        return emitter;
    }

    private String makeTypeIncludeId(String userEmail, String notyType) {
        return userEmail + "_" + notyType;
    }

    private SseEmitter getEmitter(String emitterId) {
        SseEmitter emitter = findOrMakeEmitter(emitterId);
        setOnCompletion(emitter, emitterId);
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

    private void setOnCompletion(SseEmitter emitter, String emitterId) {
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
    }

    private void sendUnreadEvent(EmitterAdaptor ea) {
        List<ReceivedNoty> receivedNotices = receiverRepository.findAllByUserEmail(ea.getUserEmail());
        receivedNotices.stream()
                .filter(ReceivedNoty::isUnread)
                .forEach(receive -> {
                    ea.setData(new NotyDto(receive));
                    sendNotification(ea);
                });
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

    public void setReadNoty(List<Long> idList) {
        idList.forEach(id -> {
            ReceivedNoty receiver = receiverRepository.findById(id).orElseThrow();
            receiver.setRead(true);
            receiverRepository.save(receiver);
        });
    }
}

package com.huyeon.superspace.domain.noty.service;

import com.huyeon.superspace.domain.noty.dto.EmitterAdaptor;
import com.huyeon.superspace.domain.noty.dto.NotyDto;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import com.huyeon.superspace.domain.noty.repository.NotyReceiverRepository;
import com.huyeon.superspace.domain.noty.repository.NotyRepository;
import com.huyeon.superspace.global.dto.NotyEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class NotyServiceTest {

    @Autowired
    NotyService notyService;

    @Autowired
    NotyReceiverRepository receiverRepository;

    @Autowired
    NotyRepository notyRepository;

    @Test
    @DisplayName("알림 구독")
    void subscribe() {
        //given
        EmitterAdaptor ea = EmitterAdaptor.builder()
                .userEmail("sub@test.com")
                .lastEventId("")
                .build();

        //when, then
        assertDoesNotThrow(() -> notyService.subscribe(ea));
    }

    @Test
    @DisplayName("미수신 알림 조회")
    void sendUnreadEvent(){
        //given
        createTestReceivedNoty();
        String sender = "test@test.com";
        String receiver = "sub@test.com";

        //when, then
        assertDoesNotThrow(() -> {
            List<NotyDto> unreadEvent = notyService.findUnreadEvent(receiver);
            assertFalse(unreadEvent.isEmpty());
            unreadEvent.forEach(event -> assertEquals(sender, event.getSenderName()));
        });
    }

    private void createTestReceivedNoty() {
        Noty noty = createTestNoty();

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .userEmail("sub@test.com")
                .noty(noty)
                .isRead(false)
                .build();

        receiverRepository.save(receivedNoty);
    }

    private Noty createTestNoty() {
        Noty noty = Noty.builder()
                .senderName("test@test.com")
                .message("Hi!")
                .type(NotyType.DIRECT_MESSAGE)
                .build();

        return  notyRepository.save(noty);
    }

    @Test
    @DisplayName("특정 사용자에게 전송된 알림을 페이지 별 조회")
    void findAllByUser(){
        //given
        createTestReceivedNoty();
        String email = "sub@test.com";
        int page = 0;

        //when
        List<NotyDto> notifications = notyService.findAllByUser(email, page);

        //then
        assertFalse(notifications.isEmpty());
    }

    @Test
    @DisplayName("Noty 읽기 완료 체크")
    void setReadNoty(){
        //given
        createTestReceivedNoty();
        String email = "sub@test.com";
        List<Long> idList = receiverRepository.findAllByUserEmail(email)
                .stream()
                .map(ReceivedNoty::getId)
                .collect(Collectors.toList());

        //when
        notyService.setReadNoty(idList);

        //then
        List<ReceivedNoty> notifications = receiverRepository.findAllByUserEmail(email);
        notifications.forEach(n -> assertTrue(n.isRead()));
    }

    @Test
    @DisplayName("알림 전송")
    void publish(){
        //given
        subscribe();
        NotyEventDto event = createEvent();

        //when, then
        assertDoesNotThrow(() -> notyService.publish(event));
    }

    private NotyEventDto createEvent() {
        String receiver = "sub@test.com";

        Noty testNoty = createTestNoty();

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .userEmail(receiver)
                .noty(testNoty)
                .build();

        return NotyEventDto.builder()
                .noty(testNoty)
                .receivers(List.of(receivedNoty))
                .build();
    }

}

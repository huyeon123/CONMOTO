package com.huyeon.superspace.domain.noty.repository;

import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class NotyReceiverRepositoryTest {

    @Autowired
    NotyRepository notyRepository;

    @Autowired
    NotyReceiverRepository receiverRepository;

    @Test
    @DisplayName("NotyReceiver 저장")
    void save(){
        //given
        Noty noty = Noty.builder()
                .senderName("test@test.com")
                .build();

        notyRepository.save(noty);

        ReceivedNoty receivedNoty = ReceivedNoty.builder()
                .noty(noty)
                .userEmail("sub@test.com")
                .build();

        //when, then
        assertDoesNotThrow(() -> receiverRepository.save(receivedNoty));
    }

    @Test
    @DisplayName("특정 사용자가 수신한 모든 알림 조회")
    void findAllByUserEmail(){
        //given
        save();
        String email = "sub@test.com";

        //when
        List<ReceivedNoty> notyList = receiverRepository.findAllByUserEmail(email);

        //then
        assertFalse(notyList.isEmpty());
        notyList.forEach(noty -> assertEquals(email, noty.getUserEmail()));
    }

    @Test
    @DisplayName("특정 사용자가 수신한 알림을 페이지 별 내림차순 조회")
    void findAllByUserEmailOrderByIdDesc(){
        //given
        save();
        String email = "sub@test.com";
        PageRequest pageRequest = PageRequest.of(0, 10);

        //when
        List<ReceivedNoty> notyList = receiverRepository.findAllByUserEmailOrderByIdDesc(email, pageRequest);

        //then
        assertFalse(notyList.isEmpty());
        notyList.forEach(noty -> assertEquals(email, noty.getUserEmail()));
    }
}

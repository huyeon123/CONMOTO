package com.huyeon.superspace.domain.noty.service;

import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotyTest {
    @Autowired
    GroupRepository groupRepository;

    @DisplayName("수신자 1명 알림 생성")
    @Test
    void test_1(){
        assertDoesNotThrow(() -> Noty.builder()
                .senderName("testGroup")
                .message("메세지")
                .type(NotyType.DIRECT_MESSAGE)
                .redirectUrl("/workspace/test-group/invite")
                .build());
    }

    @DisplayName("수신자 다수 알림 생성")
    @Test
    void test_2(){
        assertDoesNotThrow(() -> Noty.builder()
                .senderName("testGroup")
                .message("메세지")
                .type(NotyType.BOARD_TAGGED)
                .redirectUrl("/workspace/{test-group}/{category}/{boardId}")
                .build());
    }

    @DisplayName("그룹 알림 생성")
    @Test
    void test_3(){
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();

        assertDoesNotThrow(() -> Noty.builder()
                .senderName("testGroup")
                .message("메세지")
                .type(NotyType.NOTICE)
                .build());
    }

}

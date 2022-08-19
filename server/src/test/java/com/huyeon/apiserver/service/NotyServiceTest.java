package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.NotyType;
import com.huyeon.apiserver.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NotyServiceTest {
    @Autowired
    NotyService notyService;

    @Autowired
    GroupRepository groupRepository;

    @Test
    @DisplayName("알림 구독")
    void subscribe() throws Exception{
        //given
        String userEmail = "user@test.com";
        String lastEventId = "";

        //when, then
        assertDoesNotThrow(() -> notyService.subscribe(userEmail, lastEventId));
    }

    @Test
    @DisplayName("알림 생성 - 특정 한 명")
    void createNotyToMember() throws Exception{
        //given

        //when, then
        assertDoesNotThrow(() -> notyService.createNotyToMember(
                "TEST USER",
                "sub@test.com",
                NotyType.DIRECT_MESSAGE,
                "안녕하세요",
                "url"
        ));
    }

    @Test
    @DisplayName("알림 생성 - 다수")
    void createNotyToMembers() throws Exception{
        //given

        //when, then
        assertDoesNotThrow(() -> notyService.createNotyToMembers(
                "TEST USER",
                List.of("sub@test.com, admin@test.com"),
                NotyType.BOARD_TAGGED,
                "코드좀 봐주세요",
                "url"
        ));
    }

    @Test
    @DisplayName("알림 생성 - 그룹")
    void createNotyToGroup() throws Exception{
        //given
        Groups group = groupRepository.findByUrlPath("test-group").orElseThrow();

        //when, then
        assertDoesNotThrow(() -> notyService.createNotyToGroup(
                "testGroup",
                group,
                NotyType.NOTICE,
                "안녕하세요",
                "url"
        ));
    }

    @Test
    @DisplayName("알림 전송")
    public void publish() throws Exception {
        //given
        String userEmail = "user@test.com";
        String lastEventId = "";
        notyService.subscribe(userEmail, lastEventId);
        Noty noty = notyService.createNotyToMember(
                "user@test.com",
                "sub@test.com",
                NotyType.DIRECT_MESSAGE,
                "안녕하세요",
                "url");

        //when, then
        assertDoesNotThrow(() -> notyService.publish(noty));
    }
}

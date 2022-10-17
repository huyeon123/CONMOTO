package com.huyeon.superspace.domain.noty.service;

import com.huyeon.superspace.domain.noty.service.NotyService;
import com.huyeon.superspace.domain.noty.dto.EmitterAdaptor;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        EmitterAdaptor ea = EmitterAdaptor.builder()
                .userEmail("user@test.com")
                .lastEventId("")
                .build();
        String notyType = "DEFAULT";

        //when, then
        assertDoesNotThrow(() -> notyService.subscribe(ea, notyType));
    }

}

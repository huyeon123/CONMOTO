package com.huyeon.superspace.domain.noty.repository;

import com.huyeon.superspace.domain.noty.entity.Noty;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
public class NotyRepositoryTest {

    @Autowired
    NotyRepository notyRepository;

    @Test
    @DisplayName("Noty 저장")
    void save(){
        //given
        Noty noty = createNoty();

        //when, then
        assertDoesNotThrow(() -> notyRepository.save(noty));
    }

    private Noty createNoty() {
        return Noty.builder()
                .senderName("username or groupName")
                .type(NotyType.NOTICE)
                .message("message")
                .redirectUrl("/")
                .build();
    }

    @Test
    @DisplayName("Noty 조회")
    void find(){
        //given
        Noty noty = createNoty();
        Long notyId = notyRepository.save(noty).getId();
        notyRepository.flush();

        //when
        noty = notyRepository.findById(notyId).orElse(new Noty());

        //then
        assertEquals(notyId, noty.getId());
    }

    @Test
    @DisplayName("Noty 삭제")
    void delete(){
        //given
        Noty noty = createNoty();
        Long notyId = notyRepository.save(noty).getId();
        notyRepository.flush();

        //when, then
        assertDoesNotThrow(() -> notyRepository.deleteById(notyId));
    }
}

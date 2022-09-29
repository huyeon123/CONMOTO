package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.WorkGroup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

@SpringBootTest
public class BoardRepositoryTest {
    @Autowired
    BoardRepository boardRepository;

    @Autowired
    GroupRepository groupRepository;

    @DisplayName("다음 최신 게시물들을 가져오는 쿼리 테스트(최대 10개)")
    @Test
    void test_1(){
        //given
        WorkGroup group = groupRepository.findByUrlPath("test-group").orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        //when, then
        Assertions.assertDoesNotThrow(() -> boardRepository.findNextTenLatest(group, now, PageRequest.of(0, 10)));
    }
}

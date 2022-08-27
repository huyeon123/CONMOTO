package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Groups;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

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
        Groups group = groupRepository.findByUrlPath("test-group").orElseThrow();
        Long lastId = 5L;

        //when, then
        Assertions.assertDoesNotThrow(() -> boardRepository.findNext10LatestInGroup(group, lastId, PageRequest.of(0, 10)));
    }
}

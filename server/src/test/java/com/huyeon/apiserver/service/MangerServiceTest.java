package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
public class MangerServiceTest {
    @Autowired
    private GroupManagerService managerService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @DisplayName("매니저 등록 테스트")
    @Test
    void test_1(){
        User user = userRepository.findById("user@test.com").orElseThrow();
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();
        managerService.registerUserAsManager(user, workGroup);
    }

    @DisplayName("매니저 취소 테스트")
    @Test
    void test_2(){
        User user = userRepository.findById("user@test.com").orElseThrow();
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();
        managerService.revokeManager(user, workGroup);
    }
}

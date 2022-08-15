package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@SpringBootTest
public class GroupServiceTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupManagerRepository managerRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Transactional
    @Test
    void test_1() {
        User user = userRepository.findById("user@test.com").orElseThrow();
        Groups groups = groupRepository.findByUrlPath("test-group").orElseThrow();
        managerRepository.save(
                GroupManager.builder()
                        .manager(user)
                        .group(groups)
                        .build()
        );
        String res = groupService.deleteGroup(user, "test-group");
        System.out.println(res);
    }
}
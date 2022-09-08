package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.EmitterAdaptor;
import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupManagerRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @Autowired
    NotyService notyService;

    @Transactional
    @Test
    void test_1() {
        User user = userRepository.findById("user@test.com").orElseThrow();
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();
        managerRepository.save(
                GroupManager.builder()
                        .manager(user)
                        .group(workGroup)
                        .build()
        );
        String res = groupService.deleteGroup(user, "test-group");
        System.out.println(res);
    }

    @DisplayName("그룹 초대")
    @Test
    void inviteMember() {
        //given
        WorkGroup group = groupRepository.findByUrlPath("test-group").orElseThrow();
        EmitterAdaptor ea = EmitterAdaptor.builder()
                .userEmail("user@test.com")
                .lastEventId("")
                .build();
        String notyType = "DEFAULT";

        //when
        Assertions.assertDoesNotThrow(() -> groupService.inviteMember(group, ea.getUserEmail()));

        //then
        Assertions.assertDoesNotThrow(() -> notyService.subscribe(ea, notyType));
    }
}

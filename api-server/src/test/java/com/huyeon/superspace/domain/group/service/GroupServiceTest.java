package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.repository.GroupManagerRepository;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.noty.service.NotyService;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
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

    String email = "test@test.com";

    @BeforeAll
    void init() {
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup(email, request);
    }

    @Test
    @DisplayName("그룹 추가")
    void createGroup(){
        //given
        GroupDto request = new GroupDto("생성테스트", "create-test", "test");

        //when, then
        assertDoesNotThrow(() -> groupService.createGroup(email, request));
    }

    @Test
    @Transactional
    @DisplayName("그룹 수정")
    void editGroup(){
        //given
        String groupUrl = "test-group";
        GroupDto request = new GroupDto("수정그룹", "edit-group", "edited!");

        //when
        assertDoesNotThrow(() -> groupService.editGroup(groupUrl, request));
    }

    @Test
    @Transactional
    @DisplayName("그룹 삭제")
    void deleteGroup(){
        //given
        String groupUrl = "test-group";

        //when, then
        assertDoesNotThrow(() -> groupService.deleteGroup(email, groupUrl));
    }

    @Test
    @DisplayName("Url로 그룹 조회")
    void findGroupByUrl(){
        //given
        String urlPath = "test-group";

        //when
        WorkGroup group = groupService.getGroupByUrl(urlPath);

        //then
        assertEquals("테스트그룹", group.getName());
        assertEquals("test-group", group.getUrlPath());
    }

    @Test
    @DisplayName("Url로 그룹명 조회")
    void getGroupNameByUrl(){
        //given
        String urlPath = "test-group";

        //when
        String groupName = groupService.getGroupNameByUrl(urlPath);

        //then
        assertEquals("테스트그룹", groupName);
    }

    @Test
    @DisplayName("사용자가 가입한 모든 그룹 조회")
    void findGroups(){
        //given: email

        //when
        List<WorkGroup> groups = groupService.getGroups(email);

        //then
        assertFalse(groups.isEmpty());
    }

    @Test
    @DisplayName("그룹에 속한 멤버 조회")
    void findUsers(){
        //given
        WorkGroup group = groupService.getGroupByUrl("test-group");

        //when
        List<User> users = groupService.getUsers(group);

        //then
        assertFalse(users.isEmpty());
        users.forEach(user -> assertEquals(email, user.getEmail()));
    }

    @Test
    @DisplayName("멤버 역할 확인")
    void checkRole(){
        //given
        WorkGroup group = groupService.getGroupByUrl("test-group");
        User user = userRepository.findByEmail(email).orElseThrow();

        //when
        String role = groupService.checkRole(group, user);

        //then
        assertEquals("그룹 소유자", role);
    }
}

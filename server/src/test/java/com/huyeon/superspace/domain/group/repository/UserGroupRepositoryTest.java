package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class UserGroupRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @Test
    @DisplayName("새로운 UserGroup 저장")
    void save(){
        //given
        WorkGroup group = makeTestGroupInstance();
        User user = getTestUser();
        UserGroup userGroup = buildUserGroup(group, user);

        //when, then
        assertDoesNotThrow(() -> userGroupRepository.save(userGroup));
    }

    private User getTestUser() {
        return userRepository.findByEmail("test@test.com").orElseThrow();
    }

    private WorkGroup makeTestGroupInstance() {
        User user = getTestUser();

        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .owner(user)
                .build();

        return groupRepository.save(group);
    }

    private UserGroup buildUserGroup(WorkGroup group, User user) {
        return UserGroup.builder()
                .group(group)
                .user(user)
                .build();
    }

    @Test
    @DisplayName("특정 그룹에 특정 멤버 존재 확인")
    void existsByUserAndGroup(){
        //given
        User user = getTestUser();
        WorkGroup group = makeTestGroupInstance();
        UserGroup userGroup = buildUserGroup(group, user);
        userGroupRepository.save(userGroup);

        //when
        boolean exists = userGroupRepository.existsByUserAndGroup(user, group);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("특정 Email로 가입한 모든 그룹 조회")
    void findGroupsByEmail(){
        //given
        String email = "test@test.com";
        WorkGroup group = makeTestGroupInstance();

        //when
        List<UserGroup> groups = userGroupRepository.findGroupsByEmail(email);

        //then
        groups.forEach(ug -> assertEquals(email, ug.getUser().getEmail()));
    }

    @Test
    @DisplayName("특정 Group을 포함한 모든 레코드 조회")
    void findAllByGroup(){
        //given
        WorkGroup group = makeTestGroupInstance();

        //when
        List<UserGroup> groups = userGroupRepository.findAllByGroup(group);

        //then
        groups.forEach(ug -> assertEquals(group, ug.getGroup()));
    }

    @Test
    @DisplayName("특정 Group을 포함한 모든 레코드 삭제")
    void deleteAllByGroup(){
        //given
        WorkGroup group = makeTestGroupInstance();

        //when, then
        assertDoesNotThrow(() -> userGroupRepository.deleteAllByGroup(group));
    }

    @Test
    @DisplayName("특정 그룹에 속한 특정 사용자 삭제")
    void deleteByUserAndGroup(){
        //given
        User user = getTestUser();
        WorkGroup group = makeTestGroupInstance();

        //when, then
        assertDoesNotThrow(() -> userGroupRepository.deleteByUserAndGroup(user, group));
    }
}

package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.GroupManager;
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
public class GroupManagerRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    GroupManagerRepository managerRepository;

    @Test
    @DisplayName("매니저 저장 테스트")
    void save(){
        //given
        User user = getTestUser();
        WorkGroup group = makeTestGroupInstance();

        //when, then
        GroupManager groupManager = buildGroupManager(group, user);

        assertDoesNotThrow(() -> managerRepository.save(groupManager));
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

    private GroupManager buildGroupManager(WorkGroup group, User user) {
        return GroupManager.builder()
                .group(group)
                .manager(user)
                .build();
    }

    @Test
    @DisplayName("그룹의 모든 매니저 조회")
    void findAllByGroup(){
        //given
        WorkGroup workGroup = makeTestGroupInstance();

        //when
        List<GroupManager> managers = managerRepository.findAllByGroup(workGroup);

        //then
        managers.forEach(manager -> {
            assertEquals(getTestUser(), manager.getManager());
        });
    }

    @Test
    @DisplayName("특정 그룹에 특정 매니저가 존재하는지 확인")
    void existsByGroupAndManager(){
        //given
        WorkGroup group = makeTestGroupInstance();
        User user = getTestUser();
        GroupManager groupManager = buildGroupManager(group, user);
        managerRepository.save(groupManager);

        //when
        boolean exists = managerRepository.existsByGroupAndManager(group, user);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("특정 그룹의 특정 매니저 삭제")
    void deleteByGroupAndManager(){
        //given
        WorkGroup group = makeTestGroupInstance();
        User user = getTestUser();
        GroupManager groupManager = buildGroupManager(group, user);
        managerRepository.save(groupManager);

        //when, then
        assertDoesNotThrow(() -> managerRepository.deleteByGroupAndManager(group, user));
    }
}

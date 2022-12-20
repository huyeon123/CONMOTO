package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class GroupRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Test
    @DisplayName("그룹 추가")
    void save(){
        //given
        User user = userRepository.findByEmail("test@test.com").orElseThrow();

        //when, then
        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .owner(user)
                .build();

        assertDoesNotThrow(() -> groupRepository.save(group));
    }

    private WorkGroup makeTestGroupInstance() {
        User user = userRepository.findByEmail("test@test.com").orElseThrow();

        WorkGroup group = WorkGroup.builder()
                .name("테스트그룹")
                .urlPath("test-group")
                .owner(user)
                .build();

        return groupRepository.save(group);
    }

    @Test
    @DisplayName("Url로 그룹명 조회")
    void findNameByUrl(){
        //given
        String url = "test-group";
        WorkGroup group = makeTestGroupInstance();

        //when
        Optional<String> groupOptional = groupRepository.findNameByUrl(url);

        //then
        assertTrue(groupOptional.isPresent());
        assertEquals(group.getName(), groupOptional.get());
    }

    @Test
    @DisplayName("Url로 그룹 조회")
    void findByUrlPath(){
        //given
        String url = "test-group";
        WorkGroup group = makeTestGroupInstance();

        //when
        Optional<WorkGroup> groupOptional = groupRepository.findByUrlPath(url);

        //then
        assertTrue(groupOptional.isPresent());
        assertEquals(group, groupOptional.get());
    }

    @Test
    @DisplayName("주어진 Url이 존재하는지 확인")
    void existsByUrlPath(){
        //given
        String url = "test-group";
        WorkGroup group = makeTestGroupInstance();

        //when
        boolean exists = groupRepository.existsByUrlPath(url);

        //then
        assertTrue(exists);
    }

    @Test
    @DisplayName("그룹 삭제")
    void delete(){
        //given
        String url = "test-group";
        WorkGroup group = makeTestGroupInstance();

        //when, then
        assertDoesNotThrow(() -> groupRepository.delete(group));
    }
}

package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    GroupService groupService;

    WorkGroup group;

    @BeforeAll
    public void initTest() {
        String email = "test@test.com";
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup(email, request);

        group = groupService.getGroupByUrl("test-group");
    }

    @Test
    @DisplayName("카테고리 저장")
    void save(){
        //given
        Category root = categoryRepository.findById(1L).orElseThrow();
        Category category = Category.builder()
                .name("테스트 카테고리")
                .group(group)
                .parent(root)
                .build();

        //when, then
        assertDoesNotThrow(() -> categoryRepository.save(category));
    }

    @Test
    @DisplayName("그룹 내 카테고리 조회")
    void findAllByGroup(){
        //given
        save();

        //when
        List<Category> categories = categoryRepository.findAllByGroup(group);

        //then
        assertFalse(categories.isEmpty());
        categories.forEach(category -> assertEquals(group.getName(), category.getGroup().getName()));
    }

    @Test
    @DisplayName("카테고리 명으로 조회")
    void findByName(){
        //given
        String categoryName = "==최상위 카테고리==";

        //when
        Optional<Category> optionalCategory = categoryRepository.findByName(categoryName);

        //then
        assertTrue(optionalCategory.isPresent());
        assertEquals(categoryName, optionalCategory.get().getName());
    }

    @Test
    @DisplayName("그룹 내 카테고리 모두 삭제")
    void deleteAllByGroup(){
        //when, then
        assertDoesNotThrow(() -> categoryRepository.deleteAllByGroup(group));
    }
}

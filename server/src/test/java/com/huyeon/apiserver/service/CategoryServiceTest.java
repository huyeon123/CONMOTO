package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.UserGroup;
import com.huyeon.apiserver.repository.CategoryRepository;
import com.huyeon.apiserver.repository.GroupRepository;
import com.huyeon.apiserver.repository.UserGroupRepository;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional
@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @DisplayName("카테고리 생성 테스트")
    @Test
    void createRootCategory() throws Exception {
        //given
        Groups groups = initTest();

        //when
        CategoryDto rootCategory = categoryService.getRootOfCategoryTree(groups);

        //then
        Assertions.assertEquals("ROOT", rootCategory.getName());
        Assertions.assertEquals("Level 1-1",
                rootCategory.getSubCategories().get(0) //Root
                        .getSubCategories().get(0) //Level 1-1
                        .getName());
        Assertions.assertEquals("Level 2-2",
                rootCategory.getSubCategories().get(0) //Root
                        .getSubCategories().get(0) //Level 1-1
                        .getSubCategories().get(1) //Level 2-2
                        .getName());
    }

    Groups initTest() {
        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        Groups testGroup = Groups.builder().name("testGroup").build();
        testGroup = groupRepository.save(testGroup);

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(testGroup)
                .build();

        userGroupRepository.save(userGroup);

        List<Category> categories = new ArrayList<>();

        Category level1_1 = Category.builder()
                .name("Level 1-1")
                .parent(null)
                .group(testGroup)
                .build();

        Category level1_2 = Category.builder()
                .name("Level 1-2")
                .parent(null)
                .group(testGroup)
                .build();

        Category level2_1 = Category.builder()
                .name("Level 2-1")
                .parent(level1_1)
                .group(testGroup)
                .build();

        Category level2_2 = Category.builder()
                .name("Level 2-2")
                .parent(level1_1)
                .group(testGroup)
                .build();

        categories.add(level1_1);
        categories.add(level2_1);
        categories.add(level1_2);
        categories.add(level2_2);

        categoryRepository.saveAll(categories);

        return testGroup;
    }
}

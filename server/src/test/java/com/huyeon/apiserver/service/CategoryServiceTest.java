package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CategoryDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
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
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.transaction.annotation.Transactional;

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

    @BeforeTestClass
    public void initTest() {
        User user = userRepository.findByEmail("user@test.com").orElseThrow();
        WorkGroup testGroup = WorkGroup.builder().name("testGroup").build();
        testGroup = groupRepository.save(testGroup);

        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .group(testGroup)
                .build();

        userGroupRepository.save(userGroup);
    }

    @DisplayName("카테고리 조회 테스트")
    @Test
    void getRootOfCategoryTreeTest() throws Exception {
        //given
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();

        //when
        CategoryDto rootCategory = categoryService.getRootOfCategoryTree(workGroup);

        //then
        Assertions.assertEquals("==최상위 카테고리==", rootCategory.getName());
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

    @DisplayName("카테고리 순서/계층 변경 테스트")
    @Test
    void editCategoryTree() {
        WorkGroup workGroup = groupRepository.findByUrlPath("test-group").orElseThrow();
        CategoryDto rootOfCategoryTree = categoryService.getRootOfCategoryTree(workGroup);

        //parentId만 바꿔주면 알아서 계층은 잡힘

    }
}

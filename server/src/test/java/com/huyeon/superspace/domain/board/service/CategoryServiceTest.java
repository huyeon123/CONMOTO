package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.board.repository.CategoryRepository;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.repository.GroupRepository;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class CategoryServiceTest {
    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    public void initTest() {
        String email = "test@test.com";
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup(email, request);
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory(){
        //given
        CategoryDto request = new CategoryDto(1L, "Level 1-1", -1);
        String groupUrl = "test-group";

        //when
        categoryService.createCategory(request, groupUrl);

        //then
        Optional<Category> category = categoryRepository.findById(2L);
        assertTrue(category.isPresent());
        assertEquals("Level 1-1", category.get().getName());
        assertEquals(1L, category.get().getParentId());
    }

    @Test
    @DisplayName("특정 카테고리 조회")
    void getCategory(){
        //given
        String categoryName = "==최상위 카테고리==";

        //when
        Category category = categoryService.getCategory(categoryName);

        //then
        assertEquals(categoryName, category.getName());
    }

    @DisplayName("그룹 내 카테고리 계층 조회 테스트")
    @Test
    void getRootOfCategoryTreeTest(){
        //given
        WorkGroup workGroup = createTestCategory();

        //when
        CategoryDto rootCategory = categoryService.getRootOfCategoryTree(workGroup);

        //then
        assertEquals("==최상위 카테고리==", rootCategory.getName());
        assertEquals("Level 1-1", rootCategory.getSubCategories().get(0).getName());
    }

    private WorkGroup createTestCategory() {
        String groupUrl = "test-group";

        CategoryDto request1 = new CategoryDto(1L, "Level 1-1", -1);
        categoryService.createCategory(request1, groupUrl);

        CategoryDto request2 = new CategoryDto(2L, "Level 2-1", -1);
        categoryService.createCategory(request2, groupUrl);

        return groupRepository.findByUrlPath(groupUrl).orElseThrow();
    }

    @Test
    @DisplayName("그룹 내 카테고리 조회 테스트")
    void getCategoryList(){
        //given
        WorkGroup group = createTestCategory();

        //when
        List<CategoryDto> categories = categoryService.getCategoryList(group);

        //then
        assertFalse(categories.isEmpty());
    }

    @Test
    @Transactional
    @DisplayName("카테고리 순서/계층 변경 테스트")
    void editCategory(){
        //given
        WorkGroup workGroup = createTestCategory();

        //when: Level 1-1과 Level 2-1의 순서 및 계층 변경
        CategoryDto categoryDto1 = new CategoryDto(null, "Level 2-1", 0);
        CategoryDto categoryDto2 = new CategoryDto(null, "Level 1-1", 1);
        List<CategoryDto> newList = List.of(categoryDto1, categoryDto2);

        categoryService.editCategory(newList, workGroup.getUrlPath());

        //then
        CategoryDto root = categoryService.getRootOfCategoryTree(workGroup);
        List<CategoryDto> subCategories = root.getSubCategories();
        assertEquals("Level 2-1", subCategories.get(0).getName());
        assertEquals(1L, subCategories.get(0).getParentId());
        assertEquals("Level 1-1",
                subCategories.get(0).getSubCategories().get(0).getName());
        assertEquals(2L,
                subCategories.get(0).getSubCategories().get(0).getParentId());
    }
}

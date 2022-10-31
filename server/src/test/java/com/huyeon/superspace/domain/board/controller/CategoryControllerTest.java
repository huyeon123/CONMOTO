package com.huyeon.superspace.domain.board.controller;

import com.huyeon.superspace.domain.board.dto.CategoryDto;
import com.huyeon.superspace.domain.board.service.CategoryService;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class CategoryControllerTest {

    @Autowired
    CategoryService categoryService;

    @Autowired
    ControllerTestUtil util;

    Cookie cookie;

    @BeforeAll
    void init() throws Exception {
        cookie = util.login();
        util.createTestGroup();
    }

    @Test
    @DisplayName("카테고리 생성")
    void createCategory() throws Exception {
        //given
        CategoryDto request = new CategoryDto(1L, "Level 1-1", 0);

        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.post("/api/category?groupUrl=test-group")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("카테고리 수정")
    void editCategory() throws Exception {
        //given
        createTestCategory();
        List<CategoryDto> request = List.of(
                new CategoryDto(null, "Level 2-1", 0),
                new CategoryDto(null, "Level 1-1", 1)
        );

        //when
        ResultActions resultActions = util.mockMvc.perform(
                MockMvcRequestBuilders.put("/api/category?groupUrl=test-group")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(util.objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private void createTestCategory() {
        String groupUrl = "test-group";
        CategoryDto level1_1 = new CategoryDto(1L, "Level 1-1", 0);
        CategoryDto level2_1 = new CategoryDto(2L, "Level 2-1", 1);
        categoryService.createCategory(level1_1, groupUrl);
        categoryService.createCategory(level2_1, groupUrl);
    }
}

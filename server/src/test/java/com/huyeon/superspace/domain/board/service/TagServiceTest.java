package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.TagDto;
import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.repository.BoardRepository;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.service.GroupService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    GroupService groupService;

    @Autowired
    BoardRepository boardRepository;

    Long boardId;

    @BeforeAll
    void init() {
        createTestGroup();
        createTestBoard();
    }

    private void createTestGroup() {
        GroupDto request = new GroupDto("테스트그룹", "test-group", "test");
        groupService.createGroup("test@test.com", request);
    }

    private void createTestBoard() {
        String email = "test@test.com";

        Board testBoard = Board.builder()
                .userEmail(email)
                .title("테스트 게시글")
                .build();

        boardId = boardRepository.save(testBoard).getId();
    }

    @Test
    @DisplayName("특정 게시글의 태그 작성")
    void createTag() {
        //given
        List<TagDto> request = createTestReq();

        //when, then
        assertDoesNotThrow(() -> tagService.editTag(boardId, request));
    }

    private List<TagDto> createTestReq() {
        return List.of(new TagDto("태그1"), new TagDto("태그2"));
    }

    @Test
    @DisplayName("특정 게시글의 태그 조회")
    void getTagDtoByBoardId() {
        //given
        setDefaultTag();

        //when
        List<TagDto> tags = tagService.getTagResponseByBoardId(boardId);

        //then
        assertFalse(tags.isEmpty());
        tags.forEach(tag -> assertTrue(tag.getTag().startsWith("태그")));
    }

    private void setDefaultTag() {
        List<TagDto> testReq = createTestReq();
        tagService.editTag(boardId, testReq);
    }

    @Test
    @DisplayName("특정 게시글 태그 수정")
    void editTag() {
        //given
        setDefaultTag();
        List<TagDto> request = List.of(new TagDto("태그3"), new TagDto("태그4"));

        //when
        assertDoesNotThrow(() -> tagService.editTag(boardId, request));

        //then
        List<TagDto> tags = tagService.getTagResponseByBoardId(boardId);
        assertEquals(request.size(), tags.size());
        for (int i = 0; i < tags.size(); i++) {
            assertEquals(request.get(i).getTag(), tags.get(i).getTag());
        }
    }

    @Test
    @DisplayName("특정 게시글 태그 추가")
    void appendTag() {
        //given
        setDefaultTag();
        List<TagDto> request = List.of(
                new TagDto("태그1"),
                new TagDto("태그2"),
                new TagDto("태그3")
        );

        //when
        assertDoesNotThrow(() -> tagService.editTag(boardId, request));

        //then
        List<TagDto> tags = tagService.getTagResponseByBoardId(boardId);
        assertEquals(request.size(), tags.size());
        for (int i = 0; i < tags.size(); i++) {
            assertEquals(request.get(i).getTag(), tags.get(i).getTag());
        }
    }

    @Test
    @DisplayName("특정 게시글 태그 제거")
    void removeTag() {
        //given
        setDefaultTag();
        List<TagDto> request = List.of(new TagDto("태그2"));

        //when
        assertDoesNotThrow(() -> tagService.editTag(boardId, request));

        //then
        List<TagDto> tags = tagService.getTagResponseByBoardId(boardId);
        assertEquals(request.size(), tags.size());
        for (int i = 0; i < tags.size(); i++) {
            assertEquals(request.get(i).getTag(), tags.get(i).getTag());
        }
    }


}

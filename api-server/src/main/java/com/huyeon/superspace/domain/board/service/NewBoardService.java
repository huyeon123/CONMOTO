package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.document.Board;
import com.huyeon.superspace.domain.board.document.Content;
import com.huyeon.superspace.domain.board.document.TempPost;
import com.huyeon.superspace.domain.board.dto.BoardDto;
import com.huyeon.superspace.domain.board.dto.ContentDto;
import com.huyeon.superspace.domain.board.dto.ContentUpdateDto;
import com.huyeon.superspace.domain.board.repository.NewBoardRepository;
import com.huyeon.superspace.domain.board.repository.NewCommentRepository;
import com.huyeon.superspace.domain.board.repository.NewContentRepository;
import com.huyeon.superspace.domain.board.repository.TempPostRepository;
import com.huyeon.superspace.domain.group.document.Member;
import com.huyeon.superspace.domain.group.repository.NewMemberRepository;
import com.huyeon.superspace.domain.noty.dto.NotyPayloadDto;
import com.huyeon.superspace.domain.noty.entity.NotyType;
import com.huyeon.superspace.domain.noty.service.NotyUtils;
import com.huyeon.superspace.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewBoardService {
    private final NewMemberRepository memberRepository;
    private final NewBoardRepository boardRepository;
    private final NewContentRepository contentRepository;
    private final NewCommentRepository commentRepository;
    private final TempPostRepository tempPostRepository;
    private final NotyUtils notyUtils;

    public BoardDto getBoard(Long id) {
        Board board = findBoard(id);
        return new BoardDto(board);
    }

    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow();
    }

    public Long createBoard(String userEmail, BoardDto request) {
        String contentId = contentRepository.save(new Content(request.getContent())).getId();
        request.getContent().setId(contentId);

        Board board = new Board(request);
        board.setAuthor(userEmail);
        Long id = boardRepository.save(board).getId();

        CompletableFuture.runAsync(() -> {
            NotyPayloadDto payload = NotyPayloadDto.builder()
                    .type(NotyType.NOTICE)
                    .groupUrl(board.getGroupUrl())
                    .board(request)
                    .build();

            notyUtils.publishNoty(payload);
        }).thenAcceptAsync((v) -> log.info("[공지사항 알림 발송 완료]"));

        return id;
    }

    public Long saveBoard(BoardDto request, String target) {
        BoardDto origin = getBoard(request.getId());
        change(origin, request, target);
        return boardRepository.save(new Board(origin)).getId();
    }

    private void change(BoardDto origin, BoardDto request, String target) {
        switch (target) {
            case "TITLE":
                origin.setTitle(request.getTitle());
                break;
            case "DESCRIPTION":
                origin.setDescription(request.getDescription());
                break;
            case "CATEGORY":
                origin.setCategoryId(request.getCategoryId());
                origin.setCategoryName(request.getCategoryName());
                break;
            case "STATUS":
                origin.setStatus(request.getStatus());
                break;
            case "TAGS":
                origin.setTags(request.getTags());
                break;
        }
    }

    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id).orElseThrow();
        contentRepository.delete(board.getContent());
        commentRepository.deleteAllByBoardId(board.getId());
        boardRepository.deleteById(id);
    }

    public ContentDto getContentById(String contentId) {
        Optional<Content> optional = contentRepository.findById(contentId);
        return optional.map(ContentDto::new).orElseThrow();
    }

    public String saveContent(ContentUpdateDto request) {
        ContentDto origin = getBoard(request.getBoardId()).getContent();
        origin.setHtml(request.getMarkdown());
        return contentRepository.save(new Content(origin)).getId();
    }

    public List<BoardDto> getNextGroup(String groupUrl, Long lastIndex, int page) {
        List<Board> next = boardRepository.findNextLatestInGroup(
                groupUrl,
                lastIndex,
                PageRequest.of(page, 50)
        );

        convertEmailToNickname(next);

        return mapToDtoList(next);
    }

    public List<BoardDto> getNextNotice(String noticeId, Long lastIndex, int page) {
        return getNextCategory(noticeId, lastIndex, page);
    }

    private List<BoardDto> mapToDtoList(List<Board> boards) {
        return boards.stream()
                .map(BoardDto::new)
                .collect(Collectors.toList());
    }

    public List<BoardDto> getNextCategory(String categoryId, Long lastIndex, int page) {
        List<Board> next = boardRepository.findNextLatestInCategory(
                categoryId,
                lastIndex,
                PageRequest.of(page, 50)
        );

        convertEmailToNickname(next);

        return mapToDtoList(next);
    }

    private void convertEmailToNickname(List<Board> boards) {
        for (Board board : boards) {
            String author = board.getAuthor();
            String nickname = memberRepository.findByGroupUrlAndUserEmail(board.getGroupUrl(), author)
                    .orElseThrow().getNickname();
            board.setAuthor(nickname);
        }
    }

    public List<BoardDto> getNextMember(String memberId, Long lastIndex, int page) {
        Member member = findMemberById(memberId);

        List<Board> next = boardRepository.findNextLatestInUser(
                member.getUserEmail(),
                member.getGroupUrl(),
                lastIndex,
                PageRequest.of(page, 50)
        );

        return mapToDtoList(next);
    }

    private Member findMemberById(String memberId) {
        return memberRepository.findById(memberId).orElseThrow();
    }

    public void deleteAllByGroupUrl(String groupUrl) {
        boardRepository.deleteAllByGroupUrl(groupUrl);
    }

    public Long saveTemporally(String userEmail, BoardDto request) {
        long count = countTempPostByEmailAndUrl(userEmail, request.getGroupUrl());

        if (count >= 50) {
            throw new BadRequestException("임시 저장글 50개 초과로 저장할 수 없습니다.");
        }

        TempPost tempPost = new TempPost(request);
        tempPost.setAuthor(userEmail);
        return tempPostRepository.save(tempPost).getId();
    }

    private long countTempPostByEmailAndUrl(String email, String groupUrl) {
        return tempPostRepository.countAllByAuthorAndGroupUrl(email, groupUrl);
    }

    public List<TempPost> findTempPostByEmailAndUrl(String email, String groupUrl) {
        return tempPostRepository.findAllByAuthorAndGroupUrl(email, groupUrl);
    }

    public TempPost findTempPostById(Long tempPostId) {
        return tempPostRepository.findById(tempPostId).orElseThrow();
    }
}

package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CommentDto;
import com.huyeon.apiserver.model.dto.PageReqDto;
import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.history.CommentHistory;
import com.huyeon.apiserver.repository.BoardRepository;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.huyeon.apiserver.support.JsonParse.readJson;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentHistoryRepo commentHistoryRepo;

    public List<CommentDto> getCommentInUser(User user, PageReqDto request) {
        List<Comment> comments = getNextComments(user, request);
        return mapToBoardComment(comments);
    }

    private List<Comment> getNextComments(User user, PageReqDto request) {
        return commentRepository.findNextLatestByUserEmail(
                user.getEmail(),
                request.getNow(),
                PageRequest.of(request.getNextPage(), 10)
        );
    }

    private List<CommentDto> mapToBoardComment(List<Comment> comments) {
        List<CommentDto> boardComments = new ArrayList<>();

        for (Comment comment : comments) {
            Board board = getBoardByComment(comment);
            CommentDto boardComment = getBoardComment(board, comment);
            boardComments.add(boardComment);
        }
        return boardComments;
    }

    private Board getBoardByComment(Comment comment) {
        return boardRepository.findById(comment.getId()).orElseThrow();
    }

    private CommentDto getBoardComment(Board board, Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .title(board.getTitle())
                .author(comment.getUserName())
                .location(board.getGroupName() + " - " + board.getCategoryName())
                .date(comment.getUpdatedAt())
                .uploadTime(calculateUploadTime(comment.getUpdatedAt()))
                .comment(comment.getComment())
                .url("/workspace/" + board.getGroupUrl() + "/board/" + board.getId())
                .build();
    }

    private String calculateUploadTime(LocalDateTime updatedAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration time = Duration.between(updatedAt, now);
        long seconds = time.getSeconds();
        if (seconds < 60) return seconds + "초 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86_400) return (seconds / 3600) + "시간 전";

        Period date = Period.between(updatedAt.toLocalDate(), now.toLocalDate());
        if(date.getDays() < 7) return date.getDays() + "일 전";
        if(date.getDays() < 30) return (date.getDays() / 7) + "주 전";
        if(date.getMonths() < 12) return date.getMonths() + "달 전";
        return date.getYears() + "년 전";
    }

    public void createComment(User author, Board board, CommentDto commentDto) {
        Comment comment = Comment.builder()
                .board(board)
                .user(author)
                .comment(commentDto.getComment())
                .build();

        commentRepository.save(comment);
    }

    public void removeComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow();
        commentRepository.delete(comment);
    }

    public List<CommentDto> getCommentsResponseByBoardId(Long boardId) {
        List<Comment> comments = getCommentsByBoardId(boardId);
        return mapToCommentDto(comments);
    }

    private List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findAllByBoardId(boardId);
    }

    private List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentDto::new)
                .collect(Collectors.toList());
    }

    public void editComment(CommentDto editComment) {
        Comment comment = commentRepository.findById(editComment.getId()).orElseThrow();

        comment.setComment(editComment.getComment());
        commentRepository.save(comment);
    }

    //댓글 수정이력
    public List<CommentHistory> commentHistory(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()) {
            return commentHistoryRepo.findAllByCommentId(comment.get().getId());
        }
        return List.of();
    }
}

package com.huyeon.superspace.domain.board.service;

import com.huyeon.superspace.domain.board.dto.CommentDto;
import com.huyeon.superspace.domain.board.dto.PageReqDto;
import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.Comment;
import com.huyeon.superspace.domain.board.entity.history.CommentHistory;
import com.huyeon.superspace.domain.board.repository.BoardRepository;
import com.huyeon.superspace.domain.board.repository.CommentRepository;
import com.huyeon.superspace.domain.board.repository.history.CommentHistoryRepo;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentHistoryRepo commentHistoryRepo;

    public List<CommentDto> getCommentInUser(String email, PageReqDto request) {
        List<Comment> comments = getNextComments(email, request);
        return mapToBoardComment(comments);
    }

    private List<Comment> getNextComments(String email, PageReqDto request) {
        return commentRepository.findNextLatestByUserEmail(
                email,
                request.getNow(),
                PageRequest.of(request.getNextPage(), 10)
        );
    }

    private List<CommentDto> mapToBoardComment(List<Comment> comments) {
        List<CommentDto> boardComments = new ArrayList<>();

        for (Comment comment : comments) {
            Board board = comment.getBoard();
            CommentDto boardComment = getBoardComment(board, comment);
            boardComments.add(boardComment);
        }
        return boardComments;
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

    public Long createComment(String email, Long boardId, CommentDto commentDto) {
        Board board = findBoardById(boardId);
        User author = findUserByEmail(email);

        Comment comment = Comment.builder()
                .board(board)
                .user(author)
                .comment(commentDto.getComment())
                .build();

        return commentRepository.save(comment).getId();
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId).orElseThrow();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public void removeComment(Long commentId) {
        commentRepository.deleteById(commentId);
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
    public List<CommentHistory> commentHistory(Long commentId) {
        return commentHistoryRepo.findAllByCommentId(commentId);
    }
}

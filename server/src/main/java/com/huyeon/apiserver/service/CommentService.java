package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.CommentDto;
import com.huyeon.apiserver.model.entity.Comment;
import com.huyeon.apiserver.model.entity.history.CommentHistory;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.huyeon.apiserver.support.JsonParse.readJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentHistoryRepo commentHistoryRepo;

    //댓글 가져오기
    public Optional<Comment> getComment(Long id) {
        return commentRepository.findById(id);
    }

    public List<Comment> getCommentsByBoardId(Long boardId) {
        return commentRepository.findAllByBoardId(boardId).orElse(List.of());
    }

    public List<CommentDto> getCommentsResponseByBoardId(Long boardId) {
        List<Comment> comments = getCommentsByBoardId(boardId);
        return mapToCommentDto(comments);
    }

    private List<CommentDto> mapToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentDto.builder()
                        .id(comment.getId())
                        .author(comment.getUserEmail())
                        .date(calculateUploadTime(comment.getUpdatedAt()))
                        .body(comment.getComment())
                        .build())
                .collect(Collectors.toList());
    }

    private String calculateUploadTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        Duration time = Duration.between(createdAt, now);
        long seconds = time.getSeconds();
        if (seconds < 60) return seconds + "초 전";
        if (seconds < 3600) return (seconds / 60) + "분 전";
        if (seconds < 86_400) return (seconds / 3600) + "시간 전";

        Period date = Period.between(createdAt.toLocalDate(), now.toLocalDate());
        if(date.getDays() < 7) return date.getDays() + "일 전";
        if(date.getDays() < 30) return (date.getDays() / 7) + "주 전";
        if(date.getMonths() < 12) return date.getMonths() + "달 전";
        return date.getYears() + "년 전";
    }

    //댓글 생성
    public Long createComment(String author, Long boardId, Comment comment) {
        comment.setUserEmail(author);
        comment.setBoardId(boardId);
        return commentRepository.save(comment).getId();
    }

    //댓글 작성
    public boolean editComment(Long id, String editComment) {
        Optional<Comment> optional = commentRepository.findById(id);
        Comment current = optional.orElse(new Comment());

        Comment edit = readJson(editComment, Comment.class);

        if (edit != null
                && edit.getId().equals(current.getId())) {
            commentRepository.save(edit);
            return true;
        }
        return false;
    }

    //댓글 삭제
    public boolean removeComment(String email, Long id) {
        Optional<Comment> optional = commentRepository.findById(id);
        if (optional.isPresent()) {
            Comment comment = optional.get();
            if (comment.getUserEmail().equals(email)) {
                commentRepository.delete(comment);
                return true;
            }
        }
        return false;
    }

    //댓글 수정이력
    public List<CommentHistory> commentHistory(Long id) {
        Optional<Comment> comment = commentRepository.findById(id);
        if(comment.isPresent()) {
            Optional<List<CommentHistory>> histories =
                    commentHistoryRepo.findAllByCommentId(comment.get().getId());
            if (histories.isPresent()) return histories.get();
        }
        return List.of();
    }
}

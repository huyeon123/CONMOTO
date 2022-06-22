package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.Comment;
import com.huyeon.apiserver.model.dto.history.CommentHistory;
import com.huyeon.apiserver.repository.CommentRepository;
import com.huyeon.apiserver.repository.history.CommentHistoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.huyeon.apiserver.support.JsonParse.readJson;
import static com.huyeon.apiserver.support.JsonParse.writeJson;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentHistoryRepo commentHistoryRepo;

    //댓글 가져오기
    public String getComment(Long id) {
        Comment comment = commentRepository.findById(id).orElse(new Comment());
        if (comment.getId() == null) {
            return writeJson(comment);
        }
        return null;
    }

    //댓글 추가
    public boolean writeComment(String jsonComment) {
        Comment comment = readJson(jsonComment, Comment.class);
        if(comment == null) return false;
        commentRepository.save(comment);
        return true;
    }

    //댓글 수정
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
    public boolean removeComment(Long id) {
        Optional<Comment> optional = commentRepository.findById(id);
        if (optional.isPresent()) {
            commentRepository.delete(optional.get());
            return true;
        }
        return false;
    }

    //댓글 수정이력
    public String commentHistory(Long id) {
        Optional<Comment> optional = commentRepository.findById(id);
        if (optional.isPresent()) {
            List<CommentHistory> histories =
                    commentHistoryRepo.findAllByComment(optional.get());
            return writeJson(histories);
        }
        return null;
    }
}

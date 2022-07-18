package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<List<Comment>> findAllByUserEmail(String email);

    Optional<List<Comment>> findAllByBoardId(Long boardId);
}

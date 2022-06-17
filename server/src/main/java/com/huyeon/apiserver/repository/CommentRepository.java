package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

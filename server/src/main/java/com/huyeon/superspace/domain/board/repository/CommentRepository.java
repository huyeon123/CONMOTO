package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoardId(Long boardId);

    @Query(value = "select c from Comment c where user_email = :email and updated_at < :now order by updated_at desc")
    List<Comment> findNextLatestByUserEmail(
            @Param("email") String email,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}

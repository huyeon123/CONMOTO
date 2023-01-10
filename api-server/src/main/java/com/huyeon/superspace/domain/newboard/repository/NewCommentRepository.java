package com.huyeon.superspace.domain.newboard.repository;

import com.huyeon.superspace.domain.newboard.document.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewCommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByBoardId(String boardId);

    @Query("{author: ?0, updatedAt: {$lt: ?1}}")
    List<Comment> findNextByUserEmail(String userEmail, LocalDateTime now, Pageable pageable);
}

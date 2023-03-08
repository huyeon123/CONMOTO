package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.document.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewCommentRepository extends MongoRepository<Comment, Long> {
    List<Comment> findAllByBoardIdOrderByCreatedAt(Long boardId, Pageable pageable);

    @Query(value = "{id: {$lt: ?2}, author: ?0, groupUrl: ?1}", sort = "{id: -1}")
    List<Comment> findNextByUserEmail(
            String userEmail,
            String groupUrl,
            Long lastIndex,
            Pageable pageable);

    void deleteAllByBoardId(Long boardId);
}

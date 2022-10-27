package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Board;
import com.huyeon.superspace.domain.board.entity.Category;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByCategory(Category category);

    @Query(value = "select b from Board b where updatedAt < :now and group_id = :group order by updatedAt desc")
    List<Board> findNextTenLatest(@Param("group") WorkGroup group, @Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Board b " +
            "where updatedAt < :now and category_id = :category " +
            "order by updatedAt desc")
    List<Board> findNextTenLatest(
            @Param("category") Category category,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @Query(value = "select b from Board b where updatedAt < :now and user_email = :email order by updatedAt desc")
    List<Board> findNextLatest(
            @Param("email") String email,
            @Param("now") LocalDateTime now,
            Pageable pageable);
}

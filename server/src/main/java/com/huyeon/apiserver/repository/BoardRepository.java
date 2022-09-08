package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByUserEmail(String email);

    List<Board> findAllByCategory(Category category);

    @Query(value = "select b from Board b where updatedAt < :now and group_id = :group order by updatedAt desc")
    List<Board> findNextTenLatestInGroup(@Param("group") WorkGroup group, @Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Board b " +
            "where updatedAt < :now and category_id = :category " +
            "order by updatedAt desc")
    List<Board> findNextTenLatestInCategory(
            @Param("category") Category category,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    @Query(value = "select b from Board b where updatedAt < :now and user_email = :user order by updatedAt desc")
    List<Board> findNextLatestInUser(
            @Param("user") User user,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    void deleteAllByUserEmail(String email);
}

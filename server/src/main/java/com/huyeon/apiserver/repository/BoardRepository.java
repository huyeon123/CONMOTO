package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Category;
import com.huyeon.apiserver.model.entity.Groups;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByUserEmail(String email);

    List<Board> findAllByCategory(Category category);

    @Query(value = "select b from Board b where updatedAt < :now and group_id = :group order by updatedAt desc")
    List<Board> findNextTenLatestInGroup(@Param("group") Groups group, @Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Board b " +
            "where updatedAt < :now and category_id = :category " +
            "order by updatedAt desc")
    List<Board> findNextTenLatestInCategory(
            @Param("category") Category category,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    void deleteAllByUserEmail(String email);
}

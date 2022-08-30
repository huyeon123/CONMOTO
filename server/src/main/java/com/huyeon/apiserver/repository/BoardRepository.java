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
    Optional<List<Board>> findAllByUserEmail(String email);

    Optional<List<Board>> findAllByCategory(Category category);

    @Query(value = "select b from Board b where updatedAt < :now and group_id = :group order by updatedAt desc")
    List<Board> find10Latest(@Param("group") Groups group, @Param("now") LocalDateTime now, Pageable pageable);

    @Query(value = "select b from Board b " +
            "where id < :lastId and group_id = :group " +
            "order by id desc")
    List<Board> findNext10LatestInGroup(
            @Param("group") Groups group,
            @Param("lastId") Long lastId,
            Pageable pageable);

    @Query(value = "select b from Board b " +
            "where  id < :lastId and category_id = :category " +
            "order by  id desc")
    List<Board> findNext10LatestInCategory(
            @Param("category") Category category,
            @Param("lastId") Long lastId,
            Pageable pageable);

    @Query(value = "select b from Board b where updatedAt < :now and group_id = :group order by updatedAt desc")
    List<Board> findNextLatestInGroup(@Param("group") Groups group, @Param("now") LocalDateTime now, Pageable pageable);

    void deleteAllByUserEmail(String email);
}

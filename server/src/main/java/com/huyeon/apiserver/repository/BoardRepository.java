package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Board;
import com.huyeon.apiserver.model.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<List<Board>> findAllByUserEmail(String email);

    Optional<List<Board>> findAllByCategory(Category category);

    void deleteAllByUserEmail(String email);
}

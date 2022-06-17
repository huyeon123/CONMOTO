package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
}

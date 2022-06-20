package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByUser(User user);
}

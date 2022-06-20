package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.ContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {
    List<ContentBlock> findAllByBoard(Board board);
}

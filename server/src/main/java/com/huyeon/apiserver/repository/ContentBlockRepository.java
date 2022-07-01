package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.Board;
import com.huyeon.apiserver.model.dto.ContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {
    Optional<List<ContentBlock>> findAllByBoardId(Long boardId);
}

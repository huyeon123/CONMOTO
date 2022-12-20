package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.ContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {
    List<ContentBlock> findAllByBoardId(Long boardId);

    List<ContentBlock> findTop3ByBoardId(Long boardId);
}

package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByBoardId(Long boardId);

    void deleteAllByBoardId(Long boardId);
}

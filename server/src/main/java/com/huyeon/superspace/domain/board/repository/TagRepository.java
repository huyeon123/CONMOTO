package com.huyeon.superspace.domain.board.repository;

import com.huyeon.superspace.domain.board.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<List<Tag>> findAllByBoardId(Long boardId);
}

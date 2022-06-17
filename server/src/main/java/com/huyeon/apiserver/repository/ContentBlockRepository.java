package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.dto.ContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentBlockRepository extends JpaRepository<ContentBlock, Long> {
}

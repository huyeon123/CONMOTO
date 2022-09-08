package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<WorkGroup, Long> {
    boolean existsByUrlPath(String urlPath);

    Optional<WorkGroup> findByUrlPath(String urlPath);
}

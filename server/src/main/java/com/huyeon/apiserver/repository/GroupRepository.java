package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Groups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {
    boolean existsByUrlPath(String urlPath);

    Optional<Groups> findByUrlPath(String urlPath);
}

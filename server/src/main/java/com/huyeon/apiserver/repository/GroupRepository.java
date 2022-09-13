package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<WorkGroup, Long> {
    @Query(value = "select w.name from WorkGroup w where url_path = :urlPath")
    Optional<String> findNameByUrl(@Param("urlPath") String urlPath);

    Optional<WorkGroup> findByUrlPath(String urlPath);

    boolean existsByUrlPath(String urlPath);
}

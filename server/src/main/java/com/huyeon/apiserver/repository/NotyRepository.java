package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Noty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotyRepository extends JpaRepository<Noty, Long> {
}

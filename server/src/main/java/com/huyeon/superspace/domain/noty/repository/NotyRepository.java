package com.huyeon.superspace.domain.noty.repository;

import com.huyeon.superspace.domain.noty.entity.Noty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotyRepository extends JpaRepository<Noty, Long> {
}

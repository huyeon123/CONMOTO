package com.huyeon.superspace.domain.noty.repository;

import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotyReceiverRepository extends JpaRepository<ReceivedNoty, Long> {
    List<ReceivedNoty> findAllByUserEmail(String userEmail);

    List<ReceivedNoty> findAllByUserEmailOrderByIdDesc(String userEmail, Pageable pageable);
}

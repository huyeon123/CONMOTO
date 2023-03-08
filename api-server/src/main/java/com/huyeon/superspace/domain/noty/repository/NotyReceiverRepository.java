package com.huyeon.superspace.domain.noty.repository;

import com.huyeon.superspace.domain.noty.entity.ReceivedNoty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotyReceiverRepository extends JpaRepository<ReceivedNoty, Long> {
    List<ReceivedNoty> findAllByUserEmail(String userEmail);

    @Query("select n from ReceivedNoty n where id < :lastIndex and userEmail = :userEmail order by id desc")
    List<ReceivedNoty> findNextNoty(
            @Param("userEmail")String userEmail,
            @Param("lastIndex") Long lastIndex,
            Pageable pageable);
}

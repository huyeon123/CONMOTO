package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Noty;
import com.huyeon.apiserver.model.entity.NotyReceiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotyReceiverRepository extends JpaRepository<NotyReceiver, Long> {
    List<NotyReceiver> findAllByUserEmail(String userEmail);
    List<NotyReceiver> findAllByNoty(Noty noty);
}

package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupManagerRepository extends JpaRepository<GroupManager, Long> {
    Optional<List<GroupManager>> findAllByGroup(WorkGroup group);

    Optional<GroupManager> findByGroupAndManager(WorkGroup group, User manger);

    void deleteByGroupAndManager(WorkGroup group, User manager);

    boolean existsByGroupAndManager(WorkGroup group, User manager);
}

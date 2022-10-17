package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.GroupManager;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupManagerRepository extends JpaRepository<GroupManager, Long> {
    List<GroupManager> findAllByGroup(WorkGroup group);

    void deleteByGroupAndManager(WorkGroup group, User manager);

    boolean existsByGroupAndManager(WorkGroup group, User manager);
}

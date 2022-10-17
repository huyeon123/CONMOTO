package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.group.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    boolean existsByUserAndGroup(User user, WorkGroup group);

    List<UserGroup> findAllByUser(User user);

    List<UserGroup> findAllByGroup(WorkGroup group);

    void deleteAllByGroup(WorkGroup group);

    void deleteByUserAndGroup(User user, WorkGroup group);
}

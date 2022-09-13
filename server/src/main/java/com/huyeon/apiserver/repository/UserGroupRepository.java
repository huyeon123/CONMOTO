package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.WorkGroup;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    List<UserGroup> findAllByUser(User user);

    List<UserGroup> findAllByGroup(WorkGroup group);

    void deleteAllByGroup(WorkGroup group);

    void deleteByUserAndGroup(User user, WorkGroup group);
}

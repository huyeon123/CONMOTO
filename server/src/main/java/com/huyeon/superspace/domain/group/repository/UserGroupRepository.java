package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.UserGroup;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    boolean existsByUserAndGroup(User user, WorkGroup group);

    @Query(value = "select ug from UserGroup ug where user_email = :email")
    List<UserGroup> findGroupsByEmail(@Param("email") String email);

    List<UserGroup> findAllByGroup(WorkGroup group);

    void deleteAllByGroup(WorkGroup group);

    void deleteByUserAndGroup(User user, WorkGroup group);
}

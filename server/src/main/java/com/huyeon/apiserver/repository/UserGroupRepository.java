package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import com.huyeon.apiserver.model.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<List<UserGroup>> findAllByUser(User user);

    Optional<List<UserGroup>> findAllByGroup(Groups group);

    void deleteAllByGroup(Groups group);

    void deleteByUserAndGroup(User user, Groups group);
}

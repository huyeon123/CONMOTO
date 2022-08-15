package com.huyeon.apiserver.repository;

import com.huyeon.apiserver.model.entity.GroupManager;
import com.huyeon.apiserver.model.entity.Groups;
import com.huyeon.apiserver.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupManagerRepository extends JpaRepository<GroupManager, Long> {
    Optional<List<GroupManager>> findAllByGroup(Groups group);

    Optional<GroupManager> findByGroupAndManager(Groups group, User manger);

    void deleteByGroupAndManager(Groups group, User manager);

    boolean existsByGroupAndManager(Groups group, User manager);
}

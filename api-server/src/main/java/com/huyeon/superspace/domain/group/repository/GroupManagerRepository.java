package com.huyeon.superspace.domain.group.repository;

import com.huyeon.superspace.domain.group.entity.GroupManager;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupManagerRepository extends JpaRepository<GroupManager, Long> {
    List<GroupManager> findAllByGroup(WorkGroup group);

    void deleteByGroupAndManager(WorkGroup group, User manager);

    boolean existsByGroupAndManager(WorkGroup group, User manager);

    @Query(value = "SELECT CASE " +
            "WHEN COUNT(m) > 0 THEN true ELSE false END " +
            "FROM GroupManager m " +
            "WHERE m.manager.email = :email AND m.group.urlPath = :groupUrl")
    boolean existsByEmailAndUrl(@Param("email") String email, @Param("groupUrl") String groupUrl);
}

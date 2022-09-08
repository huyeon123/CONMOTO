package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.MemberDto;
import com.huyeon.apiserver.model.entity.WorkGroup;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
public class UserGroupServiceTest {
    @Autowired
    UserGroupService userGroupService;

    @Autowired
    GroupService groupService;

    @Test
    public void expelMemberTest() {
        MemberDto request = MemberDto.builder().email("user@test.com").build();
        WorkGroup group = groupService.getGroupByUrl("test-group");
        userGroupService.expelUserFromGroup(request, group);

    }
}

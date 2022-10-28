package com.huyeon.superspace.domain.group.service;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.auth.service.AuthService;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@SpringBootTest
public class MemberServiceTest {

    @Autowired
    AuthService authService;

    @Autowired
    GroupService groupService;

    @Autowired
    MemberService memberService;

    String email = "test@test.com";
    String newEmail = "sub@test.com";
    String groupUrl = "test-group";

    @BeforeAll
    void init() {
        GroupDto request = new GroupDto("테스트그룹", groupUrl, "test");
        groupService.createGroup(email, request);

        UserSignUpReq newUser = new UserSignUpReq("SUB_USER", newEmail, "12345", null);
        authService.signUp(newUser);
    }

    @Test
    @DisplayName("그룹 초대")
    void inviteMember() {
        //given: 새로운 사용자(newEmail), groupUrl

        //when, then
        assertDoesNotThrow(() -> memberService.inviteMember(groupUrl, newEmail));
    }

    @Test
    @DisplayName("그룹 가입")
    void joinMember(){
        //given: 새로운 사용자(newEmail), groupUrl

        //when, then
        assertDoesNotThrow(() -> memberService.joinMember(newEmail, groupUrl));
    }

    @Test
    @DisplayName("멤버 역할 변경")
    void saveMemberRole(){
        //given
        List<MemberDto> request = List.of(
                new MemberDto("SUB_USER", newEmail, "그룹 관리자")
        );

        //when
        memberService.saveMemberRole(groupUrl, request);

        //then
        User subUser = groupService.findUserByEmail(newEmail);
        WorkGroup group = groupService.getGroupByUrl(groupUrl);
        String role = groupService.checkRole(group, subUser);

        assertEquals("그룹 관리자", role);
    }

    @Test
    @DisplayName("멤버 강퇴")
    void expelUser(){
        //given
        MemberDto request = new MemberDto("SUB_USER", newEmail, "일반 멤버");

        //when, then
        assertDoesNotThrow(() -> memberService.expelUser(groupUrl, request));
    }
}

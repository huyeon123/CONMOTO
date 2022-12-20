package com.huyeon.superspace.domain.group.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.auth.service.AuthService;
import com.huyeon.superspace.domain.group.dto.GroupDto;
import com.huyeon.superspace.domain.group.dto.MemberDto;
import com.huyeon.superspace.domain.group.entity.WorkGroup;
import com.huyeon.superspace.domain.group.service.GroupService;
import com.huyeon.superspace.domain.group.service.MemberService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
public class GroupControllerTest {

    @Autowired
    AuthService authService;

    @Autowired
    GroupService groupService;

    @Autowired
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Cookie cookie;

    @BeforeAll
    void login() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login?username=test@test.com&password=12345&remember-me=true")
                )
                .andExpect(cookie().exists("remember-me"))
                .andExpect(result -> {
                    cookie = result.getResponse().getCookie("remember-me");
                });
    }

    @Test
    @DisplayName("그룹 생성")
    void createGroup() throws Exception {
        //given
        GroupDto request = new GroupDto("테스트그룹", "test-group", "테스트입니다.");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/group")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String contentAsString = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                    GroupDto readValue = objectMapper.readValue(contentAsString, GroupDto.class);
                    assertEquals(request.getName(), readValue.getName());
                    assertTrue(readValue.getUrl().startsWith(request.getUrl()));
                });
    }

    @Test
    @DisplayName("그룹 수정")
    void editGroup() throws Exception {
        //given
        createTestGroup();
        String groupUrl = "test-group";
        String newUrl = "new-url";
        GroupDto request = new GroupDto("수정 테스트", newUrl, "수정 완료");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/api/group/" + groupUrl)
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
        WorkGroup group = groupService.getGroupByUrl(newUrl);
        assertEquals(request.getName(), group.getName());
        assertEquals(request.getDescription(), group.getDescription());
    }

    private void createTestGroup() {
        String email = "test@test.com";
        GroupDto request = new GroupDto("테스트그룹", "test-group", "테스트입니다.");
        groupService.createGroup(email, request);
    }

    @Test
    @DisplayName("그룹 삭제")
    void deleteGroup() throws Exception {
        //given
        createTestGroup();
        String groupUrl = "test-group";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/group/" + groupUrl)
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("멤버 역할 변경")
    void saveMemberAuthority() throws Exception {
        //given
        joinNewMember();
        String groupUrl = "test-group";
        List<MemberDto> request = List.of(
                new MemberDto("SUB_USER", "sub@test.com", "그룹 관리자")
        );

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/group/" + groupUrl + "/member")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private void joinNewMember() {
        createTestGroup();
        addNewUser();
        memberService.joinMember("sub@test.com", "test-group");
    }

    private void addNewUser() {
        UserSignUpReq signUpReq = new UserSignUpReq("SUB_USER", "sub@test.com", "12345", null);
        authService.signUp(signUpReq);
    }

    @Test
    @DisplayName("멤버 강퇴")
    void expelMember() throws Exception {
        //given
        joinNewMember();
        String groupUrl = "test-group";
        MemberDto request = new MemberDto("SUB_USER", "sub@test.com", null);

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/group/" + groupUrl + "/member")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("멤버 초대")
    void inviteMember() throws Exception {
        //given
        createTestGroup();
        addNewUser();
        String groupUrl = "test-group";
        TestInvite invite = new TestInvite("sub@test.com");

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/group/" + groupUrl + "/invite")
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invite))
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private class TestInvite {
        String email;

        public TestInvite(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }
    }

    @Test
    @DisplayName("멤버 가입")
    void joinMember() throws Exception {
        //given
        createTestGroup();
        addNewUser();
        Cookie cookie = newLogin();
        String groupUrl = "test-group";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/group/" + groupUrl + "/join")
                        .cookie(cookie)
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    private Cookie newLogin() throws Exception {
        final Cookie[] cookie = new Cookie[1];

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/auth/login?username=sub@test.com&password=12345&remember-me=true")
                )
                .andExpect(cookie().exists("remember-me"))
                .andExpect(result -> {
                    cookie[0] = result.getResponse().getCookie("remember-me");
                });

        return cookie[0];
    }


}

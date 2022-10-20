package com.huyeon.superspace.domain.user.service;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    final String email = "test@test.com";

    @BeforeAll
    void init() {
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);
        userRepository.save(user);
    }

    @Test
    @DisplayName("회원정보 수정")
    void editInfo() {
        //given
        LocalDate localDate = LocalDate.now();
        String newName = "CHANGE_USER";
        User changeUser = User.builder()
                .email(email)
                .name(newName)
                .password("12345")
                .birthday(localDate)
                .build();

        //when
        userService.editInfo(email, changeUser);

        //then
        changeUser = userRepository.findByEmail(email).orElseThrow();
        assertEquals(newName, changeUser.getName());
        assertEquals(localDate, changeUser.getBirthday());
    }

    @Test
    @DisplayName("회원탈퇴")
    void resign(){
        //when, then
        assertDoesNotThrow(() -> userService.resign(email));
    }

    @Test
    @DisplayName("회원탈퇴 취소")
    void cancelResign(){
        //given
        userService.resign(email);

        //when
        userService.cancelResign(email);

        //then
        User user = userRepository.findByEmail(email).orElseThrow();
        assertTrue(user.isEnabled());
        assertNull(user.getExpireDate());
    }

    @Test
    @DisplayName("회원정보 변경이력 확인")
    void myInfoHistory(){
        //when
        List<UserHistory> userHistories = userService.myInfoHistory(email);

        //then
        assertFalse(userHistories.isEmpty());
    }
}

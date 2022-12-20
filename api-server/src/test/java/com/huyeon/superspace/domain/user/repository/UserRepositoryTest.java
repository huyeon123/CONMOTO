package com.huyeon.superspace.domain.user.repository;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    String email = "test@test.com";

    @Test
    @DisplayName("사용자 저장")
    void save() {
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", email, "12345", null);
        User user = new User(request);

        //when, then
        assertDoesNotThrow(() -> userRepository.save(user));
    }

    @Test
    @DisplayName("Email로 사용자 조회")
    void findByEmail() {
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", email, "12345", null);
        User user = new User(request);
        userRepository.save(user);

        //when
        Optional<User> optional = userRepository.findByEmail(email);

        //then
        assertTrue(optional.isPresent());
    }

    @Test
    @DisplayName("사용자 내용 수정")
    void update(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", email, "12345", null);
        User user = new User(request);
        userRepository.save(user);

        //when
        user.setBirthday(LocalDate.now());
        userRepository.save(user);

        //then
        Optional<User> optional = userRepository.findByEmail(email);
        assertTrue(optional.isPresent());
        User newUser = optional.get();
        assertEquals(user.getBirthday(), newUser.getBirthday());
    }

    @Test
    @DisplayName("사용자 삭제")
    void delete(){
        //given
        UserSignUpReq request = new UserSignUpReq("TEST_USER", email, "12345", null);
        User user = new User(request);
        userRepository.save(user);

        //when, then
        assertDoesNotThrow(() -> userRepository.delete(user));
    }
}

package com.huyeon.superspace.domain.auth.repository;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class AuthRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthRepository authRepository;

    @BeforeAll
    void addUser() {
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);
        userRepository.save(user);
    }

    @Test
    @DisplayName("이메일로 사용자 조회")
    void findByEmail(){
        //given
        String email = "test@test.com";

        //when
        Optional<User> optionalUser = authRepository.findByEmail(email);

        //then
        assertTrue(optionalUser.isPresent());
        User user = optionalUser.get();
        assertEquals(email, user.getEmail());
        System.out.println(user);
    }

    @Test
    @DisplayName("이메일 중복 여부 확인")
    void existByEmail(){
        //given
        String email = "test@test.com";

        //when
        boolean exist = authRepository.existsByEmail(email);

        //then
        assertTrue(exist);
    }
}

package com.huyeon.apiserver.service;

import com.huyeon.apiserver.model.dto.User;
import com.huyeon.apiserver.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @DisplayName("signUp")
    @Test
    void test_1(){
        String jsonMsg = "{\"name\":\"userTest\",\"email\":\"asgjh1@asg.asuc\",\"password\":\"dad\"}";
        userService.signUp(jsonMsg);
        userRepository.findAll().forEach(System.out::println);
    }

    @DisplayName("")
    @Test
    void test_2() {
        System.out.println(userService.userInfo(1L));
    }
}

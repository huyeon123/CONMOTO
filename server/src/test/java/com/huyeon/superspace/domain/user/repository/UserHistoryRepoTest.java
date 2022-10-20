package com.huyeon.superspace.domain.user.repository;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserHistoryRepoTest {
    
    @Autowired
    UserHistoryRepo userHistoryRepo;
    
    @Autowired
    UserRepository userRepository;

    @BeforeAll
    void init() {
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        User user = new User(request);
        userRepository.save(user);
    }
    
    @Test
    @DisplayName("사용자 변경이력 조회")
    void findAllByUserEmail(){
        //given
        String email = "test@test.com";
        
        //when
        List<UserHistory> histories = userHistoryRepo.findAllByUserEmail(email);

        //then
        assertEquals(1, histories.size());
    }
}

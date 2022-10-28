package com.huyeon.superspace.domain.user.listener;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import com.huyeon.superspace.domain.user.entity.User;
import com.huyeon.superspace.domain.user.entity.UserHistory;
import com.huyeon.superspace.domain.user.repository.UserHistoryRepo;
import com.huyeon.superspace.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class UserHistoryListenerTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserHistoryRepo userHistoryRepo;

    @Test
    @Transactional
    @DisplayName("사용자 내용 저장 시 history 자동 기록")
    void loggingHistoryOnPersist(){
        //given
        String email = "test@test.com";
        User user = createUserInstance(email);

        //when
        userRepository.save(user);
        userRepository.flush();

        //then
        List<UserHistory> histories = userHistoryRepo.findAllByUserEmail(email);
        assertEquals(1, histories.size());
    }

    User createUserInstance(String email) {
        UserSignUpReq request = new UserSignUpReq(
                "TEST_USER",
                email,
                "12345",
                null
        );

        return new User(request);
    }

    @Test
    @Transactional
    @DisplayName("사용자 내용 변경 시 history 자동 기록")
    void loggingHistoryOnUpdate(){
        //given
        String email = "test@test.com";
        User user = createUserInstance(email);
        userRepository.save(user);
        userRepository.flush(); //원래는 동시에 발생할 수가 없으므로 flush 처리

        //when
        user.setBirthday(LocalDate.now());
        userRepository.save(user);

        //then
        List<UserHistory> histories = userHistoryRepo.findAllByUserEmail(email);
        assertEquals(2, histories.size());
    }
}

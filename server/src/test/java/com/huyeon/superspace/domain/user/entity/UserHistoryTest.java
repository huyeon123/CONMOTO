package com.huyeon.superspace.domain.user.entity;

import com.huyeon.superspace.domain.auth.dto.UserSignUpReq;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class UserHistoryTest {

    @Test
    @DisplayName("UserHistory 생성")
    void createUserHistory(){
        //given
        User user = createUser();

        //when, then
        assertDoesNotThrow(() -> new UserHistory(user));
    }

    User createUser() {
        UserSignUpReq request = new UserSignUpReq("TEST_USER", "test@test.com", "12345", null);
        return new User(request);
    }
}

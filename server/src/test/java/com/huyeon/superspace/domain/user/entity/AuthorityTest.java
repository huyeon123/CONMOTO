package com.huyeon.superspace.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthorityTest {
    @Test
    @DisplayName("Authority 생성 테스트")
    void createAuthority() {
        //given
        String role = Authority.ROLE_USER;

        //when, then
        assertDoesNotThrow(() -> new Authority(role));
    }

    @Test
    @DisplayName("Authority equals 테스트")
    void equals() {
        //given
        Authority user1 = new Authority(Authority.ROLE_USER);
        Authority user2 = new Authority(Authority.ROLE_USER);

        //when, then
        assertTrue(user1.equals(user2));
    }

    @Test
    @DisplayName("Authority hashCode 테스트")
    void hashCodeTest() {
        //given
        Authority user1 = new Authority(Authority.ROLE_USER);
        Authority user2 = new Authority(Authority.ROLE_USER);

        //when
        int hashCode1 = user1.hashCode();
        int hashCode2 = user2.hashCode();

        //then
        assertEquals(hashCode1, hashCode2);
    }
}

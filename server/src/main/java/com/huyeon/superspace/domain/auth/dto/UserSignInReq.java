package com.huyeon.superspace.domain.auth.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignInReq {
    private String email;
    private String password;
    private boolean rememberMe;
}

package com.huyeon.apiserver.model.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class UserSignInReq {
    private String email;
    private String password;
    private boolean rememberMe;
}

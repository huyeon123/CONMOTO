package com.huyeon.apiserver.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignInReq {
    private String email;
    private String password;
    private boolean rememberMe;
}

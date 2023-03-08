package com.huyeon.superspace.domain.user.dto;

import lombok.Getter;

@Getter
public class EditRes {
    private final boolean success;
    private final String message;

    public EditRes() {
        this.success = true;
        this.message = "성공";
    }

    public EditRes(String message) {
        this.success = false;
        this.message = message;
    }
}

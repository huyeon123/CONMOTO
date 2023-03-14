package com.huyeon.cdn.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CKEditor5Res {
    private boolean uploaded;
    private String url;
    private String errorMsg;

    public CKEditor5Res(boolean uploaded, String data) {
        this.uploaded = uploaded;
        if (uploaded) this.url = data;
        else this.errorMsg = data;
    }
}

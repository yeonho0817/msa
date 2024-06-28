package com.msa.member.response;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ResponseCode {
    OK(HttpStatus.OK, "OK");

    private final HttpStatus code;
    private final String message;

    public int getCode() {
        return code.value();
    }

    public String getMessage() {
        return message;
    }
}


package com.devteria.identityservice.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    USER_EXISTED(1001, "username đã tồn tại"),
    USERNAME_INVALID(1002, "username must be at least 3 character"),
    INVALID_PASSWORD(1003, "password must be at least 8 character"),
    USER_NOT_EXISTED(1004, "user not existed")
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

}

package com.devteria.identityservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR), //error 500
    USER_EXISTED(1001, "username đã tồn tại", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "username must be at least 3 character", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "password must be at least 8 character", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "user not existed", HttpStatus.NOT_FOUND), //404
    UNAUTHENTICATED(1006, "unauthenticated", HttpStatus.UNAUTHORIZED), // 401 chưa xác thực
    UNAUTHORIZED(1006, "you do not have permission", HttpStatus.FORBIDDEN) //403 không có quyền
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}

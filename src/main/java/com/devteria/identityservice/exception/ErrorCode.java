package com.devteria.identityservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR), //error 500
    INVALID_KEY(1009, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "username đã tồn tại", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "username must be at least 3 character", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1003, "password must be at least 8 character", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1004, "user not existed", HttpStatus.NOT_FOUND), //404
    UNAUTHENTICATED(1006, "unauthenticated", HttpStatus.UNAUTHORIZED), // 401 chưa xác thực
    UNAUTHORIZED(1007, "you do not have permission", HttpStatus.FORBIDDEN), //403 không có quyền
    ROLE_ADMIN_NOT_INITIALIZED(1008, "admin role not initialized", HttpStatus.BAD_REQUEST),
    INVALID_DOD(1009, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
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

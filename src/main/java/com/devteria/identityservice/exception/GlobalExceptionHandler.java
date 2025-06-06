package com.devteria.identityservice.exception;

import com.devteria.identityservice.dto.request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*
*  @ControllerAdvice: Đây là một annotation của Spring được sử dụng để chỉ định rằng class này là một controller advice,
*  nơi bạn có thể định nghĩa các xử lý exception global cho tất cả các controller trong ứng dụng.*/
@ControllerAdvice
public class GlobalExceptionHandler {

/* @ExceptionHandler(value = RuntimeException.class): Đây là một annotation được sử dụng để xác định phương thức nào trong class
*  sẽ xử lý một loại exception cụ thể.
*  Trong trường hợp này, phương thức handlingRuntimeException() sẽ được gọi khi một RuntimeException xảy ra trong ứng dụng.*/
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse> handlingRuntimeExceotion(RuntimeException exception){
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(1001);
        apiResponse.setMessage(exception.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppExceotion(AppException exception){
        ApiResponse apiResponse = new ApiResponse();
        ErrorCode errorCode = exception.getErrorCode();

        //nhan du lieu tu enum ma ta tao truoc thay vi phai nhap nhu tren
        apiResponse.setCode(errorCode.getCode()); //ta tu dinh nghia code
        apiResponse.setMessage(errorCode.getMessage()); //ta tu dinh nghia message

        return ResponseEntity.badRequest().body(apiResponse);
    }

    //xu ly khi nhap
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){
        String enumKey = exception.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

}

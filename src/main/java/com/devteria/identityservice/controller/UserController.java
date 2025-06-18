package com.devteria.identityservice.controller;

import com.devteria.identityservice.dto.request.ApiResponse;
import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//controller sẽ tương tác trực tiếp với các class service
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;


    //post: tạo

    @PostMapping
    //api
    ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    //get: lấy

    @GetMapping
    ApiResponse<List<UserResponse>> getUsers() {
        ApiResponse<List<UserResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUsers());
        return apiResponse;

    }

    //@PathVariable: truyền tham số
    @GetMapping("/{userId}")

    ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getUser(userId));
        return apiResponse;

    }

    //put: update thong tin
    @PutMapping("/{userId}")

    ApiResponse<UserResponse> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(userId, request));
        return apiResponse;

    }

    //delete: xoa
    @DeleteMapping("/{userId}")

    ApiResponse<String> deleteUser(@PathVariable String userId) {
        ApiResponse<String> apiResponse = new ApiResponse<>();
        userService.deleteUser(userId);
        apiResponse.setMessage("User has been deleted");
        return apiResponse;
    }


}


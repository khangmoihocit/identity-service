package com.devteria.identityservice.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

//dùng để chuẩn hóa
@JsonInclude(JsonInclude.Include.NON_NULL) //khi dữ liệu(code, message, result) là null sẽ không hiện thị
public class ApiResponse <T>{
    private int code = 1000;
    private String message;
    private T result; //data byte thay đổi theo từng result api;
}

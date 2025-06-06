package com.devteria.identityservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    //thong thuong khong update username nen bo
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}

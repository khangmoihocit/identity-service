package com.devteria.identityservice.dto.request;

import com.devteria.identityservice.validator.DodConstraint;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserUpdateRequest {
    //thong thuong khong update username nen bo
    @Size(min = 8, message = "INVALID_PASSWORD")
    private String password;
    private String firstName;
    private String lastName;

    @DodConstraint(min = 16, message = "INVALID_DOD")
    private LocalDate dob;

    private List<String> roles;
}

package com.devteria.identityservice.controller;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UserCreationRequest userCreationRequest;
    private UserResponse userResponse;
    private LocalDate dob;

    //sẽ chạy vào method này trước khi chạy vào test
    @BeforeEach
    void initData() {
        dob = LocalDate.of(2005, 2, 18);

        userCreationRequest = UserCreationRequest.builder()
                .firstName("khang")
                .lastName("pham")
                .username("khangpham")
                .password("12345678")
                .dob(dob)
                .build();

        userResponse = UserResponse.builder()
                .id("gwjqgyq2u2")
                .firstName("khang")
                .lastName("pham")
                .username("khangpham")
                .dob(dob)
                .build();
    }

    @Test
    void createUser_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String content = objectMapper.writeValueAsString(userCreationRequest);

        Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(userResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value("1000"));


    }
}

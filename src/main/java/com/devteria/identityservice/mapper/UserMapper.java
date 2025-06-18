package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    @Mapping(source = "firstName", target = "lastName")
    @Mapping(source = "password", target = "password")
    UserResponse toUserResponse(User user);
    List<UserResponse> toUsersResponse(List<User> Users);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

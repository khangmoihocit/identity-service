package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.List;

/*
MapStruct sẽ tự động ánh xạ giữa các lớp nếu thỏa cả 3 điều kiện sau:
    - Tên trường giống nhau
    - Kiểu dữ liệu giống nhau (hoặc chuyển đổi được)
    - Có getter/setter đầy đủ ở cả hai bên
*/
@Mapper(componentModel = "spring")
public interface UserMapper {
    /*
@Mapping(target = ..., source = ...) là gì?
-> Đây là annotation của MapStruct dùng để khai báo ánh xạ thủ công giữa các trường khi:
        Tên trường không giống nhau
        Kiểu dữ liệu không khớp
        Hoặc muốn bỏ qua một trường
        Hoặc muốn ánh xạ một trường phức tạp, nhiều tầng

target: tên thuộc tính trong đối tượng trả về
source: tên thuộc tính trong đối tượng đầu vào
*/

    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    List<UserResponse> toUsersResponse(List<User> Users);

    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}

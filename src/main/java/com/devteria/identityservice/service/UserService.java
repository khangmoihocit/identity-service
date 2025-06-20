package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.enums.Role;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.UserMapper;
import com.devteria.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor //cac thuoc tinh final se tu tao constructor nen khong can annotation @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //makeFinal khoi tao cac thuoc tinh la final
public class UserService {
    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;


    public UserResponse createUser(UserCreationRequest request){

        //phuong thuc existsByUsername : giup kiem tra xem ten da ton tai chua
        if (userRepository.existsByUsername(request.getUsername())) throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);

        //số phức tapj cao sẽ ảnh hưởng đến tốc độ, 10 là mặc định
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //gán quyền
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }


    //tra ve userResponse thay vi user
    public List<UserResponse> getUsers(){
        //hàm trong interface UserRepository kế thừa Jpa
        return userMapper.toUsersResponse(userRepository.findAll());
    }

    public UserResponse getUser(String userId){
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    //update user
    public UserResponse updateUser(String userId, UserUpdateRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(String idUser){
        userRepository.deleteById(idUser);
    }
}

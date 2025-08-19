package com.devteria.identityservice.service;

import com.devteria.identityservice.dto.request.UserCreationRequest;
import com.devteria.identityservice.dto.request.UserUpdateRequest;
import com.devteria.identityservice.dto.response.UserResponse;
import com.devteria.identityservice.entity.Role;
import com.devteria.identityservice.entity.User;
import com.devteria.identityservice.enums.RoleEnum;
import com.devteria.identityservice.exception.AppException;
import com.devteria.identityservice.exception.ErrorCode;
import com.devteria.identityservice.mapper.UserMapper;
import com.devteria.identityservice.repository.RoleRepository;
import com.devteria.identityservice.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor //cac thuoc tinh final se tu tao constructor nen khong can annotation @Autowired
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) //makeFinal khoi tao cac thuoc tinh la final
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;


    public UserResponse createUser(UserCreationRequest request) {
        User user = userMapper.toUser(request);

        //số phức tapj cao sẽ ảnh hưởng đến tốc độ, 10 là mặc định
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        //gán quyền
        Role role = roleRepository.findById(RoleEnum.USER.name())
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        try{
            user = userRepository.save(user);
        }catch (DataIntegrityViolationException ex){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        return userMapper.toUserResponse(user);
    }

    //#PreAuthorize: kiểm tra trước khi vào method, đúng quyền mới chạy code trong method
//    @PreAuthorize("hasRole('ADMIN')")  //sẽ chỉ map với ROLE_ADMIN
    @PreAuthorize("hasAuthority('APPROVE_POST')") //map với các permission
    public List<UserResponse> getUsers() {
        log.info("In method get users");
        return userMapper.toUsersResponse(userRepository.findAll());
    }

    //@PostAuthorize: kiểm tra sau khi method thực hiện xong, nếu đúng điều kiện sẽ trả về, không thì chặn
    //user đang đăng nhập chỉ lấy đc thông tin của mình
    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(String userId) {
        return userMapper.toUserResponse(userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    //update user
    @PreAuthorize("hasAuthority('UPDATE_DATA')")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if(request.getRoles() != null){
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('DELETE_DATA')")
    public void deleteUser(String idUser) {
        userRepository.deleteById(idUser);
    }
}

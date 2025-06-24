package com.devteria.identityservice.mapper;

import com.devteria.identityservice.dto.request.PermissionRequest;
import com.devteria.identityservice.dto.request.RoleRequest;
import com.devteria.identityservice.dto.response.PermissionResponse;
import com.devteria.identityservice.dto.response.RoleResponse;
import com.devteria.identityservice.entity.Permission;
import com.devteria.identityservice.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true) //bo qua khong map
    Role toRole (RoleRequest request);
    RoleResponse toRoleResponse(Role role);
}

package com.project.phone_shop.DTO.Mapper;


import com.project.phone_shop.DTO.Request.RoleRequest;
import com.project.phone_shop.DTO.Response.RoleResponse;
import com.project.phone_shop.Entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}

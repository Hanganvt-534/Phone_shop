package com.project.phone_shop.DTO.Mapper;

import com.project.phone_shop.DTO.Request.PermissionRequest;
import com.project.phone_shop.DTO.Response.PermissionResponse;
import com.project.phone_shop.Entity.Permission;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}

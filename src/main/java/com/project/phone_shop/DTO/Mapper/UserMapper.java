package com.project.phone_shop.DTO.Mapper;

import com.project.phone_shop.DTO.Request.UserRequest;
import com.project.phone_shop.DTO.Response.UserResponse;
import com.project.phone_shop.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User save);
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    User toUser(UserRequest userRequest);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    void updateUser(@MappingTarget User user, UserRequest userRequest);
}

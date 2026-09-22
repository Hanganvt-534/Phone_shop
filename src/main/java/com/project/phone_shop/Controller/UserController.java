package com.project.phone_shop.Controller;

import com.project.phone_shop.DTO.Request.ApiResponse;
import com.project.phone_shop.DTO.Request.UserRequest;
import com.project.phone_shop.DTO.Response.UserResponse;
import com.project.phone_shop.Entity.User;
import com.project.phone_shop.Service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @GetMapping(" ")
    ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.<List<User>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/{id}")
    ApiResponse<UserResponse> getUserById(@PathVariable Long id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(id))
                .build();
    }

    @PostMapping("/create")
    ApiResponse<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(userRequest))
                .build();
    }

    @PutMapping("/update")
    ApiResponse<UserResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.updateUser(userRequest))
                .build();
    }

    @DeleteMapping("/delete/{id}")
    ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder().build();
    }
}

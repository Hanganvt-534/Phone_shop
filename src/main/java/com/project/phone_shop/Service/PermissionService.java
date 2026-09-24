package com.project.phone_shop.Service;

import com.project.phone_shop.DTO.Mapper.PermissionMapper;
import com.project.phone_shop.DTO.Request.PermissionRequest;
import com.project.phone_shop.DTO.Response.PermissionResponse;
import com.project.phone_shop.Entity.Permission;
import com.project.phone_shop.Repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public PermissionResponse create(PermissionRequest request) {
        Permission permission = permissionMapper.toPermission(request);
        permission = permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);
    }

    public List<PermissionResponse> getAll() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toPermissionResponse)
                .toList();
    }

    public void delete(Long permissionId) {
        permissionRepository.deleteById(permissionId);
    }
}

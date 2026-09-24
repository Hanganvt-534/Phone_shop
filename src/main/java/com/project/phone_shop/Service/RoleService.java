package com.project.phone_shop.Service;


import com.project.phone_shop.DTO.Mapper.RoleMapper;
import com.project.phone_shop.DTO.Request.RoleRequest;
import com.project.phone_shop.DTO.Response.RoleResponse;
import com.project.phone_shop.Repository.PermissionRepository;
import com.project.phone_shop.Repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleResponse create(RoleRequest request) {
        var role = roleMapper.toRole(request);

        if (request.getPermissions() != null && !request.getPermissions().isEmpty()) {
            var permissions = permissionRepository.findAllByNameIn(request.getPermissions());
            role.setPermissions(new HashSet<>(permissions));
        }

        role = roleRepository.save(role);
        return roleMapper.toRoleResponse(role);
    }

    public List<RoleResponse> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::toRoleResponse).toList();
    }

    public void delete(Long roleId) {
        roleRepository.deleteById(roleId);
    }
}

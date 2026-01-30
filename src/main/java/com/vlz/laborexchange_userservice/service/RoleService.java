package com.vlz.laborexchange_userservice.service;

import com.vlz.laborexchange_userservice.dto.exception.EntityNotFoundException;
import com.vlz.laborexchange_userservice.entity.Role;
import com.vlz.laborexchange_userservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Role findByRoleName(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> {
                    log.error("Role not found with name {}", roleName);
                    return new EntityNotFoundException("Role not found with name " + roleName);
                });
    }

    @Transactional(readOnly = true)
    public String getUserRoleByEmail(String email) {
        return roleRepository.findRoleNameByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public String getUserRoleById(Long id) {
        return roleRepository.findRoleNameByUserId(id);
    }
}

package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);

    String findRoleNameByUserEmail(String userEmail);
    String findRoleNameByUserId(Long userId);
}

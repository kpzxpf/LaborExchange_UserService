package com.vlz.laborexchange_userservice.repository;

import com.vlz.laborexchange_userservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    @Query("SELECT r.roleName FROM Role r JOIN r.users u WHERE u.email = :email")
    Optional<String> findRoleNameByUserEmail(@Param("email") String email);

    @Query("SELECT r.roleName FROM Role r JOIN r.users u WHERE u.id = :userId")
    Optional<String> findRoleNameByUserId(@Param("userId") Long userId);
}

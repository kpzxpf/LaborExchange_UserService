package com.vlz.laborexchange_userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminUserDto {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String roleName;
    private boolean emailVerified;
    private boolean active;
    private LocalDateTime createdAt;
}

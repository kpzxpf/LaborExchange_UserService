package com.vlz.laborexchange_userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "User profile data transfer object")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Schema(description = "User ID", example = "42")
    private Long id;

    @Schema(description = "Unique username", example = "ivan_petrov", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(description = "Email address", example = "ivan@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "First name", example = "Ivan")
    private String firstName;

    @Schema(description = "Last name", example = "Petrov")
    private String lastName;

    @Schema(description = "Phone number", example = "+79161234567")
    private String phoneNumber;

    @Schema(description = "User role name", example = "JOB_SEEKER", allowableValues = {"JOB_SEEKER", "EMPLOYER", "ADMIN"})
    private String roleName;
}

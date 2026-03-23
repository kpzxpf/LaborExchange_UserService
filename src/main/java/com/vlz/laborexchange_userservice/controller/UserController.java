package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.ChangePasswordRequest;
import com.vlz.laborexchange_userservice.dto.ForgotPasswordRequest;
import com.vlz.laborexchange_userservice.dto.LoginRequest;
import com.vlz.laborexchange_userservice.dto.RegisterRequest;
import com.vlz.laborexchange_userservice.dto.ResetPasswordRequest;
import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.mapper.UserMapper;
import com.vlz.laborexchange_userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User CRUD and lookup operations")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(summary = "Register a new user", description = "Called by AuthService during registration. Saves the user with an encoded password and returns the new user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created, returns userId", content = @Content(schema = @Schema(type = "integer", format = "int64", example = "42"))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    public Long register(@RequestBody @Valid RegisterRequest request) {
        return userService.create(request);
    }

    @Operation(summary = "Check if email exists", description = "Returns true if a user with the given email exists. Used by AuthService before registration.")
    @ApiResponse(responseCode = "200", description = "true / false", content = @Content(schema = @Schema(type = "boolean")))
    @GetMapping("/existsByEmail")
    public boolean existsByEmail(
            @Parameter(description = "Email to check", required = true, example = "ivan@example.com")
            @RequestParam String email) {
        return userService.existsUserByEmail(email);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Update user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated user", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public UserDto update(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @RequestBody @Valid UserDto userDto) {
        userDto.setId(id);
        return userMapper.toDto(userService.update(userDto));
    }

    @Operation(summary = "Get username by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Username string", content = @Content(schema = @Schema(type = "string", example = "ivan_petrov"))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/username")
    public String getUsernameByUserId(
            @Parameter(description = "User ID", required = true) @PathVariable Long userId) {
        return userService.getUsernameByUserId(userId);
    }

    @Operation(summary = "Get user profile by ID", description = "Returns full profile DTO. Result is cached in Redis for 15 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}/profile")
    public UserDto getUserProfile(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        return userService.getUserProfile(id);
    }

    @Operation(summary = "Validate login credentials", description = "Checks email/password combination. Returns true if credentials are valid. Called by AuthService.")
    @ApiResponse(responseCode = "200", description = "true if credentials match, false otherwise", content = @Content(schema = @Schema(type = "boolean")))
    @PostMapping("/checkLogin")
    public boolean checkLogin(@RequestBody LoginRequest request) {
        return userService.checkLogin(request.getEmail(), request.getPassword());
    }

    @Operation(summary = "Get email by user ID", description = "Returns email address for the given user ID. Result cached in Redis for 60 minutes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email string", content = @Content(schema = @Schema(type = "string", example = "ivan@example.com"))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/emailById")
    public String getEmailById(
            @Parameter(description = "User ID", required = true, example = "42")
            @RequestParam Long id) {
        return userService.getEmailById(id);
    }

    @Operation(summary = "Get user ID by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User ID", content = @Content(schema = @Schema(type = "integer", format = "int64", example = "42"))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/userIdByEmail")
    public Long getUserIdByEmail(
            @Parameter(description = "Email address", required = true, example = "ivan@example.com")
            @RequestParam String email) {
        return userService.getUserIdByEmail(email);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Change user password", description = "Verifies old password then updates to the new one.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Wrong current password or validation error"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}/password")
    @ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT)
    public void changePassword(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @RequestBody @Valid ChangePasswordRequest request) {
        userService.changePassword(id, request);
    }

    @Operation(summary = "Initiate email verification", description = "Sends a verification link to the user's email.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Verification email sent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}/send-verification")
    public ResponseEntity<Void> sendVerificationEmail(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {
        userService.initiateEmailVerification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Verify email by token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Email verified"),
            @ApiResponse(responseCode = "400", description = "Token not found"),
            @ApiResponse(responseCode = "409", description = "Token already used"),
            @ApiResponse(responseCode = "410", description = "Token expired")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(
            @Parameter(description = "Verification token", required = true) @RequestParam String token) {
        userService.verifyEmail(token);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Initiate password reset", description = "Sends a password reset link to the given email.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reset email sent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody @Valid ForgotPasswordRequest request) {
        userService.initiatePasswordReset(request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reset password using token")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Token not found"),
            @ApiResponse(responseCode = "409", description = "Token already used"),
            @ApiResponse(responseCode = "410", description = "Token expired")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
}

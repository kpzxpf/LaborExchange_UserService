package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.AdminUserDto;
import com.vlz.laborexchange_userservice.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Users", description = "Admin-only user management operations")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(summary = "Get all users (paginated)")
    @GetMapping
    public Page<AdminUserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return adminUserService.getAllUsers(PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @Operation(summary = "Deactivate a user")
    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long userId) {
        adminUserService.deactivate(userId);
    }

    @Operation(summary = "Activate a user")
    @PatchMapping("/{userId}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void activate(@PathVariable Long userId) {
        adminUserService.activate(userId);
    }

    @Operation(summary = "Delete a user permanently")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
    }
}

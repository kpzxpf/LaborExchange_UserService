package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Roles", description = "Role lookup — called internally by AuthService and other services")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Get role name by email")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role name", content = @Content(schema = @Schema(type = "string", example = "JOB_SEEKER", allowableValues = {"JOB_SEEKER", "EMPLOYER", "ADMIN"}))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/roleByEmail")
    public String getRoleByEmail(
            @Parameter(description = "User email", required = true, example = "ivan@example.com")
            @RequestParam String email) {
        return roleService.getUserRoleByEmail(email);
    }

    @Operation(summary = "Get role name by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Role name", content = @Content(schema = @Schema(type = "string", example = "EMPLOYER", allowableValues = {"JOB_SEEKER", "EMPLOYER", "ADMIN"}))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/roleById")
    public String getRoleById(
            @Parameter(description = "User ID", required = true, example = "42")
            @RequestParam Long id) {
        return roleService.getUserRoleById(id);
    }
}

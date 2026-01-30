package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/roleByEmail")
    public String getUserRoleByEmail(@RequestParam("email") String email){
        return roleService.getUserRoleByEmail(email);
    }

    @GetMapping("/roleById")
    public String getUserRoleById(@RequestParam("id") Long id){
        return roleService.getUserRoleById(id);
    }
}

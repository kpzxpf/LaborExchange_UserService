package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.LoginRequest;
import com.vlz.laborexchange_userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/existsByEmail")
    public boolean existsUserByEmail(@RequestParam("email") String email) {
        return userService.existsUserByEmail(email);
    }

    @PostMapping("/checkLogin")
    boolean checkLogin(@RequestBody LoginRequest request) {
        return userService.checkLogin(request.getEmail(), request.getPassword());
    }
}

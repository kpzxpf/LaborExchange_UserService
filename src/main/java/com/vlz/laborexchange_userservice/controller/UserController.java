package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.LoginRequest;
import com.vlz.laborexchange_userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/users/existsByEmail/")
    public boolean existsUserByEmail(@RequestBody String email){
        return userService.existsUserByEmail(email);
    }


    @GetMapping("/api/users/checkLogin/")
    boolean checkLogin(LoginRequest request){
        return userService.checkLogin(request.getEmail(), request.getPassword());
    }
}

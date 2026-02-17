package com.vlz.laborexchange_userservice.controller;

import com.vlz.laborexchange_userservice.dto.LoginRequest;
import com.vlz.laborexchange_userservice.dto.UserDto;
import com.vlz.laborexchange_userservice.mapper.UserMapper;
import com.vlz.laborexchange_userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/existsByEmail")
    public boolean existsUserByEmail(@RequestParam("email") String email) {
        return userService.existsUserByEmail(email);
    }

    @PostMapping("/update")
    public UserDto update(@RequestBody UserDto userDto){
        return userMapper.toDto(userService.update(userDto));
    }

    @GetMapping("/{userId}/username")
    public String getUsernameByUserId(@PathVariable Long userId) {
       return userService.getUsernameByUserId(userId);
    }

    @GetMapping("/{id}/profile")
    public UserDto getUserProfile(@PathVariable Long id) {
        UserDto userDto = userService.getUserProfile(id);
        return userDto;
    }

    @PostMapping("/checkLogin")
    public boolean checkLogin(@RequestBody LoginRequest request) {
        return userService.checkLogin(request.getEmail(), request.getPassword());
    }

    @GetMapping("/emailById")
    public String getEmailById(@RequestParam("id") Long id) {
        return userService.getEmailById(id);
    }

    @GetMapping("/userIdByEmail")
    Long getUserIdByEmail(@RequestParam("email") String email){
        return userService.getUserIdByEmail(email);
    }
}
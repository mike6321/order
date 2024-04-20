package com.example.memberservice.controller;

import com.example.memberservice.dto.ModifyUserDto;
import com.example.memberservice.dto.RegisterUserDto;
import com.example.memberservice.entity.UserEntity;
import com.example.memberservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/member/users/")
@RequiredArgsConstructor
@RestController
public class MemberController {

    private final UserService userService;

    @PostMapping("registration")
    public UserEntity registerUser(@RequestBody RegisterUserDto request) {
        return userService.registerUser(request.getLoginId(), request.getUserName());
    }

    @PutMapping("{userId}/modification")
    public UserEntity modifyUser(@PathVariable Long userId, @RequestBody ModifyUserDto request) {
        return userService.modifyUser(userId, request.getUserName());
    }

    @GetMapping("{loginId}/login")
    public UserEntity login(@PathVariable String loginId) {
        return userService.getUser(loginId);
    }

}

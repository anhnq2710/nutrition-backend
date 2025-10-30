package com.example.nutrition_backend.controller;


import com.example.nutrition_backend.dto.UserCreateDto;
import com.example.nutrition_backend.dto.UserDto;
import com.example.nutrition_backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserCreateDto dto) {
        UserDto created = userService.createUser(dto);
        return ResponseEntity.ok(created);
    }
}

package com.example.nutrition_backend.service;

import com.example.nutrition_backend.dto.UserCreateDto;
import com.example.nutrition_backend.dto.UserDto;
import com.example.nutrition_backend.entity.User;
import com.example.nutrition_backend.mapper.UserMapper;
import com.example.nutrition_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        User u = new User();
        u.setEmail(dto.getEmail());
        u.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        u = userRepository.save(u);
        return userMapper.toDto(u);
    }
}

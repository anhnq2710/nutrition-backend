package com.example.nutrition_backend.mapper;

import com.example.nutrition_backend.dto.UserDto;
import com.example.nutrition_backend.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}

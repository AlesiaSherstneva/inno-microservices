package com.innowise.userservice.service;

import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();

    UserResponseDto getUserById(Long userId);

    List<UserResponseDto> getUsersByIds(List<Long> usersIds);

    UserResponseDto getUserByEmail(String email);

    UserResponseDto createUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto);

    void deleteUser(Long userId);
}
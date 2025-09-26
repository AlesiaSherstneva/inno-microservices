package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.UserRequestDto;
import com.innowise.userservice.dto.UserResponseDto;
import com.innowise.userservice.dto.mapper.UserMapper;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.UserService;
import com.innowise.userservice.util.CardFieldsGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<User> retrievedUsers = userRepository.findAll();

        return userMapper.toResponseDtoList(retrievedUsers);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long userId) {
        User retrievedUser = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(userId));

        return userMapper.toResponseDto(retrievedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByIds(List<Long> usersIds) {
        List<User> retrievedUsers = userRepository.findUsersByIdIn(usersIds);

        return userMapper.toResponseDtoList(retrievedUsers);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserByEmail(String email) {
        User retrievedUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(email));

        return userMapper.toResponseDto(retrievedUser);
    }

    @Override
    @Transactional
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDto.getEmail());
        }

        User createdUser = userRepository.save(userMapper.toEntity(userRequestDto));

        return userMapper.toResponseDto(createdUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(userId));

        boolean nameChanged = !userToUpdate.getName().equals(userRequestDto.getName())
                || !userToUpdate.getSurname().equals(userRequestDto.getSurname());

        if (!userToUpdate.getEmail().equals(userRequestDto.getEmail())
                && userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException(userRequestDto.getEmail());
        }

        userMapper.updateUserFromDto(userRequestDto, userToUpdate);
        if (nameChanged && userToUpdate.getCards() != null) {
            userToUpdate.getCards().forEach(card ->
                    card.setHolder(CardFieldsGenerator.formatCardHolderName(userToUpdate)));
        }

        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(userId));

        userRepository.delete(userToDelete);
    }
}
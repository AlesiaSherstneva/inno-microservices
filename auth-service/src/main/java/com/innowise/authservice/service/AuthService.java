package com.innowise.authservice.service;

import com.innowise.authservice.client.UserServiceClient;
import com.innowise.authservice.exception.PhoneNumberAlreadyExistsException;
import com.innowise.authservice.model.dto.AuthResponseDto;
import com.innowise.authservice.model.dto.RegisterRequestDto;
import com.innowise.authservice.model.dto.RegisterResponseDto;
import com.innowise.authservice.model.entity.UserCredentials;
import com.innowise.authservice.repository.UserCredentialsRepository;
import com.innowise.securitystarter.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserCredentialsRepository credentialsRepository;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public AuthResponseDto registerUser(RegisterRequestDto registerRequestDto) {
        if (credentialsRepository.existsByPhoneNumber(registerRequestDto.getPhoneNumber())) {
            throw new PhoneNumberAlreadyExistsException(registerRequestDto.getPhoneNumber());
        }

        RegisterResponseDto registerResponseDto = userServiceClient.createUser(registerRequestDto);

        UserCredentials newCredentials = UserCredentials.builder()
                .phoneNumber(registerRequestDto.getPhoneNumber())
                .userId(registerResponseDto.getUserId())
                .password(passwordEncoder.encode(registerRequestDto.getPassword()))
                .build();

        UserCredentials savedCredentials = credentialsRepository.save(newCredentials);

        String accessToken = jwtProvider.generateAccessToken(
                savedCredentials.getPhoneNumber(),
                savedCredentials.getUserId(),
                savedCredentials.getRole().name()
        );
        String refreshToken = jwtProvider.generateRefreshToken(savedCredentials.getPhoneNumber());

        return new AuthResponseDto(accessToken, refreshToken);
    }
}
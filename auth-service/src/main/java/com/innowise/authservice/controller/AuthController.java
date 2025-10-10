package com.innowise.authservice.controller;

import com.innowise.authservice.model.dto.AuthResponseDto;
import com.innowise.authservice.model.dto.LoginRequestDto;
import com.innowise.authservice.model.dto.RegisterRequestDto;
import com.innowise.authservice.model.dto.TokenRequestDto;
import com.innowise.authservice.model.dto.TokenResponseDto;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        AuthResponseDto authResponseDto = authService.registerUser(registerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        AuthResponseDto authResponseDto = authService.authenticateUser(loginRequestDto);

        return ResponseEntity.ok(authResponseDto);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenResponseDto> validate(@RequestBody @Valid TokenRequestDto tokenRequestDto) {
        TokenResponseDto tokenResponseDto = authService.validateToken(tokenRequestDto);

        return ResponseEntity.ok(tokenResponseDto);
    }
}
package com.innowise.authservice.client;

import com.innowise.authservice.model.dto.RegisterRequestDto;
import com.innowise.authservice.model.dto.RegisterResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    @PostMapping("/users")
    RegisterResponseDto createUser(@RequestBody RegisterRequestDto requestDto);
}
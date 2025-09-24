package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserResponseDto {
    private String name;
    private String surname;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private String email;
    private List<CardInfoResponseDto> cards;
}
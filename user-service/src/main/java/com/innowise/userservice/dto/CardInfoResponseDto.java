package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CardInfoResponseDto {
    private String number;
    private String holder;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
}
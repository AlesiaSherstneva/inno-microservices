package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDto {
    private int status;
    private String errorMessage;
    private List<String> errorDetails;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
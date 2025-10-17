package com.innowise.orderservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto {
    private String name;
    private String surname;
    private String email;
    private String errorMessage;
}
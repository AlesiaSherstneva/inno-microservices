package com.innowise.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for retrieving cards.
 * Contains card data as returned from the system.
 * Uses as response body in card-related API endpoints.
 */
@Data
public class CardInfoResponseDto {
    /** Automatically generated unique card number. */
    private String number;

    /** Automatically generated holder name from user's name and surname. */
    private String holder;

    /** Automatically generated card expiration date. */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
}
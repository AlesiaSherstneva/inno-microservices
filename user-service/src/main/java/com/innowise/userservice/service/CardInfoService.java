package com.innowise.userservice.service;

import com.innowise.userservice.dto.CardInfoRequestDto;
import com.innowise.userservice.dto.CardInfoResponseDto;
import com.innowise.userservice.entity.CardInfo;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.util.CardFieldsGenerator;

import java.util.List;

/**
 * Service for managing cards in the system.
 * Handles business logic for card operations including automatic number generation,
 * holder name auto formatting, and expiration date calculation.
 *
 * @see CardInfo
 * @see CardFieldsGenerator
 * @see CardInfoRequestDto
 * @see CardInfoResponseDto
 */
public interface CardInfoService {
    /**
     * Retrieves all cards from the system.
     *
     * @return list of all cards, empty list if no cards exist in the database
     */
    List<CardInfoResponseDto> getAllCards();

    /**
     * Retrieves a card by unique identifier.
     *
     * @param cardId the unique identifier of the card to retrieve
     * @return the card data
     * @throws EntityNotFoundException if the card with given ID does not exist
     */
    CardInfoResponseDto getCardById(Long cardId);

    /**
     * Retrieves multiple cards by their identifiers.
     * Returns only existing cards, non-existent IDs are ignored.
     *
     * @param cardsIds the list of card identifiers to retrieve
     * @return list of found cards, empty list if no cards found by IDs
     */
    List<CardInfoResponseDto> getCardsByIds(List<Long> cardsIds);

    /**
     * Creates a new card for a user with automatically generated data.
     * Card number is uniquely generated, expiration date is set to 3 years from now,
     * and holder name is formatted from user's name and surname.
     *
     * @param cardInfoRequestDto the card creation request containing user ID
     * @return the created card data with generated fields
     * @throws EntityNotFoundException if the user with given ID does not exist
     * @see CardFieldsGenerator#generateCardNumber()
     * @see CardFieldsGenerator#formatCardHolderName(User)
     */
    CardInfoResponseDto createCard(CardInfoRequestDto cardInfoRequestDto);

    /**
     * Deletes a card by unique identifier.
     *
     * @param cardId the unique identifier of the card to delete
     * @throws EntityNotFoundException if the card with given ID does not exist
     */
    void deleteCard(Long cardId);
}
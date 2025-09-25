package com.innowise.userservice.controller;

import com.innowise.userservice.dto.CardInfoRequestDto;
import com.innowise.userservice.dto.CardInfoResponseDto;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.service.CardInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing cards in the system.
 * Provides endpoints for CRUD operations on cards.
 * Card numbers are automatically generated and guaranteed to be unique.
 *
 * @see CardInfoService
 * @see CardInfoRequestDto
 * @see CardInfoResponseDto
 */
@Validated
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardInfoController {
    private final CardInfoService cardInfoService;

    /**
     * Retrieves a card by unique identifier.
     *
     * @param cardInfoId the unique identifier of the card to retrieve
     * @return the card data
     * @throws EntityNotFoundException if the card with given ID does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponseDto> getCardById(@PathVariable("id") Long cardInfoId) {
        CardInfoResponseDto cardInfoResponseDto = cardInfoService.getCardById(cardInfoId);

        return ResponseEntity.ok(cardInfoResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<CardInfoResponseDto>> getCards(@RequestParam(required = false) List<Long> ids) {
        List<CardInfoResponseDto> retrievedCards;

        if (ids != null && !ids.isEmpty()) {
            retrievedCards = cardInfoService.getCardsByIds(ids);
        } else {
            retrievedCards = cardInfoService.getAllCards();
        }

        return ResponseEntity.ok(retrievedCards);
    }

    /**
     * Creates a new card for a given user.
     * Automatically generates unique card number and sets expiration date.
     * Holder is generated from user's name and surname.
     *
     * @param cardInfoRequestDto the card data to create, must contain valid user ID
     * @return the created card data
     * @throws EntityNotFoundException if the user with given ID does not exist
     */
    @PostMapping
    public ResponseEntity<CardInfoResponseDto> createCard(@RequestBody @Valid CardInfoRequestDto cardInfoRequestDto) {
        CardInfoResponseDto cardInfoResponseDto = cardInfoService.createCard(cardInfoRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cardInfoResponseDto);
    }

    /**
     * Deletes a card by unique identifier.
     *
     * @param id the unique identifier of the card to delete
     * @return empty response
     * @throws EntityNotFoundException if the card with given ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") Long id) {
        cardInfoService.deleteCard(id);

        return ResponseEntity.noContent().build();
    }
}
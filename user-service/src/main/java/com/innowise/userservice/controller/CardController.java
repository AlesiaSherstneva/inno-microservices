package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.CardRequestDto;
import com.innowise.userservice.model.dto.CardResponseDto;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
 * @see CardService
 * @see CardRequestDto
 * @see CardResponseDto
 */
@Validated
@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;

    /**
     * Retrieves a card by unique identifier.
     *
     * @param id the unique identifier of the card to retrieve
     * @return the card data
     * @throws ResourceNotFoundException if the card with given ID does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> getCardById(@PathVariable("id") Long id) {
        CardResponseDto cardResponseDto = cardService.getCardById(id);

        return ResponseEntity.ok(cardResponseDto);
    }

    /**
     * Retrieves specific cards by their IDs.
     *
     * @param ids list of card IDs to filter by
     * @return list of cards, empty list if no cards found by given IDs
     */
    @GetMapping(params = "ids")
    public ResponseEntity<List<CardResponseDto>> getCardsByIds(@RequestParam @NotEmpty List<Long> ids) {
        List<CardResponseDto> retrievedCards = cardService.getCardsByIds(ids);

        return ResponseEntity.ok(retrievedCards);
    }

    /**
     * Retrieves all cards in the system.
     *
     * @return list of all cards, empty list if no cards exist
     */
    @GetMapping
    public ResponseEntity<List<CardResponseDto>> getCards() {
        List<CardResponseDto> retrievedCards = cardService.getAllCards();

        return ResponseEntity.ok(retrievedCards);
    }

    /**
     * Creates a new card for a given user.
     * Automatically generates unique card number and sets expiration date.
     * Holder is generated from user's name and surname.
     *
     * @param cardRequestDto the card data to create, must contain valid user ID
     * @return the created card data
     * @throws ResourceNotFoundException if the user with given ID does not exist
     */
    @PostMapping
    public ResponseEntity<CardResponseDto> createCard(@RequestBody @Valid CardRequestDto cardRequestDto) {
        CardResponseDto cardResponseDto = cardService.createCard(cardRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponseDto);
    }

    /**
     * Deletes a card by unique identifier.
     *
     * @param id the unique identifier of the card to delete
     * @return empty response
     * @throws ResourceNotFoundException if the card with given ID does not exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable("id") Long id) {
        cardService.deleteCard(id);

        return ResponseEntity.noContent().build();
    }
}
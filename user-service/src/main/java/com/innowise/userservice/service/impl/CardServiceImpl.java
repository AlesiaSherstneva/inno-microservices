package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.CardRequestDto;
import com.innowise.userservice.dto.CardResponseDto;
import com.innowise.userservice.dto.mapper.CardMapper;
import com.innowise.userservice.entity.Card;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardService;
import com.innowise.userservice.util.CardFieldsGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardMapper cardMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDto> getAllCards() {
        List<Card> retrievedCards = cardRepository.findAll();

        return cardMapper.toResponseDtoList(retrievedCards);
    }

    @Override
    @Transactional(readOnly = true)
    public CardResponseDto getCardById(Long cardId) {
        Card retrievedCard = cardRepository.findCardById(cardId)
                .orElseThrow(() -> ResourceNotFoundException.cardNotFound(cardId));

        return cardMapper.toResponseDto(retrievedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardResponseDto> getCardsByIds(List<Long> cardsIds) {
        List<Card> retrievedCards = cardRepository.findCardsByIdIn(cardsIds);

        return cardMapper.toResponseDtoList(retrievedCards);
    }

    @Override
    @Transactional
    public CardResponseDto createCard(CardRequestDto cardRequestDto) {
        User userToAddCard = userRepository.findUserById(cardRequestDto.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.userNotFound(cardRequestDto.getUserId()));

        String newCardNumber;
        do {
            newCardNumber = CardFieldsGenerator.generateCardNumber();
        } while (cardRepository.existsByNumber(newCardNumber));

        Card newCard = Card.builder()
                .user(userToAddCard)
                .number(newCardNumber)
                .holder(CardFieldsGenerator.formatCardHolderName(userToAddCard))
                .expirationDate(LocalDate.now().plusYears(CardFieldsGenerator.CARD_VALIDITY_YEARS))
                .build();

        Card createdCard = cardRepository.save(newCard);

        return cardMapper.toResponseDto(createdCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.deleteCardById(cardId);
    }
}
package com.innowise.userservice.service.impl;

import com.innowise.userservice.dto.CardInfoRequestDto;
import com.innowise.userservice.dto.CardInfoResponseDto;
import com.innowise.userservice.dto.mapper.CardInfoMapper;
import com.innowise.userservice.entity.CardInfo;
import com.innowise.userservice.entity.User;
import com.innowise.userservice.exception.EntityNotFoundException;
import com.innowise.userservice.repository.CardInfoRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.CardInfoService;
import com.innowise.userservice.util.CardFieldsGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardInfoServiceImpl implements CardInfoService {
    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CardInfoResponseDto> getAllCards() {
        List<CardInfo> retrievedCards = cardInfoRepository.findAll();

        return cardInfoMapper.toResponseDtoList(retrievedCards);
    }

    @Override
    @Transactional(readOnly = true)
    public CardInfoResponseDto getCardById(Long cardId) {
        CardInfo retrievedCard = cardInfoRepository.findById(cardId)
                .orElseThrow(() -> EntityNotFoundException.cardNotFound(cardId));

        return cardInfoMapper.toResponseDto(retrievedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardInfoResponseDto> getCardsByIds(List<Long> cardsIds) {
        List<CardInfo> retrievedCards = cardInfoRepository.findCardInfosByIdIn(cardsIds);

        return cardInfoMapper.toResponseDtoList(retrievedCards);
    }

    @Override
    @Transactional
    public CardInfoResponseDto createCard(CardInfoRequestDto cardInfoRequestDto) {
        User userToAddCard = userRepository.findById(cardInfoRequestDto.getUserId())
                .orElseThrow(() -> EntityNotFoundException.userNotFound(cardInfoRequestDto.getUserId()));

        String newCardNumber;
        do {
            newCardNumber = CardFieldsGenerator.generateCardNumber();
        } while (cardInfoRepository.existsByNumber(newCardNumber));

        CardInfo newCard = CardInfo.builder()
                .user(userToAddCard)
                .number(newCardNumber)
                .holder(CardFieldsGenerator.formatCardHolderName(userToAddCard))
                .expirationDate(LocalDate.now().plusYears(CardFieldsGenerator.CARD_VALIDITY_YEARS))
                .build();

        CardInfo createdCard = cardInfoRepository.save(newCard);

        return cardInfoMapper.toResponseDto(createdCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        CardInfo cardToDelete = cardInfoRepository.findById(cardId)
                .orElseThrow(() -> EntityNotFoundException.cardNotFound(cardId));

        cardInfoRepository.delete(cardToDelete);
    }
}
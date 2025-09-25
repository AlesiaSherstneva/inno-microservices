package com.innowise.userservice.service;

import com.innowise.userservice.dto.CardInfoRequestDto;
import com.innowise.userservice.dto.CardInfoResponseDto;

import java.util.List;

public interface CardInfoService {
    List<CardInfoResponseDto> getAllCards();

    CardInfoResponseDto getCardById(Long cardId);

    List<CardInfoResponseDto> getCardsByIds(List<Long> cardsIds);

    CardInfoResponseDto createCard(CardInfoRequestDto cardInfoRequestDto);

    void deleteCard(Long cardId);
}
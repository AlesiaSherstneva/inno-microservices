package com.innowise.userservice.service;

import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.dto.CardRequestDto;
import com.innowise.userservice.model.dto.CardResponseDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.impl.CardServiceImpl;
import com.innowise.userservice.util.CardFieldsGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = CardServiceImpl.class)
class CardServiceTest extends BaseServiceTest {
    @Autowired
    private CardService cardService;

    private static User testUser;
    private static Card testCard;

    @BeforeAll
    static void beforeAll() {
        testUser = buildTestUser();

        testCard = Card.builder()
                .user(testUser)
                .number(CardFieldsGenerator.generateCardNumber())
                .holder(CardFieldsGenerator.formatCardHolderName(testUser))
                .expirationDate(NOW.plusYears(CardFieldsGenerator.CARD_VALIDITY_YEARS))
                .build();
    }

    @Test
    void getAllCardsWhenCardsExistTest() {
        when(cardRepository.findAll()).thenReturn(List.of(testCard));

        List<CardResponseDto> resultList = cardService.getAllCards();

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        CardResponseDto resultDto = resultList.get(0);

        assertCardResponseDtoFields(resultDto);

        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getAllCardsWhenCardsDoNotExistTest() {
        when(cardRepository.findAll()).thenReturn(Collections.emptyList());

        List<CardResponseDto> resultList = cardService.getAllCards();

        assertThat(resultList).isNotNull().isEmpty();

        verify(cardRepository, times(1)).findAll();
    }

    @Test
    void getCardByIdWhenCardExistsTest() {
        when(cardRepository.findCardById(ID)).thenReturn(Optional.of(testCard));

        CardResponseDto resultDto = cardService.getCardById(ID);

        assertCardResponseDtoFields(resultDto);

        verify(cardRepository, times(1)).findCardById(ID);
    }

    @Test
    void getCardByIdWhenCardDoesNotExistTest() {
        when(cardRepository.findCardById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.getCardById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Card not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(cardRepository, times(1)).findCardById(ID);
    }

    @Test
    void getCardsByIdsWhenCardsExistTest() {
        when(cardRepository.findCardsByIdIn(IDS)).thenReturn(List.of(testCard));

        List<CardResponseDto> resultList = cardService.getCardsByIds(IDS);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        CardResponseDto resultDto = resultList.get(0);

        assertCardResponseDtoFields(resultDto);

        verify(cardRepository, times(1)).findCardsByIdIn(IDS);
    }

    @Test
    void getCardsByIdsWhenCardsDoNotExistTest() {
        when(cardRepository.findCardsByIdIn(IDS)).thenReturn(Collections.emptyList());

        List<CardResponseDto> resultList = cardService.getCardsByIds(IDS);

        assertThat(resultList).isNotNull().isEmpty();

        verify(cardRepository, times(1)).findCardsByIdIn(IDS);
    }

    @Test
    void createCardWhenUserExistsTest() {
        CardRequestDto requestDto = CardRequestDto.builder()
                .userId(ID)
                .build();

        when(userRepository.findUserById(ID)).thenReturn(Optional.of(testUser));
        when(cardRepository.existsByNumber(anyString())).thenReturn(false);
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);
        doNothing().when(cacheEvictor).evictUser(ID, USER_EMAIL);

        CardResponseDto resultDto = cardService.createCard(requestDto);

        assertCardResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUserById(ID);
        verify(cardRepository, times(1)).existsByNumber(anyString());
        verify(cardRepository, times(1)).save(any(Card.class));
        verify(cacheEvictor, times(1)).evictUser(ID, USER_EMAIL);
    }

    @Test
    void createCardWhenUserDoesNotExistTest() {
        CardRequestDto requestDto = CardRequestDto.builder()
                .userId(ID)
                .build();

        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(userRepository, times(1)).findUserById(ID);
    }


    @Test
    void deleteCardSuccessfulTest() {
        when(cardRepository.findCardById(ID)).thenReturn(Optional.of(testCard));
        doNothing().when(cacheEvictor).evictUser(ID, USER_EMAIL);

        cardService.deleteCard(ID);

        verify(cardRepository, times(1)).findCardById(ID);
        verify(cacheEvictor, times(1)).evictUser(ID, USER_EMAIL);
        verify(cardRepository, times(1)).deleteCardById(ID);
    }

    @Test
    void deleteCardWhenCardDoesNotExistTest() {
        when(cardRepository.findCardById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.deleteCard(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Card not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(cardRepository, times(1)).findCardById(ID);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(cardRepository, userRepository, cacheEvictor);
    }

    private void assertCardResponseDtoFields(CardResponseDto responseDto) {
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getNumber()).isNotBlank().isEqualTo(testCard.getNumber()),
                () -> assertThat(responseDto.getHolder()).isNotBlank().isEqualTo(testCard.getHolder()),
                () -> assertThat(responseDto.getExpirationDate()).isNotNull().isEqualTo(testCard.getExpirationDate())
        );
    }
}
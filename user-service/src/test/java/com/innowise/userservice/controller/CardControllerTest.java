package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.CardRequestDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardControllerTest extends BaseControllerTest {
    private static final String URL = "/cards";
    private static final String CARD_NUMBER_PATTERN = "\\d{4}-\\d{4}-\\d{4}-\\d{4}";

    private User testUser;
    private Card testCard;

    @Test
    void getCardByIdWhenCardExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        testCard = cardRepository.save(buildTestCard(testUser));

        mockMvc.perform(get(String.format("%s/%d", URL, testCard.getId())))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_CARD_NUMBER).value(matchesPattern(CARD_NUMBER_PATTERN)),
                        jsonPath(Constants.JSON_PATH_CARD_HOLDER)
                                .value(String.join(" ", testUser.getName(), testUser.getSurname()).toUpperCase()),
                        jsonPath(Constants.JSON_PATH_CARD_EXPIRATION_DATE).exists()
                );
    }

    @Test
    void getCardByIdWhenDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", URL, Constants.ID)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Card not found with id")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void getCardsByIdsWhenCardExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        testCard = cardRepository.save(buildTestCard(testUser));

        String ids = String.format("%d,%d", testCard.getId(), testCard.getId() + 1);

        mockMvc.perform(get(URL)
                        .param("ids", ids))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY, hasSize(1))
                );
    }

    @Test
    void getCardsByIdsWhenCardDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(URL)
                        .param("ids", String.valueOf(Constants.ID)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isEmpty()
                );
    }

    @Test
    void getAllCardsWhenCardExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        testCard = cardRepository.save(buildTestCard(testUser));

        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY, hasSize(1))
                );
    }

    @Test
    void getAllCardsWhenCardDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isEmpty()
                );
    }

    @Test
    void createCardSuccessfulIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        CardRequestDto requestDto = CardRequestDto.builder()
                .userId(testUser.getId())
                .build();

        doNothing().when(cacheEvictor).evictUser(testUser.getId(), testUser.getEmail());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath(Constants.JSON_PATH_CARD_NUMBER).value(matchesPattern(CARD_NUMBER_PATTERN)),
                        jsonPath(Constants.JSON_PATH_CARD_HOLDER)
                                .value(String.join(" ", testUser.getName(), testUser.getSurname()).toUpperCase()),
                        jsonPath(Constants.JSON_PATH_CARD_EXPIRATION_DATE).exists()
                );
    }

    @Test
    void createCardWhenUserDoesNotExistIntegrationTest() throws Exception {
        CardRequestDto requestDto = CardRequestDto.builder()
                .userId(Constants.ID)
                .build();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("User not found with id")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void deleteCardSuccessfulIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        testCard = cardRepository.save(buildTestCard(testUser));

        doNothing().when(cacheEvictor).evictUser(testUser.getId(), testUser.getEmail());

        mockMvc.perform(delete(String.format("%s/%d", URL, testCard.getId())))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCardWhenCardDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(delete(String.format("%s/%d", URL, Constants.ID)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Card not found with id")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }
}
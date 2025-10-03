package com.innowise.userservice.controller;

import com.innowise.userservice.model.dto.UserRequestDto;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest extends BaseControllerTest {
    private static final String URL = "/users";

    private User testUser;

    @Test
    void getUserByIdWhenUserExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        cardRepository.save(buildTestCard(testUser));

        mockMvc.perform(get(String.format("%s/%d", URL, testUser.getId())))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_USER_NAME).value(testUser.getName()),
                        jsonPath(Constants.JSON_PATH_USER_SURNAME).value(testUser.getSurname()),
                        jsonPath(Constants.JSON_PATH_USER_BIRTH_DATE)
                                .value(testUser.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                        jsonPath(Constants.JSON_PATH_USER_EMAIL).value(testUser.getEmail()),
                        jsonPath(Constants.JSON_PATH_USER_CARDS).isArray(),
                        jsonPath(Constants.JSON_PATH_USER_CARDS, hasSize(1))
                );
    }

    @Test
    void getUserByIdWhenUserDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(String.format("%s/%d", URL, Constants.ID)))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("User not found with id")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void getUserByEmailWhenUserExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());

        mockMvc.perform(get(URL)
                        .param("email", testUser.getEmail()))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_USER_NAME).value(testUser.getName()),
                        jsonPath(Constants.JSON_PATH_USER_SURNAME).value(testUser.getSurname()),
                        jsonPath(Constants.JSON_PATH_USER_BIRTH_DATE)
                                .value(testUser.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                        jsonPath(Constants.JSON_PATH_USER_EMAIL).value(testUser.getEmail()),
                        jsonPath(Constants.JSON_PATH_USER_CARDS).isArray(),
                        jsonPath(Constants.JSON_PATH_USER_CARDS).isEmpty()
                );
    }

    @Test
    void getUserByEmailWhenUserDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(URL)
                        .param("email", Constants.USER_EMAIL))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("User not found with email")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void getUserByEmailWhenParameterIsNotValidIntegrationTest() throws Exception {
        mockMvc.perform(get(URL)
                        .param("email", ""))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Constraint violation")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_DETAILS, hasItem(containsString("email: must not be blank"))),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void getUsersByIdsWhenUserExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        String ids = String.format("%d,%d", testUser.getId(), testUser.getId() + 1);

        mockMvc.perform(get(URL)
                        .param("ids", ids))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY, hasSize(1))
                );
    }

    @Test
    void getUsersByIdsWhenUserDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(URL)
                        .param("ids", String.valueOf(Constants.ID)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isEmpty()
                );
    }

    @Test
    void getAllUsersWhenUserExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());

        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY, hasSize(1))
                );
    }

    @Test
    void getAllUsersWhenUserDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(get(URL))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isArray(),
                        jsonPath(Constants.JSON_PATH_COMMON_ARRAY).isEmpty()
                );
    }

    @Test
    void createUserSuccessfulIntegrationTest() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.USER_NAME)
                .birthDate(Constants.LOCAL_DATE_YESTERDAY)
                .email(Constants.USER_EMAIL)
                .build();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath(Constants.JSON_PATH_USER_NAME).value(requestDto.getName()),
                        jsonPath(Constants.JSON_PATH_USER_SURNAME).value(requestDto.getSurname()),
                        jsonPath(Constants.JSON_PATH_USER_BIRTH_DATE)
                                .value(requestDto.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                        jsonPath(Constants.JSON_PATH_USER_EMAIL).value(requestDto.getEmail())
                );
    }

    @Test
    void createUserWithNotValidFieldsIntegrationTest() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .name(String.join("", Constants.USER_NAME, "123"))
                .birthDate(LocalDate.now())
                .email(Constants.USER_EMAIL.replace("@", ""))
                .build();

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Validation failed")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_DETAILS,
                                hasItem(containsString("Name should start with capital letter and contain only English letters and hyphens"))),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_DETAILS, hasItem(containsString("Surname is required"))),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_DETAILS, hasItem(containsString("Birth date must be in the past"))),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_DETAILS, hasItem(containsString("Email should be valid"))),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void createUserWhenEmailAlreadyExistsIntegrationTest() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.USER_NAME)
                .birthDate(Constants.LOCAL_DATE_YESTERDAY)
                .email(Constants.USER_EMAIL)
                .build();

        testUser = userRepository.save(buildTestUser());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.CONFLICT.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Email already exists in the database")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void updateUserSuccessfulIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());
        cardRepository.save(buildTestCard(testUser));

        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.NEW_USER_SURNAME)
                .birthDate(Constants.NEW_USER_LOCAL_DAY)
                .email(Constants.NEW_USER_EMAIL)
                .build();

        doNothing().when(cacheEvictor).evictUser(testUser.getId(), testUser.getEmail(), requestDto.getEmail());

        mockMvc.perform(put((String.format("%s/%d", URL, testUser.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath(Constants.JSON_PATH_USER_NAME).value(Constants.USER_NAME),
                        jsonPath(Constants.JSON_PATH_USER_SURNAME).value(Constants.NEW_USER_SURNAME),
                        jsonPath(Constants.JSON_PATH_USER_BIRTH_DATE)
                                .value(Constants.NEW_USER_LOCAL_DAY.format(DateTimeFormatter.ISO_LOCAL_DATE)),
                        jsonPath(Constants.JSON_PATH_USER_EMAIL).value(Constants.NEW_USER_EMAIL),
                        jsonPath(Constants.JSON_PATH_USER_CARDS).isArray(),
                        jsonPath(Constants.JSON_PATH_USER_CARDS, hasSize(1)),
                        jsonPath(String.format("%s[0].holder", Constants.JSON_PATH_USER_CARDS))
                                .value(String.join(" ", Constants.USER_NAME, Constants.NEW_USER_SURNAME).toUpperCase())
                );
    }

    @Test
    void updateUserWhenUserDoesNotExistIntegrationTest() throws Exception {
        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.USER_NAME)
                .birthDate(Constants.LOCAL_DATE_YESTERDAY)
                .email(Constants.USER_EMAIL)
                .build();

        mockMvc.perform(put((String.format("%s/%d", URL, Constants.ID)))
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
    void updateUserWhenEmailAlreadyExistsIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());

        User secondUser = buildTestUser();
        secondUser.setEmail(Constants.NEW_USER_EMAIL);
        userRepository.save(secondUser);

        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.USER_NAME)
                .birthDate(Constants.LOCAL_DATE_YESTERDAY)
                .email(Constants.NEW_USER_EMAIL)
                .build();

        mockMvc.perform(put((String.format("%s/%d", URL, testUser.getId())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.CONFLICT.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("Email already exists in the database")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void deleteUserSuccessfulIntegrationTest() throws Exception {
        testUser = userRepository.save(buildTestUser());

        doNothing().when(cacheEvictor).evictUser(testUser.getId(), testUser.getEmail());

        mockMvc.perform(delete((String.format("%s/%d", URL, testUser.getId()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserWhenUserDoesNotExistIntegrationTest() throws Exception {
        mockMvc.perform(delete((String.format("%s/%d", URL, Constants.ID))))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.NOT_FOUND.value()),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_ERROR_MESSAGE).value(containsString("User not found with id")),
                        jsonPath(Constants.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }
}
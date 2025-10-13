package com.innowise.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.authservice.client.UserServiceClient;
import com.innowise.authservice.model.dto.RegisterDto;
import com.innowise.authservice.model.dto.RegisterRequestDto;
import com.innowise.authservice.model.entity.UserCredentials;
import com.innowise.authservice.repository.UserCredentialsRepository;
import com.innowise.authservice.util.DtoBuilder;
import com.innowise.authservice.util.TestConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Container
    protected static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @DynamicPropertySource
    protected static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    private static final String URL_FORMATTED = "/auth/%s";
    private static final String REGISTER = "register";

    @MockitoBean
    private UserServiceClient userServiceClient;

    @Autowired
    private UserCredentialsRepository credentialsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerNewUserSuccessfulIntegrationTest() throws Exception {
        RegisterRequestDto requestDto = DtoBuilder.buildRegisterRequestDto();
        RegisterDto innerResponse = DtoBuilder.buildRegisterDto();

        when(userServiceClient.createUser(requestDto)).thenReturn(innerResponse);

        mockMvc.perform(post(URL_FORMATTED.formatted(REGISTER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath(TestConstant.JSON_PATH_ACCESS_TOKEN).exists(),
                        jsonPath(TestConstant.JSON_PATH_ACCESS_TOKEN).isString(),
                        jsonPath(TestConstant.JSON_PATH_REFRESH_TOKEN).exists(),
                        jsonPath(TestConstant.JSON_PATH_REFRESH_TOKEN).isString()
                );
    }

    @Test
    void registerUserWithNotValidFieldsIntegrationTest() throws Exception {
        RegisterRequestDto requestDto = RegisterRequestDto.builder()
                .phoneNumber("123")
                .password("321")
                .build();

        mockMvc.perform(post(URL_FORMATTED.formatted(REGISTER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_ERROR_MESSAGE)
                                .value(containsString("Validation failed")),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_DETAILS,
                                hasItem(containsString("Phone number must be in format +375XXXXXXXXX with valid operator code"))),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_DETAILS,
                                hasItem(containsString("Password must be at least 8 characters"))),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @Test
    void registerUserWhenPhoneNumberAlreadyExistsIntegrationTest() throws Exception {
        UserCredentials existingUser = DtoBuilder.buildUserCredentials();
        credentialsRepository.save(existingUser);
        RegisterRequestDto requestDto = DtoBuilder.buildRegisterRequestDto();

        mockMvc.perform(post(URL_FORMATTED.formatted(REGISTER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpectAll(
                        status().isConflict(),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_STATUS).value(HttpStatus.CONFLICT.value()),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_ERROR_MESSAGE)
                                .value(containsString("Phone number already exists in the database")),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_ERROR_MESSAGE)
                                .value(containsString(TestConstant.PHONE_NUMBER)),
                        jsonPath(TestConstant.JSON_PATH_EXCEPTION_TIMESTAMP).exists()
                );
    }

    @AfterEach
    void tearDown() {
        credentialsRepository.deleteAll();
    }
}
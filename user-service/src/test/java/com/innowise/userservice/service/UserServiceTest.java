package com.innowise.userservice.service;

import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.dto.UserRequestDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.impl.UserServiceImpl;
import com.innowise.userservice.util.Constants;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = UserServiceImpl.class)
class UserServiceTest extends BaseServiceTest {
    @Autowired
    private UserService userService;

    private static User testUser;

    @BeforeAll
    static void beforeAll() {
        testUser = buildTestUser();
    }

    @Test
    void getAllUsersWhenUsersExistTest() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserResponseDto> resultList = userService.getAllUsers();

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        UserResponseDto resultDto = resultList.get(0);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersWhenUsersDoNotExistTest() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDto> resultDto = userService.getAllUsers();

        assertThat(resultDto).isNotNull().isEmpty();

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdWhenUserExistsTest() {
        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.of(testUser));

        UserResponseDto resultDto = userService.getUserById(Constants.ID);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUserById(Constants.ID);
    }

    @Test
    void getUserByIdWhenUserDoesNotExistTest() {
        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(Constants.ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(Constants.ID));

        verify(userRepository, times(1)).findUserById(Constants.ID);
    }

    @Test
    void getUsersByIdsWhenUsersExistTest() {
        when(userRepository.findUsersByIdIn(Constants.IDS)).thenReturn(List.of(testUser));

        List<UserResponseDto> resultList = userService.getUsersByIds(Constants.IDS);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        UserResponseDto resultDto = resultList.get(0);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUsersByIdIn(Constants.IDS);
    }

    @Test
    void getUsersByIdsWhenUsersDoNotExistTest() {
        when(userRepository.findUsersByIdIn(Constants.IDS)).thenReturn(Collections.emptyList());

        List<UserResponseDto> resultDto = userService.getUsersByIds(Constants.IDS);

        assertThat(resultDto).isNotNull().isEmpty();

        verify(userRepository, times(1)).findUsersByIdIn(Constants.IDS);
    }

    @Test
    void getUserByEmailWhenUserExistsTest() {
        when(userRepository.findUserByEmail(Constants.USER_EMAIL)).thenReturn(Optional.of(testUser));

        UserResponseDto resultDto = userService.getUserByEmail(Constants.USER_EMAIL);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUserByEmail(Constants.USER_EMAIL);
    }

    @Test
    void getUserByEmailWhenUserDoesNotExistTest() {
        when(userRepository.findUserByEmail(Constants.USER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(Constants.USER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(Constants.USER_EMAIL);

        verify(userRepository, times(1)).findUserByEmail(Constants.USER_EMAIL);
    }

    @Test
    void createUserSuccessfulTest() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(Constants.USER_EMAIL)
                .build();

        when(userRepository.existsByEmail(Constants.USER_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDto resultDto = userService.createUser(requestDto);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).existsByEmail(Constants.USER_EMAIL);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserWhenEmailAlreadyExistsTest() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(Constants.USER_EMAIL)
                .build();

        when(userRepository.existsByEmail(Constants.USER_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists in the database")
                .hasMessageContaining(Constants.USER_EMAIL);

        verify(userRepository, times(1)).existsByEmail(Constants.USER_EMAIL);
    }

    @Test
    void updateUserWithNewRequestDataTest() {
        User testUser = buildTestUser();
        testUser.setCards(List.of(new Card()));

        UserRequestDto requestDto = UserRequestDto.builder()
                .name(Constants.USER_NAME)
                .surname(Constants.NEW_USER_SURNAME)
                .birthDate(Constants.NEW_USER_LOCAL_DAY)
                .email(Constants.NEW_USER_EMAIL)
                .build();

        String expectedHolderName = String.join(" ", Constants.USER_NAME, Constants.NEW_USER_SURNAME).toUpperCase();

        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(Constants.NEW_USER_EMAIL)).thenReturn(false);
        doNothing().when(cardRepository).updateHolderByUserId(Constants.ID, expectedHolderName);
        doNothing().when(userRepository).updateUser(Constants.ID, Constants.USER_NAME,
                Constants.NEW_USER_SURNAME, Constants.NEW_USER_LOCAL_DAY, Constants.NEW_USER_EMAIL);
        doNothing().when(cacheEvictor).evictUser(Constants.ID, Constants.USER_EMAIL, Constants.NEW_USER_EMAIL);

        UserResponseDto resultDto = userService.updateUser(Constants.ID, requestDto);

        assertAll(
                () -> assertThat(resultDto).isNotNull(),
                () -> assertThat(resultDto.getName()).isNotBlank().isEqualTo(Constants.USER_NAME),
                () -> assertThat(resultDto.getSurname()).isNotBlank().isEqualTo(Constants.NEW_USER_SURNAME),
                () -> assertThat(resultDto.getBirthDate()).isNotNull().isEqualTo(Constants.NEW_USER_LOCAL_DAY),
                () -> assertThat(resultDto.getEmail()).isNotBlank().isEqualTo(Constants.NEW_USER_EMAIL),
                () -> assertThat(resultDto.getCards()).isNotNull().isNotEmpty().hasSize(1),
                () -> assertThat(resultDto.getCards().get(0).getHolder()).isNotBlank().isEqualTo(expectedHolderName)
        );

        verify(userRepository, times(1)).findUserById(Constants.ID);
        verify(userRepository, times(1)).existsByEmail(Constants.NEW_USER_EMAIL);
        verify(cardRepository, times(1)).updateHolderByUserId(Constants.ID, expectedHolderName);
        verify(userRepository, times(1)).updateUser(Constants.ID, Constants.USER_NAME, Constants.NEW_USER_SURNAME, Constants.NEW_USER_LOCAL_DAY, Constants.NEW_USER_EMAIL);
        verify(cacheEvictor, times(1)).evictUser(Constants.ID, Constants.USER_EMAIL, Constants.NEW_USER_EMAIL);
    }

    @Test
    void updateUserWhenUserDoesNotExistTest() {
        UserRequestDto requestDto = UserRequestDto.builder().build();

        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(Constants.ID, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(Constants.ID));

        verify(userRepository, times(1)).findUserById(Constants.ID);
    }

    @Test
    void updateUserWhenEmailAlreadyExistsTest() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(Constants.NEW_USER_EMAIL)
                .build();

        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(Constants.NEW_USER_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(Constants.ID, requestDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists in the database")
                .hasMessageContaining(Constants.NEW_USER_EMAIL);

        verify(userRepository, times(1)).findUserById(Constants.ID);
        verify(userRepository, times(1)).existsByEmail(Constants.NEW_USER_EMAIL);
    }

    @Test
    void deleteUserSuccessfulTest() {
        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.of(testUser));
        doNothing().when(cacheEvictor).evictUser(Constants.ID, Constants.USER_EMAIL);

        userService.deleteUser(Constants.ID);

        verify(userRepository, times(1)).findUserById(Constants.ID);
        verify(cacheEvictor, times(1)).evictUser(Constants.ID, Constants.USER_EMAIL);
        verify(userRepository, times(1)).deleteUserById(Constants.ID);
    }

    @Test
    void deleteUserWhenUserDoesNotExistTest() {
        when(userRepository.findUserById(Constants.ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(Constants.ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(Constants.ID));

        verify(userRepository, times(1)).findUserById(Constants.ID);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, cardRepository, cacheEvictor);
    }

    private void assertUserResponseDtoFields(UserResponseDto responseDto) {
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getName()).isNotBlank().isEqualTo(Constants.USER_NAME),
                () -> assertThat(responseDto.getSurname()).isNotBlank().isEqualTo(Constants.USER_NAME),
                () -> assertThat(responseDto.getBirthDate()).isNotNull().isEqualTo(Constants.LOCAL_DATE_YESTERDAY),
                () -> assertThat(responseDto.getEmail()).isNotBlank().isEqualTo(Constants.USER_EMAIL)
        );
    }
}
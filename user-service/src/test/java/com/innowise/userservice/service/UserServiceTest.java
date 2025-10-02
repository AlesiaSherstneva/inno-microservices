package com.innowise.userservice.service;

import com.innowise.userservice.exception.EmailAlreadyExistsException;
import com.innowise.userservice.exception.ResourceNotFoundException;
import com.innowise.userservice.model.dto.UserRequestDto;
import com.innowise.userservice.model.dto.UserResponseDto;
import com.innowise.userservice.model.entity.Card;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
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
        when(userRepository.findUserById(ID)).thenReturn(Optional.of(testUser));

        UserResponseDto resultDto = userService.getUserById(ID);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUserById(ID);
    }

    @Test
    void getUserByIdWhenUserDoesNotExistTest() {
        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(userRepository, times(1)).findUserById(ID);
    }

    @Test
    void getUsersByIdsWhenUsersExistTest() {
        when(userRepository.findUsersByIdIn(IDS)).thenReturn(List.of(testUser));

        List<UserResponseDto> resultList = userService.getUsersByIds(IDS);

        assertThat(resultList).isNotNull().isNotEmpty().hasSize(1);

        UserResponseDto resultDto = resultList.get(0);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUsersByIdIn(IDS);
    }

    @Test
    void getUsersByIdsWhenUsersDoNotExistTest() {
        when(userRepository.findUsersByIdIn(IDS)).thenReturn(Collections.emptyList());

        List<UserResponseDto> resultDto = userService.getUsersByIds(IDS);

        assertThat(resultDto).isNotNull().isEmpty();

        verify(userRepository, times(1)).findUsersByIdIn(IDS);
    }

    @Test
    void getUserByEmailWhenUserExistsTest() {
        when(userRepository.findUserByEmail(USER_EMAIL)).thenReturn(Optional.of(testUser));

        UserResponseDto resultDto = userService.getUserByEmail(USER_EMAIL);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).findUserByEmail(USER_EMAIL);
    }

    @Test
    void getUserByEmailWhenUserDoesNotExistTest() {
        when(userRepository.findUserByEmail(USER_EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(USER_EMAIL))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(USER_EMAIL);

        verify(userRepository, times(1)).findUserByEmail(USER_EMAIL);
    }

    @Test
    void createUserSuccessfulTest() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(USER_EMAIL)
                .build();

        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponseDto resultDto = userService.createUser(requestDto);

        assertUserResponseDtoFields(resultDto);

        verify(userRepository, times(1)).existsByEmail(USER_EMAIL);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUserWhenEmailAlreadyExistsTest() {
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(USER_EMAIL)
                .build();

        when(userRepository.existsByEmail(USER_EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(requestDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists in the database")
                .hasMessageContaining(USER_EMAIL);

        verify(userRepository, times(1)).existsByEmail(USER_EMAIL);
    }

    @Test
    void updateUserWithNewRequestDataTest() {
        User testUser = buildTestUser();
        testUser.setCards(List.of(new Card()));

        String newUserSurname = "New-Surname", newUserEmail = "new-email@test.test";
        LocalDate newBirthDate = NOW.plusDays(3);

        UserRequestDto requestDto = UserRequestDto.builder()
                .name(USER_NAME)
                .surname(newUserSurname)
                .birthDate(newBirthDate)
                .email(newUserEmail)
                .build();

        String expectedHolderName = String.join(" ", USER_NAME, newUserSurname).toUpperCase();

        when(userRepository.findUserById(ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newUserEmail)).thenReturn(false);
        doNothing().when(cardRepository).updateHolderByUserId(ID, expectedHolderName);
        doNothing().when(userRepository).updateUser(ID, USER_NAME, newUserSurname, newBirthDate, newUserEmail);
        doNothing().when(cacheEvictor).evictUser(ID, USER_EMAIL, newUserEmail);

        UserResponseDto resultDto = userService.updateUser(ID, requestDto);

        assertAll(
                () -> assertThat(resultDto).isNotNull(),
                () -> assertThat(resultDto.getName()).isNotBlank().isEqualTo(USER_NAME),
                () -> assertThat(resultDto.getSurname()).isNotBlank().isEqualTo(newUserSurname),
                () -> assertThat(resultDto.getBirthDate()).isNotNull().isEqualTo(newBirthDate),
                () -> assertThat(resultDto.getEmail()).isNotBlank().isEqualTo(newUserEmail),
                () -> assertThat(resultDto.getCards()).isNotNull().isNotEmpty().hasSize(1),
                () -> assertThat(resultDto.getCards().get(0).getHolder()).isNotBlank().isEqualTo(expectedHolderName)
        );

        verify(userRepository, times(1)).findUserById(ID);
        verify(userRepository, times(1)).existsByEmail(newUserEmail);
        verify(cardRepository, times(1)).updateHolderByUserId(ID, expectedHolderName);
        verify(userRepository, times(1)).updateUser(ID, USER_NAME, newUserSurname, newBirthDate, newUserEmail);
        verify(cacheEvictor, times(1)).evictUser(ID, USER_EMAIL, newUserEmail);
    }

    @Test
    void updateUserWhenUserDoesNotExistTest() {
        UserRequestDto requestDto = UserRequestDto.builder().build();

        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(ID, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(userRepository, times(1)).findUserById(ID);
    }

    @Test
    void updateUserWhenEmailAlreadyExistsTest() {
        String newUserEmail = "new-email@test.test";
        UserRequestDto requestDto = UserRequestDto.builder()
                .email(newUserEmail)
                .build();

        when(userRepository.findUserById(ID)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(newUserEmail)).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(ID, requestDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("Email already exists in the database")
                .hasMessageContaining(newUserEmail);

        verify(userRepository, times(1)).findUserById(ID);
        verify(userRepository, times(1)).existsByEmail(newUserEmail);
    }

    @Test
    void deleteUserSuccessfulTest() {
        when(userRepository.findUserById(ID)).thenReturn(Optional.of(testUser));
        doNothing().when(cacheEvictor).evictUser(ID, USER_EMAIL);

        userService.deleteUser(ID);

        verify(userRepository, times(1)).findUserById(ID);
        verify(cacheEvictor, times(1)).evictUser(ID, USER_EMAIL);
        verify(userRepository, times(1)).deleteUserById(ID);
    }

    @Test
    void deleteUserWhenUserDoesNotExistTest() {
        when(userRepository.findUserById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found")
                .hasMessageContaining(String.valueOf(ID));

        verify(userRepository, times(1)).findUserById(ID);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(userRepository, cardRepository, cacheEvictor);
    }

    private void assertUserResponseDtoFields(UserResponseDto responseDto) {
        assertAll(
                () -> assertThat(responseDto).isNotNull(),
                () -> assertThat(responseDto.getName()).isNotBlank().isEqualTo(USER_NAME),
                () -> assertThat(responseDto.getSurname()).isNotBlank().isEqualTo(USER_NAME),
                () -> assertThat(responseDto.getBirthDate()).isNotNull().isEqualTo(NOW),
                () -> assertThat(responseDto.getEmail()).isNotBlank().isEqualTo(USER_EMAIL)
        );
    }
}
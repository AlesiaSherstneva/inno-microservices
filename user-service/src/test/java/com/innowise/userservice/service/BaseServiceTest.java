package com.innowise.userservice.service;

import com.innowise.userservice.model.dto.mapper.CardMapperImpl;
import com.innowise.userservice.model.dto.mapper.UserMapperImpl;
import com.innowise.userservice.model.entity.User;
import com.innowise.userservice.repository.CardRepository;
import com.innowise.userservice.repository.UserRepository;
import com.innowise.userservice.service.cache.CacheEvictor;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UserMapperImpl.class, CardMapperImpl.class})
public abstract class BaseServiceTest {
    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected CardRepository cardRepository;

    @MockitoBean
    protected CacheEvictor cacheEvictor;

    protected static final Long ID = 1L;
    protected static final List<Long> IDS = List.of(ID);
    protected static final String USER_NAME = "Test";
    protected static final String USER_EMAIL = "test@test.test";
    protected static final LocalDate NOW = LocalDate.now();

    protected static User buildTestUser() {
        return User.builder()
                .id(ID)
                .name(USER_NAME)
                .surname(USER_NAME)
                .birthDate(NOW)
                .email(USER_EMAIL)
                .build();
    }
}
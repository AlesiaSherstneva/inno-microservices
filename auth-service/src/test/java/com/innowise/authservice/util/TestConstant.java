package com.innowise.authservice.util;

import com.innowise.authservice.model.entity.enums.Role;

import java.time.LocalDateTime;

public final class TestConstant {
    public static final Long ID = 1L;
    public static final String PHONE_NUMBER = "+375251234567";
    public static final String PASSWORD = "TestPassword";
    public static final Role ROLE_USER = Role.USER;
    public static final LocalDateTime CREATION_TIMESTAMP = LocalDateTime.now();

    public static final String WRONG_PASSWORD = "WrongPassword";

    private TestConstant() {
    }
}
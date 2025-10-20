package com.innowise.orderservice.util;

import java.time.LocalDateTime;
import java.util.List;

public class TestConstant {
    public static final Integer INTEGER_ID = 1;
    public static final Long LONG_ID = 1L;
    public static final List<Long> LONG_IDS = List.of(LONG_ID);
    public static final LocalDateTime LOCAL_DATE_TIME_NOW = LocalDateTime.now();
    public static final String USER_NAME = "Test";
    public static final String USER_EMAIL = "test@test.test";
    public static final String ITEM_NAME = "Test product";
    public static final Integer ITEM_QUANTITY = 3;

    public static final String NEW_USER_SURNAME = "New-Surname";
    public static final String NEW_USER_EMAIL = "new-email@test.test";
    public static final LocalDateTime NEW_USER_LOCAL_DAY = LOCAL_DATE_TIME_NOW.minusDays(3);

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER_PATTERN = "Bearer %s";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_SERVICE = "SERVICE";

    public static final String JSON_PATH_USER_NAME = "$.name";
    public static final String JSON_PATH_USER_SURNAME = "$.surname";
    public static final String JSON_PATH_USER_BIRTH_DATE = "$.birthDate";
    public static final String JSON_PATH_USER_EMAIL = "$.email";
    public static final String JSON_PATH_USER_CARDS = "$.cards";

    public static final String JSON_PATH_CARD_NUMBER = "$.number";
    public static final String JSON_PATH_CARD_HOLDER = "$.holder";
    public static final String JSON_PATH_CARD_EXPIRATION_DATE = "$.expirationDate";

    public static final String JSON_PATH_COMMON_ARRAY = "$";

    public static final String JSON_PATH_EXCEPTION_STATUS = "$.status";
    public static final String JSON_PATH_EXCEPTION_ERROR_MESSAGE = "$.errorMessage";
    public static final String JSON_PATH_EXCEPTION_DETAILS = "$.errorDetails";
    public static final String JSON_PATH_EXCEPTION_TIMESTAMP = "$.timestamp";

    private TestConstant() {
    }
}
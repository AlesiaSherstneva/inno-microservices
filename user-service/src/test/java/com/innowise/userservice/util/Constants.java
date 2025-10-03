package com.innowise.userservice.util;

import java.time.LocalDate;
import java.util.List;

public class Constants {
    public static final Long ID = 1L;
    public static final List<Long> IDS = List.of(ID);
    public static final String USER_NAME = "Test";
    public static final String USER_EMAIL = "test@test.test";
    public static final LocalDate LOCAL_DATE_YESTERDAY = LocalDate.now().minusDays(1);

    public static final String NEW_USER_SURNAME = "New-Surname";
    public static final String NEW_USER_EMAIL = "new-email@test.test";
    public static final LocalDate NEW_USER_LOCAL_DAY = LOCAL_DATE_YESTERDAY.minusDays(3);

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

    private Constants() {
    }
}
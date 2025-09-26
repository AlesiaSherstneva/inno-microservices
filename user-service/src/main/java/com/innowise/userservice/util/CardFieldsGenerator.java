package com.innowise.userservice.util;

import com.innowise.userservice.entity.User;

import java.util.Random;

public class CardFieldsGenerator {
    private static final Random RANDOM = new Random();
    private static final String CARD_NUMBER_PATTERN = "%04d-%04d-%04d-%04d";
    public static final int CARD_VALIDITY_YEARS = 3;

    public static String generateCardNumber() {
        return String.format(CARD_NUMBER_PATTERN,
                RANDOM.nextInt(10000), RANDOM.nextInt(10000),
                RANDOM.nextInt(10000), RANDOM.nextInt(10000));
    }

    public static String formatCardHolderName(User user) {
        return String.join(" ", user.getName(), user.getSurname()).toUpperCase();
    }
}
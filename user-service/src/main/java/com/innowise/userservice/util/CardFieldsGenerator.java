package com.innowise.userservice.util;

import com.innowise.userservice.entity.User;
import com.innowise.userservice.service.CardInfoService;

import java.util.Random;

/**
 * Utility class for generating card-related data.
 *
 * @see CardInfoService
 */
public class CardFieldsGenerator {
    /** Random number generator for card number creation */
    private static final Random RANDOM = new Random();

    /** Pattern for card number generation, produces numbers in XXXX-XXXX-XXXX-XXXX format */
    private static final String CARD_NUMBER_PATTERN = "%04d-%04d-%04d-%04d";

    /** Card validity period from creation date (3 years) */
    public static final int CARD_VALIDITY_YEARS = 3;

    /**
     * Generates a unique card number in XXXX-XXXX-XXXX-XXXX format.
     *
     * @return generated card number
     */
    public static String generateCardNumber() {
        return String.format(CARD_NUMBER_PATTERN,
                RANDOM.nextInt(10000), RANDOM.nextInt(10000),
                RANDOM.nextInt(10000), RANDOM.nextInt(10000));
    }

    /**
     * Formats user's name and surname into holder name.
     * Combines first name and last name in uppercase format.
     *
     * @param user the user entity containing name and surname
     * @return formatted holder name
     */
    public static String formatCardHolderName(User user) {
        return String.join(" ", user.getName(), user.getSurname()).toUpperCase();
    }
}
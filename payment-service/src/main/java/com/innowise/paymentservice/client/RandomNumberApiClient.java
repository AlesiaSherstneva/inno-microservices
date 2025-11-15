package com.innowise.paymentservice.client;

import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

/**
 * Client component for determining payment status using an external random number API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RandomNumberApiClient {
    private final RestTemplate restTemplate;
    private final Random fallbackRandom = new Random();

    @Value("${external-api.url}")
    private String externalApiUrl;

    /**
     * Determines the payment status based on a random number from external API.
     *
     * @return  {@link PaymentStatus#SUCCESS} if the number is even,
     *          {@link PaymentStatus#FAILED} if the number is odd
     */
    public PaymentStatus determinePaymentStatus() {
        return getRandomNumber() % 2 == 0 ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;
    }

    private int getRandomNumber() {
        try {
            int[] apiResponse = restTemplate.getForObject(externalApiUrl, int[].class);
            if (apiResponse.length > 0) {
                return apiResponse[0];
            }
        } catch (Exception e) {
            log.warn("External API unavailable, using fallback random. Error: {}", e.getMessage());
        }

        return fallbackRandom.nextInt(100) + 1;
    }
}
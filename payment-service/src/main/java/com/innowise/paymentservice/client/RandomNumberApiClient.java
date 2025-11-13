package com.innowise.paymentservice.client;

import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomNumberApiClient {
    private final RestTemplate restTemplate;
    private final Random fallbackRandom = new Random();

    @Value("${external-api.url}")
    private String externalApiUrl;

    public PaymentStatus determinePaymentStatus() {
        int randomNumber;

        try {
            randomNumber = restTemplate.getForObject(externalApiUrl, int.class);
        } catch (Exception e) {
            log.warn("External API unavailable, using fallback random. Error: {}", e.getMessage());
            randomNumber = fallbackRandom.nextInt(100) + 1;
        }

        return randomNumber % 2 == 0 ? PaymentStatus.COMPLETED : PaymentStatus.FAILED;
    }
}
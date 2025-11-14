package com.innowise.paymentservice.service.impl;

import com.innowise.paymentservice.client.RandomNumberApiClient;
import com.innowise.paymentservice.model.dto.kafka.OrderCreatedEvent;
import com.innowise.paymentservice.model.dto.mapper.PaymentMapper;
import com.innowise.paymentservice.model.entity.Payment;
import com.innowise.paymentservice.model.entity.enums.PaymentStatus;
import com.innowise.paymentservice.repository.PaymentRepository;
import com.innowise.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RandomNumberApiClient randomNumberApiClient;

    @Override
    @Transactional
    public Payment createPayment(OrderCreatedEvent event) {
        Payment newPayment = paymentMapper.toEntity(event);

        PaymentStatus paymentOperationResult = randomNumberApiClient.determinePaymentStatus();
        newPayment.setStatus(paymentOperationResult);

        return paymentRepository.save(newPayment);
    }
}
package com.innowise.paymentservice.repository;

import com.innowise.paymentservice.model.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findPaymentByOrderId(Long orderId);
}
package com.petverse.repository.business;

import org.springframework.data.jpa.repository.JpaRepository;
import com.petverse.entity.concretes.payment.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);
}

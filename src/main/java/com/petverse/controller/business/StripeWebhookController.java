package com.petverse.controller.business;

import com.petverse.entity.enums.PaymentStatus;
import com.petverse.repository.business.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handle(@RequestBody String payload,
                                         @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;
        try {

            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
            case "payment_intent.payment_failed":
            case "payment_intent.canceled":
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject().orElse(null);

                if (paymentIntent != null) {
                    updatePaymentStatus(paymentIntent);
                }
                break;
            default:
                break;
        }

        return ResponseEntity.ok("Received");
    }

    private void updatePaymentStatus(PaymentIntent paymentIntent) {
        com.petverse.entity.concretes.payment.Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntent.getId())
                .orElse(null);

        if (payment != null) {
            payment.setStatus(mapStripeStatus(paymentIntent.getStatus()));
            paymentRepository.save(payment);
        }
    }

    private PaymentStatus mapStripeStatus(String stripeStatus) {
        return switch (stripeStatus) {
            case "succeeded" -> PaymentStatus.SUCCEEDED;
            case "requires_payment_method", "requires_action" -> PaymentStatus.PENDING;
            case "canceled" -> PaymentStatus.CANCELED;
            default -> PaymentStatus.FAILED;
        };
    }
}

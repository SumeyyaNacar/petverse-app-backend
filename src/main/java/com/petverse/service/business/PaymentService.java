package com.petverse.service.business;

import com.petverse.entity.concretes.payment.Payment;
import com.petverse.entity.enums.PaymentStatus;
import com.petverse.payload.ResponseMessage;
import com.petverse.payload.messages.SuccessMessages;
import com.petverse.payload.request.business.PaymentRequest;
import com.petverse.repository.business.PaymentRepository;
import com.petverse.service.helper.MethodHelper;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PaymentRepository paymentRepository;
    private final MethodHelper methodHelper;

    public ResponseMessage<Payment> createPayment(PaymentRequest request) throws Exception {
        Stripe.apiKey = stripeApiKey;

        // Stripe PaymentIntent oluştur
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount())
                .setCurrency(request.getCurrency())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // 2️⃣ DB kaydı oluştur
        Payment payment = Payment.builder()
                .petId(request.getPetId())
                .user(methodHelper.findUserById(request.getUserId()))
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .stripePaymentIntentId(paymentIntent.getId())
                .status(mapStripeStatus(paymentIntent.getStatus()))
                .build();

        paymentRepository.save(payment);

        return ResponseMessage.<Payment>builder()
                .message(SuccessMessages.PAYMENT_CREATED_MESSAGE)
                .object(payment)
                .httpStatus(HttpStatus.CREATED)
                .build();
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

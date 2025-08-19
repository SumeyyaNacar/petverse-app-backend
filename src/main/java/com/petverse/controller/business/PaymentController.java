package com.petverse.controller.business;

import com.petverse.payload.ResponseMessage;
import com.petverse.payload.request.business.PaymentRequest;
import com.petverse.service.business.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseMessage<?> createPayment(@Valid @RequestBody PaymentRequest request) throws Exception {
        return paymentService.createPayment(request);
    }
}

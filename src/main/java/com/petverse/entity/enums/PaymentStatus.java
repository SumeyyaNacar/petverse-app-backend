package com.petverse.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING("Pending"),
    SUCCEEDED("Succeeded"),
    FAILED("Failed"),
    CANCELED("Canceled");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

}

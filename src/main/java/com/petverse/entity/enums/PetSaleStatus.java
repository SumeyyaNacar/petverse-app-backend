package com.petverse.entity.enums;
public enum PetSaleStatus {
    AVAILABLE("Available"),   // Satışta
    SOLD("Sold"),        // Satılmış
    INACTIVE("Inactive")  // Sistemden kaldırılmış
    ;

    private String petSaleStatus;
    PetSaleStatus(String petSaleStatus) {
        this.petSaleStatus = petSaleStatus;
    }
}

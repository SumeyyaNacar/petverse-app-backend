package com.petverse.payload.response.business;

import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.PetSaleStatus;
import com.petverse.entity.enums.Species;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponse {

    private UUID id;
    private String name;
    private Species species;
    private String breed;
    private Gender gender;
    private LocalDate birthDate;
    private Double weight;
    private Double height;
    private boolean vaccinated;
    private boolean sterilized;
    private String medicalHistory;
    private String color;
    private PetSaleStatus status;
    private Double price;
    private String microchipNumber;
    private String notes;
    private List<String> photoUrls;

    // sahibinin kimliği
    private UUID ownerId;
    private String ownerName;
}

package com.petverse.payload.request.business;

import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.Species;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetRequest {

    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 50, message = "Pet name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Species is required")
    private Species species;

    @NotBlank(message = "Breed is required")
    @Size(min = 2, max = 50, message = "Breed must be between 2 and 50 characters")
    private String breed;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @PastOrPresent(message = "Birth date cannot be in the future")
    private LocalDate birthDate;

    @PositiveOrZero(message = "Weight must be positive")
    private Double weight;

    @PositiveOrZero(message = "Height must be positive")
    private Double height;

    private boolean vaccinated;
    private boolean sterilized;

    @Size(max = 1000, message = "Medical history must be less than 1000 characters")
    private String medicalHistory;

    @NotBlank(message = "Color is required")
    private String color;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String microchipNumber;

    @Size(max = 1000, message = "Notes must be less than 1000 characters")
    private String notes;

    @Size(max = 10, message = "A pet can have up to 10 photos")
    private List<@NotBlank(message = "Photo URL cannot be blank") String> photoUrls;

    private UUID ownerId;
}

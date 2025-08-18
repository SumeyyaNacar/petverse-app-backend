package com.petverse.entity.concretes.business;

import com.petverse.entity.concretes.base.Base;
import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.PetSaleStatus;
import com.petverse.entity.enums.Species;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(
        name = "pet",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "owner_id"})//aynı sahip altında aynı isimde pet kaydı engellenir.
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class Pet extends Base {

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Species species;

    @Column(nullable = false)
    private String breed;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;
    private Double weight;
    private Double height;

    @Column(nullable = false)
    private boolean vaccinated;

    @Column(nullable = false)
    private boolean sterilized;

    @Column(length = 1000)
    private String medicalHistory;

    @Column(nullable = false)
    private String color;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetSaleStatus status;   // AVAILABLE, SOLD, INACTIVE

    @Column(nullable = false)
    private Double price;

    @Column(unique = true)
    private String microchipNumber;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;


}

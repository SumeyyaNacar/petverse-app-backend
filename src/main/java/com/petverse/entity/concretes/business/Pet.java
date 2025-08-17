package com.petverse.entity.concretes.business;

import com.petverse.entity.concretes.base.Base;
import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.Species;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "pet")
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
    private boolean vaccinated;

    @Column(length = 1000)
    private String medicalHistory;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private boolean availableForAdoption;
    private Double price;

    @Column(unique = true)
    private String microchipNumber;

    @Column(length = 1000)
    private String notes;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;
}

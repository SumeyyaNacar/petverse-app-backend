package com.petverse.entity.concretes.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.petverse.entity.concretes.base.Base;
import com.petverse.entity.concretes.business.Pet;
import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.RoleType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class User extends Base {

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true,  nullable = false)
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private String address;

    private LocalDateTime lastLoginAt;
    private Boolean isActive;
    private Boolean builtIn;
    private Boolean isDeleted;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private List<Pet> pets;
}

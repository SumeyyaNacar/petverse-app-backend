package com.petverse.payload.response.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.petverse.entity.enums.Gender;
import com.petverse.entity.enums.RoleType;
import com.petverse.payload.response.abstracts.BaseUserResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse extends BaseUserResponse {

    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate registredAt;
    private Gender gender;
    private RoleType roleType;
    private Boolean isActive;


}

package com.petverse.payload.request.abstracts;

import com.petverse.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractUserRequest {

    @NotNull(message = "Firstname can not be null")
    @Size(min = 4, max = 30,message = "Your firstname should be at least 4 chars")
    private String firstName;

    @NotNull(message = "Lastname can not be null")
    @Size(min = 4, max = 30,message = "Your lastname should be at least 4 chars")
    private String lastName;

    @NotNull(message = "Please enter your email")
    @Email(message = "Please enter valid email")
    @Size(min=5, max=50 , message = "Your email should be between 5 and 50 chars")
    private String email;

    @NotNull(message = "Please enter your phone number")
    @Size(min = 12, max = 12,message = "Your phone number should be 12 characters long")
    @Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
            message = "Please enter valid phone number")
    private String phoneNumber;

    @NotNull(message = "Please enter your gender")
    private Gender gender;

    @NotNull(message = "Please enter your address")
    @Size(min = 2, max = 50,message = "Your address should be at least 2 chars")
    private String address;




}

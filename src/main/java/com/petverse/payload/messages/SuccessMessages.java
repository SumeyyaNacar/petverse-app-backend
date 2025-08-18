package com.petverse.payload.messages;

import org.springframework.stereotype.Component;

public class SuccessMessages {



    public SuccessMessages() {
    }

    public static final String USER_REGISTERED_MESSAGE = "User registered successfully";
    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully" ;
    public static final Object VALIDATE_TOKEN_MESSAGE = "Token is valid. Please enter new password";
    public static final String USER_FOUND_MESSAGE = "User found successfully";
    public static final String USER_UPDATED_SUCCESSFULLY_MESSAGE = "User info updated successfully";
    public static final String USER_SAVED_MESSAGE = "User saved successfully";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully";

    public static final String PET_FOUND_MESSAGE = "Pet found successfully";
    public static final String PET_SAVED_MESSAGE = "Pet created successfully";
    public static final String PET_PURCHASED_MESSAGE = "Pet purchased successfully";
    public static final String PET_DELETED_MESSAGE = "Pet deleted successfully";
    public static final String PET_INACTIVATED_MESSAGE = "Pet  inactivated successfully";
    public static final String PHOTO_ADDED_MESSAGE = "Photo added successfully";
}

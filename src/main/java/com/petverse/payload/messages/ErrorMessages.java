package com.petverse.payload.messages;

public class ErrorMessages {



    public ErrorMessages() {
    }
    public static final String MAIL_SEND_MESSAGE = "Mail could not be sent to your email address ";

    public static final String NOT_FOUND_USER_MESSAGE = "User with id %s not found";
    public static final String NOT_PERMITTED_METHOD_MESSAGE = "Not permitted to do this operation ";
    public static final String TOKEN_TIME_OUT_MESSAGE = "Token has expired";
    public static final String ALREADY_REGISTER_MESSAGE_PHONE ="User already registered with phone number %s" ;

    public static final String ALREADY_REGISTER_MESSAGE_EMAIL = "User already registered with email %s";
    public static final String PASSWORD_NOT_MATCHED = "Passwords don't match";
    public static final Object INVALID_TOKEN_MESSAGE = "Token is invalid or expired";
    public static final String USER_ROLE_NOT_FOUND = "User role %s not found";
    public static final String NOT_VERIFIED_USER_MESSAGE = "User not verified";
    public static final String USER_NOT_FOUND_EMAIL_MESSAGE = "User with email %s not found";
    public static final String CANNOT_DELETE_SELF_MESSAGE = "User cannot delete herself/himself";
    public static final String BUILT_IN_USER_CANNOT_BE_DELETED = "Cannot delete built-in user";
    public static final String USER_ALREADY_DELETED_MESSAGE = "User already has been deleted";


}

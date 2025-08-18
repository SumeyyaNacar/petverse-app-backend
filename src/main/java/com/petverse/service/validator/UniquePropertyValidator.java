package com.petverse.service.validator;


import com.petverse.entity.concretes.user.User;
import com.petverse.exception.ConflictException;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.payload.request.abstracts.AbstractUserRequest;
import com.petverse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniquePropertyValidator {

    private final UserRepository userRepository;

    public void checkDuplicate(String email, String phoneNumber) {


        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_PHONE, phoneNumber));
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException(String.format(ErrorMessages.ALREADY_REGISTER_MESSAGE_EMAIL, email));
        }
    }

    //update de
    public void checkUniqueProperties(User existingUser, AbstractUserRequest updatedRequest) {

        boolean isChanged = false;
        String newPhoneNumber = null;
        String newEmail = null;

        if (!existingUser.getPhoneNumber().equalsIgnoreCase(updatedRequest.getPhoneNumber())) {
            newPhoneNumber = updatedRequest.getPhoneNumber();
            isChanged = true;
        }

        if (!existingUser.getEmail().equalsIgnoreCase(updatedRequest.getEmail())) {
            newEmail = updatedRequest.getEmail();
            isChanged = true;
        }

        if (isChanged) {
            checkDuplicate(
                    newPhoneNumber != null ? newPhoneNumber : existingUser.getPhoneNumber(),
                    newEmail != null ? newEmail : existingUser.getEmail()
            );
        }
    }


}

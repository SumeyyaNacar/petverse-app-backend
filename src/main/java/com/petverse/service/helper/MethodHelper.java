package com.petverse.service.helper;


import com.petverse.entity.concretes.user.User;
import com.petverse.exception.BadRequestException;
import com.petverse.exception.NoEntityFoundException;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MethodHelper {
    private final UserRepository userRepository;


    // !!! isUserExist
    public User findUserById(UUID id) {//varsa user gelecek, yoksa exception
        return userRepository.findById(id).orElseThrow(() ->
                new NoEntityFoundException(String.format(ErrorMessages.NOT_FOUND_USER_MESSAGE,
                        id)));
    }

    public void checkBuiltIn(User user){
        if(Boolean.TRUE.equals(user.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    public User findUserByEmail(String email){
        User user = userRepository.findByEmailEquals(email);//userin kendisini db den cektik
        if (user.getId() == null){
            throw new NoEntityFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        return user;

    }

}
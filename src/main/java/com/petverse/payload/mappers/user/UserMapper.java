package com.petverse.payload.mappers.user;

import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.RoleType;
import com.petverse.payload.request.abstracts.AbstractUserRequest;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.payload.response.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserMapper {


    public User mapRequestToEntity(UserRequest userRequest) {
        return User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .address(userRequest.getAddress())
                .gender(userRequest.getGender())
                .isActive(userRequest.getIsActive())
                .isDeleted(userRequest.getIsDeleted())
                .build();
    }

    public UserResponse  mapEntityToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .gender(user.getGender())
                .roleType(user.getRoleType())
                .isActive(user.getIsActive())
                .build();
    }

    public User mapUserRequestToUpdatedUser(AbstractUserRequest userRequest, UUID userId) {
        return User.builder()
                .id(userId)
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .email(userRequest.getEmail())
                .phoneNumber(userRequest.getPhoneNumber())
                .address(userRequest.getAddress())
                .gender(userRequest.getGender())
                //.roleType(RoleType.OWNER)
                .build();
    }

}

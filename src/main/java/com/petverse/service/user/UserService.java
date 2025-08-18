package com.petverse.service.user;

import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.RoleType;
import com.petverse.exception.BadRequestException;
import com.petverse.exception.NoEntityFoundException;
import com.petverse.payload.ResponseMessage;
import com.petverse.payload.mappers.user.UserMapper;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.payload.messages.SuccessMessages;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.payload.request.user.UserRequestWithoutPassword;
import com.petverse.payload.response.user.UserResponse;
import com.petverse.repository.user.UserRepository;
import com.petverse.service.helper.MethodHelper;
import com.petverse.service.helper.PageableHelper;
import com.petverse.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MethodHelper methodHelper;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final PasswordEncoder passwordEncoder;
    private final PageableHelper pageableHelper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ResponseMessage<UserResponse> getUserProfile(Principal principal) {
        User user = userRepository.findByEmailEquals(principal.getName());

        if (user == null) {
            throw new NoEntityFoundException(ErrorMessages.NOT_FOUND_USER_MESSAGE);
        }
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_FOUND_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapEntityToResponse(user))
                .build();

    }

    public ResponseMessage<UserResponse> getUserById(UUID id) {
        User foundUser = methodHelper.findUserById(id);
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_FOUND_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapEntityToResponse(foundUser))
                .build();
    }


    public Page<UserResponse> getOwnersByPage(String query, int page, int size, String sort, String type) {
        Pageable pageable = pageableHelper.getPageableWithProperties(page, size, sort, type);
        return userRepository.findUsersByQuery(query, pageable)
                .map(userMapper::mapEntityToResponse);
    }

    public ResponseMessage<UserResponse> updateUserProfile(UserRequest userRequest,
                                                           UUID id) {
        User foundUser = methodHelper.findUserById(id);
        methodHelper.checkBuiltIn(foundUser);
        uniquePropertyValidator.checkUniqueProperties(foundUser, userRequest);

        //sadece değişen alanları setle
        foundUser.setId(id);
        foundUser.setFirstName(userRequest.getFirstName());
        foundUser.setLastName(userRequest.getLastName());
        foundUser.setEmail(userRequest.getEmail());
        foundUser.setPhoneNumber(userRequest.getPhoneNumber());
        foundUser.setAddress(userRequest.getAddress());
        foundUser.setGender(userRequest.getGender());
        foundUser.setRoleType(foundUser.getRoleType());
        foundUser.setIsDeleted(userRequest.getIsDeleted());
        foundUser.setIsActive(userRequest.getIsActive());

        // Password update only if provided and not empty
        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            foundUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        } else {
            // Keep old password if no new password provided
            foundUser.setPassword(foundUser.getPassword());
        }

        User savedUser = userRepository.save(foundUser);
        return ResponseMessage.<UserResponse>builder()
                .message(SuccessMessages.USER_UPDATED_SUCCESSFULLY_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .object(userMapper.mapEntityToResponse(savedUser))
                .build();


    }
    public Integer countAllAdmin() {
        Integer count = userRepository.countByRoleType(RoleType.ADMIN);
        return count != null ? count : 0;
    }

    // ---------- CREATE ----------
    @Transactional
    public ResponseMessage<UserResponse> saveAdmin(UserRequest userRequest, String roleType) {
        // Check duplicates
        uniquePropertyValidator.checkDuplicate(
                userRequest.getPhoneNumber(),
                userRequest.getEmail());

        // Map DTO to entity
        User user = userMapper.mapRequestToEntity(userRequest);

        // Set role and built-in flag
        setRoleAndBuiltIn(user, roleType);

        // Encode password
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return ResponseMessage.<UserResponse>builder()
                .httpStatus(HttpStatus.CREATED)
                .object(userMapper.mapEntityToResponse(savedUser))
                .message(SuccessMessages.USER_SAVED_MESSAGE)
                .build();
    }


    public ResponseMessage<String> createUser(@Valid UserRequest userRequest,
                                              String roleType) {
        //requesti yapan kim?
        ensureAdmin();

        uniquePropertyValidator.checkDuplicate(userRequest.getEmail(), userRequest.getPassword());
        //map
        User mappedUser = userMapper.mapRequestToEntity(userRequest);
        //role set and built_in flag
        setRoleAndBuiltIn(mappedUser, roleType);

        //encode pass
        mappedUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        //save

        userRepository.save(mappedUser);
        return ResponseMessage.<String>builder()
                .message(SuccessMessages.USER_SAVED_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();

    }

    public ResponseMessage<String> updateUserForUsers(UserRequestWithoutPassword userRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new BadRequestException(ErrorMessages.NOT_VERIFIED_USER_MESSAGE);
        }

        String requesterEmail = auth.getName();

        User user = userRepository.findByEmailEquals(requesterEmail);

        if (user == null) {
            throw new NoEntityFoundException(
                    String.format(ErrorMessages.USER_NOT_FOUND_EMAIL_MESSAGE, requesterEmail)
            );
        }

        methodHelper.checkBuiltIn(user);
        uniquePropertyValidator.checkUniqueProperties(user, userRequest);

        User mappedUser = userMapper.mapUserRequestToUpdatedUser(userRequest, user.getId());
        userRepository.save(mappedUser);

        return ResponseMessage.<String>builder()
                .message(SuccessMessages.USER_UPDATED_SUCCESSFULLY_MESSAGE)
                .httpStatus(HttpStatus.OK)
                .build();

    }


    // ---------- PRIVATE HELPERS ----------
    private void setRoleAndBuiltIn(User user, String roleType) {
        if (roleType.equals(RoleType.ADMIN.getName())) {
            if (Objects.equals(user.getEmail(), "admin@petverse.com")) {
                user.setBuiltIn(true);
            }
            user.setRoleType(RoleType.ADMIN);
        } else if (roleType.equals(RoleType.OWNER.getName())) {
            user.setRoleType(RoleType.OWNER);
        } else {
            throw new NoEntityFoundException(
                    String.format(ErrorMessages.USER_ROLE_NOT_FOUND, roleType)
            );
        }
    }

    private void ensureAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities() == null) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));

        if (!isAdmin) {
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
    }

    // ---------- SOFT DELETE ----------
    public ResponseMessage<String> deleteUser(UUID userId) {

        ensureAdmin();

        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmailEquals(currentUserEmail);

        if (currentUser != null && currentUser.getId().equals(userId)) {
            throw new BadRequestException(ErrorMessages.CANNOT_DELETE_SELF_MESSAGE);
        }

        User foundUser = methodHelper.findUserById(userId);

        if (Boolean.TRUE.equals(foundUser.getBuiltIn())) {
            throw new BadRequestException(ErrorMessages.BUILT_IN_USER_CANNOT_BE_DELETED);
        }

        if (Boolean.TRUE.equals(foundUser.getIsDeleted())) {
            throw new BadRequestException(ErrorMessages.USER_ALREADY_DELETED_MESSAGE);
        }

        foundUser.setIsDeleted(true);
        foundUser.setIsActive(false);
        userRepository.save(foundUser);

        return ResponseMessage.<String>builder()
                .message(SuccessMessages.USER_DELETED_SUCCESSFULLY)
                .httpStatus(HttpStatus.OK)
                .build();
    }

    // ---------- HARD DELETE ----------
    @Scheduled(cron = "0 0 0 * * ?") // every day at midnight
    @Transactional
    public void deleteInactiveUsersOverOneYear() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<User> oldUsers = userRepository.findByLastLoginAtBeforeOrLastLoginAtIsNull(oneYearAgo);

        oldUsers.removeIf(user -> Boolean.TRUE.equals(user.getBuiltIn()));

        if (!oldUsers.isEmpty()) {
            userRepository.deleteAll(oldUsers);
            logger.info("{} user(s) permanently deleted due to inactivity", oldUsers.size());
        }
    }
}

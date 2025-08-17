package com.petverse.service.user;

import com.petverse.entity.concretes.user.User;
import com.petverse.entity.enums.RoleType;
import com.petverse.exception.BadRequestException;
import com.petverse.payload.ResponseMessage;
import com.petverse.payload.mappers.user.UserMapper;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.payload.messages.SuccessMessages;
import com.petverse.payload.request.authentication.LoginRequest;
import com.petverse.payload.request.user.UpdatePasswordRequest;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.payload.response.authentication.AuthResponse;
import com.petverse.repository.user.UserRepository;
import com.petverse.security.jwt.JwtUtils;
import com.petverse.security.service.UserDetailsImpl;
import com.petverse.service.helper.MethodHelper;
import com.petverse.service.validator.UniquePropertyValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {


    private final UserRepository userRepository;//user islemleri icin
    private final AuthenticationManager authenticationManager;//sistemi yoneten Authentication Manager
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final UniquePropertyValidator uniquePropertyValidator;
    private final UserMapper userMapper;
    private final MethodHelper methodHelper;

    //login
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        //login olan kullanıcı bilgsini al
        String email = request.getEmail();
        String password = request.getPassword();
        /*kullaniciyi authentice et*/
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );
        /*authentice olan kullanici gecici olarak security contexte atildi*/
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Kullanıcıyı veritabanından bul
        User userEntity = userRepository.findByEmailEquals(request.getEmail());
        if (userEntity == null) {
            throw new BadRequestException(
                    String.format(ErrorMessages.USER_NOT_FOUND_EMAIL_MESSAGE, request.getEmail())
            );
        }

        // Önceki last login bilgisini sakla
        LocalDateTime previousLoginAt = userEntity.getLastLoginAt();
        // Kullanıcı login olduğunda aktif hale getir ve son giriş tarihini güncelle
        userEntity.setIsActive(true);
        userEntity.setIsDeleted(false);
        userEntity.setLastLoginAt(LocalDateTime.now());
        userRepository.save(userEntity);


        String token = "Bearer " + jwtUtils.generateJwtToken(authentication);
        /*user a ait bilgileri cekmek icin*/
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //rolu çek
        Set<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Optional<String> role = roles.stream().findFirst();//tek bir rolu getirme

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();
        authResponse.email(userDetails.getEmail());
        authResponse.token(token.substring(7));
        authResponse.firstName(userDetails.getFirstName());
        authResponse.lastName(userDetails.getLastName());
        role.ifPresent(authResponse::role);
        return ResponseEntity.ok(authResponse.build());


    }

    //register
    public ResponseMessage<String> register(UserRequest request) {
        uniquePropertyValidator.checkDuplicate(request.getEmail(), request.getPhoneNumber());

        //map to entity
        User user = userMapper.mapRequestToEntity(request);
        //password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        //role set
        user.setRoleType(RoleType.OWNER);

        //dbye kayit
        User savedUser = userRepository.save(user);

        //entity to response
        return ResponseMessage.<String>builder()
                .message(SuccessMessages.USER_REGISTERED_MESSAGE)
                .httpStatus(HttpStatus.CREATED)
                .build();




    }

    //update password
    public ResponseMessage<String> changePassword(UpdatePasswordRequest request, HttpServletRequest httpServletRequest) {
        String email = (String) httpServletRequest.getAttribute("email");
        User foundUser = methodHelper.findUserByEmail(email);
        if (!passwordEncoder.matches(request.getOldPassword(),foundUser.getPassword())){
            throw new BadRequestException(ErrorMessages.PASSWORD_NOT_MATCHED);
        }
        if (Boolean.TRUE.equals(foundUser.getBuiltIn())){
            throw new BadRequestException(ErrorMessages.NOT_PERMITTED_METHOD_MESSAGE);
        }
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        foundUser.setPassword(hashedPassword);
        userRepository.save(foundUser);

        return ResponseMessage.<String>builder()
                .message(SuccessMessages.PASSWORD_CHANGED_SUCCESSFULLY)
                .httpStatus(HttpStatus.CREATED)
                .build();
    }
}

package com.petverse.controller.user;

import com.petverse.payload.ResponseMessage;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.payload.messages.SuccessMessages;
import com.petverse.payload.request.authentication.LoginRequest;
import com.petverse.payload.request.user.UpdatePasswordRequest;
import com.petverse.payload.request.user.UserRequest;
import com.petverse.payload.response.authentication.AuthResponse;
import com.petverse.service.user.AuthService;
import com.petverse.service.user.PasswordResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetTokenService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }


    @PostMapping("/register")
    public ResponseMessage<String> registerParent(@RequestBody UserRequest request) {
        return authService.register(request);
    }

    @PatchMapping("/change-password")
    public ResponseMessage<String> changePassword(@RequestBody @Valid UpdatePasswordRequest request, HttpServletRequest httpServletRequest) {
        return authService.changePassword(request,httpServletRequest);
    }

    // 1. Şifremi Unuttum formu
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody String email) {
        try {
            passwordResetService.createPasswordResetToken(email);
            return ResponseEntity.ok("Şifre sıfırlama linki e-postanıza gönderildi.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> showResetForm(@RequestParam String token) {
        if (passwordResetService.validateToken(token)) {
            return ResponseEntity.ok(SuccessMessages.VALIDATE_TOKEN_MESSAGE);
        } else {
            return ResponseEntity.badRequest().body(ErrorMessages.INVALID_TOKEN_MESSAGE);
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestParam String token,
            @RequestBody String newPassword ) {

        try {
            passwordResetService.resetPassword(token, newPassword);
            return ResponseEntity.ok(SuccessMessages.PASSWORD_CHANGED_SUCCESSFULLY);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}








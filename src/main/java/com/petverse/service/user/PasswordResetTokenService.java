package com.petverse.service.user;

import com.petverse.entity.concretes.user.PasswordResetTokenEntity;
import com.petverse.entity.concretes.user.User;
import com.petverse.payload.messages.ErrorMessages;
import com.petverse.repository.user.PasswordResetTokenRepository;
import com.petverse.repository.user.UserRepository;
import com.petverse.service.helper.MethodHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final MethodHelper methodHelper;

    // 1. Kullanıcı e-posta girer → token oluştur, mail gönder
    public void createPasswordResetToken(String email) {
        User user = methodHelper.findUserByEmail(email);

        // Eski token varsa sil
        tokenRepository.deleteByUserId(user.getId());

        // Yeni token oluştur
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15);

        PasswordResetTokenEntity resetToken = new PasswordResetTokenEntity();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(expiry);

        tokenRepository.save(resetToken);

        // Mail gönder
        emailService.sendPasswordResetLink(email, token);
    }

    // 2. Token geçerli mi?
    public boolean validateToken(String token) {
        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token);
        return resetToken != null && !resetToken.isExpired();
    }

    // 3. Yeni şifreyi kaydet (User entity üzerinden)
    public void resetPassword(String token, String newPassword) {
        if (!validateToken(token)) {
            throw new RuntimeException(ErrorMessages.TOKEN_TIME_OUT_MESSAGE);
        }

        PasswordResetTokenEntity resetToken = tokenRepository.findByToken(token);
        User user = resetToken.getUser();

        // Şifreyi hash'le ve User entity'sine kaydet
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user); // Bu satır, veritabanındaki şifreyi günceller

        // Token'ı kullanıldıktan sonra sil
        tokenRepository.delete(resetToken);
    }

}
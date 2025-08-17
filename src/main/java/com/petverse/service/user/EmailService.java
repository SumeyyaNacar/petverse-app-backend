package com.petverse.service.user;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.petverse.payload.messages.ErrorMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetLink(String email, String token) {
        String resetUrl = "http://localhost:8080/auth/reset-password?token=" + token;
        String subject = "Petverse - Reset Password";
        String body = """
            <p>Hello,</p>
            <p>Please enter the following link to reset password</p>
            <a href="%s" target="_blank">Reset Password</a>
            <p>Link will be active for 15 minutes</p>
            <p>Have a good day</p>
            """.formatted(resetUrl);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("petversepetshop@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(ErrorMessages.MAIL_SEND_MESSAGE, e);
        }
    }
}
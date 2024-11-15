package com.thantuan.backend.service;

import com.thantuan.backend.entity.Token;
import com.thantuan.backend.entity.User;
import com.thantuan.backend.repository.ITokenRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final ITokenRepo tokenRepo;

    @Async
    public void sendEmail(String to, String verificationCode, String subject)
            throws MessagingException {
        Context context = new Context();
        context.setVariable("code", verificationCode);

        String htmlContent = templateEngine.process("email-template", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF_8.name());

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendValidationEmail(User user, String subject) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        this.sendEmail(user.getEmail(), newToken, subject);
    }

    private String generateAndSaveActivationToken(User user) {
        tokenRepo.deleteByUserId(user.getId());
        String generatedToken = generateActivationCode();
        var token = Token.builder()
                .token(generatedToken)
                .expiresAt(LocalDateTime.now()
                        .plusMinutes(1))
                .user(user)
                .build();
        tokenRepo.save(token);
        return generatedToken;
    }

    private String generateActivationCode() {
        String characters = "0123456789";
        int length = 6;
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            codeBuilder.append(characters.charAt(secureRandom.nextInt(characters.length())));
        }
        return codeBuilder.toString();
    }
}

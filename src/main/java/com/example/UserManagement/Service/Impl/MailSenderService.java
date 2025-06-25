package com.example.UserManagement.Service.Impl;

import com.example.UserManagement.Entity.User;
import com.example.UserManagement.Service.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderService {
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Value("${spring.application.name}")
    private String webName;
    @Value("${spring.mail.username}")
    private String senderAddress;//tienthinh28112004@gmail.com


    public void sendEmailUser(User user) throws MessagingException, MailException {
        try {
            String otp = generateOtp();
            user.setOtp(otp);
            redisService.save(otp, user.getEmail(),5, TimeUnit.MINUTES);
            String htmlContent = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head><meta charset=\"UTF-8\"/><title>Email Verification</title></head>" +
                    "<body style=\"font-family: Arial, sans-serif; text-align: center;\">" +
                    "<h2 style=\"color: #333;\">Xác minh tài khoản của bạn</h2>" +
                    "<p>Cảm ơn bạn đã đăng ký! Dưới đây là mã xác minh (OTP) của bạn:</p>" +
                    "<h3 style=\"font-size: 24px; color: #007bff;\">" + otp + "</h3>" +
                    "<p>Vui lòng nhập mã này trên trang web của chúng tôi để hoàn tất xác minh.</p>" +
                    "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>" +
                    "<hr style=\"margin: 20px 0;\">" +
                    "<p style=\"font-size: 12px; color: #666;\">© 2024 " + webName + ". Mọi quyền được bảo lưu.</p>" +
                    "</body>" +
                    "</html>";
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
            mimeMessageHelper.setFrom(senderAddress);
            mimeMessageHelper.setTo(user.getEmail());
            mimeMessageHelper.setSubject("Verify Email Your:");
            mimeMessageHelper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (RuntimeException e) {
            log.error("xác minh email thất bại");
        }
    }
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Mã OTP 6 chữ số
        return String.valueOf(otp);
    }
}

package org.example.radicalmotor.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendMail(String to, String resetLink) {
        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Password Reset Request");

            String htmlMsg = "<p>Dear User,</p>"
                    + "<p>You have requested to reset your password. Click the link below to reset your password:</p>"
                    + "<p><a href='" + resetLink + "'>Reset Password</a></p>"
                    + "<p>If you did not request a password reset, please ignore this email.</p>"
                    + "<p>Thank you,<br/>RadicalMotor Team</p>";

            helper.setText(htmlMsg, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}

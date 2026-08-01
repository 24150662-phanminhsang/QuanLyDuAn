package service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import util.EmailTemplate;
import util.MailConfig;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class EmailService {

    private static final int OTP_EXPIRATION_MINUTES = 10;

    public void sendVerificationOtp(
            String recipientEmail,
            String fullName,
            String otp
    ) throws MessagingException {

        validateEmail(recipientEmail);
        validateOtp(otp);
        ensureConfigured();

        String subject =
                "Mã xác nhận tài khoản CourseManager";

        String htmlContent =
                EmailTemplate.buildOtpTemplate(
                        fullName,
                        otp,
                        OTP_EXPIRATION_MINUTES
                );

        sendHtmlEmail(
                recipientEmail,
                subject,
                htmlContent
        );
    }

    public void sendHtmlEmail(
            String recipientEmail,
            String subject,
            String htmlContent
    ) throws MessagingException {

        validateEmail(recipientEmail);
        ensureConfigured();

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException(
                    "Tiêu đề email không được để trống."
            );
        }

        if (htmlContent == null
                || htmlContent.isBlank()) {

            throw new IllegalArgumentException(
                    "Nội dung email không được để trống."
            );
        }

        Session session =
                MailConfig.createSession();

        MimeMessage message =
                new MimeMessage(session);

        try {
            message.setFrom(
                    new InternetAddress(
                            MailConfig.getSenderEmail(),
                            MailConfig.getSenderName(),
                            StandardCharsets.UTF_8.name()
                    )
            );

        } catch (UnsupportedEncodingException exception) {
            throw new MessagingException(
                    "Không thể thiết lập người gửi.",
                    exception
            );
        }

        message.setRecipient(
                Message.RecipientType.TO,
                new InternetAddress(
                        recipientEmail.trim()
                )
        );

        message.setSubject(
                subject.trim(),
                StandardCharsets.UTF_8.name()
        );

        message.setContent(
                htmlContent,
                "text/html; charset=UTF-8"
        );

        message.setSentDate(new Date());

        /*
         * Kết nối rõ username/password để dễ phát hiện lỗi.
         */
        try (Transport transport =
                     session.getTransport("smtp")) {

            transport.connect(
                    MailConfig.getUsername(),
                    MailConfig.getPassword()
            );

            transport.sendMessage(
                    message,
                    message.getAllRecipients()
            );
        }
    }

    public boolean testConnection() {
        if (!MailConfig.isConfigured()) {
            System.err.println(
                    "SMTP chưa được cấu hình bằng email "
                            + "và App Password thật."
            );

            return false;
        }

        try {
            Session session =
                    MailConfig.createSession();

            try (Transport transport =
                         session.getTransport("smtp")) {

                transport.connect(
                        MailConfig.getUsername(),
                        MailConfig.getPassword()
                );

                System.out.println(
                        "Kết nối Gmail SMTP thành công."
                );

                return transport.isConnected();
            }

        } catch (MessagingException exception) {
            System.err.println(
                    "Kết nối Gmail SMTP thất bại."
            );

            exception.printStackTrace();
            return false;
        }
    }

    private void ensureConfigured() {
        if (!MailConfig.isConfigured()) {
            throw new IllegalStateException(
                    "SMTP chưa cấu hình đầy đủ. "
                            + "Hãy nhập Gmail thật và App Password "
                            + "trong config.properties."
            );
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email người nhận không được để trống."
            );
        }

        if (!email.trim().matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "Email người nhận không hợp lệ."
            );
        }
    }

    private void validateOtp(String otp) {
        if (otp == null
                || !otp.matches("\\d{6}")) {

            throw new IllegalArgumentException(
                    "OTP phải gồm đúng 6 chữ số."
            );
        }
    }
}
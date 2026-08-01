package util;

import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

import java.util.Properties;

public final class MailConfig {

    private MailConfig() {
    }

    public static Session createSession() {
        String host = require("mail.host");
        String port = require("mail.port");
        String username = require("mail.username");
        String appPassword = require("mail.password");

        boolean auth = getBoolean(
                "mail.auth",
                true
        );

        boolean startTls = getBoolean(
                "mail.starttls",
                false
        );

        boolean ssl = getBoolean(
                "mail.ssl",
                true
        );

        boolean debug = getBoolean(
                "mail.debug",
                false
        );

        Properties properties =
                new Properties();

        properties.put(
                "mail.smtp.host",
                host
        );

        properties.put(
                "mail.smtp.port",
                port
        );

        properties.put(
                "mail.smtp.auth",
                String.valueOf(auth)
        );

        /*
         * Cổng 465 sử dụng SSL trực tiếp.
         */
        properties.put(
                "mail.smtp.ssl.enable",
                String.valueOf(ssl)
        );

        /*
         * Chỉ bật STARTTLS khi dùng cổng 587.
         */
        properties.put(
                "mail.smtp.starttls.enable",
                String.valueOf(startTls)
        );

        properties.put(
                "mail.smtp.starttls.required",
                String.valueOf(startTls)
        );

        /*
         * Ép trust smtp.gmail.com để tránh lỗi chứng chỉ
         * trong môi trường demo.
         */
        properties.put(
                "mail.smtp.ssl.trust",
                host
        );

        properties.put(
                "mail.smtp.connectiontimeout",
                "30000"
        );

        properties.put(
                "mail.smtp.timeout",
                "30000"
        );

        properties.put(
                "mail.smtp.writetimeout",
                "30000"
        );

        Session session =
                Session.getInstance(
                        properties,
                        new Authenticator() {
                            @Override
                            protected PasswordAuthentication
                            getPasswordAuthentication() {

                                return new PasswordAuthentication(
                                        username,
                                        appPassword
                                );
                            }
                        }
                );

        session.setDebug(debug);

        return session;
    }

    public static String getUsername() {
        return require(
                "mail.username"
        );
    }

    public static String getPassword() {
        return require(
                "mail.password"
        );
    }

    public static String getSenderEmail() {
        String sender =
                ConfigLoader.get(
                        "mail.from"
                );

        if (sender == null
                || sender.isBlank()) {

            return getUsername();
        }

        return sender.trim();
    }

    public static String getSenderName() {
        String senderName =
                ConfigLoader.get(
                        "mail.fromName"
                );

        if (senderName == null
                || senderName.isBlank()) {

            return "Course Management System";
        }

        return senderName.trim();
    }

    public static boolean isConfigured() {
        String host =
                ConfigLoader.get(
                        "mail.host"
                );

        String port =
                ConfigLoader.get(
                        "mail.port"
                );

        String username =
                ConfigLoader.get(
                        "mail.username"
                );

        String password =
                ConfigLoader.get(
                        "mail.password"
                );

        return host != null
                && !host.isBlank()
                && port != null
                && !port.isBlank()
                && username != null
                && username.contains("@")
                && !username.contains(
                "EMAIL_GUI_THAT"
        )
                && !username.contains(
                "your_email"
        )
                && password != null
                && !password.isBlank()
                && !password.contains(
                "APP_PASSWORD"
        )
                && !password.contains(
                "YOUR_APP_PASSWORD"
        );
    }

    private static String require(
            String key
    ) {
        String value =
                ConfigLoader.get(key);

        if (value == null
                || value.isBlank()) {

            throw new IllegalStateException(
                    "Thiếu cấu hình email: "
                            + key
            );
        }

        return value.trim();
    }

    private static boolean getBoolean(
            String key,
            boolean defaultValue
    ) {
        String value =
                ConfigLoader.get(key);

        if (value == null
                || value.isBlank()) {

            return defaultValue;
        }

        return Boolean.parseBoolean(
                value.trim()
        );
    }
}
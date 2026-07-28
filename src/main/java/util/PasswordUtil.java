package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được để trống."
            );
        }

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    password.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder();

            for (byte item : hash) {
                result.append(
                        String.format("%02X", item)
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Không thể mã hóa mật khẩu.",
                    exception
            );
        }
    }

    public static boolean matches(
            String rawPassword,
            String storedHash
    ) {
        if (storedHash == null) {
            return false;
        }

        return hashPassword(rawPassword)
                .equalsIgnoreCase(storedHash);
    }
}
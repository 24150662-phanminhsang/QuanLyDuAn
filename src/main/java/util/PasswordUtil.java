package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtil {

    private static final String HASH_ALGORITHM =
            "SHA-256";

    private PasswordUtil() {
    }

    /**
     * Băm mật khẩu bằng SHA-256.
     *
     * Kết quả là chuỗi hexadecimal 64 ký tự,
     * tương thích với HASHBYTES('SHA2_256', ...)
     * trong dữ liệu mẫu SQL Server.
     */
    public static String hashPassword(
            String rawPassword
    ) {
        if (rawPassword == null) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được null."
            );
        }

        try {
            MessageDigest messageDigest =
                    MessageDigest.getInstance(
                            HASH_ALGORITHM
                    );

            byte[] hashedBytes =
                    messageDigest.digest(
                            rawPassword.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder result =
                    new StringBuilder(
                            hashedBytes.length * 2
                    );

            for (byte hashedByte : hashedBytes) {
                result.append(
                        String.format(
                                "%02X",
                                hashedByte & 0xFF
                        )
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "Không thể sử dụng SHA-256.",
                    exception
            );
        }
    }

    public static boolean matches(
            String rawPassword,
            String storedPasswordHash
    ) {
        if (
                rawPassword == null
                        || storedPasswordHash == null
        ) {
            return false;
        }

        String inputHash =
                hashPassword(rawPassword);

        return inputHash.equalsIgnoreCase(
                storedPasswordHash.trim()
        );
    }
}
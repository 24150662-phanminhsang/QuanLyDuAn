package util;

import java.security.SecureRandom;

public final class OtpUtil {

    private static final SecureRandom RANDOM =
            new SecureRandom();

    private static final int OTP_LENGTH = 6;

    private OtpUtil() {
    }

    /**
     * Sinh OTP gồm 6 chữ số.
     *
     * Ví dụ:
     * 028491
     * 735102
     */
    public static String generateOtp() {
        StringBuilder otp =
                new StringBuilder(OTP_LENGTH);

        for (int index = 0;
             index < OTP_LENGTH;
             index++) {

            otp.append(
                    RANDOM.nextInt(10)
            );
        }

        return otp.toString();
    }

    /**
     * Kiểm tra định dạng OTP.
     */
    public static boolean isValidFormat(
            String otp
    ) {
        return otp != null
                && otp.matches("\\d{6}");
    }
}
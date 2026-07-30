package util;

import java.util.regex.Pattern;

public final class Validation {

    private Validation() {
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "^[0-9]{9,11}$"
            );

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    public static boolean isValidEmail(String email) {

        if (isBlank(email)) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPhone(String phone) {

        if (isBlank(phone)) {
            return false;
        }

        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    public static boolean isValidPassword(String password) {

        return password != null
                && password.length() >= 6;
    }

    public static boolean isValidUsername(String username) {

        if (isBlank(username)) {
            return false;
        }

        return username.trim().length() >= 4;
    }
}
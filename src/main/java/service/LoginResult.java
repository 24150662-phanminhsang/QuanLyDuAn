package service;

import model.User;

public record LoginResult(
        boolean successful,
        String message,
        User user
) {

    public static LoginResult success(
            User user
    ) {
        return new LoginResult(
                true,
                "Đăng nhập thành công.",
                user
        );
    }

    public static LoginResult failure(
            String message
    ) {
        return new LoginResult(
                false,
                message,
                null
        );
    }
}
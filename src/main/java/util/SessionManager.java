package util;

import model.Role;
import model.User;

public final class SessionManager {

    private static User currentUser;

    private SessionManager() {
    }

    public static void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "User đăng nhập không được null."
            );
        }

        currentUser = user;
    }

    public static void logout() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getCurrentUserId() {
        ensureLoggedIn();
        return currentUser.getUserId();
    }

    public static String getUsername() {
        ensureLoggedIn();
        return currentUser.getUsername();
    }

    public static String getFullName() {
        ensureLoggedIn();
        return currentUser.getFullName();
    }

    public static Role getRole() {
        ensureLoggedIn();
        return currentUser.getRole();
    }

    public static boolean hasRole(Role role) {
        return isLoggedIn()
                && currentUser.getRole() == role;
    }

    private static void ensureLoggedIn() {
        if (!isLoggedIn()) {
            throw new IllegalStateException(
                    "Chưa có người dùng đăng nhập."
            );
        }
    }
}
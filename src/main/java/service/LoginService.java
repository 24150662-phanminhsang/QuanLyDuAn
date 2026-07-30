package service;

import dao.UserDAO;
import model.AccountStatus;
import model.User;
import util.PasswordUtil;
import util.SessionManager;

import java.sql.SQLException;

public class LoginService {

    private final UserDAO userDAO;

    public LoginService() {
        this.userDAO = new UserDAO();
    }

    public LoginResult login(
            String username,
            String password
    ) throws SQLException {

        String normalizedUsername =
                username == null
                        ? ""
                        : username.trim();

        if (normalizedUsername.isBlank()) {
            return LoginResult.failure(
                    "Tên đăng nhập không được để trống."
            );
        }

        if (password == null || password.isBlank()) {
            return LoginResult.failure(
                    "Mật khẩu không được để trống."
            );
        }

        User user =
                userDAO.findByUsername(
                        normalizedUsername
                );

        if (user == null) {
            return LoginResult.failure(
                    "Tên đăng nhập hoặc mật khẩu không đúng."
            );
        }

        if (user.getStatus() == AccountStatus.LOCKED) {
            return LoginResult.failure(
                    "Tài khoản đã bị khóa."
            );
        }

        if (user.getStatus() == AccountStatus.INACTIVE) {
            return LoginResult.failure(
                    "Tài khoản đã ngừng hoạt động."
            );
        }

        if (!PasswordUtil.matches(
                password,
                user.getPasswordHash()
        )) {
            return LoginResult.failure(
                    "Tên đăng nhập hoặc mật khẩu không đúng."
            );
        }

        SessionManager.login(user);

        return LoginResult.success(user);
    }

    public void logout() {
        SessionManager.logout();
    }
}
package controller;

import service.LoginResult;
import service.LoginService;

import java.sql.SQLException;

public class LoginController {

    private final LoginService loginService;

    public LoginController() {
        loginService = new LoginService();
    }

    public LoginResult login(
            String username,
            String password
    ) throws SQLException {

        return loginService.login(
                username,
                password
        );
    }

    public void logout() {
        loginService.logout();
    }
}
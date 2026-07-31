package controller;

import dao.UserDAO;
import model.AccountStatus;
import model.Role;
import model.User;
import view.AdminDashboardView;
import view.LoginView;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginController {

    private final LoginView loginView;
    private final UserDAO userDAO;

    public LoginController(LoginView loginView) {
        this.loginView = loginView;
        this.userDAO = new UserDAO();

        registerEvents();
    }

    private void registerEvents() {
        loginView.getBtnLogin().addActionListener(
                event -> login()
        );

        loginView.getBtnExit().addActionListener(
                event -> exitApplication()
        );
    }

    private void login() {
        String username = loginView.getUsername();
        char[] passwordChars = loginView.getPassword();
        String password = new String(passwordChars);

        try {
            if (username.isBlank()) {
                showWarning("Vui lòng nhập tên đăng nhập.");
                loginView.getTxtUsername().requestFocusInWindow();
                return;
            }

            if (password.isBlank()) {
                showWarning("Vui lòng nhập mật khẩu.");
                loginView.getTxtPassword().requestFocusInWindow();
                return;
            }

            setLoginLoading(true);

            User user = userDAO.findByUsername(username);

            if (user == null) {
                showLoginFailed();
                return;
            }

            if (!util.PasswordUtil.matches(
                    password,
                    user.getPasswordHash()
            )) {
                showLoginFailed();
                return;
            }

            if (user.getStatus() != AccountStatus.ACTIVE) {
                showAccountStatusMessage(user.getStatus());
                return;
            }

            openDashboard(user);

        } catch (SQLException exception) {
            exception.printStackTrace();

            JOptionPane.showMessageDialog(
                    loginView,
                    "Không thể kết nối đến cơ sở dữ liệu.\n"
                            + "Vui lòng kiểm tra SQL Server và DBConnection.\n\n"
                            + exception.getMessage(),
                    "Lỗi cơ sở dữ liệu",
                    JOptionPane.ERROR_MESSAGE
            );
        } finally {
            Arrays.fill(passwordChars, '\0');
            setLoginLoading(false);
        }
    }

    private void openDashboard(User user) {
        Role role = user.getRole();

        if (role == null) {
            JOptionPane.showMessageDialog(
                    loginView,
                    "Tài khoản chưa được gán vai trò.",
                    "Không thể đăng nhập",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        switch (role) {
            case ADMIN -> openAdminDashboard(user);

            case TEACHER -> JOptionPane.showMessageDialog(
                    loginView,
                    "Đăng nhập thành công với vai trò TEACHER.\n"
                            + "Giao diện giáo viên chưa được hoàn thiện.",
                    "Đăng nhập thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            case STUDENT -> JOptionPane.showMessageDialog(
                    loginView,
                    "Đăng nhập thành công với vai trò STUDENT.\n"
                            + "Giao diện sinh viên chưa được hoàn thiện.",
                    "Đăng nhập thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            default -> JOptionPane.showMessageDialog(
                    loginView,
                    "Vai trò tài khoản không được hỗ trợ.",
                    "Không thể đăng nhập",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void openAdminDashboard(User user) {
        JOptionPane.showMessageDialog(
                loginView,
                "Xin chào, " + user.getFullName() + "!",
                "Đăng nhập thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        loginView.dispose();

        SwingUtilities.invokeLater(() -> {
            AdminDashboardView dashboard = new AdminDashboardView();

            dashboard.setLocationRelativeTo(null);
            dashboard.setVisible(true);
        });
    }

    private void showLoginFailed() {
        JOptionPane.showMessageDialog(
                loginView,
                "Tên đăng nhập hoặc mật khẩu không chính xác.",
                "Đăng nhập thất bại",
                JOptionPane.WARNING_MESSAGE
        );

        loginView.clearPassword();
    }

    private void showAccountStatusMessage(
            AccountStatus status
    ) {
        String message;

        if (status == AccountStatus.LOCKED) {
            message = "Tài khoản đã bị khóa.";
        } else if (status == AccountStatus.INACTIVE) {
            message = "Tài khoản đã ngừng hoạt động.";
        } else {
            message = "Tài khoản không thể đăng nhập.";
        }

        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Không thể đăng nhập",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void setLoginLoading(boolean loading) {
        loginView.getBtnLogin().setEnabled(!loading);
        loginView.getBtnExit().setEnabled(!loading);
        loginView.getTxtUsername().setEnabled(!loading);
        loginView.getTxtPassword().setEnabled(!loading);

        loginView.getBtnLogin().setText(
                loading ? "Đang đăng nhập..." : "Đăng nhập"
        );

        loginView.setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
                loginView,
                "Bạn có chắc muốn thoát chương trình?",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            loginView.dispose();
            System.exit(0);
        }
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Thiếu thông tin",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
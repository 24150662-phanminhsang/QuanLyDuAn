package controller;

import dao.UserDAO;
import model.AccountStatus;
import model.Role;
import model.User;
import util.PasswordUtil;
import util.SessionManager;
import view.AdminDashboardView;
import view.LoginView;
import view.StudentMainDashboard;
import view.TeacherMainDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginController {

    private final LoginView loginView;
    private final UserDAO userDAO;

    public LoginController(LoginView loginView) {
        if (loginView == null) {
            throw new IllegalArgumentException(
                    "LoginView không được null."
            );
        }

        this.loginView = loginView;
        this.userDAO = new UserDAO();

        registerEvents();
    }

    /* =====================================================
       ĐĂNG KÝ SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        loginView.getBtnLogin().addActionListener(
                event -> login()
        );

        loginView.getBtnExit().addActionListener(
                event -> exitApplication()
        );
    }

    /* =====================================================
       ĐĂNG NHẬP
       ===================================================== */

    private void login() {
        String username =
                loginView.getUsername() == null
                        ? ""
                        : loginView.getUsername().trim();

        char[] passwordChars =
                loginView.getPassword();

        if (passwordChars == null) {
            passwordChars = new char[0];
        }

        String password =
                new String(passwordChars);

        try {
            if (username.isBlank()) {
                showWarning(
                        "Vui lòng nhập tên đăng nhập."
                );

                loginView
                        .getTxtUsername()
                        .requestFocusInWindow();

                return;
            }

            if (password.isBlank()) {
                showWarning(
                        "Vui lòng nhập mật khẩu."
                );

                loginView
                        .getTxtPassword()
                        .requestFocusInWindow();

                return;
            }

            setLoginLoading(true);

            User user =
                    userDAO.findByUsername(
                            username
                    );

            if (user == null) {
                showLoginFailed();
                return;
            }

            boolean passwordMatches =
                    PasswordUtil.matches(
                            password,
                            user.getPasswordHash()
                    );

            if (!passwordMatches) {
                showLoginFailed();
                return;
            }

            if (user.getStatus()
                    != AccountStatus.ACTIVE) {

                showAccountStatusMessage(
                        user.getStatus()
                );

                return;
            }

            if (user.getRole() == null) {
                JOptionPane.showMessageDialog(
                        loginView,
                        "Tài khoản chưa được gán vai trò.",
                        "Không thể đăng nhập",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            SessionManager.login(user);

            openDashboard(user);

        } catch (SQLException exception) {
            exception.printStackTrace();

            JOptionPane.showMessageDialog(
                    loginView,
                    "Không thể kết nối đến cơ sở dữ liệu.\n"
                            + "Vui lòng kiểm tra SQL Server "
                            + "và DBConnection.\n\n"
                            + getRootErrorMessage(exception),
                    "Lỗi cơ sở dữ liệu",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            JOptionPane.showMessageDialog(
                    loginView,
                    "Đã xảy ra lỗi khi đăng nhập.\n"
                            + getRootErrorMessage(exception),
                    "Lỗi hệ thống",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {
            Arrays.fill(
                    passwordChars,
                    '\0'
            );

            setLoginLoading(false);
        }
    }

    /* =====================================================
       PHÂN QUYỀN
       ===================================================== */

    private void openDashboard(
            User user
    ) {
        Role role =
                user.getRole();

        switch (role) {
            case ADMIN ->
                    openAdminDashboard(user);

            case TEACHER ->
                    openTeacherDashboard(user);

            case STUDENT ->
                    openStudentDashboard(user);

            default ->
                    JOptionPane.showMessageDialog(
                            loginView,
                            "Vai trò tài khoản "
                                    + "không được hỗ trợ.",
                            "Không thể đăng nhập",
                            JOptionPane.WARNING_MESSAGE
                    );
        }
    }

    /* =====================================================
       MỞ ADMIN DASHBOARD
       ===================================================== */

    private void openAdminDashboard(
            User user
    ) {
        showLoginSuccess(user);

        loginView.dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                AdminDashboardView dashboard =
                        new AdminDashboardView();

                dashboard.setLocationRelativeTo(null);
                dashboard.setVisible(true);

            } catch (RuntimeException exception) {
                exception.printStackTrace();

                handleDashboardOpenError(
                        "Không thể mở giao diện quản trị viên.",
                        exception
                );
            }
        });
    }

    /* =====================================================
       MỞ TEACHER DASHBOARD
       ===================================================== */

    private void openTeacherDashboard(
            User user
    ) {
        showLoginSuccess(user);

        loginView.dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                TeacherMainDashboard dashboard =
                        new TeacherMainDashboard();

                JFrame frame =
                        createDashboardFrame(
                                "Hệ thống quản lý khóa học - Giảng viên",
                                dashboard
                        );

                frame.setVisible(true);

            } catch (RuntimeException exception) {
                exception.printStackTrace();

                handleDashboardOpenError(
                        "Không thể mở giao diện giảng viên.",
                        exception
                );
            }
        });
    }

    /* =====================================================
       MỞ STUDENT DASHBOARD
       ===================================================== */

    private void openStudentDashboard(
            User user
    ) {
        showLoginSuccess(user);

        loginView.dispose();

        SwingUtilities.invokeLater(() -> {
            try {
                StudentMainDashboard dashboard =
                        new StudentMainDashboard();

                JFrame frame =
                        createDashboardFrame(
                                "Hệ thống quản lý khóa học - Sinh viên",
                                dashboard
                        );

                frame.setVisible(true);

            } catch (RuntimeException exception) {
                exception.printStackTrace();

                handleDashboardOpenError(
                        "Không thể mở giao diện sinh viên.",
                        exception
                );
            }
        });
    }

    /* =====================================================
       TẠO FRAME CHUNG
       ===================================================== */

    private JFrame createDashboardFrame(
            String title,
            JPanel dashboard
    ) {
        JFrame frame =
                new JFrame(title);

        frame.setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE
        );

        frame.setContentPane(
                dashboard
        );

        frame.setMinimumSize(
                new Dimension(
                        1100,
                        680
                )
        );

        frame.setSize(
                1360,
                800
        );

        frame.setLocationRelativeTo(null);

        return frame;
    }

    /* =====================================================
       XỬ LÝ LỖI MỞ DASHBOARD
       ===================================================== */

    private void handleDashboardOpenError(
            String message,
            Throwable throwable
    ) {
        SessionManager.logout();

        JOptionPane.showMessageDialog(
                null,
                message
                        + "\n"
                        + getRootErrorMessage(
                        throwable
                ),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );

        showNewLoginView();
    }

    private void showNewLoginView() {
        LoginView newLoginView =
                new LoginView();

        /*
         * Bắt buộc tạo controller mới,
         * nếu không nút Đăng nhập sẽ không hoạt động.
         */
        new LoginController(
                newLoginView
        );

        newLoginView.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        newLoginView.setLocationRelativeTo(null);
        newLoginView.setVisible(true);
    }

    /* =====================================================
       THÔNG BÁO
       ===================================================== */

    private void showLoginSuccess(
            User user
    ) {
        String fullName =
                user.getFullName();

        if (fullName == null
                || fullName.isBlank()) {

            fullName = user.getUsername();
        }

        JOptionPane.showMessageDialog(
                loginView,
                "Xin chào, "
                        + fullName
                        + "!",
                "Đăng nhập thành công",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showLoginFailed() {
        JOptionPane.showMessageDialog(
                loginView,
                "Tên đăng nhập hoặc mật khẩu "
                        + "không chính xác.",
                "Đăng nhập thất bại",
                JOptionPane.WARNING_MESSAGE
        );

        loginView.clearPassword();

        loginView
                .getTxtPassword()
                .requestFocusInWindow();
    }

    private void showAccountStatusMessage(
            AccountStatus status
    ) {
        String message;

        if (status == AccountStatus.LOCKED) {
            message =
                    "Tài khoản đã bị khóa.";

        } else if (
                status == AccountStatus.INACTIVE
        ) {
            message =
                    "Tài khoản đã ngừng hoạt động.";

        } else {
            message =
                    "Tài khoản không thể đăng nhập.";
        }

        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Không thể đăng nhập",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showWarning(
            String message
    ) {
        JOptionPane.showMessageDialog(
                loginView,
                message,
                "Thiếu thông tin",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /* =====================================================
       TRẠNG THÁI LOADING
       ===================================================== */

    private void setLoginLoading(
            boolean loading
    ) {
        loginView
                .getBtnLogin()
                .setEnabled(!loading);

        loginView
                .getBtnExit()
                .setEnabled(!loading);

        loginView
                .getTxtUsername()
                .setEnabled(!loading);

        loginView
                .getTxtPassword()
                .setEnabled(!loading);

        loginView
                .getBtnLogin()
                .setText(
                        loading
                                ? "Đang đăng nhập..."
                                : "Đăng nhập"
                );

        loginView.setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    /* =====================================================
       THOÁT ỨNG DỤNG
       ===================================================== */

    private void exitApplication() {
        int result =
                JOptionPane.showConfirmDialog(
                        loginView,
                        "Bạn có chắc muốn thoát chương trình?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (result
                == JOptionPane.YES_OPTION) {

            loginView.dispose();
            System.exit(0);
        }
    }

    /* =====================================================
       LẤY LỖI GỐC
       ===================================================== */

    private String getRootErrorMessage(
            Throwable throwable
    ) {
        if (throwable == null) {
            return "Không xác định";
        }

        Throwable current =
                throwable;

        while (current.getCause() != null) {
            current =
                    current.getCause();
        }

        if (current.getMessage() != null
                && !current.getMessage().isBlank()) {

            return current.getMessage();
        }

        return current
                .getClass()
                .getSimpleName();
    }
}
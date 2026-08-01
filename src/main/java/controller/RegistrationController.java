package controller;

import model.RegistrationResult;
import model.Role;
import model.Student;
import model.Teacher;
import service.EmailVerificationService;
import service.RegistrationService;
import view.EmailVerificationDialog;
import view.RegisterView;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;

public class RegistrationController {

    private final RegisterView registerView;

    private final RegistrationService registrationService;
    private final EmailVerificationService verificationService;

    private Runnable backAction;

    public RegistrationController(
            RegisterView registerView
    ) {
        if (registerView == null) {
            throw new IllegalArgumentException(
                    "RegisterView không được null."
            );
        }

        this.registerView = registerView;

        this.registrationService =
                new RegistrationService();

        this.verificationService =
                new EmailVerificationService();

        this.backAction = () ->
                registerView.dispose();

        registerEvents();
    }

    private void registerEvents() {
        registerView
                .getBtnRegister()
                .addActionListener(
                        event -> handleRegistration()
                );

        registerView
                .getBtnBack()
                .addActionListener(
                        event -> handleBack()
                );
    }

    public void setBackAction(
            Runnable backAction
    ) {
        this.backAction =
                backAction == null
                        ? () -> registerView.dispose()
                        : backAction;
    }

    private void handleRegistration() {
        char[] passwordChars =
                registerView.getPassword();

        char[] confirmPasswordChars =
                registerView.getConfirmPassword();

        try {
            registerView.clearStatusMessage();

            String password =
                    new String(passwordChars);

            String confirmPassword =
                    new String(
                            confirmPasswordChars
                    );

            RegistrationResult result;

            if (registerView.getSelectedRole()
                    == Role.TEACHER) {

                Teacher teacher =
                        buildTeacherFromView();

                result =
                        registrationService
                                .registerTeacher(
                                        registerView
                                                .getUsername(),
                                        password,
                                        confirmPassword,
                                        teacher
                                );

            } else {
                Student student =
                        buildStudentFromView();

                result =
                        registrationService
                                .registerStudent(
                                        registerView
                                                .getUsername(),
                                        password,
                                        confirmPassword,
                                        student
                                );
            }

            if (!result.isSuccess()) {
                showError(
                        result.getMessage()
                );

                return;
            }

            registerView.setStatusMessage(
                    result.getMessage(),
                    true
            );

            openVerificationDialog(result);

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (SQLException exception) {
            exception.printStackTrace();

            showError(
                    buildDatabaseErrorMessage(
                            exception
                    )
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            showError(
                    "Đã xảy ra lỗi trong quá trình đăng ký.\n"
                            + exception.getMessage()
            );

        } finally {
            Arrays.fill(
                    passwordChars,
                    '\0'
            );

            Arrays.fill(
                    confirmPasswordChars,
                    '\0'
            );

            registerView.setLoading(false);
        }
    }

    private Student buildStudentFromView() {
        Student student = new Student();

        student.setStudentCode(
                requireText(
                        registerView.getProfileCode(),
                        "Mã sinh viên"
                )
        );

        student.setFullName(
                requireText(
                        registerView.getFullName(),
                        "Họ và tên"
                )
        );

        student.setDateOfBirth(
                parseOptionalDate(
                        registerView.getDateOfBirth()
                )
        );

        student.setGender(
                registerView.getGender()
        );

        student.setEmail(
                requireText(
                        registerView.getEmail(),
                        "Email"
                )
        );

        student.setPhone(
                normalizeOptional(
                        registerView.getPhone()
                )
        );

        student.setAddress(
                normalizeOptional(
                        registerView.getAddress()
                )
        );

        student.setStatus("ACTIVE");

        return student;
    }

    private Teacher buildTeacherFromView() {
        Teacher teacher = new Teacher();

        teacher.setTeacherCode(
                requireText(
                        registerView.getProfileCode(),
                        "Mã giảng viên"
                )
        );

        teacher.setFullName(
                requireText(
                        registerView.getFullName(),
                        "Họ và tên"
                )
        );

        teacher.setDateOfBirth(
                parseOptionalDate(
                        registerView.getDateOfBirth()
                )
        );

        teacher.setGender(
                registerView.getGender()
        );

        teacher.setEmail(
                requireText(
                        registerView.getEmail(),
                        "Email"
                )
        );

        teacher.setPhone(
                normalizeOptional(
                        registerView.getPhone()
                )
        );

        teacher.setAddress(
                normalizeOptional(
                        registerView.getAddress()
                )
        );

        teacher.setSpecialization(
                requireText(
                        registerView.getSpecialization(),
                        "Chuyên môn"
                )
        );

        teacher.setStatus("ACTIVE");

        return teacher;
    }

    private void openVerificationDialog(
            RegistrationResult result
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        registerView
                );

        EmailVerificationDialog dialog =
                new EmailVerificationDialog(
                        owner,
                        result.getEmail()
                );

        dialog.getBtnVerify().addActionListener(
                event -> verifyOtp(
                        dialog,
                        result
                )
        );

        /*
         * Chức năng gửi lại OTP sẽ được nối ở bước sau.
         * Hiện nút được giữ lại nhưng tạm thông báo.
         */
        dialog.getBtnResend().addActionListener(
                event ->
                        JOptionPane.showMessageDialog(
                                dialog,
                                "Chức năng gửi lại OTP "
                                        + "sẽ được hoàn thiện ở bước tiếp theo.",
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE
                        )
        );

        dialog.getBtnCancel().addActionListener(
                event -> dialog.dispose()
        );

        dialog.setVisible(true);
    }

    private void verifyOtp(
            EmailVerificationDialog dialog,
            RegistrationResult registrationResult
    ) {
        try {
            dialog.setMessage(" ", false);

            dialog.setLoading(true);

            EmailVerificationService
                    .VerificationResult result =
                    verificationService
                            .verifyEmailOtp(
                                    registrationResult
                                            .getUserId(),
                                    dialog.getOtp()
                            );

            if (!result.isSuccess()) {
                dialog.setMessage(
                        result.getMessage(),
                        false
                );

                dialog.clearOtp();
                return;
            }

            dialog.setMessage(
                    result.getMessage(),
                    true
            );

            JOptionPane.showMessageDialog(
                    dialog,
                    result.getMessage(),
                    "Xác minh thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dialog.dispose();
            registerView.dispose();

            if (backAction != null) {
                backAction.run();
            }

        } catch (IllegalArgumentException exception) {
            dialog.setMessage(
                    exception.getMessage(),
                    false
            );

            dialog.clearOtp();

        } catch (SQLException exception) {
            exception.printStackTrace();

            dialog.setMessage(
                    "Không thể xác minh OTP do lỗi cơ sở dữ liệu.",
                    false
            );

        } finally {
            dialog.setLoading(false);
        }
    }

    private Date parseOptionalDate(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return Date.valueOf(
                    value.trim()
            );

        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Ngày sinh phải đúng định dạng yyyy-MM-dd."
            );
        }
    }

    private String requireText(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        return value.trim();
    }

    private String normalizeOptional(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String buildDatabaseErrorMessage(
            SQLException exception
    ) {
        String message =
                exception.getMessage();

        if (message == null) {
            return "Không thể kết nối hoặc cập nhật cơ sở dữ liệu.";
        }

        String lowerMessage =
                message.toLowerCase();

        if (lowerMessage.contains("username")) {
            return "Tên đăng nhập đã tồn tại.";
        }

        if (lowerMessage.contains("email")) {
            return "Email đã được sử dụng.";
        }

        if (lowerMessage.contains("student_code")) {
            return "Mã sinh viên đã tồn tại.";
        }

        if (lowerMessage.contains("teacher_code")) {
            return "Mã giảng viên đã tồn tại.";
        }

        if (lowerMessage.contains(
                "foreign key"
        )) {
            return "Dữ liệu liên kết không hợp lệ.";
        }

        return "Không thể hoàn thành đăng ký.\n"
                + "Chi tiết: "
                + message;
    }

    private void handleBack() {
        registerView.dispose();

        if (backAction != null) {
            backAction.run();
        }
    }

    private void showWarning(
            String message
    ) {
        registerView.setStatusMessage(
                message,
                false
        );

        JOptionPane.showMessageDialog(
                registerView,
                message,
                "Thông tin chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(
            String message
    ) {
        registerView.setStatusMessage(
                message,
                false
        );

        JOptionPane.showMessageDialog(
                registerView,
                message,
                "Đăng ký thất bại",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
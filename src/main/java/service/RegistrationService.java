package service;

import dao.EmailVerificationDAO;
import dao.StudentDAO;
import dao.TeacherDAO;
import dao.UserDAO;
import dao.impl.TeacherDAOImpl;
import jakarta.mail.MessagingException;
import model.AccountStatus;
import model.EmailVerification;
import model.RegistrationResult;
import model.Role;
import model.Student;
import model.Teacher;
import model.User;
import util.DBConnection;
import util.OtpUtil;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RegistrationService {

    private static final String EMAIL_VERIFICATION =
            "EMAIL_VERIFICATION";

    private static final int OTP_EXPIRATION_MINUTES =
            10;

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;
    private final EmailVerificationDAO verificationDAO;
    private final EmailService emailService;

    public RegistrationService() {
        this.userDAO = new UserDAO();
        this.studentDAO = new StudentDAO();
        this.teacherDAO = new TeacherDAOImpl();
        this.verificationDAO =
                new EmailVerificationDAO();
        this.emailService =
                new EmailService();
    }

    /* =====================================================
       ĐĂNG KÝ SINH VIÊN
       ===================================================== */

    public RegistrationResult registerStudent(
            String username,
            String password,
            String confirmPassword,
            Student student
    ) throws SQLException {

        validateAccountInformation(
                username,
                password,
                confirmPassword
        );

        validateStudentInformation(student);

        String normalizedUsername =
                username.trim();

        String normalizedEmail =
                normalizeRequiredEmail(
                        student.getEmail()
                );

        validateDuplicateAccount(
                normalizedUsername,
                normalizedEmail
        );

        if (studentDAO.existsByStudentCode(
                student.getStudentCode()
        )) {
            throw new IllegalArgumentException(
                    "Mã sinh viên đã tồn tại."
            );
        }

        User user = createPendingUser(
                normalizedUsername,
                password,
                student.getFullName(),
                normalizedEmail,
                student.getPhone(),
                Role.STUDENT
        );

        String rawOtp =
                OtpUtil.generateOtp();

        int userId;
        int studentId;

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                userId = userDAO.insert(
                        connection,
                        user
                );

                student.setUserId(userId);
                student.setEmail(normalizedEmail);
                student.setStatus("ACTIVE");

                studentId = studentDAO.insert(
                        connection,
                        student
                );

                createVerification(
                        connection,
                        userId,
                        rawOtp
                );

                connection.commit();

            } catch (Exception exception) {
                rollbackQuietly(connection);

                throw convertToSQLException(
                        exception,
                        "Không thể đăng ký tài khoản sinh viên."
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }

        EmailSendResult emailResult =
                sendOtpAfterCommit(
                        normalizedEmail,
                        student.getFullName(),
                        rawOtp
                );

        String message;

        if (emailResult.success()) {
            message =
                    "Đăng ký sinh viên thành công. "
                            + "Mã OTP đã được gửi đến email "
                            + maskEmail(normalizedEmail)
                            + ".";
        } else {
            message =
                    "Đăng ký sinh viên thành công, "
                            + "nhưng chưa thể gửi email OTP. "
                            + "Vui lòng kiểm tra cấu hình SMTP "
                            + "hoặc yêu cầu gửi lại mã.";
        }

        return RegistrationResult.success(
                userId,
                studentId,
                Role.STUDENT,
                AccountStatus.PENDING_EMAIL,
                normalizedEmail,
                rawOtp,
                message
        );
    }

    /* =====================================================
       ĐĂNG KÝ GIẢNG VIÊN
       ===================================================== */

    public RegistrationResult registerTeacher(
            String username,
            String password,
            String confirmPassword,
            Teacher teacher
    ) throws SQLException {

        validateAccountInformation(
                username,
                password,
                confirmPassword
        );

        validateTeacherInformation(teacher);

        String normalizedUsername =
                username.trim();

        String normalizedEmail =
                normalizeRequiredEmail(
                        teacher.getEmail()
                );

        validateDuplicateAccount(
                normalizedUsername,
                normalizedEmail
        );

        if (teacherDAO.existsByTeacherCode(
                teacher.getTeacherCode()
        )) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã tồn tại."
            );
        }

        User user = createPendingUser(
                normalizedUsername,
                password,
                teacher.getFullName(),
                normalizedEmail,
                teacher.getPhone(),
                Role.TEACHER
        );

        String rawOtp =
                OtpUtil.generateOtp();

        int userId;
        int teacherId;

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                userId = userDAO.insert(
                        connection,
                        user
                );

                teacher.setUserId(userId);
                teacher.setEmail(normalizedEmail);
                teacher.setStatus("ACTIVE");

                teacherId = teacherDAO.insert(
                        connection,
                        teacher
                );

                createVerification(
                        connection,
                        userId,
                        rawOtp
                );

                connection.commit();

            } catch (Exception exception) {
                rollbackQuietly(connection);

                throw convertToSQLException(
                        exception,
                        "Không thể đăng ký tài khoản giảng viên."
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }

        EmailSendResult emailResult =
                sendOtpAfterCommit(
                        normalizedEmail,
                        teacher.getFullName(),
                        rawOtp
                );

        String message;

        if (emailResult.success()) {
            message =
                    "Đăng ký giảng viên thành công. "
                            + "Mã OTP đã được gửi đến email "
                            + maskEmail(normalizedEmail)
                            + ". Sau khi xác minh email, "
                            + "tài khoản sẽ chờ Admin xét duyệt.";
        } else {
            message =
                    "Đăng ký giảng viên thành công, "
                            + "nhưng chưa thể gửi email OTP. "
                            + "Vui lòng kiểm tra cấu hình SMTP "
                            + "hoặc yêu cầu gửi lại mã.";
        }

        return RegistrationResult.success(
                userId,
                teacherId,
                Role.TEACHER,
                AccountStatus.PENDING_EMAIL,
                normalizedEmail,
                rawOtp,
                message
        );
    }

    /* =====================================================
       TẠO USER
       ===================================================== */

    private User createPendingUser(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            Role role
    ) {
        User user = new User();

        user.setUsername(
                username.trim()
        );

        user.setPasswordHash(
                PasswordUtil.hashPassword(
                        password
                )
        );

        user.setFullName(
                fullName.trim()
        );

        user.setEmail(email);

        user.setPhone(
                normalizeNullable(phone)
        );

        user.setRole(role);

        user.setStatus(
                AccountStatus.PENDING_EMAIL
        );

        user.setEmailVerified(false);
        user.setEmailVerifiedAt(null);

        user.setRegistrationSource(
                "SELF_REGISTER"
        );

        user.setLoginAttempts(0);
        user.setLockedUntil(null);

        return user;
    }

    /* =====================================================
       TẠO OTP TRONG DATABASE
       ===================================================== */

    private void createVerification(
            Connection connection,
            int userId,
            String rawOtp
    ) throws SQLException {

        verificationDAO.invalidateActiveCodes(
                connection,
                userId,
                EMAIL_VERIFICATION
        );

        EmailVerification verification =
                new EmailVerification();

        verification.setUserId(userId);

        verification.setOtpHash(
                PasswordUtil.hashPassword(
                        rawOtp
                )
        );

        verification.setPurpose(
                EMAIL_VERIFICATION
        );

        verification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(
                                OTP_EXPIRATION_MINUTES
                        )
        );

        verification.setAttemptCount(0);
        verification.setResendCount(0);

        verificationDAO.insert(
                connection,
                verification
        );
    }

    /* =====================================================
       GỬI EMAIL SAU KHI DATABASE COMMIT
       ===================================================== */

    private EmailSendResult sendOtpAfterCommit(
            String email,
            String fullName,
            String rawOtp
    ) {
        try {
            emailService.sendVerificationOtp(
                    email,
                    fullName,
                    rawOtp
            );

            return new EmailSendResult(
                    true,
                    null
            );

        } catch (MessagingException
                 | IllegalStateException
                 | IllegalArgumentException exception) {

            System.err.println(
                    "Không thể gửi email OTP đến "
                            + maskEmail(email)
                            + ": "
                            + exception.getMessage()
            );

            return new EmailSendResult(
                    false,
                    exception.getMessage()
            );
        }
    }

    /* =====================================================
       VALIDATE
       ===================================================== */

    private void validateAccountInformation(
            String username,
            String password,
            String confirmPassword
    ) {
        if (username == null
                || username.isBlank()) {

            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống."
            );
        }

        String normalizedUsername =
                username.trim();

        if (normalizedUsername.length() < 4) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập phải có ít nhất 4 ký tự."
            );
        }

        if (!normalizedUsername.matches(
                "[A-Za-z0-9._-]+"
        )) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập chỉ được chứa chữ, "
                            + "số, dấu chấm, gạch dưới "
                            + "và gạch ngang."
            );
        }

        if (password == null
                || password.length() < 6) {

            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );
        }

        if (confirmPassword == null
                || !password.equals(confirmPassword)) {

            throw new IllegalArgumentException(
                    "Mật khẩu xác nhận không khớp."
            );
        }
    }

    private void validateStudentInformation(
            Student student
    ) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Thông tin sinh viên không được null."
            );
        }

        if (student.getStudentCode() == null
                || student.getStudentCode().isBlank()) {

            throw new IllegalArgumentException(
                    "Mã sinh viên không được để trống."
            );
        }

        if (student.getFullName() == null
                || student.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                    "Họ tên sinh viên không được để trống."
            );
        }
    }

    private void validateTeacherInformation(
            Teacher teacher
    ) {
        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Thông tin giảng viên không được null."
            );
        }

        if (teacher.getTeacherCode() == null
                || teacher.getTeacherCode().isBlank()) {

            throw new IllegalArgumentException(
                    "Mã giảng viên không được để trống."
            );
        }

        if (teacher.getFullName() == null
                || teacher.getFullName().isBlank()) {

            throw new IllegalArgumentException(
                    "Họ tên giảng viên không được để trống."
            );
        }

        if (teacher.getSpecialization() == null
                || teacher.getSpecialization().isBlank()) {

            throw new IllegalArgumentException(
                    "Chuyên môn giảng viên "
                            + "không được để trống."
            );
        }
    }

    private void validateDuplicateAccount(
            String username,
            String email
    ) throws SQLException {

        if (userDAO.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập đã tồn tại."
            );
        }

        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng."
            );
        }
    }

    private String normalizeRequiredEmail(
            String email
    ) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email không được để trống."
            );
        }

        String normalized =
                email.trim().toLowerCase();

        if (!normalized.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        )) {
            throw new IllegalArgumentException(
                    "Email không đúng định dạng."
            );
        }

        return normalized;
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    /* =====================================================
       TRANSACTION HELPERS
       ===================================================== */

    private SQLException convertToSQLException(
            Exception exception,
            String defaultMessage
    ) {
        if (exception instanceof SQLException sqlException) {
            return sqlException;
        }

        if (exception
                instanceof IllegalArgumentException
                illegalArgumentException) {

            throw illegalArgumentException;
        }

        return new SQLException(
                defaultMessage,
                exception
        );
    }

    private void rollbackQuietly(
            Connection connection
    ) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();

        } catch (SQLException exception) {
            System.err.println(
                    "Không thể rollback transaction: "
                            + exception.getMessage()
            );
        }
    }

    private void restoreAutoCommit(
            Connection connection,
            boolean originalAutoCommit
    ) {
        if (connection == null) {
            return;
        }

        try {
            connection.setAutoCommit(
                    originalAutoCommit
            );

        } catch (SQLException exception) {
            System.err.println(
                    "Không thể khôi phục AutoCommit: "
                            + exception.getMessage()
            );
        }
    }

    /* =====================================================
       BẢO VỆ EMAIL KHI HIỂN THỊ
       ===================================================== */

    private String maskEmail(
            String email
    ) {
        if (email == null || !email.contains("@")) {
            return "***";
        }

        String[] parts =
                email.split("@", 2);

        String localPart =
                parts[0];

        String domain =
                parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0)
                    + "***@"
                    + domain;
        }

        return localPart.substring(0, 2)
                + "***@"
                + domain;
    }

    private record EmailSendResult(
            boolean success,
            String errorMessage
    ) {
    }
}
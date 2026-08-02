package service;

import dao.EmailVerificationDAO;
import dao.UserDAO;
import model.AccountStatus;
import model.EmailVerification;
import model.Role;
import model.User;
import util.DBConnection;
import util.OtpUtil;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

public class EmailVerificationService {

    private static final String EMAIL_VERIFICATION =
            "EMAIL_VERIFICATION";

    private static final int MAX_ATTEMPTS = 5;
    private static final int MAX_RESENDS = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int OTP_EXPIRATION_MINUTES = 10;

    private static final SecureRandom SECURE_RANDOM =
            new SecureRandom();

    private final EmailVerificationDAO verificationDAO;
    private final UserDAO userDAO;
    private final EmailService emailService;

    public EmailVerificationService() {
        this.verificationDAO =
                new EmailVerificationDAO();

        this.userDAO =
                new UserDAO();

        this.emailService =
                new EmailService();
    }

    /**
     * Gửi lại OTP xác minh email.
     *
     * - Khóa gửi lại trong 60 giây.
     * - Tối đa 5 lần gửi lại.
     * - OTP cũ bị vô hiệu hóa.
     * - OTP mới có hiệu lực 10 phút.
     */
    public ResendResult resendVerificationOtp(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        User user =
                userDAO.findById(userId);

        if (user == null) {
            return ResendResult.failure(
                    "Không tìm thấy tài khoản."
            );
        }

        if (user.isEmailVerified()) {
            return ResendResult.failure(
                    "Email của tài khoản đã được xác minh."
            );
        }

        if (user.getStatus()
                != AccountStatus.PENDING_EMAIL) {
            return ResendResult.failure(
                    "Tài khoản không ở trạng thái chờ xác minh email."
            );
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {
            return ResendResult.failure(
                    "Tài khoản chưa có địa chỉ email."
            );
        }

        EmailVerification latest =
                verificationDAO.findLatestByUserId(
                        userId,
                        EMAIL_VERIFICATION
                );

        int nextResendCount = 1;

        if (latest != null) {
            nextResendCount =
                    latest.getResendCount() + 1;

            if (nextResendCount > MAX_RESENDS) {
                return ResendResult.failure(
                        "Bạn đã gửi lại OTP quá "
                                + MAX_RESENDS
                                + " lần. Vui lòng liên hệ quản trị viên."
                );
            }

            if (latest.getCreatedAt() != null) {
                long elapsedSeconds =
                        Duration.between(
                                latest.getCreatedAt(),
                                LocalDateTime.now()
                        ).getSeconds();

                if (elapsedSeconds
                        < RESEND_COOLDOWN_SECONDS) {
                    int remainingSeconds =
                            (int) (
                                    RESEND_COOLDOWN_SECONDS
                                            - elapsedSeconds
                            );

                    return ResendResult.cooldown(
                            remainingSeconds,
                            "Vui lòng chờ "
                                    + remainingSeconds
                                    + " giây trước khi gửi lại OTP."
                    );
                }
            }
        }

        String rawOtp = generateOtp();

        EmailVerification newVerification =
                new EmailVerification();

        newVerification.setUserId(userId);
        newVerification.setOtpHash(
                PasswordUtil.hashPassword(rawOtp)
        );
        newVerification.setPurpose(
                EMAIL_VERIFICATION
        );
        newVerification.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(
                                OTP_EXPIRATION_MINUTES
                        )
        );
        newVerification.setAttemptCount(0);
        newVerification.setResendCount(
                nextResendCount
        );

        long verificationId;

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                verificationDAO.invalidateActiveCodes(
                        connection,
                        userId,
                        EMAIL_VERIFICATION
                );

                verificationId =
                        verificationDAO.insert(
                                connection,
                                newVerification
                        );

                connection.commit();

            } catch (Exception exception) {
                rollbackQuietly(connection);

                if (exception
                        instanceof SQLException sqlException) {
                    throw sqlException;
                }

                throw new SQLException(
                        "Không thể tạo OTP mới.",
                        exception
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }

        try {
            emailService.sendVerificationOtp(
                    user.getEmail(),
                    user.getFullName(),
                    rawOtp
            );

        } catch (Exception exception) {
            verificationDAO.invalidateById(
                    verificationId
            );

            return ResendResult.failure(
                    "Không thể gửi OTP đến email. Chi tiết: "
                            + rootMessage(exception)
            );
        }

        return ResendResult.success(
                RESEND_COOLDOWN_SECONDS,
                "Mã OTP mới đã được gửi đến email "
                        + maskEmail(user.getEmail())
                        + ". Mã có hiệu lực trong "
                        + OTP_EXPIRATION_MINUTES
                        + " phút."
        );
    }

    /**
     * Tên rút gọn để tương thích với View/Controller.
     */
    public ResendResult resendOtp(
            int userId
    ) throws SQLException {
        return resendVerificationOtp(userId);
    }

    /**
     * Xác nhận OTP email.
     *
     * Sinh viên:
     * PENDING_EMAIL -> ACTIVE
     *
     * Giảng viên:
     * PENDING_EMAIL -> PENDING_APPROVAL
     */
    public VerificationResult verifyEmailOtp(
            int userId,
            String rawOtp
    ) throws SQLException {

        validateUserId(userId);
        validateOtp(rawOtp);

        User user =
                userDAO.findById(userId);

        if (user == null) {
            return VerificationResult.failure(
                    "Không tìm thấy tài khoản."
            );
        }

        if (user.isEmailVerified()) {
            return VerificationResult.failure(
                    "Email của tài khoản đã được xác minh."
            );
        }

        if (user.getStatus()
                != AccountStatus.PENDING_EMAIL) {

            return VerificationResult.failure(
                    "Tài khoản không ở trạng thái "
                            + "chờ xác minh email."
            );
        }

        EmailVerification verification =
                verificationDAO
                        .findLatestActiveByUserId(
                                userId,
                                EMAIL_VERIFICATION
                        );

        if (verification == null) {
            return VerificationResult.failure(
                    "Không tìm thấy mã OTP còn hiệu lực. "
                            + "Vui lòng yêu cầu gửi lại mã."
            );
        }

        if (verification.isInvalidated()) {
            return VerificationResult.failure(
                    "Mã OTP đã bị vô hiệu hóa."
            );
        }

        if (verification.isVerified()) {
            return VerificationResult.failure(
                    "Mã OTP đã được sử dụng."
            );
        }

        if (verification.isExpired()) {
            verificationDAO.invalidateById(
                    verification.getVerificationId()
            );

            return VerificationResult.failure(
                    "Mã OTP đã hết hạn."
            );
        }

        if (verification.getAttemptCount()
                >= MAX_ATTEMPTS) {

            verificationDAO.invalidateById(
                    verification.getVerificationId()
            );

            return VerificationResult.failure(
                    "Bạn đã nhập sai OTP quá "
                            + MAX_ATTEMPTS
                            + " lần. Vui lòng yêu cầu mã mới."
            );
        }

        boolean otpMatches =
                PasswordUtil.matches(
                        rawOtp.trim(),
                        verification.getOtpHash()
                );

        if (!otpMatches) {
            verificationDAO.incrementAttemptCount(
                    verification.getVerificationId()
            );

            int remainingAttempts =
                    MAX_ATTEMPTS
                            - verification.getAttemptCount()
                            - 1;

            if (remainingAttempts <= 0) {
                verificationDAO.invalidateById(
                        verification.getVerificationId()
                );

                return VerificationResult.failure(
                        "Mã OTP không chính xác. "
                                + "Mã đã bị vô hiệu hóa "
                                + "do nhập sai quá số lần cho phép."
                );
            }

            return VerificationResult.failure(
                    "Mã OTP không chính xác. "
                            + "Bạn còn "
                            + remainingAttempts
                            + " lần thử."
            );
        }

        AccountStatus nextStatus =
                determineStatusAfterVerification(user);

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                boolean otpUpdated =
                        verificationDAO.markVerified(
                                connection,
                                verification.getVerificationId()
                        );

                if (!otpUpdated) {
                    throw new SQLException(
                            "Không thể đánh dấu OTP đã xác nhận."
                    );
                }

                boolean userUpdated =
                        updateVerifiedUser(
                                connection,
                                userId,
                                nextStatus
                        );

                if (!userUpdated) {
                    throw new SQLException(
                            "Không thể cập nhật trạng thái tài khoản."
                    );
                }

                connection.commit();

                String message =
                        buildSuccessMessage(
                                user.getRole(),
                                nextStatus
                        );

                return VerificationResult.success(
                        userId,
                        nextStatus,
                        message
                );

            } catch (Exception exception) {
                rollbackQuietly(connection);

                if (exception instanceof SQLException sqlException) {
                    throw sqlException;
                }

                throw new SQLException(
                        "Không thể xác minh email.",
                        exception
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }
    }

    /**
     * Kiểm tra tài khoản đã xác minh email chưa.
     */
    public boolean isEmailVerified(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        User user =
                userDAO.findById(userId);

        return user != null
                && user.isEmailVerified();
    }

    /**
     * Kiểm tra người dùng có OTP đang hoạt động không.
     */
    public boolean hasActiveOtp(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        return verificationDAO.hasActiveCode(
                userId,
                EMAIL_VERIFICATION
        );
    }

    /**
     * Sau xác minh:
     *
     * STUDENT -> ACTIVE
     * TEACHER -> PENDING_APPROVAL
     */
    private AccountStatus determineStatusAfterVerification(
            User user
    ) {
        if (user.getRole() == Role.STUDENT) {
            return AccountStatus.ACTIVE;
        }

        if (user.getRole() == Role.TEACHER) {
            return AccountStatus.PENDING_APPROVAL;
        }

        throw new IllegalArgumentException(
                "Vai trò không hỗ trợ tự xác minh email: "
                        + user.getRole()
        );
    }

    /**
     * Cập nhật email_verified và status trong cùng transaction.
     */
    private boolean updateVerifiedUser(
            Connection connection,
            int userId,
            AccountStatus nextStatus
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users
                SET
                    email_verified = 1,
                    email_verified_at = SYSDATETIME(),
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                  AND status = 'PENDING_EMAIL'
                  AND email_verified = 0
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    nextStatus.name()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate() > 0;
        }
    }

    private String buildSuccessMessage(
            Role role,
            AccountStatus status
    ) {
        if (role == Role.STUDENT
                && status == AccountStatus.ACTIVE) {

            return "Xác minh email thành công. "
                    + "Tài khoản sinh viên đã được kích hoạt.";
        }

        if (role == Role.TEACHER
                && status
                == AccountStatus.PENDING_APPROVAL) {

            return "Xác minh email thành công. "
                    + "Tài khoản giảng viên đang chờ "
                    + "Admin xét duyệt.";
        }

        return "Xác minh email thành công.";
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID không hợp lệ."
            );
        }
    }

    private void validateOtp(String otp) {
        if (otp == null || otp.isBlank()) {
            throw new IllegalArgumentException(
                    "Mã OTP không được để trống."
            );
        }

        if (!OtpUtil.isValidFormat(otp.trim())) {
            throw new IllegalArgumentException(
                    "Mã OTP phải gồm đúng 6 chữ số."
            );
        }
    }

    private String generateOtp() {
        int value =
                SECURE_RANDOM.nextInt(1_000_000);

        return String.format("%06d", value);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "email đã đăng ký";
        }

        String[] parts = email.trim().split("@", 2);
        String local = parts[0];
        String domain = parts[1];

        String visible =
                local.length() <= 2
                        ? local.substring(0, 1)
                        : local.substring(0, 2);

        return visible + "***@" + domain;
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;

        while (current != null
                && current.getCause() != null) {
            current = current.getCause();
        }

        return current == null
                || current.getMessage() == null
                || current.getMessage().isBlank()
                ? "Không xác định"
                : current.getMessage();
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
                    "Không thể rollback transaction OTP: "
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

    /**
     * Kết quả gửi lại OTP.
     */
    public static final class ResendResult {

        private final boolean success;
        private final int cooldownSeconds;
        private final String message;

        private ResendResult(
                boolean success,
                int cooldownSeconds,
                String message
        ) {
            this.success = success;
            this.cooldownSeconds = cooldownSeconds;
            this.message = message;
        }

        public static ResendResult success(
                int cooldownSeconds,
                String message
        ) {
            return new ResendResult(
                    true,
                    cooldownSeconds,
                    message
            );
        }

        public static ResendResult failure(
                String message
        ) {
            return new ResendResult(
                    false,
                    0,
                    message
            );
        }

        public static ResendResult cooldown(
                int cooldownSeconds,
                String message
        ) {
            return new ResendResult(
                    false,
                    cooldownSeconds,
                    message
            );
        }

        public boolean isSuccess() {
            return success;
        }

        public int getCooldownSeconds() {
            return cooldownSeconds;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Kết quả xác minh OTP.
     */
    public static final class VerificationResult {

        private final boolean success;
        private final int userId;
        private final AccountStatus status;
        private final String message;

        private VerificationResult(
                boolean success,
                int userId,
                AccountStatus status,
                String message
        ) {
            this.success = success;
            this.userId = userId;
            this.status = status;
            this.message = message;
        }

        public static VerificationResult success(
                int userId,
                AccountStatus status,
                String message
        ) {
            return new VerificationResult(
                    true,
                    userId,
                    status,
                    message
            );
        }

        public static VerificationResult failure(
                String message
        ) {
            return new VerificationResult(
                    false,
                    0,
                    null,
                    message
            );
        }

        public boolean isSuccess() {
            return success;
        }

        public int getUserId() {
            return userId;
        }

        public AccountStatus getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
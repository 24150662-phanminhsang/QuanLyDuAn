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

public class EmailVerificationService {

    private static final String EMAIL_VERIFICATION =
            "EMAIL_VERIFICATION";

    private static final int MAX_ATTEMPTS = 5;

    private final EmailVerificationDAO verificationDAO;
    private final UserDAO userDAO;

    public EmailVerificationService() {
        this.verificationDAO =
                new EmailVerificationDAO();

        this.userDAO =
                new UserDAO();
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
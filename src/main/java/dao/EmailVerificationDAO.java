package dao;

import model.EmailVerification;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EmailVerificationDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                verification_id,
                user_id,
                otp_hash,
                purpose,
                expires_at,
                verified_at,
                invalidated_at,
                attempt_count,
                resend_count,
                created_at
            FROM dbo.EmailVerifications
            """;

    /**
     * Thêm mã OTP bằng kết nối riêng.
     */
    public long insert(
            EmailVerification verification
    ) throws SQLException {

        try (Connection connection =
                     DBConnection.getConnection()) {

            return insert(
                    connection,
                    verification
            );
        }
    }

    /**
     * Thêm mã OTP bằng Connection có sẵn.
     *
     * Phương thức này sẽ được RegistrationService dùng
     * trong cùng transaction với Users và Students/Teachers.
     */
    public long insert(
            Connection connection,
            EmailVerification verification
    ) throws SQLException {

        validateConnection(connection);
        validateVerificationForInsert(verification);

        String sql =
                """
                INSERT INTO dbo.EmailVerifications
                (
                    user_id,
                    otp_hash,
                    purpose,
                    expires_at,
                    attempt_count,
                    resend_count,
                    created_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, SYSDATETIME()
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             java.sql.Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setInt(
                    1,
                    verification.getUserId()
            );

            statement.setString(
                    2,
                    verification.getOtpHash()
            );

            statement.setString(
                    3,
                    normalizePurpose(
                            verification.getPurpose()
                    )
            );

            statement.setTimestamp(
                    4,
                    Timestamp.valueOf(
                            verification.getExpiresAt()
                    )
            );

            statement.setInt(
                    5,
                    Math.max(
                            0,
                            verification.getAttemptCount()
                    )
            );

            statement.setInt(
                    6,
                    Math.max(
                            0,
                            verification.getResendCount()
                    )
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo mã xác nhận email."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    long verificationId =
                            generatedKeys.getLong(1);

                    verification.setVerificationId(
                            verificationId
                    );

                    return verificationId;
                }
            }
        }

        throw new SQLException(
                "Không lấy được ID của mã xác nhận."
        );
    }

    /**
     * Lấy OTP theo ID.
     */
    public EmailVerification findById(
            long verificationId
    ) throws SQLException {

        if (verificationId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE verification_id = ?
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    verificationId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapVerification(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Lấy OTP mới nhất của một tài khoản theo mục đích.
     */
    public EmailVerification findLatestByUserId(
            int userId,
            String purpose
    ) throws SQLException {

        if (userId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE user_id = ?
                          AND purpose = ?
                        ORDER BY created_at DESC,
                                 verification_id DESC
                        OFFSET 0 ROWS
                        FETCH NEXT 1 ROWS ONLY
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    normalizePurpose(purpose)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapVerification(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Lấy OTP đang còn hiệu lực mới nhất.
     */
    public EmailVerification findLatestActiveByUserId(
            int userId,
            String purpose
    ) throws SQLException {

        if (userId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE user_id = ?
                          AND purpose = ?
                          AND verified_at IS NULL
                          AND invalidated_at IS NULL
                          AND expires_at > SYSDATETIME()
                        ORDER BY created_at DESC,
                                 verification_id DESC
                        OFFSET 0 ROWS
                        FETCH NEXT 1 ROWS ONLY
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    normalizePurpose(purpose)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapVerification(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Vô hiệu hóa các OTP cũ bằng kết nối riêng.
     */
    public int invalidateActiveCodes(
            int userId,
            String purpose
    ) throws SQLException {

        try (Connection connection =
                     DBConnection.getConnection()) {

            return invalidateActiveCodes(
                    connection,
                    userId,
                    purpose
            );
        }
    }

    /**
     * Vô hiệu hóa các OTP cũ trong transaction hiện tại.
     */
    public int invalidateActiveCodes(
            Connection connection,
            int userId,
            String purpose
    ) throws SQLException {

        validateConnection(connection);
        validateUserId(userId);

        String sql =
                """
                UPDATE dbo.EmailVerifications
                SET invalidated_at = SYSDATETIME()
                WHERE user_id = ?
                  AND purpose = ?
                  AND verified_at IS NULL
                  AND invalidated_at IS NULL
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    normalizePurpose(purpose)
            );

            return statement.executeUpdate();
        }
    }

    /**
     * Tăng số lần nhập OTP sai.
     */
    public boolean incrementAttemptCount(
            long verificationId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.EmailVerifications
                SET attempt_count = attempt_count + 1
                WHERE verification_id = ?
                  AND verified_at IS NULL
                  AND invalidated_at IS NULL
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    verificationId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Tăng số lần gửi lại OTP.
     */
    public boolean incrementResendCount(
            long verificationId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.EmailVerifications
                SET resend_count = resend_count + 1
                WHERE verification_id = ?
                  AND verified_at IS NULL
                  AND invalidated_at IS NULL
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    verificationId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Đánh dấu OTP đã được xác minh.
     */
    public boolean markVerified(
            long verificationId
    ) throws SQLException {

        try (Connection connection =
                     DBConnection.getConnection()) {

            return markVerified(
                    connection,
                    verificationId
            );
        }
    }

    /**
     * Đánh dấu OTP đã xác minh trong transaction.
     */
    public boolean markVerified(
            Connection connection,
            long verificationId
    ) throws SQLException {

        validateConnection(connection);

        if (verificationId <= 0) {
            throw new IllegalArgumentException(
                    "ID mã xác nhận không hợp lệ."
            );
        }

        String sql =
                """
                UPDATE dbo.EmailVerifications
                SET verified_at = SYSDATETIME()
                WHERE verification_id = ?
                  AND verified_at IS NULL
                  AND invalidated_at IS NULL
                  AND expires_at > SYSDATETIME()
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    verificationId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Vô hiệu hóa một mã cụ thể.
     */
    public boolean invalidateById(
            long verificationId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.EmailVerifications
                SET invalidated_at = SYSDATETIME()
                WHERE verification_id = ?
                  AND invalidated_at IS NULL
                  AND verified_at IS NULL
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    verificationId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Xóa các OTP cũ đã hết hạn hoặc đã vô hiệu.
     */
    public int deleteExpiredCodes()
            throws SQLException {

        String sql =
                """
                DELETE FROM dbo.EmailVerifications
                WHERE
                    expires_at < DATEADD(DAY, -7, SYSDATETIME())
                    OR verified_at < DATEADD(DAY, -7, SYSDATETIME())
                    OR invalidated_at < DATEADD(DAY, -7, SYSDATETIME())
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            return statement.executeUpdate();
        }
    }

    /**
     * Kiểm tra người dùng có OTP đang còn hiệu lực hay không.
     */
    public boolean hasActiveCode(
            int userId,
            String purpose
    ) throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.EmailVerifications
                WHERE user_id = ?
                  AND purpose = ?
                  AND verified_at IS NULL
                  AND invalidated_at IS NULL
                  AND expires_at > SYSDATETIME()
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            statement.setString(
                    2,
                    normalizePurpose(purpose)
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }
        }
    }

    private EmailVerification mapVerification(
            ResultSet resultSet
    ) throws SQLException {

        EmailVerification verification =
                new EmailVerification();

        verification.setVerificationId(
                resultSet.getLong(
                        "verification_id"
                )
        );

        verification.setUserId(
                resultSet.getInt(
                        "user_id"
                )
        );

        verification.setOtpHash(
                resultSet.getString(
                        "otp_hash"
                )
        );

        verification.setPurpose(
                resultSet.getString(
                        "purpose"
                )
        );

        Timestamp expiresAt =
                resultSet.getTimestamp(
                        "expires_at"
                );

        if (expiresAt != null) {
            verification.setExpiresAt(
                    expiresAt.toLocalDateTime()
            );
        }

        Timestamp verifiedAt =
                resultSet.getTimestamp(
                        "verified_at"
                );

        if (verifiedAt != null) {
            verification.setVerifiedAt(
                    verifiedAt.toLocalDateTime()
            );
        }

        Timestamp invalidatedAt =
                resultSet.getTimestamp(
                        "invalidated_at"
                );

        if (invalidatedAt != null) {
            verification.setInvalidatedAt(
                    invalidatedAt.toLocalDateTime()
            );
        }

        verification.setAttemptCount(
                resultSet.getInt(
                        "attempt_count"
                )
        );

        verification.setResendCount(
                resultSet.getInt(
                        "resend_count"
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at"
                );

        if (createdAt != null) {
            verification.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return verification;
    }

    private void validateVerificationForInsert(
            EmailVerification verification
    ) {
        if (verification == null) {
            throw new IllegalArgumentException(
                    "Thông tin xác nhận email không được null."
            );
        }

        validateUserId(
                verification.getUserId()
        );

        if (verification.getOtpHash() == null
                || verification.getOtpHash().isBlank()) {

            throw new IllegalArgumentException(
                    "OTP hash không được để trống."
            );
        }

        if (verification.getExpiresAt() == null) {
            throw new IllegalArgumentException(
                    "Thời hạn OTP không được để trống."
            );
        }

        if (verification.getExpiresAt()
                .isBefore(java.time.LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Thời hạn OTP phải ở tương lai."
            );
        }
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID không hợp lệ."
            );
        }
    }

    private void validateConnection(
            Connection connection
    ) throws SQLException {

        if (connection == null
                || connection.isClosed()) {

            throw new SQLException(
                    "Connection không hợp lệ hoặc đã đóng."
            );
        }
    }

    private String normalizePurpose(
            String purpose
    ) {
        if (purpose == null || purpose.isBlank()) {
            return "EMAIL_VERIFICATION";
        }

        String normalized =
                purpose.trim().toUpperCase();

        if (!normalized.equals(
                "EMAIL_VERIFICATION"
        )
                && !normalized.equals(
                "PASSWORD_RESET"
        )) {

            throw new IllegalArgumentException(
                    "Mục đích OTP không hợp lệ: "
                            + purpose
            );
        }

        return normalized;
    }
}
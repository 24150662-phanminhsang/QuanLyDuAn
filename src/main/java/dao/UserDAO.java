package dao;

import model.AccountStatus;
import model.Role;
import model.User;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /*
     * Các cột dùng chung khi truy vấn User.
     * Đã đồng bộ với database V2.
     */
    private static final String USER_COLUMNS =
            """
            SELECT
                u.user_id,
                u.username,
                u.password_hash,
                u.full_name,
                u.email,
                u.phone,

                u.role_id,
                r.role_name,

                u.status,

                u.email_verified,
                u.email_verified_at,
                u.registration_source,

                u.last_login,
                u.login_attempts,
                u.locked_until,

                u.approved_by,
                u.approved_at,

                u.rejected_by,
                u.rejected_at,
                u.rejection_reason,

                u.created_at,
                u.updated_at

            FROM dbo.Users AS u

            INNER JOIN dbo.Roles AS r
                ON u.role_id = r.role_id
            """;

    /* =====================================================
       1. DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    /**
     * Lấy toàn bộ người dùng.
     */
    public List<User> findAll()
            throws SQLException {

        String sql =
                USER_COLUMNS
                        + """
                        ORDER BY u.user_id DESC
                        """;

        List<User> users =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(
                        mapResultSetToUser(resultSet)
                );
            }
        }

        return users;
    }

    /**
     * Lấy danh sách người dùng theo trang.
     */
    public List<User> findAll(
            int page,
            int pageSize
    ) throws SQLException {

        return search(
                "",
                page,
                pageSize
        );
    }

    /**
     * Tìm kiếm người dùng và phân trang.
     */
    public List<User> search(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {

        validatePagination(
                page,
                pageSize
        );

        String sql =
                USER_COLUMNS
                        + """
                        WHERE
                            u.username LIKE ?
                            OR u.full_name LIKE ?
                            OR COALESCE(u.email, '') LIKE ?
                            OR COALESCE(u.phone, '') LIKE ?
                            OR r.role_name LIKE ?
                            OR u.status LIKE ?

                        ORDER BY u.user_id DESC

                        OFFSET ? ROWS
                        FETCH NEXT ? ROWS ONLY
                        """;

        String normalizedKeyword =
                normalizeKeyword(keyword);

        String searchValue =
                "%" + normalizedKeyword + "%";

        int offset =
                (page - 1) * pageSize;

        List<User> users =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            statement.setString(5, searchValue);
            statement.setString(6, searchValue);

            statement.setInt(7, offset);
            statement.setInt(8, pageSize);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    users.add(
                            mapResultSetToUser(
                                    resultSet
                            )
                    );
                }
            }
        }

        return users;
    }

    /**
     * Tên thay thế để tương thích code cũ.
     */
    public List<User> findByKeyword(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {

        return search(
                keyword,
                page,
                pageSize
        );
    }

    /**
     * Đếm toàn bộ người dùng.
     */
    public int countAll()
            throws SQLException {

        return count("");
    }

    /**
     * Đếm người dùng theo từ khóa.
     */
    public int count(String keyword)
            throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users AS u

                INNER JOIN dbo.Roles AS r
                    ON u.role_id = r.role_id

                WHERE
                    u.username LIKE ?
                    OR u.full_name LIKE ?
                    OR COALESCE(u.email, '') LIKE ?
                    OR COALESCE(u.phone, '') LIKE ?
                    OR r.role_name LIKE ?
                    OR u.status LIKE ?
                """;

        String normalizedKeyword =
                normalizeKeyword(keyword);

        String searchValue =
                "%" + normalizedKeyword + "%";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            statement.setString(5, searchValue);
            statement.setString(6, searchValue);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return resultSet.getInt(
                            "total"
                    );
                }
            }
        }

        return 0;
    }

    /**
     * Tên thay thế để tương thích code cũ.
     */
    public int countByKeyword(
            String keyword
    ) throws SQLException {

        return count(keyword);
    }

    /**
     * Tính tổng số trang.
     */
    public int getTotalPages(
            String keyword,
            int pageSize
    ) throws SQLException {

        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "Số dòng trên một trang phải lớn hơn 0."
            );
        }

        int totalRecords =
                count(keyword);

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) totalRecords
                                / pageSize
                )
        );
    }

    /* =====================================================
       2. TÌM USER
       ===================================================== */

    /**
     * Tìm User theo ID.
     */
    public User findById(int userId)
            throws SQLException {

        if (userId <= 0) {
            return null;
        }

        String sql =
                USER_COLUMNS
                        + """
                        WHERE u.user_id = ?
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapResultSetToUser(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Tìm User theo username.
     */
    public User findByUsername(
            String username
    ) throws SQLException {

        if (
                username == null
                        || username.isBlank()
        ) {
            return null;
        }

        String sql =
                USER_COLUMNS
                        + """
                        WHERE LOWER(u.username)
                            = LOWER(?)
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    username.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapResultSetToUser(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Tìm User theo email.
     */
    public User findByEmail(
            String email
    ) throws SQLException {

        if (
                email == null
                        || email.isBlank()
        ) {
            return null;
        }

        String sql =
                USER_COLUMNS
                        + """
                        WHERE LOWER(u.email)
                            = LOWER(?)
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    email.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapResultSetToUser(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /* =====================================================
       3. KIỂM TRA TRÙNG DỮ LIỆU
       ===================================================== */

    /**
     * Kiểm tra username đã tồn tại.
     */
    public boolean existsByUsername(
            String username
    ) throws SQLException {

        if (
                username == null
                        || username.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users

                WHERE LOWER(username)
                    = LOWER(?)
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    username.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    /**
     * Kiểm tra username thuộc tài khoản khác.
     */
    public boolean existsByUsernameExceptId(
            String username,
            int excludedUserId
    ) throws SQLException {

        if (
                username == null
                        || username.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users

                WHERE LOWER(username)
                    = LOWER(?)

                  AND user_id <> ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    username.trim()
            );

            statement.setInt(
                    2,
                    excludedUserId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    /**
     * Kiểm tra email đã tồn tại.
     */
    public boolean existsByEmail(
            String email
    ) throws SQLException {

        if (
                email == null
                        || email.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users

                WHERE LOWER(email)
                    = LOWER(?)
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    email.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    /**
     * Kiểm tra email thuộc tài khoản khác.
     */
    public boolean existsByEmailExceptId(
            String email,
            int excludedUserId
    ) throws SQLException {

        if (
                email == null
                        || email.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users

                WHERE LOWER(email)
                    = LOWER(?)

                  AND user_id <> ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    email.trim()
            );

            statement.setInt(
                    2,
                    excludedUserId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    /* =====================================================
       4. THÊM USER
       ===================================================== */

    /**
     * Thêm User bằng Connection riêng.
     */
    public boolean insert(User user)
            throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            return insert(
                    connection,
                    user
            ) > 0;
        }
    }

    /**
     * Thêm User trong transaction hiện tại.
     *
     * Dùng cho:
     *
     * Users + Students/Teachers
     * + EmailVerifications.
     *
     * @return user_id vừa tạo.
     */
    public int insert(
            Connection connection,
            User user
    ) throws SQLException {

        validateConnection(connection);
        validateUserForInsert(user);

        String sql =
                """
                INSERT INTO dbo.Users
                (
                    username,
                    password_hash,
                    full_name,
                    email,
                    phone,

                    role_id,
                    status,

                    email_verified,
                    email_verified_at,
                    registration_source,

                    login_attempts,

                    created_at,
                    updated_at
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,

                    (
                        SELECT role_id
                        FROM dbo.Roles
                        WHERE role_name = ?
                    ),

                    ?,
                    ?,
                    ?,
                    ?,
                    ?,

                    SYSDATETIME(),
                    SYSDATETIME()
                )
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            statement.setString(
                    1,
                    user.getUsername().trim()
            );

            statement.setString(
                    2,
                    user.getPasswordHash()
            );

            statement.setString(
                    3,
                    user.getFullName().trim()
            );

            setNullableString(
                    statement,
                    4,
                    user.getEmail()
            );

            setNullableString(
                    statement,
                    5,
                    user.getPhone()
            );

            statement.setString(
                    6,
                    user.getRole().name()
            );

            AccountStatus status =
                    user.getStatus() == null
                            ? AccountStatus.ACTIVE
                            : user.getStatus();

            statement.setString(
                    7,
                    status.name()
            );

            statement.setBoolean(
                    8,
                    user.isEmailVerified()
            );

            if (
                    user.getEmailVerifiedAt()
                            == null
            ) {
                statement.setNull(
                        9,
                        Types.TIMESTAMP
                );
            } else {
                statement.setTimestamp(
                        9,
                        Timestamp.valueOf(
                                user.getEmailVerifiedAt()
                        )
                );
            }

            String registrationSource =
                    normalizeRegistrationSource(
                            user.getRegistrationSource()
                    );

            statement.setString(
                    10,
                    registrationSource
            );

            statement.setInt(
                    11,
                    Math.max(
                            0,
                            user.getLoginAttempts()
                    )
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo tài khoản người dùng."
                );
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {
                if (generatedKeys.next()) {
                    int userId =
                            generatedKeys.getInt(1);

                    user.setUserId(userId);

                    return userId;
                }
            }
        }

        throw new SQLException(
                "Không lấy được user_id vừa tạo."
        );
    }

    /**
     * Tên thay thế để tương thích code cũ.
     */
    public boolean create(User user)
            throws SQLException {

        return insert(user);
    }

    /**
     * Thêm User bằng mật khẩu chưa băm.
     */
    public boolean insertWithRawPassword(
            User user,
            String rawPassword
    ) throws SQLException {

        if (
                rawPassword == null
                        || rawPassword.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được để trống."
            );
        }

        user.setPasswordHash(
                PasswordUtil.hashPassword(
                        rawPassword
                )
        );

        return insert(user);
    }

    /* =====================================================
       5. CẬP NHẬT USER
       ===================================================== */

    /**
     * Cập nhật thông tin User.
     * Không cập nhật mật khẩu.
     */
    public boolean update(User user)
            throws SQLException {

        validateUserForUpdate(user);

        String sql =
                """
                UPDATE dbo.Users

                SET
                    full_name = ?,
                    email = ?,
                    phone = ?,

                    role_id =
                    (
                        SELECT role_id
                        FROM dbo.Roles
                        WHERE role_name = ?
                    ),

                    status = ?,
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    user.getFullName().trim()
            );

            setNullableString(
                    statement,
                    2,
                    user.getEmail()
            );

            setNullableString(
                    statement,
                    3,
                    user.getPhone()
            );

            statement.setString(
                    4,
                    user.getRole().name()
            );

            AccountStatus status =
                    user.getStatus() == null
                            ? AccountStatus.ACTIVE
                            : user.getStatus();

            statement.setString(
                    5,
                    status.name()
            );

            statement.setInt(
                    6,
                    user.getUserId()
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Cập nhật trạng thái.
     */
    public boolean updateStatus(
            int userId,
            AccountStatus status
    ) throws SQLException {

        if (status == null) {
            throw new IllegalArgumentException(
                    "Trạng thái không được null."
            );
        }

        String sql =
                """
                UPDATE dbo.Users

                SET
                    status = ?,
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    status.name()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Cập nhật vai trò.
     */
    public boolean updateRole(
            int userId,
            Role role
    ) throws SQLException {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được null."
            );
        }

        String sql =
                """
                UPDATE dbo.Users

                SET
                    role_id =
                    (
                        SELECT role_id
                        FROM dbo.Roles
                        WHERE role_name = ?
                    ),

                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    role.name()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /* =====================================================
       6. XÁC MINH EMAIL VÀ DUYỆT TEACHER
       ===================================================== */

    /**
     * Đánh dấu email đã được xác minh.
     */
    public boolean verifyEmail(
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users

                SET
                    email_verified = 1,
                    email_verified_at = SYSDATETIME(),
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Xác minh email trong transaction hiện tại.
     */
    public boolean verifyEmail(
            Connection connection,
            int userId
    ) throws SQLException {

        validateConnection(connection);

        String sql =
                """
                UPDATE dbo.Users

                SET
                    email_verified = 1,
                    email_verified_at = SYSDATETIME(),
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Admin duyệt tài khoản Teacher.
     */
    public boolean approveUser(
            int userId,
            int adminId
    ) throws SQLException {

        if (
                userId <= 0
                        || adminId <= 0
        ) {
            throw new IllegalArgumentException(
                    "User ID hoặc Admin ID không hợp lệ."
            );
        }

        String sql =
                """
                UPDATE dbo.Users

                SET
                    status = 'ACTIVE',

                    approved_by = ?,
                    approved_at = SYSDATETIME(),

                    rejected_by = NULL,
                    rejected_at = NULL,
                    rejection_reason = NULL,

                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    adminId
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Admin từ chối tài khoản Teacher.
     */
    public boolean rejectUser(
            int userId,
            int adminId,
            String reason
    ) throws SQLException {

        if (
                userId <= 0
                        || adminId <= 0
        ) {
            throw new IllegalArgumentException(
                    "User ID hoặc Admin ID không hợp lệ."
            );
        }

        String sql =
                """
                UPDATE dbo.Users

                SET
                    status = 'INACTIVE',

                    rejected_by = ?,
                    rejected_at = SYSDATETIME(),
                    rejection_reason = ?,

                    approved_by = NULL,
                    approved_at = NULL,

                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    adminId
            );

            setNullableString(
                    statement,
                    2,
                    reason
            );

            statement.setInt(
                    3,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Lấy danh sách Teacher đang chờ duyệt.
     */
    public List<User> findPendingTeachers()
            throws SQLException {

        String sql =
                USER_COLUMNS
                        + """
                        WHERE r.role_name = 'TEACHER'
                          AND u.status = 'PENDING_APPROVAL'

                        ORDER BY u.created_at ASC
                        """;

        List<User> users =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(
                        mapResultSetToUser(
                                resultSet
                        )
                );
            }
        }

        return users;
    }

    /* =====================================================
       7. ĐĂNG NHẬP VÀ KHÓA TÀI KHOẢN
       ===================================================== */

    /**
     * Đăng nhập bằng username và mật khẩu chưa băm.
     *
     * Chỉ trả về User nếu ACTIVE.
     */
    public User login(
            String username,
            String rawPassword
    ) throws SQLException {

        if (
                username == null
                        || username.isBlank()
                        || rawPassword == null
                        || rawPassword.isBlank()
        ) {
            return null;
        }

        User user =
                findByUsername(
                        username.trim()
                );

        if (user == null) {
            return null;
        }

        boolean passwordCorrect =
                PasswordUtil.matches(
                        rawPassword,
                        user.getPasswordHash()
                );

        if (!passwordCorrect) {
            increaseLoginAttempt(
                    user.getUserId()
            );

            return null;
        }

        if (
                user.getStatus()
                        != AccountStatus.ACTIVE
        ) {
            return null;
        }

        updateLastLogin(
                user.getUserId()
        );

        return findById(
                user.getUserId()
        );
    }

    /**
     * Cập nhật lần đăng nhập thành công.
     */
    public void updateLastLogin(
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users

                SET
                    last_login = SYSDATETIME(),
                    login_attempts = 0,
                    locked_until = NULL,
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            statement.executeUpdate();
        }
    }

    /**
     * Tăng số lần đăng nhập sai.
     */
    public void increaseLoginAttempt(
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users

                SET
                    login_attempts =
                        login_attempts + 1,

                    locked_until =
                        CASE
                            WHEN login_attempts + 1 >= 5
                            THEN DATEADD(
                                    MINUTE,
                                    15,
                                    SYSDATETIME()
                                 )
                            ELSE locked_until
                        END,

                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            statement.executeUpdate();
        }
    }

    /**
     * Đặt lại số lần đăng nhập sai.
     */
    public boolean resetLoginAttempts(
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users

                SET
                    login_attempts = 0,
                    locked_until = NULL,
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Khóa tài khoản.
     */
    public boolean lockUser(
            int userId
    ) throws SQLException {

        return updateStatus(
                userId,
                AccountStatus.LOCKED
        );
    }

    /**
     * Mở khóa tài khoản.
     */
    public boolean unlockUser(
            int userId
    ) throws SQLException {

        boolean updated =
                updateStatus(
                        userId,
                        AccountStatus.ACTIVE
                );

        if (updated) {
            resetLoginAttempts(userId);
        }

        return updated;
    }

    /**
     * Ngừng hoạt động tài khoản.
     */
    public boolean deactivateUser(
            int userId
    ) throws SQLException {

        return updateStatus(
                userId,
                AccountStatus.INACTIVE
        );
    }

    /* =====================================================
       8. MẬT KHẨU
       ===================================================== */

    /**
     * Reset mật khẩu với chuỗi đã băm.
     */
    public boolean resetPasswordHash(
            int userId,
            String newPasswordHash
    ) throws SQLException {

        if (
                newPasswordHash == null
                        || newPasswordHash.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu băm không được để trống."
            );
        }

        String sql =
                """
                UPDATE dbo.Users

                SET
                    password_hash = ?,
                    updated_at = SYSDATETIME()

                WHERE user_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    newPasswordHash.trim()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate()
                    > 0;
        }
    }

    /**
     * Reset mật khẩu bằng mật khẩu chưa băm.
     */
    public boolean resetPassword(
            int userId,
            String rawPassword
    ) throws SQLException {

        if (
                rawPassword == null
                        || rawPassword.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới không được để trống."
            );
        }

        String hashedPassword =
                PasswordUtil.hashPassword(
                        rawPassword
                );

        return resetPasswordHash(
                userId,
                hashedPassword
        );
    }

    /* =====================================================
       9. XÓA USER
       ===================================================== */

    /**
     * Xóa User.
     */
    /**
     * Xóa tài khoản người dùng an toàn.
     *
     * Quy tắc:
     * - Không xóa hồ sơ Student/Teacher.
     * - Không xóa đăng ký học, điểm hoặc thanh toán.
     * - Chỉ gỡ liên kết user_id khỏi Student/Teacher.
     * - Xóa dữ liệu xác minh email và thông báo gắn trực tiếp với User.
     * - Cuối cùng mới xóa bản ghi Users.
     */
    public boolean delete(int userId)
            throws SQLException {

        return deleteById(userId);
    }

    /**
     * Xóa tài khoản trong một transaction.
     */
    public boolean deleteById(int userId)
            throws SQLException {

        validateUserIdForDelete(userId);

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                User user =
                        findUserForDelete(
                                connection,
                                userId
                        );

                if (user == null) {
                    connection.rollback();
                    return false;
                }

                /*
                 * Bảo vệ tài khoản quản trị chính.
                 */
                if ("admin".equalsIgnoreCase(
                        user.getUsername()
                )) {
                    throw new IllegalStateException(
                            "Không thể xóa tài khoản quản trị chính."
                    );
                }

                /*
                 * Xóa các mã OTP/xác minh email.
                 */
                deleteEmailVerificationsByUserId(
                        connection,
                        userId
                );

                /*
                 * Xóa thông báo liên kết trực tiếp với user.
                 * Nếu bảng Notifications của bạn không có user_id,
                 * hãy bỏ lời gọi này.
                 */
                deleteNotificationsByUserId(
                        connection,
                        userId
                );

                /*
                 * Giữ nguyên hồ sơ học viên và dữ liệu học tập.
                 * Chỉ tháo liên kết tài khoản.
                 */
                unlinkStudentAccount(
                        connection,
                        userId
                );

                /*
                 * Giữ nguyên hồ sơ giảng viên và các lớp liên quan.
                 * Chỉ tháo liên kết tài khoản.
                 */
                unlinkTeacherAccount(
                        connection,
                        userId
                );

                int affectedRows =
                        deleteUserRecord(
                                connection,
                                userId
                        );

                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                connection.commit();
                return true;

            } catch (SQLException
                     | RuntimeException exception) {

                rollbackTransaction(
                        connection,
                        exception
                );

                throw exception;

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }
    }

    /**
     * Lấy tài khoản cần xóa trong transaction hiện tại.
     */
    private User findUserForDelete(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql =
                """
                SELECT
                    u.user_id,
                    u.username,
                    u.password_hash,
                    u.full_name,
                    u.email,
                    u.phone,
                    u.role_id,
                    r.role_name,
                    u.status,
                    u.email_verified,
                    u.email_verified_at,
                    u.registration_source,
                    u.last_login,
                    u.login_attempts,
                    u.locked_until,
                    u.approved_by,
                    u.approved_at,
                    u.rejected_by,
                    u.rejected_at,
                    u.rejection_reason,
                    u.created_at,
                    u.updated_at
                FROM Users AS u
                INNER JOIN Roles AS r
                    ON u.role_id = r.role_id
                WHERE u.user_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapResultSetToUser(
                            resultSet
                    );
                }
            }
        }

        return null;
    }

    /**
     * Xóa các mã OTP của tài khoản.
     */
    private int deleteEmailVerificationsByUserId(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql =
                """
                DELETE FROM EmailVerifications
                WHERE user_id = ?
                """;

        return executeUpdateByUserId(
                connection,
                sql,
                userId
        );
    }

    /**
     * Xóa thông báo gắn trực tiếp với tài khoản.
     */
    private int deleteNotificationsByUserId(
            Connection connection,
            int userId
    ) throws SQLException {

        /*
         * Kiểm tra cột user_id có tồn tại để tránh lỗi
         * nếu database cũ chưa có cột này.
         */
        if (!columnExists(
                connection,
                "Notifications",
                "user_id"
        )) {
            return 0;
        }

        String sql =
                """
                DELETE FROM Notifications
                WHERE user_id = ?
                """;

        return executeUpdateByUserId(
                connection,
                sql,
                userId
        );
    }

    /**
     * Gỡ liên kết giữa tài khoản và hồ sơ sinh viên.
     *
     * Hồ sơ sinh viên, đăng ký lớp, điểm và thanh toán
     * vẫn được giữ nguyên.
     */
    private int unlinkStudentAccount(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE Students
                SET
                    user_id = NULL,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                """;

        return executeUpdateByUserId(
                connection,
                sql,
                userId
        );
    }

    /**
     * Gỡ liên kết giữa tài khoản và hồ sơ giảng viên.
     *
     * Hồ sơ giảng viên và lớp học vẫn được giữ nguyên.
     */
    private int unlinkTeacherAccount(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql =
                """
                UPDATE Teachers
                SET
                    user_id = NULL,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                """;

        return executeUpdateByUserId(
                connection,
                sql,
                userId
        );
    }

    /**
     * Xóa bản ghi Users sau khi đã xử lý toàn bộ liên kết.
     */
    private int deleteUserRecord(
            Connection connection,
            int userId
    ) throws SQLException {

        String sql =
                """
                DELETE FROM Users
                WHERE user_id = ?
                """;

        return executeUpdateByUserId(
                connection,
                sql,
                userId
        );
    }

    /**
     * Chạy câu UPDATE/DELETE có một tham số user_id.
     */
    private int executeUpdateByUserId(
            Connection connection,
            String sql,
            int userId
    ) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            return statement.executeUpdate();
        }
    }

    /**
     * Kiểm tra một cột có tồn tại trong database hay không.
     */
    private boolean columnExists(
            Connection connection,
            String tableName,
            String columnName
    ) throws SQLException {

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    tableName
            );

            statement.setString(
                    2,
                    columnName
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }
        }
    }

    /**
     * Kiểm tra ID trước khi xóa.
     */
    private void validateUserIdForDelete(
            int userId
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Mã người dùng không hợp lệ."
            );
        }
    }

    /**
     * Rollback transaction và giữ lại lỗi rollback nếu có.
     */
    private void rollbackTransaction(
            Connection connection,
            Exception originalException
    ) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();

        } catch (SQLException rollbackException) {
            originalException.addSuppressed(
                    rollbackException
            );
        }
    }

    /**
     * Khôi phục AutoCommit ban đầu.
     */
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
       10. THỐNG KÊ
       ===================================================== */

    /**
     * Đếm User theo vai trò.
     */
    public int countByRole(
            Role role
    ) throws SQLException {

        if (role == null) {
            return 0;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users AS u

                INNER JOIN dbo.Roles AS r
                    ON u.role_id = r.role_id

                WHERE r.role_name = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    role.name()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return resultSet.getInt(
                            "total"
                    );
                }
            }
        }

        return 0;
    }

    /**
     * Đếm User theo trạng thái.
     */
    public int countByStatus(
            AccountStatus status
    ) throws SQLException {

        if (status == null) {
            return 0;
        }

        String sql =
                """
                SELECT COUNT(*) AS total

                FROM dbo.Users

                WHERE status = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    status.name()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return resultSet.getInt(
                            "total"
                    );
                }
            }
        }

        return 0;
    }

    /* =====================================================
       11. MAPPING RESULTSET
       ===================================================== */

    private User mapResultSetToUser(
            ResultSet resultSet
    ) throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getInt(
                        "user_id"
                )
        );

        user.setUsername(
                resultSet.getString(
                        "username"
                )
        );

        user.setPasswordHash(
                resultSet.getString(
                        "password_hash"
                )
        );

        user.setFullName(
                resultSet.getString(
                        "full_name"
                )
        );

        user.setEmail(
                resultSet.getString(
                        "email"
                )
        );

        user.setPhone(
                resultSet.getString(
                        "phone"
                )
        );

        user.setRoleId(
                resultSet.getInt(
                        "role_id"
                )
        );

        String roleName =
                resultSet.getString(
                        "role_name"
                );

        if (
                roleName != null
                        && !roleName.isBlank()
        ) {
            user.setRole(
                    Role.valueOf(
                            roleName
                                    .trim()
                                    .toUpperCase()
                    )
            );
        }

        String statusName =
                resultSet.getString(
                        "status"
                );

        if (
                statusName != null
                        && !statusName.isBlank()
        ) {
            user.setStatus(
                    AccountStatus.valueOf(
                            statusName
                                    .trim()
                                    .toUpperCase()
                    )
            );
        }

        user.setEmailVerified(
                resultSet.getBoolean(
                        "email_verified"
                )
        );

        Timestamp emailVerifiedAt =
                resultSet.getTimestamp(
                        "email_verified_at"
                );

        if (emailVerifiedAt != null) {
            user.setEmailVerifiedAt(
                    emailVerifiedAt
                            .toLocalDateTime()
            );
        }

        user.setRegistrationSource(
                resultSet.getString(
                        "registration_source"
                )
        );

        Timestamp lastLogin =
                resultSet.getTimestamp(
                        "last_login"
                );

        if (lastLogin != null) {
            user.setLastLogin(
                    lastLogin.toLocalDateTime()
            );
        }

        user.setLoginAttempts(
                resultSet.getInt(
                        "login_attempts"
                )
        );

        Timestamp lockedUntil =
                resultSet.getTimestamp(
                        "locked_until"
                );

        if (lockedUntil != null) {
            user.setLockedUntil(
                    lockedUntil.toLocalDateTime()
            );
        }

        int approvedBy =
                resultSet.getInt(
                        "approved_by"
                );

        if (resultSet.wasNull()) {
            user.setApprovedBy(null);
        } else {
            user.setApprovedBy(
                    approvedBy
            );
        }

        Timestamp approvedAt =
                resultSet.getTimestamp(
                        "approved_at"
                );

        if (approvedAt != null) {
            user.setApprovedAt(
                    approvedAt.toLocalDateTime()
            );
        }

        int rejectedBy =
                resultSet.getInt(
                        "rejected_by"
                );

        if (resultSet.wasNull()) {
            user.setRejectedBy(null);
        } else {
            user.setRejectedBy(
                    rejectedBy
            );
        }

        Timestamp rejectedAt =
                resultSet.getTimestamp(
                        "rejected_at"
                );

        if (rejectedAt != null) {
            user.setRejectedAt(
                    rejectedAt.toLocalDateTime()
            );
        }

        user.setRejectionReason(
                resultSet.getString(
                        "rejection_reason"
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at"
                );

        if (createdAt != null) {
            user.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp(
                        "updated_at"
                );

        if (updatedAt != null) {
            user.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return user;
    }

    /* =====================================================
       12. VALIDATION
       ===================================================== */

    private void validateUserForInsert(
            User user
    ) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "User không được null."
            );
        }

        if (
                user.getUsername() == null
                        || user.getUsername().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống."
            );
        }

        if (
                user.getPasswordHash() == null
                        || user.getPasswordHash().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu chưa được thiết lập."
            );
        }

        if (
                user.getFullName() == null
                        || user.getFullName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống."
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống."
            );
        }
    }

    private void validateUserForUpdate(
            User user
    ) {
        if (user == null) {
            throw new IllegalArgumentException(
                    "User không được null."
            );
        }

        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException(
                    "User ID không hợp lệ."
            );
        }

        if (
                user.getFullName() == null
                        || user.getFullName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống."
            );
        }

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống."
            );
        }
    }

    private void validatePagination(
            int page,
            int pageSize
    ) {
        if (page <= 0) {
            throw new IllegalArgumentException(
                    "Trang hiện tại phải lớn hơn 0."
            );
        }

        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "Số dòng trên trang phải lớn hơn 0."
            );
        }
    }

    private void validateConnection(
            Connection connection
    ) throws SQLException {

        if (
                connection == null
                        || connection.isClosed()
        ) {
            throw new SQLException(
                    "Connection không hợp lệ hoặc đã đóng."
            );
        }
    }

    /* =====================================================
       13. HÀM HỖ TRỢ
       ===================================================== */

    private String normalizeKeyword(
            String keyword
    ) {
        return keyword == null
                ? ""
                : keyword.trim();
    }

    private String normalizeRegistrationSource(
            String source
    ) {
        if (
                source == null
                        || source.isBlank()
        ) {
            return "ADMIN";
        }

        String normalized =
                source.trim().toUpperCase();

        if (
                !normalized.equals("ADMIN")
                        && !normalized.equals(
                        "SELF_REGISTER"
                )
        ) {
            throw new IllegalArgumentException(
                    "Nguồn đăng ký không hợp lệ: "
                            + source
            );
        }

        return normalized;
    }

    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (
                value == null
                        || value.isBlank()
        ) {
            statement.setNull(
                    parameterIndex,
                    Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
}
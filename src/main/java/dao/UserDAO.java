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
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /*
     * Danh sách cột dùng chung khi truy vấn User.
     */
    private static final String USER_COLUMNS = """
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
                u.created_at,
                u.updated_at
            FROM dbo.Users AS u
            INNER JOIN dbo.Roles AS r
                ON u.role_id = r.role_id
            """;

    /**
     * Lấy toàn bộ người dùng.
     */
    public List<User> findAll() throws SQLException {
        String sql = USER_COLUMNS + """
                ORDER BY u.user_id DESC
                """;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
        }

        return users;
    }

    /**
     * Lấy danh sách User theo trang.
     *
     * @param page     trang hiện tại, bắt đầu từ 1
     * @param pageSize số dòng trên một trang
     */
    public List<User> findAll(
            int page,
            int pageSize
    ) throws SQLException {
        return search("", page, pageSize);
    }

    /**
     * Tìm kiếm User kết hợp phân trang.
     *
     * Tìm theo:
     * - username
     * - họ tên
     * - email
     * - số điện thoại
     * - role
     * - trạng thái
     */
    public List<User> search(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {

        validatePagination(page, pageSize);

        String sql = USER_COLUMNS + """
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

        String normalizedKeyword = normalizeKeyword(keyword);
        String searchValue = "%" + normalizedKeyword + "%";
        int offset = (page - 1) * pageSize;

        List<User> users = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            statement.setString(5, searchValue);
            statement.setString(6, searchValue);
            statement.setInt(7, offset);
            statement.setInt(8, pageSize);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(mapResultSetToUser(resultSet));
                }
            }
        }

        return users;
    }

    /**
     * Tên thay thế để tương thích với Controller/View
     * nếu code cũ đang gọi findByKeyword().
     */
    public List<User> findByKeyword(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {
        return search(keyword, page, pageSize);
    }

    /**
     * Đếm toàn bộ User.
     */
    public int countAll() throws SQLException {
        return count("");
    }

    /**
     * Đếm User theo từ khóa tìm kiếm.
     */
    public int count(String keyword) throws SQLException {
        String sql = """
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

        String normalizedKeyword = normalizeKeyword(keyword);
        String searchValue = "%" + normalizedKeyword + "%";

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            statement.setString(5, searchValue);
            statement.setString(6, searchValue);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    /**
     * Tên thay thế nếu code cũ đang gọi countByKeyword().
     */
    public int countByKeyword(String keyword) throws SQLException {
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

        int totalRecords = count(keyword);

        return Math.max(
                1,
                (int) Math.ceil(
                        (double) totalRecords / pageSize
                )
        );
    }

    /**
     * Tìm User theo khóa chính.
     */
    public User findById(int userId) throws SQLException {
        String sql = USER_COLUMNS + """
                WHERE u.user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Tìm User theo username.
     *
     * Phương thức này được LoginService sử dụng.
     */
    public User findByUsername(String username) throws SQLException {
        String sql = USER_COLUMNS + """
                WHERE LOWER(u.username) = LOWER(?)
                """;

        if (username == null || username.isBlank()) {
            return null;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToUser(resultSet);
                }
            }
        }

        return null;
    }

    /**
     * Kiểm tra username đã tồn tại hay chưa.
     */
    public boolean existsByUsername(String username) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users
                WHERE LOWER(username) = LOWER(?)
                """;

        if (username == null || username.isBlank()) {
            return false;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }
        }
    }

    /**
     * Kiểm tra username đã thuộc User khác hay chưa.
     *
     * Dùng khi cập nhật tài khoản.
     */
    public boolean existsByUsernameExceptId(
            String username,
            int excludedUserId
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users
                WHERE LOWER(username) = LOWER(?)
                  AND user_id <> ?
                """;

        if (username == null || username.isBlank()) {
            return false;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, username.trim());
            statement.setInt(2, excludedUserId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }
        }
    }

    /**
     * Kiểm tra email đã tồn tại hay chưa.
     */
    public boolean existsByEmail(String email) throws SQLException {
        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users
                WHERE LOWER(email) = LOWER(?)
                """;

        if (email == null || email.isBlank()) {
            return false;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email.trim());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }
        }
    }

    /**
     * Kiểm tra email đã thuộc User khác hay chưa.
     */
    public boolean existsByEmailExceptId(
            String email,
            int excludedUserId
    ) throws SQLException {

        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users
                WHERE LOWER(email) = LOWER(?)
                  AND user_id <> ?
                """;

        if (email == null || email.isBlank()) {
            return false;
        }

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email.trim());
            statement.setInt(2, excludedUserId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }
        }
    }

    /**
     * Thêm User mới.
     *
     * Mật khẩu truyền vào phải là mật khẩu đã được băm.
     */
    public boolean insert(User user) throws SQLException {
        validateUserForInsert(user);

        String sql = """
                INSERT INTO dbo.Users
                (
                    username,
                    password_hash,
                    full_name,
                    email,
                    phone,
                    role_id,
                    status,
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
                    SYSDATETIME(),
                    SYSDATETIME()
                )
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
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

            AccountStatus status = user.getStatus() == null
                    ? AccountStatus.ACTIVE
                    : user.getStatus();

            statement.setString(
                    7,
                    status.name()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Tên thay thế nếu Service cũ đang gọi create().
     */
    public boolean create(User user) throws SQLException {
        return insert(user);
    }

    /**
     * Thêm User bằng mật khẩu chưa băm.
     *
     * Phương thức sẽ tự băm mật khẩu trước khi lưu.
     */
    public boolean insertWithRawPassword(
            User user,
            String rawPassword
    ) throws SQLException {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Mật khẩu không được để trống."
            );
        }

        user.setPasswordHash(
                PasswordUtil.hashPassword(rawPassword)
        );

        return insert(user);
    }

    /**
     * Cập nhật thông tin User.
     *
     * Không cập nhật mật khẩu tại đây.
     */
    public boolean update(User user) throws SQLException {
        validateUserForUpdate(user);

        String sql = """
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
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
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

            AccountStatus status = user.getStatus() == null
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

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Xóa User.
     *
     * Có thể thất bại nếu User đang được tham chiếu bởi
     * Students, Teachers hoặc các bảng liên quan khác.
     */
    public boolean delete(int userId) throws SQLException {
        String sql = """
                DELETE FROM dbo.Users
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, userId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Tên thay thế nếu code cũ gọi deleteById().
     */
    public boolean deleteById(int userId) throws SQLException {
        return delete(userId);
    }

    /**
     * Cập nhật trạng thái tài khoản.
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

        String sql = """
                UPDATE dbo.Users
                SET
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, status.name());
            statement.setInt(2, userId);

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Khóa tài khoản.
     */
    public boolean lockUser(int userId) throws SQLException {
        return updateStatus(
                userId,
                AccountStatus.LOCKED
        );
    }

    /**
     * Mở khóa tài khoản.
     */
    public boolean unlockUser(int userId) throws SQLException {
        return updateStatus(
                userId,
                AccountStatus.ACTIVE
        );
    }

    /**
     * Ngừng hoạt động tài khoản.
     */
    public boolean deactivateUser(int userId) throws SQLException {
        return updateStatus(
                userId,
                AccountStatus.INACTIVE
        );
    }

    /**
     * Reset mật khẩu với chuỗi đã được băm.
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

        String sql = """
                UPDATE dbo.Users
                SET
                    password_hash = ?,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    newPasswordHash.trim()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Reset mật khẩu bằng mật khẩu chưa băm.
     *
     * PasswordUtil sẽ băm trước khi lưu vào SQL Server.
     */
    public boolean resetPassword(
            int userId,
            String rawPassword
    ) throws SQLException {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới không được để trống."
            );
        }

        String hashedPassword =
                PasswordUtil.hashPassword(rawPassword);

        return resetPasswordHash(
                userId,
                hashedPassword
        );
    }

    /**
     * Cập nhật vai trò của User.
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

        String sql = """
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
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    role.name()
            );

            statement.setInt(
                    2,
                    userId
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Đếm User theo vai trò.
     */
    public int countByRole(Role role) throws SQLException {
        if (role == null) {
            return 0;
        }

        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users AS u
                INNER JOIN dbo.Roles AS r
                    ON u.role_id = r.role_id
                WHERE r.role_name = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, role.name());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
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

        String sql = """
                SELECT COUNT(*) AS total
                FROM dbo.Users
                WHERE status = ?
                """;

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    status.name()
            );

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
            }
        }

        return 0;
    }

    /**
     * Chuyển một dòng ResultSet thành đối tượng User.
     */
    private User mapResultSetToUser(
            ResultSet resultSet
    ) throws SQLException {

        User user = new User();

        user.setUserId(
                resultSet.getInt("user_id")
        );

        user.setUsername(
                resultSet.getString("username")
        );

        user.setPasswordHash(
                resultSet.getString("password_hash")
        );

        user.setFullName(
                resultSet.getString("full_name")
        );

        user.setEmail(
                resultSet.getString("email")
        );

        user.setPhone(
                resultSet.getString("phone")
        );

        user.setRoleId(
                resultSet.getInt("role_id")
        );

        String roleName =
                resultSet.getString("role_name");

        if (roleName != null) {
            user.setRole(
                    Role.valueOf(
                            roleName.trim().toUpperCase()
                    )
            );
        }

        String statusName =
                resultSet.getString("status");

        if (statusName != null) {
            user.setStatus(
                    AccountStatus.valueOf(
                            statusName.trim().toUpperCase()
                    )
            );
        }

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            user.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp("updated_at");

        if (updatedAt != null) {
            user.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return user;
    }

    private void validateUserForInsert(User user) {
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

    private void validateUserForUpdate(User user) {
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

    private String normalizeKeyword(String keyword) {
        return keyword == null
                ? ""
                : keyword.trim();
    }

    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            statement.setNull(
                    parameterIndex,
                    Types.NVARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
}
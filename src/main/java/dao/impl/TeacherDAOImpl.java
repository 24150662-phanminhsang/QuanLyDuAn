package dao.impl;

import dao.TeacherDAO;
import model.Teacher;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAOImpl implements TeacherDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                teacher_id,
                teacher_code,
                user_id,
                full_name,
                date_of_birth,
                gender,
                email,
                phone,
                address,
                specialization,
                status,
                created_at,
                updated_at
            FROM dbo.Teachers
            """;

    /**
     * Thêm giảng viên bằng Connection riêng.
     */
    @Override
    public boolean insert(Teacher teacher) {
        try (Connection connection =
                     DBConnection.getConnection()) {

            return insert(
                    connection,
                    teacher
            ) > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể thêm giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Thêm giảng viên trong transaction hiện tại.
     *
     * Trả về teacher_id vừa tạo.
     */
    @Override
    public int insert(
            Connection connection,
            Teacher teacher
    ) throws SQLException {

        validateConnection(connection);
        validateTeacherForInsert(teacher);

        String sql =
                """
                INSERT INTO dbo.Teachers
                (
                    teacher_code,
                    user_id,
                    full_name,
                    date_of_birth,
                    gender,
                    email,
                    phone,
                    address,
                    specialization,
                    status,
                    created_at,
                    updated_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                    SYSDATETIME(),
                    SYSDATETIME()
                )
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(
                             sql,
                             Statement.RETURN_GENERATED_KEYS
                     )) {

            statement.setString(
                    1,
                    normalizeTeacherCode(
                            teacher.getTeacherCode()
                    )
            );

            if (teacher.getUserId() == null) {
                statement.setNull(
                        2,
                        Types.INTEGER
                );
            } else {
                statement.setInt(
                        2,
                        teacher.getUserId()
                );
            }

            statement.setString(
                    3,
                    teacher.getFullName().trim()
            );

            if (teacher.getDateOfBirth() == null) {
                statement.setNull(
                        4,
                        Types.DATE
                );
            } else {
                statement.setDate(
                        4,
                        teacher.getDateOfBirth()
                );
            }

            setNullableString(
                    statement,
                    5,
                    normalizeGender(
                            teacher.getGender()
                    )
            );

            setNullableString(
                    statement,
                    6,
                    teacher.getEmail()
            );

            setNullableString(
                    statement,
                    7,
                    teacher.getPhone()
            );

            setNullableString(
                    statement,
                    8,
                    teacher.getAddress()
            );

            setNullableString(
                    statement,
                    9,
                    teacher.getSpecialization()
            );

            statement.setString(
                    10,
                    normalizeStatus(
                            teacher.getStatus()
                    )
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo hồ sơ giảng viên."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    int teacherId =
                            generatedKeys.getInt(1);

                    teacher.setTeacherId(
                            teacherId
                    );

                    return teacherId;
                }
            }
        }

        throw new SQLException(
                "Không lấy được teacher_id vừa tạo."
        );
    }

    /**
     * Cập nhật giảng viên bằng Connection riêng.
     */
    @Override
    public boolean update(Teacher teacher) {
        try (Connection connection =
                     DBConnection.getConnection()) {

            return update(
                    connection,
                    teacher
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Cập nhật giảng viên trong transaction hiện tại.
     */
    @Override
    public boolean update(
            Connection connection,
            Teacher teacher
    ) throws SQLException {

        validateConnection(connection);
        validateTeacherForUpdate(teacher);

        String sql =
                """
                UPDATE dbo.Teachers
                SET
                    teacher_code = ?,
                    user_id = ?,
                    full_name = ?,
                    date_of_birth = ?,
                    gender = ?,
                    email = ?,
                    phone = ?,
                    address = ?,
                    specialization = ?,
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE teacher_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    normalizeTeacherCode(
                            teacher.getTeacherCode()
                    )
            );

            if (teacher.getUserId() == null) {
                statement.setNull(
                        2,
                        Types.INTEGER
                );
            } else {
                statement.setInt(
                        2,
                        teacher.getUserId()
                );
            }

            statement.setString(
                    3,
                    teacher.getFullName().trim()
            );

            if (teacher.getDateOfBirth() == null) {
                statement.setNull(
                        4,
                        Types.DATE
                );
            } else {
                statement.setDate(
                        4,
                        teacher.getDateOfBirth()
                );
            }

            setNullableString(
                    statement,
                    5,
                    normalizeGender(
                            teacher.getGender()
                    )
            );

            setNullableString(
                    statement,
                    6,
                    teacher.getEmail()
            );

            setNullableString(
                    statement,
                    7,
                    teacher.getPhone()
            );

            setNullableString(
                    statement,
                    8,
                    teacher.getAddress()
            );

            setNullableString(
                    statement,
                    9,
                    teacher.getSpecialization()
            );

            statement.setString(
                    10,
                    normalizeStatus(
                            teacher.getStatus()
                    )
            );

            statement.setInt(
                    11,
                    teacher.getTeacherId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Xóa giảng viên.
     */
    @Override
    public boolean delete(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        String sql =
                """
                DELETE FROM dbo.Teachers
                WHERE teacher_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    teacherId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể xóa giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Tìm giảng viên theo ID.
     */
    @Override
    public Teacher getById(int teacherId) {
        if (teacherId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE teacher_id = ?
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    teacherId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapTeacher(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm giảng viên theo ID: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Tìm giảng viên theo tài khoản.
     */
    @Override
    public Teacher getByUserId(int userId) {
        if (userId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE user_id = ?
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapTeacher(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm giảng viên theo tài khoản: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Tìm giảng viên theo mã.
     */
    @Override
    public Teacher getByCode(String teacherCode) {
        if (teacherCode == null
                || teacherCode.isBlank()) {

            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(teacher_code) = UPPER(?)
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    teacherCode.trim()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapTeacher(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm giảng viên theo mã: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Kiểm tra mã giảng viên đã tồn tại.
     */
    @Override
    public boolean existsByTeacherCode(
            String teacherCode
    ) {
        if (teacherCode == null
                || teacherCode.isBlank()) {

            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Teachers
                WHERE UPPER(teacher_code) = UPPER(?)
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    teacherCode.trim()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra mã giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Kiểm tra user_id đã có hồ sơ giảng viên.
     */
    @Override
    public boolean existsByUserId(int userId) {
        if (userId <= 0) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Teachers
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    userId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra tài khoản giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Lấy toàn bộ danh sách giảng viên.
     */
    @Override
    public List<Teacher> getAll() {
        String sql =
                BASE_SELECT
                        + """
                        ORDER BY teacher_id DESC
                        """;

        List<Teacher> teachers =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                teachers.add(
                        mapTeacher(resultSet)
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }

        return teachers;
    }

    /**
     * Tìm kiếm giảng viên.
     */
    @Override
    public List<Teacher> search(String keyword) {
        String sql =
                BASE_SELECT
                        + """
                        WHERE
                            teacher_code LIKE ?
                            OR full_name LIKE ?
                            OR COALESCE(email, '') LIKE ?
                            OR COALESCE(phone, '') LIKE ?
                            OR COALESCE(specialization, '') LIKE ?
                        ORDER BY teacher_id DESC
                        """;

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        String searchValue =
                "%" + normalizedKeyword + "%";

        List<Teacher> teachers =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);
            statement.setString(5, searchValue);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    teachers.add(
                            mapTeacher(resultSet)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm giảng viên: "
                            + exception.getMessage(),
                    exception
            );
        }

        return teachers;
    }

    /**
     * Chuyển ResultSet thành Teacher.
     */
    private Teacher mapTeacher(
            ResultSet resultSet
    ) throws SQLException {

        Teacher teacher = new Teacher();

        teacher.setTeacherId(
                resultSet.getInt(
                        "teacher_id"
                )
        );

        teacher.setTeacherCode(
                resultSet.getString(
                        "teacher_code"
                )
        );

        int userId =
                resultSet.getInt(
                        "user_id"
                );

        if (resultSet.wasNull()) {
            teacher.setUserId(null);
        } else {
            teacher.setUserId(userId);
        }

        teacher.setFullName(
                resultSet.getString(
                        "full_name"
                )
        );

        teacher.setDateOfBirth(
                resultSet.getDate(
                        "date_of_birth"
                )
        );

        teacher.setGender(
                resultSet.getString(
                        "gender"
                )
        );

        teacher.setEmail(
                resultSet.getString(
                        "email"
                )
        );

        teacher.setPhone(
                resultSet.getString(
                        "phone"
                )
        );

        teacher.setAddress(
                resultSet.getString(
                        "address"
                )
        );

        teacher.setSpecialization(
                resultSet.getString(
                        "specialization"
                )
        );

        teacher.setStatus(
                resultSet.getString(
                        "status"
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at"
                );

        if (createdAt != null) {
            teacher.setCreatedAt(
                    createdAt
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp(
                        "updated_at"
                );

        if (updatedAt != null) {
            teacher.setUpdatedAt(
                    updatedAt
            );
        }

        return teacher;
    }

    private void validateTeacherForInsert(
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

        if (teacher.getUserId() == null
                || teacher.getUserId() <= 0) {

            throw new IllegalArgumentException(
                    "Giảng viên phải được liên kết với tài khoản Users."
            );
        }
    }

    private void validateTeacherForUpdate(
            Teacher teacher
    ) {
        validateTeacherForInsert(teacher);

        if (teacher.getTeacherId() <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }
    }

    private String normalizeTeacherCode(
            String teacherCode
    ) {
        if (teacherCode == null
                || teacherCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Mã giảng viên không được để trống."
            );
        }

        return teacherCode
                .trim()
                .toUpperCase();
    }

    private String normalizeGender(
            String gender
    ) {
        if (gender == null
                || gender.isBlank()) {

            return null;
        }

        String normalized =
                gender.trim().toUpperCase();

        return switch (normalized) {
            case "MALE", "NAM" -> "MALE";
            case "FEMALE", "NỮ", "NU" -> "FEMALE";
            case "OTHER", "KHÁC", "KHAC" -> "OTHER";

            default -> throw new IllegalArgumentException(
                    "Giới tính không hợp lệ: "
                            + gender
            );
        };
    }

    private String normalizeStatus(
            String status
    ) {
        if (status == null
                || status.isBlank()) {

            return "ACTIVE";
        }

        String normalized =
                status.trim().toUpperCase();

        if (!normalized.equals("ACTIVE")
                && !normalized.equals("INACTIVE")) {

            throw new IllegalArgumentException(
                    "Trạng thái giảng viên không hợp lệ: "
                            + status
            );
        }

        return normalized;
    }

    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (value == null
                || value.isBlank()) {

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
}
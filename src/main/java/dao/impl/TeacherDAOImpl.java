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
import java.util.Locale;

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

    private static final int SQL_SERVER_FOREIGN_KEY_ERROR = 547;
    private static final int SQL_SERVER_DUPLICATE_KEY_ERROR = 2627;
    private static final int SQL_SERVER_DUPLICATE_INDEX_ERROR = 2601;

    /* =====================================================
       THÊM GIẢNG VIÊN
       ===================================================== */

    @Override
    public boolean insert(
            Teacher teacher
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            return insert(
                    connection,
                    teacher
            ) > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể thêm giảng viên.",
                    exception
            );
        }
    }

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

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            bindTeacherData(
                    statement,
                    teacher
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows <= 0) {
                throw new SQLException(
                        "Không thể tạo hồ sơ giảng viên."
                );
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {
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

    /* =====================================================
       CẬP NHẬT GIẢNG VIÊN
       ===================================================== */

    @Override
    public boolean update(
            Teacher teacher
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            return update(
                    connection,
                    teacher
            );

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể cập nhật giảng viên.",
                    exception
            );
        }
    }

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

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            bindTeacherData(
                    statement,
                    teacher
            );

            statement.setInt(
                    11,
                    teacher.getTeacherId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /* =====================================================
       XÓA GIẢNG VIÊN
       ===================================================== */

    @Override
    public boolean delete(
            int teacherId
    ) {
        validatePositiveId(
                teacherId,
                "ID giảng viên"
        );

        if (hasAssignedClasses(teacherId)) {
            throw new IllegalStateException(
                    "Không thể xóa giảng viên vì đang có lớp được phân công. "
                            + "Hãy chuyển giảng viên sang trạng thái INACTIVE."
            );
        }

        String sql =
                """
                DELETE FROM dbo.Teachers
                WHERE teacher_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    teacherId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể xóa giảng viên.",
                    exception
            );
        }
    }

    /* =====================================================
       TÌM GIẢNG VIÊN
       ===================================================== */

    @Override
    public Teacher getById(
            int teacherId
    ) {
        if (teacherId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE teacher_id = ?
                        """;

        return querySingleByInt(
                sql,
                teacherId,
                "Không thể tìm giảng viên theo ID."
        );
    }

    @Override
    public Teacher getByUserId(
            int userId
    ) {
        if (userId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE user_id = ?
                        """;

        return querySingleByInt(
                sql,
                userId,
                "Không thể tìm giảng viên theo tài khoản."
        );
    }

    @Override
    public Teacher getByCode(
            String teacherCode
    ) {
        if (
                teacherCode == null
                        || teacherCode.isBlank()
        ) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(teacher_code) = UPPER(?)
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    teacherCode.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapTeacher(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tìm giảng viên theo mã.",
                    exception
            );
        }
    }

    /* =====================================================
       DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    @Override
    public List<Teacher> getAll() {
        String sql =
                BASE_SELECT
                        + """
                        ORDER BY teacher_id DESC
                        """;

        return queryList(
                sql,
                null,
                null,
                "Không thể tải danh sách giảng viên."
        );
    }

    @Override
    public List<Teacher> search(
            String keyword
    ) {
        String sql =
                BASE_SELECT
                        + """
                        WHERE
                            teacher_code LIKE ?
                            OR full_name LIKE ?
                            OR COALESCE(email, '') LIKE ?
                            OR COALESCE(phone, '') LIKE ?
                            OR COALESCE(specialization, '') LIKE ?
                            OR status LIKE ?
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

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            for (int index = 1; index <= 6; index++) {
                statement.setString(
                        index,
                        searchValue
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    teachers.add(
                            mapTeacher(resultSet)
                    );
                }
            }

            return teachers;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tìm kiếm giảng viên.",
                    exception
            );
        }
    }

    @Override
    public List<Teacher> getByStatus(
            String status
    ) {
        String normalizedStatus =
                normalizeStatus(status);

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(status) = ?
                        ORDER BY teacher_id DESC
                        """;

        return queryList(
                sql,
                normalizedStatus,
                null,
                "Không thể tải giảng viên theo trạng thái."
        );
    }

    /* =====================================================
       KIỂM TRA TỒN TẠI
       ===================================================== */

    @Override
    public boolean existsByTeacherCode(
            String teacherCode
    ) {
        if (
                teacherCode == null
                        || teacherCode.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Teachers
                WHERE UPPER(teacher_code) = UPPER(?)
                """;

        return queryCount(
                sql,
                teacherCode.trim(),
                null,
                "Không thể kiểm tra mã giảng viên."
        ) > 0;
    }

    @Override
    public boolean existsByTeacherCodeExceptId(
            String teacherCode,
            int excludedTeacherId
    ) {
        if (
                teacherCode == null
                        || teacherCode.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Teachers
                WHERE UPPER(teacher_code) = UPPER(?)
                  AND teacher_id <> ?
                """;

        return queryCount(
                sql,
                teacherCode.trim(),
                excludedTeacherId,
                "Không thể kiểm tra mã giảng viên."
        ) > 0;
    }

    @Override
    public boolean existsByUserId(
            int userId
    ) {
        if (userId <= 0) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Teachers
                WHERE user_id = ?
                """;

        return queryCountByInt(
                sql,
                userId,
                "Không thể kiểm tra tài khoản giảng viên."
        ) > 0;
    }

    /* =====================================================
       TRẠNG THÁI GIẢNG VIÊN
       ===================================================== */

    @Override
    public boolean updateStatus(
            int teacherId,
            String status
    ) {
        validatePositiveId(
                teacherId,
                "ID giảng viên"
        );

        String normalizedStatus =
                normalizeStatus(status);

        String sql =
                """
                UPDATE dbo.Teachers
                SET
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE teacher_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    normalizedStatus
            );

            statement.setInt(
                    2,
                    teacherId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể cập nhật trạng thái giảng viên.",
                    exception
            );
        }
    }

    @Override
    public boolean activateTeacher(
            int teacherId
    ) {
        return updateStatus(
                teacherId,
                "ACTIVE"
        );
    }

    @Override
    public boolean deactivateTeacher(
            int teacherId
    ) {
        return updateStatus(
                teacherId,
                "INACTIVE"
        );
    }

    /* =====================================================
       KIỂM TRA LỚP PHỤ TRÁCH
       ===================================================== */

    @Override
    public boolean hasAssignedClasses(
            int teacherId
    ) {
        return countAssignedClasses(
                teacherId
        ) > 0;
    }

    @Override
    public int countAssignedClasses(
            int teacherId
    ) {
        validatePositiveId(
                teacherId,
                "ID giảng viên"
        );

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.CourseClasses
                WHERE teacher_id = ?
                """;

        return queryCountByInt(
                sql,
                teacherId,
                "Không thể đếm lớp của giảng viên."
        );
    }

    @Override
    public int countActiveClasses(
            int teacherId
    ) {
        validatePositiveId(
                teacherId,
                "ID giảng viên"
        );

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.CourseClasses
                WHERE teacher_id = ?
                  AND status IN ('OPEN', 'CLOSED')
                """;

        return queryCountByInt(
                sql,
                teacherId,
                "Không thể đếm lớp đang hoạt động."
        );
    }

    /* =====================================================
       HÀM DÙNG CHUNG
       ===================================================== */

    private Teacher querySingleByInt(
            String sql,
            int value,
            String errorMessage
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    value
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? mapTeacher(resultSet)
                        : null;
            }

        } catch (SQLException exception) {
            throw translateException(
                    errorMessage,
                    exception
            );
        }
    }

    private List<Teacher> queryList(
            String sql,
            String textParameter,
            Integer intParameter,
            String errorMessage
    ) {
        List<Teacher> teachers =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            int parameterIndex = 1;

            if (textParameter != null) {
                statement.setString(
                        parameterIndex++,
                        textParameter
                );
            }

            if (intParameter != null) {
                statement.setInt(
                        parameterIndex,
                        intParameter
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    teachers.add(
                            mapTeacher(resultSet)
                    );
                }
            }

            return teachers;

        } catch (SQLException exception) {
            throw translateException(
                    errorMessage,
                    exception
            );
        }
    }

    private int queryCount(
            String sql,
            String textParameter,
            Integer intParameter,
            String errorMessage
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    textParameter
            );

            if (intParameter != null) {
                statement.setInt(
                        2,
                        intParameter
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? resultSet.getInt("total")
                        : 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    errorMessage,
                    exception
            );
        }
    }

    private int queryCountByInt(
            String sql,
            int intParameter,
            String errorMessage
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    intParameter
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? resultSet.getInt("total")
                        : 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    errorMessage,
                    exception
            );
        }
    }

    private void bindTeacherData(
            PreparedStatement statement,
            Teacher teacher
    ) throws SQLException {

        statement.setString(
                1,
                normalizeTeacherCode(
                        teacher.getTeacherCode()
                )
        );

        Integer userId =
                teacher.getUserId();

        if (
                userId == null
                        || userId <= 0
        ) {
            statement.setNull(
                    2,
                    Types.INTEGER
            );
        } else {
            statement.setInt(
                    2,
                    userId
            );
        }

        statement.setString(
                3,
                normalizeRequiredText(
                        teacher.getFullName(),
                        "Họ tên giảng viên"
                )
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
    }

    private Teacher mapTeacher(
            ResultSet resultSet
    ) throws SQLException {

        Teacher teacher =
                new Teacher();

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

        teacher.setUserId(
                resultSet.wasNull()
                        ? null
                        : userId
        );

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

        teacher.setCreatedAt(
                createdAt
        );

        Timestamp updatedAt =
                resultSet.getTimestamp(
                        "updated_at"
                );

        teacher.setUpdatedAt(
                updatedAt
        );

        return teacher;
    }

    /* =====================================================
       VALIDATION VÀ CHUẨN HÓA
       ===================================================== */

    private void validateTeacherForInsert(
            Teacher teacher
    ) {
        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Thông tin giảng viên không được null."
            );
        }

        normalizeTeacherCode(
                teacher.getTeacherCode()
        );

        normalizeRequiredText(
                teacher.getFullName(),
                "Họ tên giảng viên"
        );
    }

    private void validateTeacherForUpdate(
            Teacher teacher
    ) {
        validateTeacherForInsert(
                teacher
        );

        validatePositiveId(
                teacher.getTeacherId(),
                "ID giảng viên"
        );
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " phải lớn hơn 0."
            );
        }
    }

    private String normalizeTeacherCode(
            String teacherCode
    ) {
        if (
                teacherCode == null
                        || teacherCode.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mã giảng viên không được để trống."
            );
        }

        return teacherCode
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizeRequiredText(
            String value,
            String fieldName
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            throw new IllegalArgumentException(
                    fieldName + " không được để trống."
            );
        }

        return value.trim();
    }

    private String normalizeGender(
            String gender
    ) {
        if (
                gender == null
                        || gender.isBlank()
        ) {
            return null;
        }

        String normalized =
                gender.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "MALE", "NAM" ->
                    "MALE";

            case "FEMALE", "NỮ", "NU" ->
                    "FEMALE";

            case "OTHER", "KHÁC", "KHAC" ->
                    "OTHER";

            default ->
                    throw new IllegalArgumentException(
                            "Giới tính không hợp lệ: "
                                    + gender
                    );
        };
    }

    private String normalizeStatus(
            String status
    ) {
        if (
                status == null
                        || status.isBlank()
        ) {
            return "ACTIVE";
        }

        String normalized =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        if (
                !"ACTIVE".equals(normalized)
                        && !"INACTIVE".equals(normalized)
        ) {
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

        if (
                value == null
                        || value.isBlank()
        ) {
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

        if (
                connection == null
                        || connection.isClosed()
        ) {
            throw new SQLException(
                    "Connection không hợp lệ hoặc đã đóng."
            );
        }
    }

    private RuntimeException translateException(
            String defaultMessage,
            SQLException exception
    ) {
        int errorCode =
                exception.getErrorCode();

        if (
                errorCode
                        == SQL_SERVER_DUPLICATE_KEY_ERROR
                        || errorCode
                        == SQL_SERVER_DUPLICATE_INDEX_ERROR
        ) {
            return new IllegalArgumentException(
                    "Mã giảng viên hoặc tài khoản liên kết đã tồn tại.",
                    exception
            );
        }

        if (
                errorCode
                        == SQL_SERVER_FOREIGN_KEY_ERROR
        ) {
            return new IllegalStateException(
                    "Dữ liệu giảng viên đang được sử dụng "
                            + "bởi lớp học hoặc dữ liệu liên quan.",
                    exception
            );
        }

        return new RuntimeException(
                defaultMessage
                        + "\nChi tiết: "
                        + exception.getMessage(),
                exception
        );
    }
}
package dao;

import model.Student;
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

public class StudentDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                student_id,
                student_code,
                user_id,
                full_name,
                date_of_birth,
                gender,
                email,
                phone,
                address,
                status,
                created_at,
                updated_at
            FROM dbo.Students
            """;

    /**
     * Thêm sinh viên bằng kết nối riêng.
     *
     * Giữ lại để tương thích với StudentController cũ.
     */
    public boolean addStudent(Student student) {
        try (Connection connection =
                     DBConnection.getConnection()) {

            return insert(
                    connection,
                    student
            ) > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể thêm sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Tên thay thế nếu code khác gọi insert(Student).
     */
    public boolean insert(Student student) {
        return addStudent(student);
    }

    /**
     * Thêm sinh viên trong Connection hiện tại.
     *
     * Dùng khi tạo đồng thời:
     * Users + Students + EmailVerifications.
     *
     * @return student_id vừa được tạo
     */
    public int insert(
            Connection connection,
            Student student
    ) throws SQLException {

        validateConnection(connection);
        validateStudentForInsert(student);

        String sql =
                """
                INSERT INTO dbo.Students
                (
                    student_code,
                    user_id,
                    full_name,
                    date_of_birth,
                    gender,
                    email,
                    phone,
                    address,
                    status,
                    created_at,
                    updated_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?,
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
                    normalizeStudentCode(
                            student.getStudentCode()
                    )
            );

            if (student.getUserId() == null) {
                statement.setNull(
                        2,
                        Types.INTEGER
                );
            } else {
                statement.setInt(
                        2,
                        student.getUserId()
                );
            }

            statement.setString(
                    3,
                    student.getFullName().trim()
            );

            if (student.getDateOfBirth() == null) {
                statement.setNull(
                        4,
                        Types.DATE
                );
            } else {
                statement.setDate(
                        4,
                        student.getDateOfBirth()
                );
            }

            setNullableString(
                    statement,
                    5,
                    normalizeGender(
                            student.getGender()
                    )
            );

            setNullableString(
                    statement,
                    6,
                    student.getEmail()
            );

            setNullableString(
                    statement,
                    7,
                    student.getPhone()
            );

            setNullableString(
                    statement,
                    8,
                    student.getAddress()
            );

            statement.setString(
                    9,
                    normalizeStatus(
                            student.getStatus()
                    )
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException(
                        "Không thể tạo hồ sơ sinh viên."
                );
            }

            try (ResultSet generatedKeys =
                         statement.getGeneratedKeys()) {

                if (generatedKeys.next()) {
                    int studentId =
                            generatedKeys.getInt(1);

                    student.setStudentID(
                            studentId
                    );

                    return studentId;
                }
            }
        }

        throw new SQLException(
                "Không lấy được student_id vừa tạo."
        );
    }

    /**
     * Lấy toàn bộ sinh viên.
     */
    public List<Student> getAllStudents() {
        String sql =
                BASE_SELECT
                        + """
                        ORDER BY student_id DESC
                        """;

        return queryList(sql);
    }

    /**
     * Tìm sinh viên theo ID.
     */
    public Student getStudentByID(int studentId) {
        if (studentId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE student_id = ?
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    studentId
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapStudent(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm sinh viên theo ID: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Tên tương thích với cách đặt tên mới.
     */
    public Student getStudentById(int studentId) {
        return getStudentByID(studentId);
    }

    /**
     * Tìm sinh viên theo mã sinh viên.
     */
    public Student getStudentByCode(
            String studentCode
    ) {
        if (studentCode == null
                || studentCode.isBlank()) {

            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(student_code) = UPPER(?)
                        """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    studentCode.trim()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapStudent(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm sinh viên theo mã: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Tìm hồ sơ sinh viên theo user_id.
     *
     * Phương thức này dùng sau khi tài khoản STUDENT đăng nhập.
     */
    public Student findByUserId(int userId) {
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
                    return mapStudent(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm hồ sơ sinh viên theo tài khoản: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    /**
     * Chỉ lấy student_id theo user_id.
     *
     * Dùng cho Dashboard và đăng ký môn học.
     */
    public int findStudentIdByUserId(int userId) {
        if (userId <= 0) {
            return 0;
        }

        String sql =
                """
                SELECT student_id
                FROM dbo.Students
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
                    return resultSet.getInt(
                            "student_id"
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể lấy student_id theo user_id: "
                            + exception.getMessage(),
                    exception
            );
        }

        return 0;
    }

    /**
     * Kiểm tra mã sinh viên đã tồn tại.
     */
    public boolean existsByStudentCode(
            String studentCode
    ) {
        if (studentCode == null
                || studentCode.isBlank()) {

            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Students
                WHERE UPPER(student_code) = UPPER(?)
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    studentCode.trim()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                return resultSet.next()
                        && resultSet.getInt("total") > 0;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra mã sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Kiểm tra user_id đã liên kết hồ sơ Student chưa.
     */
    public boolean existsByUserId(int userId) {
        if (userId <= 0) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Students
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
                    "Không thể kiểm tra tài khoản sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Cập nhật sinh viên bằng kết nối riêng.
     */
    public boolean updateStudent(Student student) {
        try (Connection connection =
                     DBConnection.getConnection()) {

            return update(
                    connection,
                    student
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Cập nhật sinh viên trong transaction hiện tại.
     */
    public boolean update(
            Connection connection,
            Student student
    ) throws SQLException {

        validateConnection(connection);
        validateStudentForUpdate(student);

        String sql =
                """
                UPDATE dbo.Students
                SET
                    student_code = ?,
                    user_id = ?,
                    full_name = ?,
                    date_of_birth = ?,
                    gender = ?,
                    email = ?,
                    phone = ?,
                    address = ?,
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE student_id = ?
                """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    normalizeStudentCode(
                            student.getStudentCode()
                    )
            );

            if (student.getUserId() == null) {
                statement.setNull(
                        2,
                        Types.INTEGER
                );
            } else {
                statement.setInt(
                        2,
                        student.getUserId()
                );
            }

            statement.setString(
                    3,
                    student.getFullName().trim()
            );

            if (student.getDateOfBirth() == null) {
                statement.setNull(
                        4,
                        Types.DATE
                );
            } else {
                statement.setDate(
                        4,
                        student.getDateOfBirth()
                );
            }

            setNullableString(
                    statement,
                    5,
                    normalizeGender(
                            student.getGender()
                    )
            );

            setNullableString(
                    statement,
                    6,
                    student.getEmail()
            );

            setNullableString(
                    statement,
                    7,
                    student.getPhone()
            );

            setNullableString(
                    statement,
                    8,
                    student.getAddress()
            );

            statement.setString(
                    9,
                    normalizeStatus(
                            student.getStatus()
                    )
            );

            statement.setInt(
                    10,
                    student.getStudentID()
            );

            return statement.executeUpdate() > 0;
        }
    }

    /**
     * Xóa sinh viên.
     */
    public boolean deleteStudent(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        String sql =
                """
                DELETE FROM dbo.Students
                WHERE student_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(
                    1,
                    studentId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể xóa sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Tìm kiếm sinh viên.
     */
    public List<Student> searchStudents(
            String keyword
    ) {
        String sql =
                BASE_SELECT
                        + """
                        WHERE
                            student_code LIKE ?
                            OR full_name LIKE ?
                            OR COALESCE(email, '') LIKE ?
                            OR COALESCE(phone, '') LIKE ?
                        ORDER BY student_id DESC
                        """;

        String value =
                "%"
                        + (
                        keyword == null
                                ? ""
                                : keyword.trim()
                )
                        + "%";

        List<Student> students =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, value);
            statement.setString(2, value);
            statement.setString(3, value);
            statement.setString(4, value);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {
                    students.add(
                            mapStudent(resultSet)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }

        return students;
    }

    /**
     * Lấy danh sách theo câu SQL không có tham số.
     */
    private List<Student> queryList(
            String sql
    ) {
        List<Student> students =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet =
                     statement.executeQuery()) {

            while (resultSet.next()) {
                students.add(
                        mapStudent(resultSet)
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }

        return students;
    }

    /**
     * Mapping một dòng SQL thành Student.
     */
    private Student mapStudent(
            ResultSet resultSet
    ) throws SQLException {

        Student student = new Student();

        student.setStudentID(
                resultSet.getInt(
                        "student_id"
                )
        );

        student.setStudentCode(
                resultSet.getString(
                        "student_code"
                )
        );

        int userId =
                resultSet.getInt(
                        "user_id"
                );

        if (resultSet.wasNull()) {
            student.setUserId(null);
        } else {
            student.setUserId(userId);
        }

        student.setFullName(
                resultSet.getString(
                        "full_name"
                )
        );

        student.setDateOfBirth(
                resultSet.getDate(
                        "date_of_birth"
                )
        );

        student.setGender(
                resultSet.getString(
                        "gender"
                )
        );

        student.setEmail(
                resultSet.getString(
                        "email"
                )
        );

        student.setPhone(
                resultSet.getString(
                        "phone"
                )
        );

        student.setAddress(
                resultSet.getString(
                        "address"
                )
        );

        student.setStatus(
                resultSet.getString(
                        "status"
                )
        );

        Timestamp createdAt =
                resultSet.getTimestamp(
                        "created_at"
                );

        if (createdAt != null) {
            student.setCreatedAt(
                    createdAt
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp(
                        "updated_at"
                );

        if (updatedAt != null) {
            student.setUpdatedAt(
                    updatedAt
            );
        }

        return student;
    }

    private void validateStudentForInsert(
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

        /*
         * Dữ liệu mới bắt buộc phải liên kết tài khoản.
         * Các bản ghi cũ user_id NULL vẫn được giữ trong database.
         */
        if (student.getUserId() == null
                || student.getUserId() <= 0) {

            throw new IllegalArgumentException(
                    "Sinh viên phải được liên kết với tài khoản Users."
            );
        }
    }

    private void validateStudentForUpdate(
            Student student
    ) {
        validateStudentForInsert(student);

        if (student.getStudentID() <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }
    }

    private String normalizeStudentCode(
            String studentCode
    ) {
        if (studentCode == null
                || studentCode.isBlank()) {

            throw new IllegalArgumentException(
                    "Mã sinh viên không được để trống."
            );
        }

        return studentCode
                .trim()
                .toUpperCase();
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
                    "Trạng thái sinh viên không hợp lệ: "
                            + status
            );
        }

        return normalized;
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
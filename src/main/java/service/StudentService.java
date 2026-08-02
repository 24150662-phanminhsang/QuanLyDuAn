package service;

import dao.StudentDAO;
import dao.UserDAO;
import model.AccountStatus;
import model.Role;
import model.Student;
import model.User;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StudentService {

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9._-]{4,50}$"
            );

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "^[0-9]{9,11}$"
            );

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    public StudentService() {
        this(
                new StudentDAO(),
                new UserDAO()
        );
    }

    public StudentService(
            StudentDAO studentDAO,
            UserDAO userDAO
    ) {
        if (studentDAO == null) {
            throw new IllegalArgumentException(
                    "StudentDAO không được null."
            );
        }

        if (userDAO == null) {
            throw new IllegalArgumentException(
                    "UserDAO không được null."
            );
        }

        this.studentDAO = studentDAO;
        this.userDAO = userDAO;
    }

    /* =====================================================
       TẠO TÀI KHOẢN VÀ HỒ SƠ HỌC VIÊN
       ===================================================== */

    /**
     * Admin tạo đồng thời tài khoản Users và hồ sơ Students.
     *
     * Luồng transaction:
     * 1. Kiểm tra username, email và mã học viên.
     * 2. INSERT dbo.Users.
     * 3. Lấy user_id vừa tạo.
     * 4. Gán user_id vào Student.
     * 5. INSERT dbo.Students.
     * 6. COMMIT.
     *
     * Nếu bất kỳ bước nào lỗi thì ROLLBACK.
     */
    public boolean createStudentAccount(
            Student student,
            String username,
            String rawPassword
    ) {
        validateStudentForCreate(
                student
        );

        validateUsername(
                username
        );

        validatePassword(
                rawPassword
        );

        normalizeStudent(
                student
        );

        String normalizedUsername =
                username.trim();

        try {
            if (
                    userDAO.existsByUsername(
                            normalizedUsername
                    )
            ) {
                throw new IllegalArgumentException(
                        "Tên đăng nhập đã tồn tại."
                );
            }

            if (
                    student.getEmail() != null
                            && userDAO.existsByEmail(
                            student.getEmail()
                    )
            ) {
                throw new IllegalArgumentException(
                        "Email đã được sử dụng bởi tài khoản khác."
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra thông tin tài khoản.",
                    exception
            );
        }

        if (
                studentDAO.existsByStudentCode(
                        student.getStudentCode()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã học viên đã tồn tại."
            );
        }

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(
                        false
                );

                User user =
                        buildStudentUser(
                                student,
                                normalizedUsername,
                                rawPassword
                        );

                int userId =
                        userDAO.insert(
                                connection,
                                user
                        );

                if (userId <= 0) {
                    throw new SQLException(
                            "Không lấy được user_id vừa tạo."
                    );
                }

                student.setUserId(
                        userId
                );

                int studentId =
                        studentDAO.insert(
                                connection,
                                student
                        );

                if (studentId <= 0) {
                    throw new SQLException(
                            "Không lấy được student_id vừa tạo."
                    );
                }

                connection.commit();

                return true;

            } catch (
                    SQLException
                    | RuntimeException exception
            ) {
                rollback(
                        connection,
                        exception
                );

                throw translateCreateException(
                        exception
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể mở kết nối để tạo học viên.",
                    exception
            );
        }
    }

    /**
     * Phiên bản có xác nhận mật khẩu để View gọi trực tiếp.
     */
    public boolean createStudentAccount(
            Student student,
            String username,
            String rawPassword,
            String confirmPassword
    ) {
        if (
                confirmPassword == null
                        || !rawPassword.equals(
                        confirmPassword
                )
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu xác nhận không khớp."
            );
        }

        return createStudentAccount(
                student,
                username,
                rawPassword
        );
    }

    /* =====================================================
       THÊM HỒ SƠ CŨ
       ===================================================== */

    /**
     * Giữ tương thích với code cũ.
     *
     * Phương thức này chỉ dùng khi Student đã có user_id.
     */
    public boolean addStudent(
            Student student
    ) {
        validateStudentForCreate(
                student
        );

        normalizeStudent(
                student
        );

        if (
                student.getUserId() == null
                        || student.getUserId() <= 0
        ) {
            throw new IllegalArgumentException(
                    "Học viên chưa được liên kết với tài khoản Users. "
                            + "Hãy dùng createStudentAccount(...)."
            );
        }

        if (
                studentDAO.existsByStudentCode(
                        student.getStudentCode()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã học viên đã tồn tại."
            );
        }

        if (
                studentDAO.existsByUserId(
                        student.getUserId()
                )
        ) {
            throw new IllegalArgumentException(
                    "Tài khoản này đã có hồ sơ học viên."
            );
        }

        return studentDAO.addStudent(
                student
        );
    }

    /* =====================================================
       DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    public List<Student> getAllStudents() {
        return safe(
                studentDAO.getAllStudents()
        );
    }

    public List<Student> searchStudents(
            String keyword
    ) {
        if (
                keyword == null
                        || keyword.isBlank()
        ) {
            return getAllStudents();
        }

        return safe(
                studentDAO.searchStudents(
                        keyword.trim()
                )
        );
    }

    /* =====================================================
       TÌM HỌC VIÊN
       ===================================================== */

    public Student getStudentByID(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID học viên"
        );

        return studentDAO.getStudentByID(
                studentId
        );
    }

    public Student getStudentById(
            int studentId
    ) {
        return getStudentByID(
                studentId
        );
    }

    public Student getStudentByCode(
            String studentCode
    ) {
        if (
                studentCode == null
                        || studentCode.isBlank()
        ) {
            return null;
        }

        return studentDAO.getStudentByCode(
                studentCode.trim()
        );
    }

    public Student getStudentByUserId(
            int userId
    ) {
        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return studentDAO.findByUserId(
                userId
        );
    }

    /* =====================================================
       CẬP NHẬT HỌC VIÊN
       ===================================================== */

    public boolean updateStudent(
            Student student
    ) {
        validateStudentForUpdate(
                student
        );

        normalizeStudent(
                student
        );

        Student currentStudent =
                studentDAO.getStudentByID(
                        student.getStudentID()
                );

        if (currentStudent == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy học viên cần cập nhật."
            );
        }

        Student sameCode =
                studentDAO.getStudentByCode(
                        student.getStudentCode()
                );

        if (
                sameCode != null
                        && sameCode.getStudentID()
                        != student.getStudentID()
        ) {
            throw new IllegalArgumentException(
                    "Mã học viên đã được sử dụng."
            );
        }

        /*
         * Không cho thay đổi liên kết user_id tùy ý.
         * Nếu View không truyền user_id thì giữ lại giá trị hiện tại.
         */
        if (student.getUserId() == null) {
            student.setUserId(
                    currentStudent.getUserId()
            );
        }

        return studentDAO.updateStudent(
                student
        );
    }

    /**
     * Cập nhật hồ sơ và đồng bộ thông tin cơ bản sang Users.
     *
     * Không đổi username và không đổi mật khẩu.
     */
    public boolean updateStudentAndUser(
            Student student
    ) {
        validateStudentForUpdate(
                student
        );

        normalizeStudent(
                student
        );

        Student currentStudent =
                studentDAO.getStudentByID(
                        student.getStudentID()
                );

        if (currentStudent == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy học viên cần cập nhật."
            );
        }

        if (
                currentStudent.getUserId() == null
                        || currentStudent.getUserId() <= 0
        ) {
            return updateStudent(
                    student
            );
        }

        int userId =
                currentStudent.getUserId();

        student.setUserId(
                userId
        );

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(
                        false
                );

                boolean studentUpdated =
                        studentDAO.update(
                                connection,
                                student
                        );

                if (!studentUpdated) {
                    throw new SQLException(
                            "Không thể cập nhật hồ sơ học viên."
                    );
                }

                updateUserBasicInformation(
                        connection,
                        userId,
                        student
                );

                connection.commit();

                return true;

            } catch (
                    SQLException
                    | RuntimeException exception
            ) {
                rollback(
                        connection,
                        exception
                );

                throw new RuntimeException(
                        "Không thể cập nhật học viên và tài khoản.",
                        exception
                );

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể mở kết nối cập nhật học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       ĐỔI MẬT KHẨU TÀI KHOẢN HỌC VIÊN
       ===================================================== */

    public boolean resetStudentPassword(
            int studentId,
            String newPassword,
            String confirmPassword
    ) {
        validatePassword(
                newPassword
        );

        if (
                confirmPassword == null
                        || !newPassword.equals(
                        confirmPassword
                )
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu xác nhận không khớp."
            );
        }

        Student student =
                getStudentByID(
                        studentId
                );

        if (
                student == null
                        || student.getUserId() == null
                        || student.getUserId() <= 0
        ) {
            throw new IllegalStateException(
                    "Học viên chưa có tài khoản đăng nhập."
            );
        }

        try {
            return userDAO.resetPassword(
                    student.getUserId(),
                    newPassword
            );

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể đổi mật khẩu học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       XÓA HỌC VIÊN
       ===================================================== */

    public boolean deleteStudent(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID học viên"
        );

        return studentDAO.deleteStudent(
                studentId
        );
    }

    /* =====================================================
       KIỂM TRA TỒN TẠI
       ===================================================== */

    public boolean existsByStudentCode(
            String studentCode
    ) {
        if (
                studentCode == null
                        || studentCode.isBlank()
        ) {
            return false;
        }

        return studentDAO.existsByStudentCode(
                studentCode.trim()
        );
    }

    public boolean existsByUserId(
            int userId
    ) {
        if (userId <= 0) {
            return false;
        }

        return studentDAO.existsByUserId(
                userId
        );
    }

    /* =====================================================
       TẠO USER CHO HỌC VIÊN
       ===================================================== */

    private User buildStudentUser(
            Student student,
            String username,
            String rawPassword
    ) {
        User user =
                new User();

        user.setUsername(
                username
        );

        user.setPasswordHash(
                PasswordUtil.hashPassword(
                        rawPassword
                )
        );

        user.setFullName(
                student.getFullName()
        );

        user.setEmail(
                student.getEmail()
        );

        user.setPhone(
                student.getPhone()
        );

        user.setRole(
                Role.STUDENT
        );

        user.setRoleId(
                3
        );

        user.setStatus(
                AccountStatus.ACTIVE
        );

        user.setEmailVerified(
                true
        );

        user.setEmailVerifiedAt(
                LocalDateTime.now()
        );

        user.setRegistrationSource(
                "ADMIN"
        );

        user.setLoginAttempts(
                0
        );

        return user;
    }

    private void updateUserBasicInformation(
            Connection connection,
            int userId,
            Student student
    ) throws SQLException {

        String sql =
                """
                UPDATE dbo.Users
                SET
                    full_name = ?,
                    email = ?,
                    phone = ?,
                    status = ?,
                    updated_at = SYSDATETIME()
                WHERE user_id = ?
                """;

        try (
                var statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    student.getFullName()
            );

            setNullableString(
                    statement,
                    2,
                    student.getEmail()
            );

            setNullableString(
                    statement,
                    3,
                    student.getPhone()
            );

            statement.setString(
                    4,
                    normalizeStatus(
                            student.getStatus()
                    )
            );

            statement.setInt(
                    5,
                    userId
            );

            if (statement.executeUpdate() <= 0) {
                throw new SQLException(
                        "Không thể cập nhật tài khoản học viên."
                );
            }
        }
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateStudentForCreate(
            Student student
    ) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Thông tin học viên không được null."
            );
        }

        if (
                student.getStudentCode() == null
                        || student.getStudentCode().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mã học viên không được để trống."
            );
        }

        if (
                student.getFullName() == null
                        || student.getFullName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Họ tên học viên không được để trống."
            );
        }

        validateEmail(
                student.getEmail()
        );

        validatePhone(
                student.getPhone()
        );

        normalizeStatus(
                student.getStatus()
        );
    }

    private void validateStudentForUpdate(
            Student student
    ) {
        validateStudentForCreate(
                student
        );

        validatePositiveId(
                student.getStudentID(),
                "ID học viên"
        );
    }

    private void validateUsername(
            String username
    ) {
        if (
                username == null
                        || username.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống."
            );
        }

        String normalized =
                username.trim();

        if (!USERNAME_PATTERN.matcher(
                normalized
        ).matches()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập phải có từ 4 đến 50 ký tự "
                            + "và chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang."
            );
        }
    }

    private void validatePassword(
            String password
    ) {
        if (
                password == null
                        || password.length()
                        < MIN_PASSWORD_LENGTH
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất "
                            + MIN_PASSWORD_LENGTH
                            + " ký tự."
            );
        }
    }

    private void validateEmail(
            String email
    ) {
        if (
                email == null
                        || email.isBlank()
        ) {
            return;
        }

        if (!EMAIL_PATTERN.matcher(
                email.trim()
        ).matches()) {
            throw new IllegalArgumentException(
                    "Email không đúng định dạng."
            );
        }
    }

    private void validatePhone(
            String phone
    ) {
        if (
                phone == null
                        || phone.isBlank()
        ) {
            return;
        }

        String normalizedPhone =
                phone.replaceAll(
                        "\\s+",
                        ""
                );

        if (!PHONE_PATTERN.matcher(
                normalizedPhone
        ).matches()) {
            throw new IllegalArgumentException(
                    "Số điện thoại phải gồm từ 9 đến 11 chữ số."
            );
        }
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }

    /* =====================================================
       CHUẨN HÓA
       ===================================================== */

    private void normalizeStudent(
            Student student
    ) {
        student.setStudentCode(
                student.getStudentCode()
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
        );

        student.setFullName(
                student.getFullName()
                        .trim()
        );

        student.setEmail(
                normalizeNullable(
                        student.getEmail()
                )
        );

        student.setPhone(
                normalizePhone(
                        student.getPhone()
                )
        );

        student.setAddress(
                normalizeNullable(
                        student.getAddress()
                )
        );

        student.setStatus(
                normalizeStatus(
                        student.getStatus()
                )
        );
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
                    "Trạng thái học viên không hợp lệ: "
                            + status
            );
        }

        return normalized;
    }

    private String normalizeNullable(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }

    private String normalizePhone(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.replaceAll(
                "\\s+",
                ""
        );
    }

    private void setNullableString(
            java.sql.PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {

        if (
                value == null
                        || value.isBlank()
        ) {
            statement.setNull(
                    parameterIndex,
                    java.sql.Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }

    private List<Student> safe(
            List<Student> students
    ) {
        return students == null
                ? Collections.emptyList()
                : students;
    }

    /* =====================================================
       TRANSACTION
       ===================================================== */

    private void rollback(
            Connection connection,
            Exception originalException
    ) {
        try {
            connection.rollback();

        } catch (SQLException rollbackException) {
            originalException.addSuppressed(
                    rollbackException
            );
        }
    }

    private void restoreAutoCommit(
            Connection connection,
            boolean originalAutoCommit
    ) {
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

    private RuntimeException translateCreateException(
            Exception exception
    ) {
        if (
                exception
                        instanceof IllegalArgumentException
                        illegalArgumentException
        ) {
            return illegalArgumentException;
        }

        if (
                exception
                        instanceof IllegalStateException
                        illegalStateException
        ) {
            return illegalStateException;
        }

        Throwable cause =
                exception;

        while (
                cause != null
                        && cause.getCause() != null
        ) {
            cause =
                    cause.getCause();
        }

        String detail =
                cause == null
                        || cause.getMessage() == null
                        ? "Không xác định"
                        : cause.getMessage();

        return new RuntimeException(
                "Không thể tạo tài khoản học viên. "
                        + "Chi tiết: "
                        + detail,
                exception
        );
    }
}
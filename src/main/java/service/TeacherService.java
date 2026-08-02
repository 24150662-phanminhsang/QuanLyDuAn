package service;

import dao.TeacherDAO;
import dao.UserDAO;
import dao.impl.TeacherDAOImpl;
import model.AccountStatus;
import model.Role;
import model.Teacher;
import model.User;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class TeacherService {

    private static final String ACTIVE = "ACTIVE";
    private static final String INACTIVE = "INACTIVE";

    private static final Pattern USERNAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9._-]{4,50}$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9]{9,11}$");

    private final TeacherDAO teacherDAO;
    private final UserDAO userDAO;

    public TeacherService() {
        this(new TeacherDAOImpl(), new UserDAO());
    }
    public boolean updateProfile(Teacher teacher) {
        return updateTeacherAndUser(teacher);
    }

    public TeacherService(
            TeacherDAO teacherDAO,
            UserDAO userDAO
    ) {
        if (teacherDAO == null || userDAO == null) {
            throw new IllegalArgumentException(
                    "DAO không được null."
            );
        }

        this.teacherDAO = teacherDAO;
        this.userDAO = userDAO;
    }

    /* =====================================================
       TẠO USER + TEACHER TRONG CÙNG TRANSACTION
       ===================================================== */

    public boolean createTeacherAccount(
            Teacher teacher,
            String username,
            String password,
            String confirmPassword
    ) {
        validateTeacherForCreate(teacher);
        validateUsername(username);
        validatePassword(password);

        if (confirmPassword == null
                || !password.equals(confirmPassword)) {
            throw new IllegalArgumentException(
                    "Mật khẩu xác nhận không khớp."
            );
        }

        normalizeTeacher(teacher);
        String normalizedUsername = username.trim();

        try {
            if (userDAO.existsByUsername(normalizedUsername)) {
                throw new IllegalArgumentException(
                        "Tên đăng nhập đã tồn tại."
                );
            }

            if (teacher.getEmail() != null
                    && userDAO.existsByEmail(
                    teacher.getEmail()
            )) {
                throw new IllegalArgumentException(
                        "Email đã được sử dụng."
                );
            }
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra tài khoản.",
                    exception
            );
        }

        if (teacherDAO.existsByTeacherCode(
                teacher.getTeacherCode()
        )) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã tồn tại."
            );
        }

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean oldAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                User user = buildTeacherUser(
                        teacher,
                        normalizedUsername,
                        password
                );

                int userId =
                        userDAO.insert(
                                connection,
                                user
                        );

                if (userId <= 0) {
                    throw new SQLException(
                            "Không lấy được user_id."
                    );
                }

                teacher.setUserId(userId);

                int teacherId =
                        teacherDAO.insert(
                                connection,
                                teacher
                        );

                if (teacherId <= 0) {
                    throw new SQLException(
                            "Không lấy được teacher_id."
                    );
                }

                connection.commit();
                return true;

            } catch (SQLException
                     | RuntimeException exception) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }

                if (exception
                        instanceof IllegalArgumentException
                        illegalArgumentException) {
                    throw illegalArgumentException;
                }

                throw new RuntimeException(
                        "Không thể tạo tài khoản giảng viên: "
                                + rootMessage(exception),
                        exception
                );

            } finally {
                try {
                    connection.setAutoCommit(
                            oldAutoCommit
                    );
                } catch (SQLException ignored) {
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể mở kết nối tạo giảng viên.",
                    exception
            );
        }
    }

    /* =====================================================
       CRUD
       ===================================================== */

    public boolean addTeacher(
            Teacher teacher
    ) {
        validateTeacherForCreate(teacher);
        normalizeTeacher(teacher);

        if (teacher.getUserId() == null
                || teacher.getUserId() <= 0) {
            throw new IllegalArgumentException(
                    "Hãy dùng createTeacherAccount(...) "
                            + "để tạo tài khoản và giảng viên."
            );
        }

        if (teacherDAO.existsByTeacherCode(
                teacher.getTeacherCode()
        )) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã tồn tại."
            );
        }

        if (teacherDAO.existsByUserId(
                teacher.getUserId()
        )) {
            throw new IllegalArgumentException(
                    "Tài khoản đã có hồ sơ giảng viên."
            );
        }

        return teacherDAO.insert(teacher);
    }

    public boolean updateTeacher(
            Teacher teacher
    ) {
        validateTeacherForUpdate(teacher);
        normalizeTeacher(teacher);

        if (teacherDAO.existsByTeacherCodeExceptId(
                teacher.getTeacherCode(),
                teacher.getTeacherId()
        )) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã được sử dụng."
            );
        }

        Teacher current =
                requireTeacher(
                        teacher.getTeacherId()
                );

        if (teacher.getUserId() == null) {
            teacher.setUserId(
                    current.getUserId()
            );
        }

        return teacherDAO.update(teacher);
    }

    public boolean updateTeacherAndUser(
            Teacher teacher
    ) {
        validateTeacherForUpdate(teacher);
        normalizeTeacher(teacher);

        Teacher current =
                requireTeacher(
                        teacher.getTeacherId()
                );

        if (teacherDAO.existsByTeacherCodeExceptId(
                teacher.getTeacherCode(),
                teacher.getTeacherId()
        )) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã được sử dụng."
            );
        }

        if (current.getUserId() == null
                || current.getUserId() <= 0) {
            return updateTeacher(teacher);
        }

        int userId = current.getUserId();
        teacher.setUserId(userId);

        try (Connection connection =
                     DBConnection.getConnection()) {

            boolean oldAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                if (!teacherDAO.update(
                        connection,
                        teacher
                )) {
                    throw new SQLException(
                            "Không thể cập nhật giảng viên."
                    );
                }

                updateUserBasic(
                        connection,
                        userId,
                        teacher
                );

                connection.commit();
                return true;

            } catch (SQLException
                     | RuntimeException exception) {

                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }

                throw new RuntimeException(
                        "Không thể cập nhật giảng viên "
                                + "và tài khoản: "
                                + rootMessage(exception),
                        exception
                );

            } finally {
                try {
                    connection.setAutoCommit(
                            oldAutoCommit
                    );
                } catch (SQLException ignored) {
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể mở kết nối cập nhật.",
                    exception
            );
        }
    }

    public boolean deleteTeacher(
            int teacherId
    ) {
        Teacher teacher =
                requireTeacher(teacherId);

        if (teacherDAO.hasAssignedClasses(
                teacherId
        )) {
            throw new IllegalStateException(
                    "Không thể xóa giảng viên \""
                            + safe(teacher.getFullName())
                            + "\" vì đang phụ trách lớp. "
                            + "Hãy chuyển sang INACTIVE."
            );
        }

        return teacherDAO.delete(teacherId);
    }

    public boolean canDeleteTeacher(
            int teacherId
    ) {
        return teacherId > 0
                && teacherDAO.getById(teacherId)
                != null
                && !teacherDAO.hasAssignedClasses(
                teacherId
        );
    }

    /* =====================================================
       DANH SÁCH / TÌM KIẾM
       ===================================================== */

    public Teacher getTeacherById(int id) {
        validateId(id);
        return teacherDAO.getById(id);
    }

    public Teacher getTeacherByUserId(int userId) {
        validateId(userId);
        return teacherDAO.getByUserId(userId);
    }

    public Teacher getTeacherByCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }

        return teacherDAO.getByCode(code.trim());
    }

    public List<Teacher> getAllTeachers() {
        return safeList(teacherDAO.getAll());
    }

    public List<Teacher> searchTeachers(
            String keyword
    ) {
        return keyword == null
                || keyword.isBlank()
                ? getAllTeachers()
                : safeList(
                teacherDAO.search(
                        keyword.trim()
                )
        );
    }

    public List<Teacher> getTeachersByStatus(
            String status
    ) {
        return safeList(
                teacherDAO.getByStatus(
                        normalizeStatus(status)
                )
        );
    }

    /* =====================================================
       TRẠNG THÁI / LỚP
       ===================================================== */

    public boolean activateTeacher(int id) {
        Teacher teacher = requireTeacher(id);
        boolean updated =
                teacherDAO.activateTeacher(id);
        syncUserStatus(
                teacher.getUserId(),
                AccountStatus.ACTIVE
        );
        return updated;
    }

    public boolean deactivateTeacher(int id) {
        Teacher teacher = requireTeacher(id);
        boolean updated =
                teacherDAO.deactivateTeacher(id);
        syncUserStatus(
                teacher.getUserId(),
                AccountStatus.INACTIVE
        );
        return updated;
    }

    public boolean updateTeacherStatus(
            int id,
            String status
    ) {
        String normalized =
                normalizeStatus(status);

        return ACTIVE.equals(normalized)
                ? activateTeacher(id)
                : deactivateTeacher(id);
    }

    public boolean hasAssignedClasses(int id) {
        return id > 0
                && teacherDAO.hasAssignedClasses(id);
    }

    public int countAssignedClasses(int id) {
        validateId(id);
        return teacherDAO.countAssignedClasses(id);
    }

    public int countActiveClasses(int id) {
        validateId(id);
        return teacherDAO.countActiveClasses(id);
    }

    public boolean existsByTeacherCode(String code) {
        return code != null
                && !code.isBlank()
                && teacherDAO.existsByTeacherCode(
                code.trim()
        );
    }

    public boolean existsByUserId(int userId) {
        return userId > 0
                && teacherDAO.existsByUserId(userId);
    }

    /* =====================================================
       PRIVATE
       ===================================================== */

    private User buildTeacherUser(
            Teacher teacher,
            String username,
            String password
    ) {
        User user = new User();

        user.setUsername(username);
        user.setPasswordHash(
                PasswordUtil.hashPassword(password)
        );
        user.setFullName(
                teacher.getFullName()
        );
        user.setEmail(teacher.getEmail());
        user.setPhone(teacher.getPhone());
        user.setRole(Role.TEACHER);
        user.setRoleId(2);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(
                LocalDateTime.now()
        );
        user.setRegistrationSource("ADMIN");
        user.setLoginAttempts(0);

        return user;
    }

    private void updateUserBasic(
            Connection connection,
            int userId,
            Teacher teacher
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

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    teacher.getFullName()
            );

            setNullable(
                    statement,
                    2,
                    teacher.getEmail()
            );

            setNullable(
                    statement,
                    3,
                    teacher.getPhone()
            );

            statement.setString(
                    4,
                    normalizeStatus(
                            teacher.getStatus()
                    )
            );

            statement.setInt(5, userId);

            if (statement.executeUpdate() <= 0) {
                throw new SQLException(
                        "Không thể cập nhật tài khoản."
                );
            }
        }
    }

    private void syncUserStatus(
            Integer userId,
            AccountStatus status
    ) {
        if (userId == null || userId <= 0) {
            return;
        }

        try {
            userDAO.updateStatus(
                    userId,
                    status
            );
        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể đồng bộ trạng thái tài khoản.",
                    exception
            );
        }
    }

    private void validateTeacherForCreate(
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
                    "Họ tên không được để trống."
            );
        }

        if (teacher.getSpecialization() == null
                || teacher.getSpecialization().isBlank()) {
            throw new IllegalArgumentException(
                    "Chuyên môn không được để trống."
            );
        }

        validateEmail(teacher.getEmail());
        validatePhone(teacher.getPhone());
        normalizeStatus(teacher.getStatus());
    }

    private void validateTeacherForUpdate(
            Teacher teacher
    ) {
        validateTeacherForCreate(teacher);
        validateId(teacher.getTeacherId());
    }

    private void validateUsername(String username) {
        if (username == null
                || !USERNAME_PATTERN.matcher(
                username.trim()
        ).matches()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập phải có 4-50 ký tự "
                            + "và chỉ gồm chữ, số, dấu chấm, "
                            + "gạch dưới hoặc gạch ngang."
            );
        }
    }

    private void validatePassword(String password) {
        if (password == null
                || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );
        }
    }

    private void validateEmail(String email) {
        if (email != null
                && !email.isBlank()
                && !EMAIL_PATTERN.matcher(
                email.trim()
        ).matches()) {
            throw new IllegalArgumentException(
                    "Email không đúng định dạng."
            );
        }
    }

    private void validatePhone(String phone) {
        if (phone != null
                && !phone.isBlank()
                && !PHONE_PATTERN.matcher(
                phone.replaceAll("\\s+", "")
        ).matches()) {
            throw new IllegalArgumentException(
                    "Số điện thoại phải gồm 9-11 chữ số."
            );
        }
    }

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID phải lớn hơn 0."
            );
        }
    }

    private Teacher requireTeacher(int id) {
        validateId(id);
        Teacher teacher = teacherDAO.getById(id);

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy giảng viên."
            );
        }

        return teacher;
    }

    private void normalizeTeacher(
            Teacher teacher
    ) {
        teacher.setTeacherCode(
                teacher.getTeacherCode()
                        .trim()
                        .toUpperCase(Locale.ROOT)
        );

        teacher.setFullName(
                teacher.getFullName().trim()
        );

        teacher.setEmail(
                normalizeNullable(
                        teacher.getEmail()
                )
        );

        teacher.setPhone(
                teacher.getPhone() == null
                        || teacher.getPhone().isBlank()
                        ? null
                        : teacher.getPhone()
                        .replaceAll("\\s+", "")
        );

        teacher.setAddress(
                normalizeNullable(
                        teacher.getAddress()
                )
        );

        teacher.setSpecialization(
                teacher.getSpecialization().trim()
        );

        teacher.setStatus(
                normalizeStatus(
                        teacher.getStatus()
                )
        );
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return ACTIVE;
        }

        String value =
                status.trim()
                        .toUpperCase(Locale.ROOT);

        if (!ACTIVE.equals(value)
                && !INACTIVE.equals(value)) {
            throw new IllegalArgumentException(
                    "Trạng thái không hợp lệ: "
                            + status
            );
        }

        return value;
    }

    private String normalizeNullable(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }

    private void setNullable(
            PreparedStatement statement,
            int index,
            String value
    ) throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value.trim());
        }
    }

    private List<Teacher> safeList(
            List<Teacher> teachers
    ) {
        return teachers == null
                ? Collections.emptyList()
                : teachers;
    }

    private String safe(String value) {
        return value == null || value.isBlank()
                ? "không xác định"
                : value.trim();
    }

    private String rootMessage(Throwable throwable) {
        Throwable current = throwable;

        while (current != null
                && current.getCause() != null) {
            current = current.getCause();
        }

        return current == null
                || current.getMessage() == null
                ? "Không xác định"
                : current.getMessage();
    }
}

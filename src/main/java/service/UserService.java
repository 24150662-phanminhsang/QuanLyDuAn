package service;

import dao.StudentDAO;
import dao.TeacherDAO;
import dao.UserDAO;
import dao.impl.TeacherDAOImpl;
import model.AccountStatus;
import model.Role;
import model.Student;
import model.Teacher;
import model.User;
import util.DBConnection;
import util.PasswordUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;
    private final StudentDAO studentDAO;
    private final TeacherDAO teacherDAO;

    public UserService() {
        this(
                new UserDAO(),
                new StudentDAO(),
                new TeacherDAOImpl()
        );
    }

    public UserService(
            UserDAO userDAO,
            StudentDAO studentDAO,
            TeacherDAO teacherDAO
    ) {
        if (userDAO == null) {
            throw new IllegalArgumentException(
                    "UserDAO không được null."
            );
        }

        if (studentDAO == null) {
            throw new IllegalArgumentException(
                    "StudentDAO không được null."
            );
        }

        if (teacherDAO == null) {
            throw new IllegalArgumentException(
                    "TeacherDAO không được null."
            );
        }

        this.userDAO = userDAO;
        this.studentDAO = studentDAO;
        this.teacherDAO = teacherDAO;
    }

    /* =====================================================
       DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    public List<User> getAllUsers()
            throws SQLException {

        List<User> users =
                userDAO.findAll();

        return users == null
                ? Collections.emptyList()
                : users;
    }

    public Optional<User> getUserById(
            int userId
    ) throws SQLException {

        if (userId <= 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                userDAO.findById(userId)
        );
    }

    public List<User> getUsers(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {

        validatePagination(
                page,
                pageSize
        );

        List<User> users =
                userDAO.search(
                        keyword,
                        page,
                        pageSize
                );

        return users == null
                ? Collections.emptyList()
                : users;
    }

    public int countUsers(
            String keyword
    ) throws SQLException {

        return userDAO.count(keyword);
    }

    public int getTotalPages(
            String keyword,
            int pageSize
    ) throws SQLException {

        if (pageSize <= 0) {
            throw new IllegalArgumentException(
                    "Số dòng trên trang phải lớn hơn 0."
            );
        }

        return userDAO.getTotalPages(
                keyword,
                pageSize
        );
    }

    public List<User> getPendingTeachers()
            throws SQLException {

        List<User> users =
                userDAO.findPendingTeachers();

        return users == null
                ? Collections.emptyList()
                : users;
    }

    /* =====================================================
       TẠO TÀI KHOẢN THEO VAI TRÒ
       ===================================================== */

    /**
     * Hàm cũ được giữ để tương thích.
     *
     * Chỉ nên dùng trực tiếp cho ADMIN.
     * STUDENT và TEACHER cần gọi createRoleAccount(...)
     * để tạo đồng thời Users + hồ sơ vai trò.
     */
    public boolean createUser(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            int roleId
    ) throws SQLException {

        Role role =
                convertRoleIdToRole(roleId);

        if (role != Role.ADMIN) {
            throw new IllegalArgumentException(
                    "Student hoặc Teacher cần nhập đầy đủ "
                            + "hồ sơ theo vai trò."
            );
        }

        User user =
                buildAdminCreatedUser(
                        username,
                        password,
                        fullName,
                        email,
                        phone,
                        role
                );

        validateUniqueUserData(user);

        return userDAO.insert(user);
    }

    /**
     * Tạo tài khoản từ trang Admin.
     *
     * ADMIN:
     * - Chỉ tạo Users.
     *
     * STUDENT:
     * - Tạo Users + Students trong cùng transaction.
     *
     * TEACHER:
     * - Tạo Users + Teachers trong cùng transaction.
     */
    public boolean createRoleAccount(
            String username,
            String rawPassword,
            String fullName,
            String email,
            String phone,
            Role role,
            Student studentProfile,
            Teacher teacherProfile
    ) throws SQLException {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống."
            );
        }

        User user =
                buildAdminCreatedUser(
                        username,
                        rawPassword,
                        fullName,
                        email,
                        phone,
                        role
                );

        validateUniqueUserData(user);
        validateRoleProfile(
                role,
                studentProfile,
                teacherProfile
        );

        if (role == Role.STUDENT) {
            validateUniqueStudentProfile(
                    studentProfile
            );
        }

        if (role == Role.TEACHER) {
            validateUniqueTeacherProfile(
                    teacherProfile
            );
        }

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            boolean originalAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                int userId =
                        userDAO.insert(
                                connection,
                                user
                        );

                if (userId <= 0) {
                    throw new SQLException(
                            "Không tạo được tài khoản Users."
                    );
                }

                switch (role) {
                    case ADMIN -> {
                        // ADMIN chỉ lưu trong Users.
                    }

                    case STUDENT -> {
                        prepareStudentProfile(
                                studentProfile,
                                user
                        );

                        int studentId =
                                studentDAO.insert(
                                        connection,
                                        studentProfile
                                );

                        if (studentId <= 0) {
                            throw new SQLException(
                                    "Không tạo được hồ sơ sinh viên."
                            );
                        }
                    }

                    case TEACHER -> {
                        prepareTeacherProfile(
                                teacherProfile,
                                user
                        );

                        int teacherId =
                                teacherDAO.insert(
                                        connection,
                                        teacherProfile
                                );

                        if (teacherId <= 0) {
                            throw new SQLException(
                                    "Không tạo được hồ sơ giảng viên."
                            );
                        }
                    }
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

                throw exception;

            } finally {
                restoreAutoCommit(
                        connection,
                        originalAutoCommit
                );
            }
        }
    }

    private User buildAdminCreatedUser(
            String username,
            String rawPassword,
            String fullName,
            String email,
            String phone,
            Role role
    ) {
        validateRequired(
                username,
                rawPassword,
                fullName
        );

        User user =
                new User();

        user.setUsername(
                username.trim()
        );

        user.setPasswordHash(
                PasswordUtil.hashPassword(
                        rawPassword
                )
        );

        user.setFullName(
                fullName.trim()
        );

        user.setEmail(
                normalize(email)
        );

        user.setPhone(
                normalize(phone)
        );

        user.setRole(role);
        user.setRoleId(
                convertRoleToRoleId(role)
        );

        /*
         * Tài khoản do Admin tạo được hoạt động ngay.
         */
        user.setStatus(
                AccountStatus.ACTIVE
        );

        user.setRegistrationSource(
                "ADMIN"
        );

        /*
         * Tài khoản do Admin trực tiếp tạo được xem là
         * đã xác minh thông tin email.
         */
        user.setEmailVerified(
                true
        );

        user.setEmailVerifiedAt(
                LocalDateTime.now()
        );

        user.setLoginAttempts(0);

        return user;
    }

    private void prepareStudentProfile(
            Student student,
            User user
    ) {
        student.setUserId(
                user.getUserId()
        );

        student.setFullName(
                user.getFullName()
        );

        student.setEmail(
                user.getEmail()
        );

        student.setPhone(
                user.getPhone()
        );

        student.setStatus(
                "ACTIVE"
        );
    }

    private void prepareTeacherProfile(
            Teacher teacher,
            User user
    ) {
        teacher.setUserId(
                user.getUserId()
        );

        teacher.setFullName(
                user.getFullName()
        );

        teacher.setEmail(
                user.getEmail()
        );

        teacher.setPhone(
                user.getPhone()
        );

        /*
         * Teacher do Admin tạo được ACTIVE ngay.
         * Teacher tự đăng ký vẫn do luồng đăng ký cũ xử lý
         * với PENDING_APPROVAL ở bảng Users.
         */
        teacher.setStatus(
                "ACTIVE"
        );
    }

    /* =====================================================
       CẬP NHẬT TÀI KHOẢN
       ===================================================== */

    public boolean updateUser(
            User user
    ) throws SQLException {

        validateUserForUpdate(user);

        if (
                user.getEmail() != null
                        && !user.getEmail().isBlank()
                        && userDAO.existsByEmailExceptId(
                        user.getEmail().trim(),
                        user.getUserId()
                )
        ) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng bởi tài khoản khác."
            );
        }

        user.setFullName(
                user.getFullName().trim()
        );

        user.setEmail(
                normalize(user.getEmail())
        );

        user.setPhone(
                normalize(user.getPhone())
        );

        if (user.getRole() == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống."
            );
        }

        user.setRoleId(
                convertRoleToRoleId(
                        user.getRole()
                )
        );

        if (user.getStatus() == null) {
            user.setStatus(
                    AccountStatus.ACTIVE
            );
        }

        return userDAO.update(user);
    }

    /* =====================================================
       XÓA, KHÓA, MỞ KHÓA
       ===================================================== */

    public boolean deleteUser(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        return userDAO.deleteById(userId);
    }

    public boolean lockUser(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        User user =
                requireUser(userId);

        protectMainAdmin(user);

        return userDAO.lockUser(userId);
    }

    public boolean unlockUser(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        return userDAO.unlockUser(userId);
    }

    public boolean deactivateUser(
            int userId
    ) throws SQLException {

        validateUserId(userId);

        User user =
                requireUser(userId);

        protectMainAdmin(user);

        return userDAO.deactivateUser(userId);
    }

    /* =====================================================
       DUYỆT / TỪ CHỐI TEACHER
       ===================================================== */

    public boolean approveTeacher(
            int userId,
            int adminId
    ) throws SQLException {

        validateUserId(userId);
        validateUserId(adminId);

        User teacherUser =
                requireUser(userId);

        if (teacherUser.getRole()
                != Role.TEACHER) {

            throw new IllegalArgumentException(
                    "Tài khoản được chọn không phải giảng viên."
            );
        }

        if (
                teacherUser.getStatus()
                        != AccountStatus.PENDING_APPROVAL
        ) {
            throw new IllegalStateException(
                    "Tài khoản giảng viên không ở trạng thái chờ duyệt."
            );
        }

        /*
         * Việc gửi email sẽ được gọi ở Controller hoặc
         * EmailService sau khi cập nhật thành công.
         */
        return userDAO.approveUser(
                userId,
                adminId
        );
    }

    public boolean rejectTeacher(
            int userId,
            int adminId,
            String reason
    ) throws SQLException {

        validateUserId(userId);
        validateUserId(adminId);

        User teacherUser =
                requireUser(userId);

        if (teacherUser.getRole()
                != Role.TEACHER) {

            throw new IllegalArgumentException(
                    "Tài khoản được chọn không phải giảng viên."
            );
        }

        if (
                reason == null
                        || reason.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập lý do từ chối."
            );
        }

        return userDAO.rejectUser(
                userId,
                adminId,
                reason.trim()
        );
    }

    /* =====================================================
       RESET MẬT KHẨU
       ===================================================== */

    public boolean resetPassword(
            int userId,
            String newPassword
    ) throws SQLException {

        validateUserId(userId);

        if (
                newPassword == null
                        || newPassword.length() < 6
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 6 ký tự."
            );
        }

        /*
         * UserDAO.resetPassword tự băm mật khẩu.
         */
        return userDAO.resetPassword(
                userId,
                newPassword
        );
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateUniqueUserData(
            User user
    ) throws SQLException {

        if (
                userDAO.existsByUsername(
                        user.getUsername()
                )
        ) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập đã tồn tại."
            );
        }

        if (
                user.getEmail() != null
                        && userDAO.existsByEmail(
                        user.getEmail()
                )
        ) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng."
            );
        }
    }

    private void validateRoleProfile(
            Role role,
            Student student,
            Teacher teacher
    ) {
        switch (role) {
            case ADMIN -> {
                // Không cần hồ sơ phụ.
            }

            case STUDENT -> {
                if (student == null) {
                    throw new IllegalArgumentException(
                            "Vui lòng nhập hồ sơ sinh viên."
                    );
                }

                if (
                        student.getStudentCode() == null
                                || student.getStudentCode().isBlank()
                ) {
                    throw new IllegalArgumentException(
                            "Mã sinh viên không được để trống."
                    );
                }
            }

            case TEACHER -> {
                if (teacher == null) {
                    throw new IllegalArgumentException(
                            "Vui lòng nhập hồ sơ giảng viên."
                    );
                }

                if (
                        teacher.getTeacherCode() == null
                                || teacher.getTeacherCode().isBlank()
                ) {
                    throw new IllegalArgumentException(
                            "Mã giảng viên không được để trống."
                    );
                }
            }
        }
    }

    private void validateUniqueStudentProfile(
            Student student
    ) {
        if (
                studentDAO.existsByStudentCode(
                        student.getStudentCode()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã sinh viên đã tồn tại."
            );
        }
    }

    private void validateUniqueTeacherProfile(
            Teacher teacher
    ) {
        if (
                teacherDAO.existsByTeacherCode(
                        teacher.getTeacherCode()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã giảng viên đã tồn tại."
            );
        }
    }

    private void validateUserForUpdate(
            User user
    ) {
        if (
                user == null
                        || user.getUserId() <= 0
        ) {
            throw new IllegalArgumentException(
                    "Người dùng không hợp lệ."
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
    }

    private void validateRequired(
            String username,
            String password,
            String fullName
    ) {
        if (
                username == null
                        || username.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống."
            );
        }

        if (
                password == null
                        || password.length() < 6
        ) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );
        }

        if (
                fullName == null
                        || fullName.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống."
            );
        }
    }

    private User requireUser(
            int userId
    ) throws SQLException {

        User user =
                userDAO.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy tài khoản."
            );
        }

        return user;
    }

    private void protectMainAdmin(
            User user
    ) {
        if (
                user != null
                        && "admin".equalsIgnoreCase(
                        user.getUsername()
                )
        ) {
            throw new IllegalStateException(
                    "Không thể khóa hoặc ngừng hoạt động "
                            + "tài khoản quản trị chính."
            );
        }
    }

    private void validateUserId(
            int userId
    ) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Mã người dùng không hợp lệ."
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

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private String normalize(
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

    private int convertRoleToRoleId(
            Role role
    ) {
        if (role == null) {
            throw new IllegalArgumentException(
                    "Vai trò không hợp lệ."
            );
        }

        return switch (role) {
            case ADMIN -> 1;
            case TEACHER -> 2;
            case STUDENT -> 3;
        };
    }

    private Role convertRoleIdToRole(
            int roleId
    ) {
        return switch (roleId) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.TEACHER;
            case 3 -> Role.STUDENT;

            default -> throw new IllegalArgumentException(
                    "Vai trò không hợp lệ: "
                            + roleId
            );
        };
    }

    private void rollback(
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
}
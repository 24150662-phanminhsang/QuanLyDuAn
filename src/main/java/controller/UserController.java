package controller;

import model.Role;
import model.Student;
import model.Teacher;
import model.User;
import service.UserService;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService =
                new UserService();
    }

    public UserController(
            UserService userService
    ) {
        if (userService == null) {
            throw new IllegalArgumentException(
                    "UserService không được null."
            );
        }

        this.userService =
                userService;
    }

    /* =====================================================
       DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    public List<User> getAllUsers()
            throws SQLException {

        List<User> users =
                userService.getAllUsers();

        return users == null
                ? Collections.emptyList()
                : users;
    }

    public Optional<User> getUserById(
            int userId
    ) throws SQLException {

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return userService.getUserById(
                userId
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
                userService.getUsers(
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

        return userService.countUsers(
                keyword
        );
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

        return userService.getTotalPages(
                keyword,
                pageSize
        );
    }

    public List<User> getPendingTeachers()
            throws SQLException {

        List<User> users =
                userService.getPendingTeachers();

        return users == null
                ? Collections.emptyList()
                : users;
    }

    /* =====================================================
       TẠO TÀI KHOẢN CŨ
       Giữ lại để tương thích với View cũ.
       Chỉ phù hợp khi tạo ADMIN.
       ===================================================== */

    public boolean createUser(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            int roleId
    ) throws SQLException {

        return userService.createUser(
                username,
                password,
                fullName,
                email,
                phone,
                roleId
        );
    }

    /* =====================================================
       TẠO ADMIN
       ===================================================== */

    public boolean createAdmin(
            String username,
            String password,
            String fullName,
            String email,
            String phone
    ) throws SQLException {

        return userService.createRoleAccount(
                username,
                password,
                fullName,
                email,
                phone,
                Role.ADMIN,
                null,
                null
        );
    }

    /* =====================================================
       TẠO STUDENT
       ===================================================== */

    public boolean createStudent(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            Student student
    ) throws SQLException {

        if (student == null) {
            throw new IllegalArgumentException(
                    "Thông tin sinh viên không được null."
            );
        }

        return userService.createRoleAccount(
                username,
                password,
                fullName,
                email,
                phone,
                Role.STUDENT,
                student,
                null
        );
    }

    /* =====================================================
       TẠO TEACHER
       ===================================================== */

    public boolean createTeacher(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            Teacher teacher
    ) throws SQLException {

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Thông tin giảng viên không được null."
            );
        }

        return userService.createRoleAccount(
                username,
                password,
                fullName,
                email,
                phone,
                Role.TEACHER,
                null,
                teacher
        );
    }

    /* =====================================================
       TẠO THEO VAI TRÒ
       Dùng trực tiếp cho form Admin mới.
       ===================================================== */

    public boolean createRoleAccount(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            Role role,
            Student student,
            Teacher teacher
    ) throws SQLException {

        if (role == null) {
            throw new IllegalArgumentException(
                    "Vai trò không được để trống."
            );
        }

        return userService.createRoleAccount(
                username,
                password,
                fullName,
                email,
                phone,
                role,
                student,
                teacher
        );
    }

    /* =====================================================
       CẬP NHẬT TÀI KHOẢN
       ===================================================== */

    public boolean updateUser(
            User user
    ) throws SQLException {

        if (user == null) {
            throw new IllegalArgumentException(
                    "Thông tin tài khoản không được null."
            );
        }

        return userService.updateUser(
                user
        );
    }

    /* =====================================================
       XÓA TÀI KHOẢN
       ===================================================== */

    public boolean deleteUser(
            int userId
    ) throws SQLException {

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return userService.deleteUser(
                userId
        );
    }

    /* =====================================================
       KHÓA / MỞ KHÓA / NGỪNG HOẠT ĐỘNG
       ===================================================== */

    public boolean lockUser(
            int userId
    ) throws SQLException {

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return userService.lockUser(
                userId
        );
    }

    public boolean unlockUser(
            int userId
    ) throws SQLException {

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return userService.unlockUser(
                userId
        );
    }

    public boolean deactivateUser(
            int userId
    ) throws SQLException {

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        return userService.deactivateUser(
                userId
        );
    }

    /* =====================================================
       DUYỆT / TỪ CHỐI GIẢNG VIÊN
       ===================================================== */

    public boolean approveTeacher(
            int teacherUserId,
            int adminUserId
    ) throws SQLException {

        validatePositiveId(
                teacherUserId,
                "ID tài khoản giảng viên"
        );

        validatePositiveId(
                adminUserId,
                "ID tài khoản quản trị viên"
        );

        return userService.approveTeacher(
                teacherUserId,
                adminUserId
        );
    }

    public boolean rejectTeacher(
            int teacherUserId,
            int adminUserId,
            String reason
    ) throws SQLException {

        validatePositiveId(
                teacherUserId,
                "ID tài khoản giảng viên"
        );

        validatePositiveId(
                adminUserId,
                "ID tài khoản quản trị viên"
        );

        if (reason == null
                || reason.isBlank()) {

            throw new IllegalArgumentException(
                    "Lý do từ chối không được để trống."
            );
        }

        return userService.rejectTeacher(
                teacherUserId,
                adminUserId,
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

        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        if (newPassword == null
                || newPassword.length() < 6) {

            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 6 ký tự."
            );
        }

        return userService.resetPassword(
                userId,
                newPassword
        );
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

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
}
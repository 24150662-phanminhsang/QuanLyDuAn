package service;

import dao.UserDAO;
import model.AccountStatus;
import model.Role;
import model.User;
import util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Lấy toàn bộ danh sách người dùng.
     */
    public List<User> getAllUsers()
            throws SQLException {

        return userDAO.findAll();
    }

    /**
     * Tìm người dùng theo ID.
     */
    public Optional<User> getUserById(int id)
            throws SQLException {

        if (id <= 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                userDAO.findById(id)
        );
    }

    /**
     * Tạo người dùng mới.
     */
    public boolean createUser(
            String username,
            String password,
            String fullName,
            String email,
            String phone,
            int roleId
    ) throws SQLException {

        validateRequired(
                username,
                password,
                fullName
        );

        if (roleId <= 0) {
            throw new IllegalArgumentException(
                    "Vai trò không hợp lệ."
            );
        }

        User existingUser =
                userDAO.findByUsername(
                        username.trim()
                );

        if (existingUser != null) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập đã tồn tại."
            );
        }

        if (
                email != null
                        && !email.isBlank()
                        && userDAO.existsByEmail(email.trim())
        ) {
            throw new IllegalArgumentException(
                    "Email đã được sử dụng."
            );
        }

        User user = new User();

        user.setUsername(
                username.trim()
        );

        user.setPasswordHash(
                PasswordUtil.hashPassword(password)
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

        Role role = convertRoleIdToRole(roleId);

        user.setRoleId(roleId);
        user.setRole(role);

        user.setStatus(
                AccountStatus.ACTIVE
        );

        return userDAO.insert(user);
    }

    /**
     * Cập nhật thông tin người dùng.
     */
    public boolean updateUser(User user)
            throws SQLException {

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

        if (user.getStatus() == null) {
            user.setStatus(
                    AccountStatus.ACTIVE
            );
        }

        return userDAO.update(user);
    }

    /**
     * Xóa người dùng.
     */
    public boolean deleteUser(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Mã người dùng không hợp lệ."
            );
        }

        return userDAO.deleteById(userId);
    }

    /**
     * Khóa tài khoản.
     */
    public boolean lockUser(int userId)
            throws SQLException {

        validateUserId(userId);

        return userDAO.lockUser(userId);
    }

    /**
     * Mở khóa tài khoản.
     */
    public boolean unlockUser(int userId)
            throws SQLException {

        validateUserId(userId);

        return userDAO.unlockUser(userId);
    }

    /**
     * Ngừng hoạt động tài khoản.
     */
    public boolean deactivateUser(int userId)
            throws SQLException {

        validateUserId(userId);

        return userDAO.deactivateUser(userId);
    }

    /**
     * Reset mật khẩu.
     */
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

        return userDAO.resetPassword(
                userId,
                newPassword
        );
    }

    /**
     * Lấy danh sách theo trang.
     */
    public List<User> getUsers(
            String keyword,
            int page,
            int pageSize
    ) throws SQLException {

        validatePagination(
                page,
                pageSize
        );

        return userDAO.search(
                keyword,
                page,
                pageSize
        );
    }

    /**
     * Đếm người dùng theo từ khóa.
     */
    public int countUsers(String keyword)
            throws SQLException {

        return userDAO.count(keyword);
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
                    "Số dòng trên trang phải lớn hơn 0."
            );
        }

        return userDAO.getTotalPages(
                keyword,
                pageSize
        );
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

    private void validateUserId(int userId) {
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

    private String normalize(String value) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }

    /**
     * Chuyển roleId thành Role.
     *
     * Phải đảm bảo ID trong bảng Roles đúng theo thứ tự này:
     * 1 = ADMIN
     * 2 = TEACHER
     * 3 = STUDENT
     */
    private Role convertRoleIdToRole(int roleId) {
        return switch (roleId) {
            case 1 -> Role.ADMIN;
            case 2 -> Role.TEACHER;
            case 3 -> Role.STUDENT;

            default -> throw new IllegalArgumentException(
                    "Vai trò không hợp lệ: " + roleId
            );
        };
    }
}
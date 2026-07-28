package service;

import dao.UserDAO;
import model.AccountStatus;
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

    public List<User> getAllUsers()
            throws SQLException {

        return userDAO.findAll();
    }

    public Optional<User> getUserById(int id)
            throws SQLException {

        return userDAO.findById(id);
    }

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

        if (userDAO.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập đã tồn tại."
            );
        }

        User user = new User();

        user.setUsername(username.trim());
        user.setPasswordHash(
                PasswordUtil.hashPassword(password)
        );
        user.setFullName(fullName.trim());
        user.setEmail(normalize(email));
        user.setPhone(normalize(phone));
        user.setRoleId(roleId);
        user.setStatus(AccountStatus.ACTIVE);

        return userDAO.insert(user);
    }

    public boolean updateUser(User user)
            throws SQLException {

        if (user == null || user.getUserId() <= 0) {
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

        return userDAO.update(user);
    }

    public boolean deleteUser(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Mã người dùng không hợp lệ."
            );
        }

        return userDAO.deleteById(userId);
    }

    private void validateRequired(
            String username,
            String password,
            String fullName
    ) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Tên đăng nhập không được để trống."
            );
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Họ tên không được để trống."
            );
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
package controller;

import model.User;
import service.UserService;

import java.sql.SQLException;
import java.util.List;

public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public List<User> getAllUsers() throws SQLException {
        return userService.getAllUsers();
    }

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

    public boolean updateUser(User user)
            throws SQLException {

        return userService.updateUser(user);
    }

    public boolean deleteUser(int userId)
            throws SQLException {

        return userService.deleteUser(userId);
    }
}
import model.User;
import service.UserService;

import java.util.List;

public class TestUserDAO {

    public static void main(String[] args) {

        UserService userService = new UserService();

        try {
            List<User> users =
                    userService.getAllUsers();

            System.out.println(
                    "Tổng số tài khoản: "
                            + users.size()
            );

            for (User user : users) {
                System.out.println(
                        user.getUserId()
                                + " | "
                                + user.getUsername()
                                + " | "
                                + user.getFullName()
                                + " | "
                                + user.getRole()
                                + " | "
                                + user.getStatus()
                );
            }

        } catch (Exception exception) {
            System.err.println(
                    "Lỗi: " + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
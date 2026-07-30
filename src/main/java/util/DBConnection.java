package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Quản lý việc tạo kết nối đến SQL Server.
 */
public final class DBConnection {

    private static final String URL =
            ConfigLoader.get("db.url");

    private static final String USERNAME =
            ConfigLoader.get("db.username");

    private static final String PASSWORD =
            ConfigLoader.get("db.password");

    private static final String DRIVER =
            ConfigLoader.get("db.driver");

    static {
        loadDriver();
    }

    private DBConnection() {
    }

    private static void loadDriver() {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError(
                    "Không tìm thấy SQL Server JDBC Driver. "
                            + "Hãy kiểm tra pom.xml."
            );
        }
    }

    /**
     * Mỗi lần gọi sẽ trả về một Connection mới.
     */
    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                URL,
                USERNAME,
                PASSWORD
        );
    }

    /**
     * Kiểm tra kết nối với SQL Server.
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null
                    && connection.isValid(5);

        } catch (SQLException exception) {
            System.err.println(
                    "Kết nối SQL Server thất bại: "
                            + exception.getMessage()
            );

            return false;
        }
    }
    public static void main(String[] args) {
        try (java.sql.Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("----------------------------------------");
                System.out.println("===> KẾT NỐI SQL SERVER THÀNH CÔNG! <===");
                System.out.println("----------------------------------------");
            }
        } catch (Exception e) {
            System.out.println("----------------------------------------");
            System.out.println("===> LỖI KẾT NỐI SQL SERVER: <===");
            System.out.println("----------------------------------------");
            e.printStackTrace();
        }
    }
}
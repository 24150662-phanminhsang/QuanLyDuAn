import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestDatabaseConnection {

    private static final String SQL = """
            SELECT
                DB_NAME() AS database_name,
                COUNT(*) AS user_count
            FROM dbo.Users
            """;

    public static void main(String[] args) {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(SQL);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            System.out.println(
                    "Kết nối SQL Server thành công!"
            );

            if (resultSet.next()) {
                System.out.println(
                        "Database: "
                                + resultSet.getString(
                                "database_name"
                        )
                );

                System.out.println(
                        "Số tài khoản: "
                                + resultSet.getInt(
                                "user_count"
                        )
                );
            }

        } catch (SQLException exception) {
            System.err.println(
                    "Không thể truy vấn database."
            );

            System.err.println(
                    "Chi tiết: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
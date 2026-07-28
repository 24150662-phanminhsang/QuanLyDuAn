package dao;

import model.Statistics;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticsDAO {

    private static final String SQL = """
            SELECT
                (SELECT COUNT(*) FROM dbo.Users)
                    AS total_users,

                (SELECT COUNT(*) FROM dbo.Students)
                    AS total_students,

                (SELECT COUNT(*) FROM dbo.Teachers)
                    AS total_teachers,

                (SELECT COUNT(*) FROM dbo.Courses)
                    AS total_courses,

                (SELECT COUNT(*) FROM dbo.CourseClasses)
                    AS total_classes,

                (
                    SELECT COUNT(*)
                    FROM dbo.Users
                    WHERE status = 'ACTIVE'
                ) AS active_users
            """;

    public Statistics getOverview() throws SQLException {

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(SQL);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            if (resultSet.next()) {
                return new Statistics(
                        resultSet.getInt("total_users"),
                        resultSet.getInt("total_students"),
                        resultSet.getInt("total_teachers"),
                        resultSet.getInt("total_courses"),
                        resultSet.getInt("total_classes"),
                        resultSet.getInt("active_users")
                );
            }
        }

        return new Statistics();
    }
}
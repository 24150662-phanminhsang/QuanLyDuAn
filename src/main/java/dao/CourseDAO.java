package dao;

import model.Course;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    /**
     * Lấy các khóa học đang hoạt động để hiển thị
     * trên Landing Page.
     */
    public List<Course> findActiveCourses()
            throws SQLException {

        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT
                    course_id,
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                FROM dbo.Courses
                WHERE status = 'ACTIVE'
                ORDER BY created_at DESC, course_id DESC
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                courses.add(mapCourse(resultSet));
            }
        }

        return courses;
    }

    /**
     * Lấy tối đa số lượng khóa học được yêu cầu.
     * Dùng cho mục Khóa học nổi bật.
     */
    public List<Course> findFeaturedCourses(int limit)
            throws SQLException {

        if (limit <= 0) {
            return new ArrayList<>();
        }

        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT
                    course_id,
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                FROM dbo.Courses
                WHERE status = 'ACTIVE'
                ORDER BY created_at DESC, course_id DESC
                OFFSET 0 ROWS
                FETCH NEXT ? ROWS ONLY
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, limit);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    courses.add(mapCourse(resultSet));
                }
            }
        }

        return courses;
    }

    public Course findById(int courseId)
            throws SQLException {

        String sql = """
                SELECT
                    course_id,
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                FROM dbo.Courses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, courseId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapCourse(resultSet);
                }
            }
        }

        return null;
    }

    public List<Course> findAll()
            throws SQLException {

        List<Course> courses = new ArrayList<>();

        String sql = """
                SELECT
                    course_id,
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                FROM dbo.Courses
                ORDER BY course_id DESC
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                courses.add(mapCourse(resultSet));
            }
        }

        return courses;
    }

    public boolean insert(Course course)
            throws SQLException {

        String sql = """
                INSERT INTO dbo.Courses
                (
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, SYSDATETIME()
                )
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            setCourseParameters(
                    statement,
                    course
            );

            return statement.executeUpdate() > 0;
        }
    }

    public boolean update(Course course)
            throws SQLException {

        String sql = """
                UPDATE dbo.Courses
                SET
                    course_code = ?,
                    course_name = ?,
                    description = ?,
                    credits = ?,
                    tuition_fee = ?,
                    status = ?
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            setCourseParameters(
                    statement,
                    course
            );

            statement.setInt(
                    7,
                    course.getCourseId()
            );

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteById(int courseId)
            throws SQLException {

        String sql = """
                DELETE FROM dbo.Courses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, courseId);

            return statement.executeUpdate() > 0;
        }
    }

    private void setCourseParameters(
            PreparedStatement statement,
            Course course
    ) throws SQLException {

        statement.setString(
                1,
                course.getCourseCode()
        );

        statement.setString(
                2,
                course.getCourseName()
        );

        statement.setString(
                3,
                course.getDescription()
        );

        statement.setInt(
                4,
                course.getCredits()
        );

        BigDecimal tuitionFee =
                course.getTuitionFee();

        if (tuitionFee == null) {
            statement.setNull(
                    5,
                    Types.DECIMAL
            );
        } else {
            statement.setBigDecimal(
                    5,
                    tuitionFee
            );
        }

        statement.setString(
                6,
                course.getStatus()
        );
    }

    private Course mapCourse(
            ResultSet resultSet
    ) throws SQLException {

        Course course = new Course();

        course.setCourseId(
                resultSet.getInt("course_id")
        );

        course.setCourseCode(
                resultSet.getString("course_code")
        );

        course.setCourseName(
                resultSet.getString("course_name")
        );

        course.setDescription(
                resultSet.getString("description")
        );

        course.setCredits(
                resultSet.getInt("credits")
        );

        course.setTuitionFee(
                resultSet.getBigDecimal("tuition_fee")
        );

        course.setStatus(
                resultSet.getString("status")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            course.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return course;
    }
}
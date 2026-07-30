package dao;

import model.Course;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CourseDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                course_id,
                course_code,
                course_name,
                description,
                credits,
                tuition_fee,
                status,
                created_at
            FROM Courses
            """;

    private static final int SQL_SERVER_FOREIGN_KEY_ERROR = 547;
    private static final int SQL_SERVER_DUPLICATE_KEY_ERROR = 2627;
    private static final int SQL_SERVER_DUPLICATE_INDEX_ERROR = 2601;

    /**
     * Thêm khóa học mới.
     */
    public boolean addCourse(Course course) {
        String sql =
                """
                INSERT INTO Courses
                (
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            bindCourseData(
                    statement,
                    course
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể thêm khóa học.",
                    exception
            );
        }
    }

    /**
     * Lấy toàn bộ khóa học.
     */
    public List<Course> getAllCourses() {
        String sql =
                BASE_SELECT
                        + " ORDER BY course_id DESC";

        return queryList(sql);
    }

    /**
     * Lấy khóa học theo ID.
     */
    public Course getCourseById(int id) {
        String sql =
                BASE_SELECT
                        + " WHERE course_id = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapCourse(resultSet);
                }
            }

            return null;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tìm khóa học.",
                    exception
            );
        }
    }

    /**
     * Giữ lại để tương thích với code cũ.
     */
    public Course getCourseByID(int id) {
        return getCourseById(id);
    }

    /**
     * Cập nhật khóa học.
     */
    public boolean updateCourse(Course course) {
        String sql =
                """
                UPDATE Courses
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
            bindCourseData(
                    statement,
                    course
            );

            statement.setInt(
                    7,
                    course.getCourseId()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể cập nhật khóa học.",
                    exception
            );
        }
    }

    /**
     * Xóa khóa học theo ID.
     */
    public boolean deleteCourse(int id) {
        String sql =
                """
                DELETE FROM Courses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(1, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            if (
                    exception.getErrorCode()
                            == SQL_SERVER_FOREIGN_KEY_ERROR
            ) {
                throw new RuntimeException(
                        "Không thể xóa khóa học vì khóa học "
                                + "đang được sử dụng trong lớp học, "
                                + "đăng ký hoặc dữ liệu liên quan.",
                        exception
                );
            }

            throw translateException(
                    "Không thể xóa khóa học.",
                    exception
            );
        }
    }

    /**
     * Lấy các khóa học đang hoạt động.
     */
    public List<Course> getActiveCourses() {
        String sql =
                BASE_SELECT
                        + """
                         WHERE UPPER(status) = 'ACTIVE'
                         ORDER BY created_at DESC, course_id DESC
                         """;

        return queryList(sql);
    }

    /**
     * Lấy danh sách khóa học nổi bật.
     */
    public List<Course> getFeaturedCourses(
            int limit
    ) {
        String sql =
                """
                SELECT TOP (?)
                    course_id,
                    course_code,
                    course_name,
                    description,
                    credits,
                    tuition_fee,
                    status,
                    created_at
                FROM Courses
                WHERE UPPER(status) = 'ACTIVE'
                ORDER BY created_at DESC, course_id DESC
                """;

        List<Course> courses =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    Math.max(limit, 1)
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    courses.add(
                            mapCourse(resultSet)
                    );
                }
            }

            return courses;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tải khóa học nổi bật.",
                    exception
            );
        }
    }

    /**
     * Tìm kiếm khóa học theo mã, tên, mô tả hoặc trạng thái.
     */
    public List<Course> searchCourses(
            String keyword
    ) {
        String sql =
                BASE_SELECT
                        + """
                         WHERE
                             course_code LIKE ?
                             OR course_name LIKE ?
                             OR description LIKE ?
                             OR status LIKE ?
                         ORDER BY course_id DESC
                         """;

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        String searchValue =
                "%" + normalizedKeyword + "%";

        List<Course> courses =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(1, searchValue);
            statement.setString(2, searchValue);
            statement.setString(3, searchValue);
            statement.setString(4, searchValue);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    courses.add(
                            mapCourse(resultSet)
                    );
                }
            }

            return courses;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tìm kiếm khóa học.",
                    exception
            );
        }
    }

    /**
     * Thực thi truy vấn không có tham số và trả về danh sách khóa học.
     */
    private List<Course> queryList(
            String sql
    ) {
        List<Course> courses =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                courses.add(
                        mapCourse(resultSet)
                );
            }

            return courses;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tải danh sách khóa học.",
                    exception
            );
        }
    }

    /**
     * Gán dữ liệu khóa học vào PreparedStatement.
     *
     * Dùng chung cho INSERT và UPDATE.
     */
    private void bindCourseData(
            PreparedStatement statement,
            Course course
    ) throws SQLException {
        statement.setString(
                1,
                normalizeText(
                        course.getCourseCode()
                )
        );

        statement.setString(
                2,
                normalizeText(
                        course.getCourseName()
                )
        );

        setNullableString(
                statement,
                3,
                course.getDescription()
        );

        statement.setInt(
                4,
                course.getCredits()
        );

        statement.setBigDecimal(
                5,
                safeMoney(
                        course.getTuitionFee()
                )
        );

        statement.setString(
                6,
                normalizeStatus(
                        course.getStatus()
                )
        );
    }

    /**
     * Chuyển một dòng ResultSet thành đối tượng Course.
     */
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

        course.setCreatedAt(
                resultSet.getTimestamp("created_at")
        );

        return course;
    }

    /**
     * Gán chuỗi có thể null vào câu SQL.
     */
    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {
        if (
                value == null
                        || value.isBlank()
        ) {
            statement.setNull(
                    parameterIndex,
                    Types.NVARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }

    private String normalizeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
    }

    /**
     * Tránh học phí bị null.
     */
    private BigDecimal safeMoney(
            BigDecimal value
    ) {
        return value == null
                ? BigDecimal.ZERO
                : value;
    }

    /**
     * Tránh trạng thái bị null hoặc rỗng.
     */
    private String normalizeStatus(
            String status
    ) {
        if (
                status == null
                        || status.isBlank()
        ) {
            return "ACTIVE";
        }

        return status.trim()
                .toUpperCase(Locale.ROOT);
    }

    /**
     * Chuyển lỗi SQL thành thông báo dễ hiểu hơn.
     */
    private RuntimeException translateException(
            String defaultMessage,
            SQLException exception
    ) {
        int errorCode =
                exception.getErrorCode();

        if (
                errorCode
                        == SQL_SERVER_DUPLICATE_KEY_ERROR
                        || errorCode
                        == SQL_SERVER_DUPLICATE_INDEX_ERROR
        ) {
            return new RuntimeException(
                    "Mã khóa học đã tồn tại. "
                            + "Vui lòng sử dụng mã khác.",
                    exception
            );
        }

        return new RuntimeException(
                defaultMessage
                        + "\nChi tiết: "
                        + exception.getMessage(),
                exception
        );
    }

}
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
            FROM dbo.Courses
            """;

    private static final int SQL_SERVER_FOREIGN_KEY_ERROR = 547;
    private static final int SQL_SERVER_DUPLICATE_KEY_ERROR = 2627;
    private static final int SQL_SERVER_DUPLICATE_INDEX_ERROR = 2601;

    /* =====================================================
       THÊM KHÓA HỌC
       ===================================================== */

    public boolean addCourse(Course course) {
        validateCourseForInsert(course);

        String sql =
                """
                INSERT INTO dbo.Courses
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

    /* =====================================================
       DANH SÁCH KHÓA HỌC
       ===================================================== */

    public List<Course> getAllCourses() {
        String sql =
                BASE_SELECT
                        + """
                        ORDER BY course_id DESC
                        """;

        return queryList(sql);
    }

    public List<Course> getActiveCourses() {
        return getCoursesByStatus("ACTIVE");
    }

    public List<Course> getInactiveCourses() {
        return getCoursesByStatus("INACTIVE");
    }

    public List<Course> getArchivedCourses() {
        return getCoursesByStatus("ARCHIVED");
    }

    public List<Course> getCoursesByStatus(
            String status
    ) {
        String normalizedStatus =
                normalizeStatus(status);

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(status) = ?
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
            statement.setString(
                    1,
                    normalizedStatus
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
                    "Không thể tải danh sách khóa học theo trạng thái.",
                    exception
            );
        }
    }

    public List<Course> getFeaturedCourses(
            int limit
    ) {
        if (limit <= 0) {
            return new ArrayList<>();
        }

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
                FROM dbo.Courses
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
                    limit
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

    /* =====================================================
       TÌM KHÓA HỌC
       ===================================================== */

    public Course getCourseById(
            int courseId
    ) {
        if (courseId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE course_id = ?
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    courseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapCourse(
                            resultSet
                    );
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

    public Course getCourseByID(
            int courseId
    ) {
        return getCourseById(
                courseId
        );
    }

    public Course getCourseByCode(
            String courseCode
    ) {
        if (
                courseCode == null
                        || courseCode.isBlank()
        ) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE UPPER(course_code) = UPPER(?)
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    courseCode.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapCourse(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể tìm khóa học theo mã.",
                    exception
            );
        }
    }

    public List<Course> searchCourses(
            String keyword
    ) {
        String sql =
                BASE_SELECT
                        + """
                        WHERE
                            course_code LIKE ?
                            OR course_name LIKE ?
                            OR COALESCE(description, '') LIKE ?
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

    /* =====================================================
       KIỂM TRA TRÙNG DỮ LIỆU
       ===================================================== */

    public boolean existsByCourseCode(
            String courseCode
    ) {
        if (
                courseCode == null
                        || courseCode.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Courses
                WHERE UPPER(course_code) = UPPER(?)
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    courseCode.trim()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể kiểm tra mã khóa học.",
                    exception
            );
        }
    }

    public boolean existsByCourseCodeExceptId(
            String courseCode,
            int excludedCourseId
    ) {
        if (
                courseCode == null
                        || courseCode.isBlank()
        ) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Courses
                WHERE UPPER(course_code) = UPPER(?)
                  AND course_id <> ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    courseCode.trim()
            );

            statement.setInt(
                    2,
                    excludedCourseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể kiểm tra mã khóa học.",
                    exception
            );
        }
    }

    /* =====================================================
       KIỂM TRA LIÊN KẾT LỚP HỌC
       ===================================================== */

    public boolean hasClasses(
            int courseId
    ) {
        if (courseId <= 0) {
            return false;
        }

        /*
         * Database hiện tại sử dụng bảng CourseClasses.
         */
        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.CourseClasses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    courseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể kiểm tra lớp học của khóa học.",
                    exception
            );
        }
    }

    public int countClasses(
            int courseId
    ) {
        if (courseId <= 0) {
            return 0;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.CourseClasses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    courseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        ? resultSet.getInt(
                        "total"
                )
                        : 0;
            }

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể đếm số lớp của khóa học.",
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT KHÓA HỌC
       ===================================================== */

    public boolean updateCourse(
            Course course
    ) {
        validateCourseForUpdate(course);

        String sql =
                """
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

    public boolean updateStatus(
            int courseId,
            String status
    ) {
        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        String normalizedStatus =
                normalizeStatus(status);

        String sql =
                """
                UPDATE dbo.Courses
                SET status = ?
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    normalizedStatus
            );

            statement.setInt(
                    2,
                    courseId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw translateException(
                    "Không thể cập nhật trạng thái khóa học.",
                    exception
            );
        }
    }

    public boolean activateCourse(
            int courseId
    ) {
        return updateStatus(
                courseId,
                "ACTIVE"
        );
    }

    public boolean deactivateCourse(
            int courseId
    ) {
        return updateStatus(
                courseId,
                "INACTIVE"
        );
    }

    public boolean archiveCourse(
            int courseId
    ) {
        return updateStatus(
                courseId,
                "ARCHIVED"
        );
    }

    /* =====================================================
       XÓA KHÓA HỌC
       ===================================================== */

    public boolean deleteCourse(
            int courseId
    ) {
        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        if (hasClasses(courseId)) {
            throw new IllegalStateException(
                    "Không thể xóa khóa học vì khóa học đã có lớp học. "
                            + "Hãy chuyển khóa học sang trạng thái ARCHIVED."
            );
        }

        String sql =
                """
                DELETE FROM dbo.Courses
                WHERE course_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    courseId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            if (
                    exception.getErrorCode()
                            == SQL_SERVER_FOREIGN_KEY_ERROR
            ) {
                throw new IllegalStateException(
                        "Không thể xóa khóa học vì khóa học "
                                + "đang được sử dụng trong dữ liệu liên quan.",
                        exception
                );
            }

            throw translateException(
                    "Không thể xóa khóa học.",
                    exception
            );
        }
    }

    /* =====================================================
       HÀM TRUY VẤN DÙNG CHUNG
       ===================================================== */

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

    private void bindCourseData(
            PreparedStatement statement,
            Course course
    ) throws SQLException {

        statement.setString(
                1,
                normalizeCourseCode(
                        course.getCourseCode()
                )
        );

        statement.setString(
                2,
                normalizeRequiredText(
                        course.getCourseName(),
                        "Tên khóa học"
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

    private Course mapCourse(
            ResultSet resultSet
    ) throws SQLException {

        Course course =
                new Course();

        course.setCourseId(
                resultSet.getInt(
                        "course_id"
                )
        );

        course.setCourseCode(
                resultSet.getString(
                        "course_code"
                )
        );

        course.setCourseName(
                resultSet.getString(
                        "course_name"
                )
        );

        course.setDescription(
                resultSet.getString(
                        "description"
                )
        );

        course.setCredits(
                resultSet.getInt(
                        "credits"
                )
        );

        course.setTuitionFee(
                resultSet.getBigDecimal(
                        "tuition_fee"
                )
        );

        course.setStatus(
                resultSet.getString(
                        "status"
                )
        );

        course.setCreatedAt(
                resultSet.getTimestamp(
                        "created_at"
                )
        );

        return course;
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateCourseForInsert(
            Course course
    ) {
        validateCourseCommon(course);
    }

    private void validateCourseForUpdate(
            Course course
    ) {
        validateCourseCommon(course);

        if (course.getCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }
    }

    private void validateCourseCommon(
            Course course
    ) {
        if (course == null) {
            throw new IllegalArgumentException(
                    "Thông tin khóa học không được null."
            );
        }

        normalizeCourseCode(
                course.getCourseCode()
        );

        normalizeRequiredText(
                course.getCourseName(),
                "Tên khóa học"
        );

        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException(
                    "Số tín chỉ phải lớn hơn 0."
            );
        }

        if (
                course.getTuitionFee() != null
                        && course.getTuitionFee()
                        .signum() < 0
        ) {
            throw new IllegalArgumentException(
                    "Học phí không được âm."
            );
        }

        normalizeStatus(
                course.getStatus()
        );
    }

    /* =====================================================
       CHUẨN HÓA DỮ LIỆU
       ===================================================== */

    private String normalizeCourseCode(
            String courseCode
    ) {
        if (
                courseCode == null
                        || courseCode.isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mã khóa học không được để trống."
            );
        }

        return courseCode
                .trim()
                .toUpperCase(
                        Locale.ROOT
                );
    }

    private String normalizeRequiredText(
            String value,
            String fieldName
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        return value.trim();
    }

    private String normalizeStatus(
            String status
    ) {
        if (
                status == null
                        || status.isBlank()
        ) {
            return "ACTIVE";
        }

        String normalized =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "ACTIVE",
                 "OPEN" ->
                    "ACTIVE";

            case "INACTIVE",
                 "CLOSED" ->
                    "INACTIVE";

            case "ARCHIVED" ->
                    "ARCHIVED";

            default ->
                    throw new IllegalArgumentException(
                            "Trạng thái khóa học không hợp lệ: "
                                    + status
                    );
        };
    }

    private BigDecimal safeMoney(
            BigDecimal value
    ) {
        return value == null
                ? BigDecimal.ZERO
                : value;
    }

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

    /* =====================================================
       XỬ LÝ LỖI SQL
       ===================================================== */

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
            return new IllegalArgumentException(
                    "Mã khóa học đã tồn tại. "
                            + "Vui lòng sử dụng mã khác.",
                    exception
            );
        }

        if (
                errorCode
                        == SQL_SERVER_FOREIGN_KEY_ERROR
        ) {
            return new IllegalStateException(
                    "Dữ liệu khóa học đang được sử dụng "
                            + "bởi bảng khác trong hệ thống.",
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
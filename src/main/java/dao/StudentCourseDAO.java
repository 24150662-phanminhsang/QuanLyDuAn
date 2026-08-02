package dao;

import model.dto.StudentCourseDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentCourseDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                e.enrollment_id,
                e.student_id,
                e.progress_percent,
                e.status AS enrollment_status,

                cc.class_id,
                cc.class_code,
                cc.course_id,
                cc.teacher_id,
                cc.semester,
                cc.school_year,
                cc.schedule_text,
                cc.room,
                cc.start_date,
                cc.end_date,

                c.course_code,
                c.course_name,
                c.tuition_fee,

                t.full_name AS teacher_name

            FROM dbo.Enrollments e

            INNER JOIN dbo.CourseClasses cc
                ON cc.class_id = e.class_id

            INNER JOIN dbo.Courses c
                ON c.course_id = cc.course_id

            LEFT JOIN dbo.Teachers t
                ON t.teacher_id = cc.teacher_id
            """;

    /**
     * Lấy toàn bộ khóa học mà sinh viên đang tham gia.
     */
    public List<StudentCourseDTO> getByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                BASE_SELECT
                        + """
                         WHERE e.student_id = ?
                         ORDER BY
                             e.enrollment_id DESC,
                             cc.class_id DESC
                         """;

        return queryByStudentId(
                sql,
                studentId
        );
    }

    /**
     * Lấy các khóa học đang học của sinh viên.
     *
     * Chỉ lấy enrollment có trạng thái ENROLLED.
     */
    public List<StudentCourseDTO> getActiveByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                BASE_SELECT
                        + """
                         WHERE e.student_id = ?
                           AND UPPER(e.status) = 'ENROLLED'
                         ORDER BY
                             cc.start_date ASC,
                             cc.class_id DESC
                         """;

        return queryByStudentId(
                sql,
                studentId
        );
    }

    /**
     * Lấy một khóa học cụ thể của sinh viên
     * theo enrollment_id.
     */
    public StudentCourseDTO getByEnrollmentId(
            int enrollmentId
    ) {
        validatePositiveId(
                enrollmentId,
                "ID đăng ký học"
        );

        String sql =
                BASE_SELECT
                        + """
                         WHERE e.enrollment_id = ?
                         """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    enrollmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapStudentCourse(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải thông tin khóa học "
                            + "của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Lấy khóa học của sinh viên theo class_id.
     */
    public StudentCourseDTO getByStudentAndClass(
            int studentId,
            int classId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        validatePositiveId(
                classId,
                "ID lớp học"
        );

        String sql =
                BASE_SELECT
                        + """
                         WHERE e.student_id = ?
                           AND e.class_id = ?
                         """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    classId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapStudentCourse(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải khóa học theo lớp: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Tìm kiếm trong các khóa học của sinh viên.
     *
     * Tìm theo:
     * - mã khóa học
     * - tên khóa học
     * - mã lớp
     * - tên giảng viên
     * - học kỳ
     * - năm học
     */
    public List<StudentCourseDTO> searchByStudentId(
            int studentId,
            String keyword
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        if (normalizedKeyword.isBlank()) {
            return getByStudentId(
                    studentId
            );
        }

        String searchValue =
                "%"
                        + normalizedKeyword
                        + "%";

        String sql =
                BASE_SELECT
                        + """
                         WHERE e.student_id = ?
                           AND
                           (
                               c.course_code LIKE ?
                               OR c.course_name LIKE ?
                               OR cc.class_code LIKE ?
                               OR COALESCE(t.full_name, '') LIKE ?
                               OR COALESCE(cc.semester, '') LIKE ?
                               OR COALESCE(cc.school_year, '') LIKE ?
                               OR COALESCE(cc.schedule_text, '') LIKE ?
                               OR COALESCE(cc.room, '') LIKE ?
                           )
                         ORDER BY
                             e.enrollment_id DESC,
                             cc.class_id DESC
                         """;

        List<StudentCourseDTO> courses =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            for (
                    int parameterIndex = 2;
                    parameterIndex <= 9;
                    parameterIndex++
            ) {
                statement.setString(
                        parameterIndex,
                        searchValue
                );
            }

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    courses.add(
                            mapStudentCourse(
                                    resultSet
                            )
                    );
                }
            }

            return courses;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm khóa học "
                            + "của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Đếm số lớp sinh viên đang tham gia.
     */
    public int countActiveCourses(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Enrollments
                WHERE student_id = ?
                  AND UPPER(status) = 'ENROLLED'
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return resultSet.getInt(
                            "total"
                    );
                }
            }

            return 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể đếm số khóa học "
                            + "của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Kiểm tra sinh viên có đăng ký lớp hay không.
     */
    public boolean existsByStudentAndClass(
            int studentId,
            int classId
    ) {
        if (studentId <= 0 || classId <= 0) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Enrollments
                WHERE student_id = ?
                  AND class_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            statement.setInt(
                    2,
                    classId
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
            throw new RuntimeException(
                    "Không thể kiểm tra đăng ký học: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Hàm dùng chung cho truy vấn chỉ có studentId.
     */
    private List<StudentCourseDTO> queryByStudentId(
            String sql,
            int studentId
    ) {
        List<StudentCourseDTO> courses =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    courses.add(
                            mapStudentCourse(
                                    resultSet
                            )
                    );
                }
            }

            return courses;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách khóa học "
                            + "của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /**
     * Chuyển ResultSet thành StudentCourseDTO.
     */
    private StudentCourseDTO mapStudentCourse(
            ResultSet resultSet
    ) throws SQLException {
        StudentCourseDTO course =
                new StudentCourseDTO();

        course.setEnrollmentId(
                resultSet.getInt(
                        "enrollment_id"
                )
        );

        course.setStudentId(
                resultSet.getInt(
                        "student_id"
                )
        );

        course.setProgressPercent(
                resultSet.getInt(
                        "progress_percent"
                )
        );

        course.setEnrollmentStatus(
                resultSet.getString(
                        "enrollment_status"
                )
        );

        course.setClassId(
                resultSet.getInt(
                        "class_id"
                )
        );

        course.setClassCode(
                resultSet.getString(
                        "class_code"
                )
        );

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

        int teacherId =
                resultSet.getInt(
                        "teacher_id"
                );

        course.setTeacherId(
                resultSet.wasNull()
                        ? 0
                        : teacherId
        );

        course.setTeacherName(
                resultSet.getString(
                        "teacher_name"
                )
        );

        course.setSemester(
                resultSet.getString(
                        "semester"
                )
        );

        course.setSchoolYear(
                resultSet.getString(
                        "school_year"
                )
        );

        course.setScheduleText(
                resultSet.getString(
                        "schedule_text"
                )
        );

        course.setRoom(
                resultSet.getString(
                        "room"
                )
        );

        course.setTuitionFee(
                resultSet.getBigDecimal(
                        "tuition_fee"
                )
        );

        course.setStartDate(
                resultSet.getDate(
                        "start_date"
                )
        );

        course.setEndDate(
                resultSet.getDate(
                        "end_date"
                )
        );

        return course;
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }
}
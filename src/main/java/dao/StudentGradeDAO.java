package dao;

import model.dto.StudentGradeDTO;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentGradeDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                g.grade_id,
                g.enrollment_id,

                e.student_id,
                e.class_id,

                cc.course_id,
                cc.class_code,

                c.course_code,
                c.course_name,

                g.attendance_score,
                g.midterm_score,
                g.final_score,
                g.average_score,
                g.result,
                g.updated_at

            FROM dbo.Grades g

            INNER JOIN dbo.Enrollments e
                ON e.enrollment_id = g.enrollment_id

            INNER JOIN dbo.CourseClasses cc
                ON cc.class_id = e.class_id

            INNER JOIN dbo.Courses c
                ON c.course_id = cc.course_id
            """;

    public List<StudentGradeDTO> getByStudentId(
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
                             g.updated_at DESC,
                             g.grade_id DESC
                         """;

        return queryList(
                sql,
                studentId
        );
    }

    public List<StudentGradeDTO> searchByStudentId(
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
                               OR COALESCE(g.result, '') LIKE ?
                           )
                         ORDER BY
                             g.updated_at DESC,
                             g.grade_id DESC
                         """;

        List<StudentGradeDTO> grades =
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

            statement.setString(
                    2,
                    searchValue
            );

            statement.setString(
                    3,
                    searchValue
            );

            statement.setString(
                    4,
                    searchValue
            );

            statement.setString(
                    5,
                    searchValue
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    grades.add(
                            mapStudentGrade(
                                    resultSet
                            )
                    );
                }
            }

            return grades;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm kết quả học tập: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public StudentGradeDTO getByGradeId(
            int gradeId
    ) {
        validatePositiveId(
                gradeId,
                "ID điểm"
        );

        String sql =
                BASE_SELECT
                        + """
                         WHERE g.grade_id = ?
                         """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    gradeId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapStudentGrade(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải chi tiết kết quả học tập: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    public int countPassedSubjects(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Grades g
                INNER JOIN dbo.Enrollments e
                    ON e.enrollment_id = g.enrollment_id
                WHERE e.student_id = ?
                  AND UPPER(g.result) = 'PASSED'
                """;

        return queryCount(
                sql,
                studentId
        );
    }

    public int countFailedSubjects(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Grades g
                INNER JOIN dbo.Enrollments e
                    ON e.enrollment_id = g.enrollment_id
                WHERE e.student_id = ?
                  AND UPPER(g.result) = 'FAILED'
                """;

        return queryCount(
                sql,
                studentId
        );
    }

    public double calculateAverageScore(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String sql =
                """
                SELECT AVG(g.average_score) AS average_score
                FROM dbo.Grades g
                INNER JOIN dbo.Enrollments e
                    ON e.enrollment_id = g.enrollment_id
                WHERE e.student_id = ?
                  AND g.average_score IS NOT NULL
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
                    double average =
                            resultSet.getDouble(
                                    "average_score"
                            );

                    return resultSet.wasNull()
                            ? 0.0
                            : average;
                }
            }

            return 0.0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tính điểm trung bình: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private List<StudentGradeDTO> queryList(
            String sql,
            int studentId
    ) {
        List<StudentGradeDTO> grades =
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
                    grades.add(
                            mapStudentGrade(
                                    resultSet
                            )
                    );
                }
            }

            return grades;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải kết quả học tập: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private int queryCount(
            String sql,
            int studentId
    ) {
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
                    "Không thể thống kê kết quả học tập: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    private StudentGradeDTO mapStudentGrade(
            ResultSet resultSet
    ) throws SQLException {
        StudentGradeDTO grade =
                new StudentGradeDTO();

        grade.setGradeId(
                resultSet.getInt(
                        "grade_id"
                )
        );

        grade.setEnrollmentId(
                resultSet.getInt(
                        "enrollment_id"
                )
        );

        grade.setStudentId(
                resultSet.getInt(
                        "student_id"
                )
        );

        grade.setClassId(
                resultSet.getInt(
                        "class_id"
                )
        );

        grade.setCourseId(
                resultSet.getInt(
                        "course_id"
                )
        );

        grade.setClassCode(
                resultSet.getString(
                        "class_code"
                )
        );

        grade.setCourseCode(
                resultSet.getString(
                        "course_code"
                )
        );

        grade.setCourseName(
                resultSet.getString(
                        "course_name"
                )
        );

        grade.setAttendanceScore(
                getNullableDouble(
                        resultSet,
                        "attendance_score"
                )
        );

        grade.setMidtermScore(
                getNullableDouble(
                        resultSet,
                        "midterm_score"
                )
        );

        grade.setFinalScore(
                getNullableDouble(
                        resultSet,
                        "final_score"
                )
        );

        grade.setAverageScore(
                getNullableDouble(
                        resultSet,
                        "average_score"
                )
        );

        grade.setResult(
                resultSet.getString(
                        "result"
                )
        );

        grade.setUpdatedAt(
                resultSet.getTimestamp(
                        "updated_at"
                )
        );

        return grade;
    }

    private Double getNullableDouble(
            ResultSet resultSet,
            String columnName
    ) throws SQLException {
        double value =
                resultSet.getDouble(
                        columnName
                );

        return resultSet.wasNull()
                ? null
                : value;
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
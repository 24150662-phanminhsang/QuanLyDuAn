package dao.impl;

import dao.GradeDAO;
import model.Grade;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAOImpl implements GradeDAO {

    private static final String BASE_SELECT =
            """
            SELECT

                g.grade_id,
                g.enrollment_id,

                e.student_id,
                e.class_id,

                g.attendance_score,
                g.midterm_score,
                g.final_score,
                g.average_score,

                g.result,
                g.updated_at

            FROM Grades g

            INNER JOIN Enrollments e
                    ON g.enrollment_id = e.enrollment_id
            """;
    @Override
    public boolean insert(Grade grade) {

        String sql =
                """
                INSERT INTO Grades
                (
                    enrollment_id,
                    attendance_score,
                    midterm_score,
                    final_score,
                    average_score,
                    result
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?
                )
                """;

        try (

                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)

        ) {

            ps.setInt(
                    1,
                    grade.getEnrollmentId()
            );

            ps.setDouble(
                    2,
                    grade.getAttendanceScore()
            );

            ps.setDouble(
                    3,
                    grade.getMidtermScore()
            );

            ps.setDouble(
                    4,
                    grade.getFinalScore()
            );

            ps.setDouble(
                    5,
                    grade.getAverageScore()
            );

            ps.setString(
                    6,
                    grade.getResult()
            );

            return ps.executeUpdate() > 0;

        }

        catch (SQLException e) {

            throw new RuntimeException(e);

        }

    }
    @Override
    public boolean update(Grade grade) {

        String sql =
                """
                UPDATE Grades

                SET

                    attendance_score=?,

                    midterm_score=?,

                    final_score=?,

                    average_score=?,

                    result=?,

                    updated_at=SYSDATETIME()

                WHERE grade_id=?
                """;

        try (

                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)

        ) {

            ps.setDouble(
                    1,
                    grade.getAttendanceScore()
            );

            ps.setDouble(
                    2,
                    grade.getMidtermScore()
            );

            ps.setDouble(
                    3,
                    grade.getFinalScore()
            );

            ps.setDouble(
                    4,
                    grade.getAverageScore()
            );

            ps.setString(
                    5,
                    grade.getResult()
            );

            ps.setInt(
                    6,
                    grade.getGradeId()
            );

            return ps.executeUpdate() > 0;

        }

        catch (SQLException e) {

            throw new RuntimeException(e);

        }

    }
    @Override
    public boolean delete(int gradeId) {

        String sql =
                """
                DELETE
                FROM Grades
                WHERE grade_id=?
                """;

        try (

                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement ps =
                        conn.prepareStatement(sql)

        ) {

            ps.setInt(
                    1,
                    gradeId
            );

            return ps.executeUpdate() > 0;

        }

        catch (SQLException e) {

            throw new RuntimeException(e);

        }

    }
    @Override
    public Grade getById(int gradeId) {
        if (gradeId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE g.grade_id = ?
                        """;

        return querySingle(
                sql,
                gradeId
        );
    }

    @Override
    public Grade getByEnrollmentId(
            int enrollmentId
    ) {
        if (enrollmentId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE g.enrollment_id = ?
                        """;

        return querySingle(
                sql,
                enrollmentId
        );
    }

    @Override
    public Grade getByStudentAndClass(
            int studentId,
            int classId
    ) {
        if (studentId <= 0 || classId <= 0) {
            return null;
        }

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
                    return mapGrade(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể lấy điểm của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    @Override
    public List<Grade> getByStudentId(
            int studentId
    ) {
        if (studentId <= 0) {
            return new ArrayList<>();
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE e.student_id = ?
                        ORDER BY g.updated_at DESC,
                                 g.grade_id DESC
                        """;

        return queryList(
                sql,
                studentId
        );
    }

    @Override
    public List<Grade> getByClassId(
            int classId
    ) {
        if (classId <= 0) {
            return new ArrayList<>();
        }

        String sql =
                BASE_SELECT
                        + """
                        WHERE e.class_id = ?
                        ORDER BY e.student_id ASC,
                                 g.grade_id ASC
                        """;

        return queryList(
                sql,
                classId
        );
    }

    @Override
    public List<Grade> getAll() {
        String sql =
                BASE_SELECT
                        + """
                        ORDER BY g.updated_at DESC,
                                 g.grade_id DESC
                        """;

        List<Grade> grades =
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
                grades.add(
                        mapGrade(resultSet)
                );
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách điểm: "
                            + exception.getMessage(),
                    exception
            );
        }

        return grades;
    }

    private Grade querySingle(
            String sql,
            int parameter
    ) {
        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    parameter
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapGrade(
                            resultSet
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải thông tin điểm: "
                            + exception.getMessage(),
                    exception
            );
        }

        return null;
    }

    private List<Grade> queryList(
            String sql,
            int parameter
    ) {
        List<Grade> grades =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    parameter
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    grades.add(
                            mapGrade(resultSet)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách điểm: "
                            + exception.getMessage(),
                    exception
            );
        }

        return grades;
    }

    private Grade mapGrade(
            ResultSet resultSet
    ) throws SQLException {

        Grade grade =
                new Grade();

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

        if (resultSet.wasNull()) {
            return null;
        }

        return value;
    }
}






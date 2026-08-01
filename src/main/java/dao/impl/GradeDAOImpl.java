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
                grade_id,
                enrollment_id,
                attendance_score,
                midterm_score,
                final_score,
                average_score,
                result,
                updated_at
            FROM Grades
            """;

    @Override
    public boolean insert(Grade grade) {
        validateGrade(grade);

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
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, grade.getEnrollmentId());

            setNullableDouble(
                    ps,
                    2,
                    grade.getAttendanceScore()
            );

            setNullableDouble(
                    ps,
                    3,
                    grade.getMidtermScore()
            );

            setNullableDouble(
                    ps,
                    4,
                    grade.getFinalScore()
            );

            Double averageScore =
                    calculateAverageScore(grade);

            setNullableDouble(
                    ps,
                    5,
                    averageScore
            );

            setNullableString(
                    ps,
                    6,
                    determineResult(
                            averageScore,
                            grade.getResult()
                    )
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể thêm điểm: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public boolean update(Grade grade) {
        validateGrade(grade);

        if (grade.getGradeId() <= 0) {
            throw new IllegalArgumentException(
                    "ID điểm không hợp lệ."
            );
        }

        String sql =
                """
                UPDATE Grades
                SET
                    enrollment_id = ?,
                    attendance_score = ?,
                    midterm_score = ?,
                    final_score = ?,
                    average_score = ?,
                    result = ?,
                    updated_at = SYSDATETIME()
                WHERE grade_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, grade.getEnrollmentId());

            setNullableDouble(
                    ps,
                    2,
                    grade.getAttendanceScore()
            );

            setNullableDouble(
                    ps,
                    3,
                    grade.getMidtermScore()
            );

            setNullableDouble(
                    ps,
                    4,
                    grade.getFinalScore()
            );

            Double averageScore =
                    calculateAverageScore(grade);

            setNullableDouble(
                    ps,
                    5,
                    averageScore
            );

            setNullableString(
                    ps,
                    6,
                    determineResult(
                            averageScore,
                            grade.getResult()
                    )
            );

            ps.setInt(7, grade.getGradeId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể cập nhật điểm: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public boolean delete(int gradeId) {
        if (gradeId <= 0) {
            throw new IllegalArgumentException(
                    "ID điểm phải lớn hơn 0."
            );
        }

        String sql =
                "DELETE FROM Grades WHERE grade_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gradeId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể xóa điểm: " + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public Grade getById(int gradeId) {
        if (gradeId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + " WHERE grade_id = ?";

        return querySingle(sql, gradeId);
    }

    @Override
    public Grade getByEnrollmentId(int enrollmentId) {
        if (enrollmentId <= 0) {
            return null;
        }

        String sql =
                BASE_SELECT
                        + " WHERE enrollment_id = ?";

        return querySingle(sql, enrollmentId);
    }

    @Override
    public Grade getByStudentAndClass(
            int studentId,
            int classId
    ) {
        String sql =
                """
                SELECT
                    g.grade_id,
                    g.enrollment_id,
                    g.attendance_score,
                    g.midterm_score,
                    g.final_score,
                    g.average_score,
                    g.result,
                    g.updated_at
                FROM Grades g
                INNER JOIN Enrollments e
                    ON g.enrollment_id = e.enrollment_id
                WHERE e.student_id = ?
                  AND e.class_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, classId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGrade(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể lấy điểm của học viên: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    @Override
    public List<Grade> getByStudentId(int studentId) {
        String sql =
                """
                SELECT
                    g.grade_id,
                    g.enrollment_id,
                    g.attendance_score,
                    g.midterm_score,
                    g.final_score,
                    g.average_score,
                    g.result,
                    g.updated_at
                FROM Grades g
                INNER JOIN Enrollments e
                    ON g.enrollment_id = e.enrollment_id
                WHERE e.student_id = ?
                ORDER BY g.updated_at DESC
                """;

        return queryList(sql, studentId);
    }

    @Override
    public List<Grade> getByClassId(int classId) {
        String sql =
                """
                SELECT
                    g.grade_id,
                    g.enrollment_id,
                    g.attendance_score,
                    g.midterm_score,
                    g.final_score,
                    g.average_score,
                    g.result,
                    g.updated_at
                FROM Grades g
                INNER JOIN Enrollments e
                    ON g.enrollment_id = e.enrollment_id
                WHERE e.class_id = ?
                ORDER BY g.updated_at DESC
                """;

        return queryList(sql, classId);
    }

    @Override
    public List<Grade> getAll() {
        String sql =
                BASE_SELECT
                        + " ORDER BY updated_at DESC";

        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                grades.add(mapGrade(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách điểm: "
                            + e.getMessage(),
                    e
            );
        }

        return grades;
    }

    private Grade querySingle(
            String sql,
            int parameter
    ) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, parameter);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGrade(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tải thông tin điểm: "
                            + e.getMessage(),
                    e
            );
        }

        return null;
    }

    private List<Grade> queryList(
            String sql,
            int parameter
    ) {
        List<Grade> grades = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(sql)) {

            ps.setInt(1, parameter);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapGrade(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách điểm: "
                            + e.getMessage(),
                    e
            );
        }

        return grades;
    }

    private Grade mapGrade(ResultSet rs)
            throws SQLException {

        Grade grade = new Grade();

        grade.setGradeId(
                rs.getInt("grade_id")
        );

        grade.setEnrollmentId(
                rs.getInt("enrollment_id")
        );

        grade.setAttendanceScore(
                getNullableDouble(
                        rs,
                        "attendance_score"
                )
        );

        grade.setMidtermScore(
                getNullableDouble(
                        rs,
                        "midterm_score"
                )
        );

        grade.setFinalScore(
                getNullableDouble(
                        rs,
                        "final_score"
                )
        );

        grade.setAverageScore(
                getNullableDouble(
                        rs,
                        "average_score"
                )
        );

        grade.setResult(
                rs.getString("result")
        );

        grade.setUpdatedAt(
                rs.getTimestamp("updated_at")
        );

        return grade;
    }

    private Double getNullableDouble(
            ResultSet rs,
            String columnName
    ) throws SQLException {

        double value = rs.getDouble(columnName);

        return rs.wasNull()
                ? null
                : value;
    }

    private void setNullableDouble(
            PreparedStatement ps,
            int index,
            Double value
    ) throws SQLException {

        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setDouble(index, value);
        }
    }

    private void setNullableString(
            PreparedStatement ps,
            int index,
            String value
    ) throws SQLException {

        if (value == null || value.isBlank()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value);
        }
    }

    private Double calculateAverageScore(
            Grade grade
    ) {
        if (grade.getAverageScore() != null) {
            return grade.getAverageScore();
        }

        Double attendance =
                grade.getAttendanceScore();

        Double midterm =
                grade.getMidtermScore();

        Double finalScore =
                grade.getFinalScore();

        if (attendance == null
                || midterm == null
                || finalScore == null) {

            return null;
        }

        double average =
                attendance * 0.1
                        + midterm * 0.3
                        + finalScore * 0.6;

        return Math.round(average * 100.0) / 100.0;
    }

    private String determineResult(
            Double averageScore,
            String currentResult
    ) {
        if (currentResult != null
                && !currentResult.isBlank()) {

            return currentResult.trim().toUpperCase();
        }

        if (averageScore == null) {
            return null;
        }

        return averageScore >= 5
                ? "PASSED"
                : "FAILED";
    }

    private void validateGrade(Grade grade) {
        if (grade == null) {
            throw new IllegalArgumentException(
                    "Thông tin điểm không được null."
            );
        }

        if (grade.getEnrollmentId() <= 0) {
            throw new IllegalArgumentException(
                    "Enrollment ID không hợp lệ."
            );
        }
    }
}
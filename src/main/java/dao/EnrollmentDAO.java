package dao;

import model.Enrollment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    private static final String BASE_SELECT =
            """
            SELECT
                enrollment_id,
                student_id,
                class_id,
                enrollment_date,
                progress_percent,
                status,
                created_at
            FROM Enrollments
            """;

    public boolean addEnrollment(Enrollment enrollment) {

        String sql =
                """
                INSERT INTO Enrollments
                (
                    student_id,
                    class_id,
                    enrollment_date,
                    progress_percent,
                    status
                )
                VALUES
                (
                    ?, ?, ?, ?, ?
                )
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getClassId());
            ps.setDate(3, enrollment.getEnrollmentDate());
            ps.setInt(4, enrollment.getProgressPercent());
            ps.setString(5, enrollment.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Enrollment> getAllEnrollments() {

        List<Enrollment> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             BASE_SELECT +
                                     " ORDER BY enrollment_id DESC"
                     );
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    public Enrollment getEnrollmentById(int id) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             BASE_SELECT +
                                     " WHERE enrollment_id=?"
                     )) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return map(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public List<Enrollment> getByStudentId(int studentId) {

        List<Enrollment> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             BASE_SELECT +
                                     " WHERE student_id=?"
                     )) {

            ps.setInt(1, studentId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }
    public List<Enrollment> getByClassId(
            int classId
    ) {
        if (classId <= 0) {
            throw new IllegalArgumentException(
                    "ID lớp học phải lớn hơn 0."
            );
        }

        List<Enrollment> list =
                new ArrayList<>();

        String sql =
                BASE_SELECT
                        + """
                     WHERE class_id = ?
                     ORDER BY enrollment_id DESC
                     """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    classId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    list.add(
                            map(resultSet)
                    );
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách đăng ký theo lớp: "
                            + exception.getMessage(),
                    exception
            );
        }

        return list;
    }

    public boolean updateEnrollment(Enrollment enrollment) {

        String sql =
                """
                UPDATE Enrollments
                SET
                    student_id=?,
                    class_id=?,
                    enrollment_date=?,
                    progress_percent=?,
                    status=?
                WHERE enrollment_id=?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollment.getStudentId());
            ps.setInt(2, enrollment.getClassId());
            ps.setDate(3, enrollment.getEnrollmentDate());
            ps.setInt(4, enrollment.getProgressPercent());
            ps.setString(5, enrollment.getStatus());
            ps.setInt(6, enrollment.getEnrollmentId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteEnrollment(int id) {

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps =
                     conn.prepareStatement(
                             "DELETE FROM Enrollments WHERE enrollment_id=?"
                     )) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Enrollment map(ResultSet rs)
            throws SQLException {

        Enrollment e = new Enrollment();

        e.setEnrollmentId(
                rs.getInt("enrollment_id")
        );

        e.setStudentId(
                rs.getInt("student_id")
        );

        e.setClassId(
                rs.getInt("class_id")
        );

        e.setEnrollmentDate(
                rs.getDate("enrollment_date")
        );

        e.setProgressPercent(
                rs.getInt("progress_percent")
        );

        e.setStatus(
                rs.getString("status")
        );

        e.setCreatedAt(
                rs.getTimestamp("created_at")
        );

        return e;
    }
    public int findEnrollmentId(
            int studentId,
            int classId
    ) {
        String sql =
                """
                SELECT enrollment_id
                FROM Enrollments
                WHERE student_id = ?
                  AND class_id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, classId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("enrollment_id");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tìm đăng ký học: "
                            + e.getMessage(),
                    e
            );
        }

        return 0;
    }
}
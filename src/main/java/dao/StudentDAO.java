package dao;

import model.Student;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * StudentDAO
 */
public class StudentDAO {

    private static final String BASE_SELECT =
            "SELECT student_id, student_code, user_id, full_name, " +
                    "date_of_birth, gender, email, phone, address, status, created_at " +
                    "FROM Students";

    // Thêm sinh viên
    public boolean addStudent(Student student) {
        String sql =
                "INSERT INTO Students " +
                        "(student_code, user_id, full_name, date_of_birth, " +
                        "gender, email, phone, address, status) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getStudentCode());

            if (student.getUserId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, student.getUserId());
            }

            ps.setString(3, student.getFullName());

            if (student.getDateOfBirth() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, student.getDateOfBirth());
            }

            ps.setString(5, student.getGender());
            ps.setString(6, student.getEmail());
            ps.setString(7, student.getPhone());
            ps.setString(8, student.getAddress());
            ps.setString(9, normalizeStatus(student.getStatus()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể thêm sinh viên: " + e.getMessage(),
                    e
            );
        }
    }

    // Lấy danh sách sinh viên
    public List<Student> getAllStudents() {
        String sql = BASE_SELECT + " ORDER BY student_id DESC";
        return queryList(sql);
    }

    // Tìm sinh viên theo ID
    public Student getStudentByID(int id) {
        String sql = BASE_SELECT + " WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tìm sinh viên theo ID: " + e.getMessage(),
                    e
            );
        }

        return null;
    }

    // Tên hàm phụ để tương thích với cách đặt tên mới
    public Student getStudentById(int id) {
        return getStudentByID(id);
    }

    // Tìm sinh viên theo mã sinh viên
    public Student getStudentByCode(String studentCode) {
        String sql = BASE_SELECT + " WHERE student_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, studentCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tìm sinh viên theo mã: " + e.getMessage(),
                    e
            );
        }

        return null;
    }

    // Cập nhật sinh viên
    public boolean updateStudent(Student student) {
        String sql =
                "UPDATE Students SET " +
                        "student_code = ?, " +
                        "user_id = ?, " +
                        "full_name = ?, " +
                        "date_of_birth = ?, " +
                        "gender = ?, " +
                        "email = ?, " +
                        "phone = ?, " +
                        "address = ?, " +
                        "status = ? " +
                        "WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, student.getStudentCode());

            if (student.getUserId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, student.getUserId());
            }

            ps.setString(3, student.getFullName());

            if (student.getDateOfBirth() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, student.getDateOfBirth());
            }

            ps.setString(5, student.getGender());
            ps.setString(6, student.getEmail());
            ps.setString(7, student.getPhone());
            ps.setString(8, student.getAddress());
            ps.setString(9, normalizeStatus(student.getStatus()));
            ps.setInt(10, student.getStudentID());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể cập nhật sinh viên: " + e.getMessage(),
                    e
            );
        }
    }

    // Xóa sinh viên
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM Students WHERE student_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể xóa sinh viên: " + e.getMessage(),
                    e
            );
        }
    }

    // Tìm kiếm sinh viên
    public List<Student> searchStudents(String keyword) {
        String sql =
                BASE_SELECT +
                        " WHERE student_code LIKE ? " +
                        "OR full_name LIKE ? " +
                        "OR email LIKE ? " +
                        "OR phone LIKE ? " +
                        "ORDER BY student_id DESC";

        String value =
                "%" + (keyword == null ? "" : keyword.trim()) + "%";

        List<Student> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, value);
            ps.setString(4, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tìm kiếm sinh viên: " + e.getMessage(),
                    e
            );
        }

        return list;
    }

    // Hàm dùng chung để lấy danh sách sinh viên
    private List<Student> queryList(String sql) {
        List<Student> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapStudent(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách sinh viên: " + e.getMessage(),
                    e
            );
        }

        return list;
    }

    // Chuyển dữ liệu ResultSet thành đối tượng Student
    private Student mapStudent(ResultSet rs) throws SQLException {
        Student student = new Student();

        student.setStudentID(rs.getInt("student_id"));
        student.setStudentCode(rs.getString("student_code"));

        int userId = rs.getInt("user_id");

        if (rs.wasNull()) {
            student.setUserId(null);
        } else {
            student.setUserId(userId);
        }

        student.setFullName(rs.getString("full_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setGender(rs.getString("gender"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setAddress(rs.getString("address"));
        student.setStatus(rs.getString("status"));
        student.setCreatedAt(rs.getTimestamp("created_at"));

        return student;
    }

    // Chuẩn hóa trạng thái sinh viên
    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }

        return status.trim().toUpperCase();
    }
}
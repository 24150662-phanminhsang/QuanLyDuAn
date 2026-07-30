package dao;

import connection.DBConnection;
import model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 * StudentDAO
 *
 * TODO: Implement later.
 */
public class StudentDAO {
    // Thêm Student
    public boolean addStudent(Student Student) {
        String sql = "INSERT INTO Student(FullName, Gender, Phone, Email, Address) VALUES(?,?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Student.getFullName());
            ps.setString(2, Student.getGender());
            ps.setString(3, Student.getPhone());
            ps.setString(4, Student.getEmail());
            ps.setString(5, Student.getAddress());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Lấy danh sách Student
    public List<Student> getAllStudents() {

        List<Student> list = new ArrayList<>();

        String sql = "SELECT * FROM Student";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Student s = new Student();

                s.setStudentID(rs.getInt("StudentID"));
                s.setFullName(rs.getString("FullName"));
                s.setGender(rs.getString("Gender"));
                s.setPhone(rs.getString("Phone"));
                s.setEmail(rs.getString("Email"));
                s.setAddress(rs.getString("Address"));

                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Tìm Student theo ID
    public Student getStudentByID(int id) {

        String sql = "SELECT * FROM Student WHERE StudentID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Student s = new Student();

                s.setStudentID(rs.getInt("StudentID"));
                s.setFullName(rs.getString("FullName"));
                s.setGender(rs.getString("Gender"));
                s.setPhone(rs.getString("Phone"));
                s.setEmail(rs.getString("Email"));
                s.setAddress(rs.getString("Address"));

                return s;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Cập nhật Student
    public boolean updateStudent(Student Student) {

        String sql = "UPDATE Student SET FullName=?, Gender=?, Phone=?, Email=?, Address=? WHERE StudentID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Student.getFullName());
            ps.setString(2, Student.getGender());
            ps.setString(3, Student.getPhone());
            ps.setString(4, Student.getEmail());
            ps.setString(5, Student.getAddress());
            ps.setInt(6, Student.getStudentID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa Student
    public boolean deleteStudent(int id) {

        String sql = "DELETE FROM Student WHERE StudentID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

}



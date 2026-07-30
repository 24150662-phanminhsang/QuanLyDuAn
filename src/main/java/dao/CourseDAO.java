package dao;
import connection.DBConnection;
import model.Course;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CourseDAO
 *
 * TODO: Implement later.
 */
public interface CourseDAO {

    // Thêm khóa học
    public boolean addCourse(Course course) {

        String sql = "INSERT INTO Course(CourseName, Fee, Duration) VALUES(?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseName());
            ps.setDouble(2, course.getFee());
            ps.setInt(3, course.getDuration());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Lấy tất cả khóa học
    public List<Course> getAllCourses() {

        List<Course> list = new ArrayList<>();

        String sql = "SELECT * FROM Course";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {

                Course c = new Course();

                c.setCourseID(rs.getInt("CourseID"));
                c.setCourseName(rs.getString("CourseName"));
                c.setFee(rs.getDouble("Fee"));
                c.setDuration(rs.getInt("Duration"));

                list.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Tìm khóa học theo ID
    public Course getCourseByID(int id) {

        String sql = "SELECT * FROM Course WHERE CourseID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                Course c = new Course();

                c.setCourseID(rs.getInt("CourseID"));
                c.setCourseName(rs.getString("CourseName"));
                c.setFee(rs.getDouble("Fee"));
                c.setDuration(rs.getInt("Duration"));

                return c;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Cập nhật khóa học
    public boolean updateCourse(Course course) {

        String sql = "UPDATE Course SET CourseName=?, Fee=?, Duration=? WHERE CourseID=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCourseName());
            ps.setDouble(2, course.getFee());
            ps.setInt(3, course.getDuration());
            ps.setInt(4, course.getCourseID());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // Xóa khóa học
    public boolean deleteCourse(int id) {

        String sql = "DELETE FROM Course WHERE CourseID=?";

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

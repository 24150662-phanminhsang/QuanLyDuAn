package dao.impl;

import dao.ClassDAO;
import model.ClassRoom;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAOImpl implements ClassDAO {

    @Override
    public boolean insert(ClassRoom classRoom) {
        String sql = "INSERT INTO CLASS_ROOMS (course_id, teacher_id, class_name, schedule, room, max_students) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classRoom.getTeacherId()); // course_id/teacher_id
            ps.setObject(2, classRoom.getTeacherId() > 0 ? classRoom.getTeacherId() : null, Types.INTEGER);
            ps.setString(3, classRoom.getClassName());
            ps.setString(4, classRoom.getSchedule());
            ps.setString(5, classRoom.getRoom());
            ps.setInt(6, classRoom.getMaxStudents());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(ClassRoom classRoom) {
        String sql = "UPDATE CLASS_ROOMS SET class_name = ?, schedule = ?, room = ?, max_students = ? WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, classRoom.getClassName());
            ps.setString(2, classRoom.getSchedule());
            ps.setString(3, classRoom.getRoom());
            ps.setInt(4, classRoom.getMaxStudents());
            ps.setInt(5, classRoom.getClassId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int classId) {
        String sql = "DELETE FROM CLASS_ROOMS WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ClassRoom getById(int classId) {
        String sql = "SELECT * FROM CLASS_ROOMS WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapClass(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<ClassRoom> getAll() {
        List<ClassRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM CLASS_ROOMS";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapClass(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ClassRoom> getByTeacherId(int teacherId) {
        List<ClassRoom> list = new ArrayList<>();
        String sql = "SELECT * FROM CLASS_ROOMS WHERE teacher_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapClass(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean assignTeacher(int classId, int teacherId) {
        String sql = "UPDATE CLASS_ROOMS SET teacher_id = ? WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            ps.setInt(2, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private ClassRoom mapClass(ResultSet rs) throws SQLException {
        ClassRoom c = new ClassRoom();
        c.setClassId(rs.getInt("class_id"));
        c.setTeacherId(rs.getInt("teacher_id"));
        c.setClassName(rs.getString("class_name"));
        c.setSchedule(rs.getString("schedule"));
        c.setRoom(rs.getString("room"));
        c.setMaxStudents(rs.getInt("max_students"));
        return c;
    }
}
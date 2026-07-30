package dao.impl;

import dao.GradeDAO;
import model.Grade;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAOImpl implements GradeDAO {

    @Override
    public boolean insert(Grade grade) {
        String sql = "INSERT INTO GRADES (student_id, class_id, score) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grade.getStudentId());
            ps.setInt(2, grade.getClassId());
            ps.setDouble(3, grade.getScore());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Grade grade) {
        String sql = "UPDATE GRADES SET score = ? WHERE student_id = ? AND class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, grade.getScore());
            ps.setInt(2, grade.getStudentId());
            ps.setInt(3, grade.getClassId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Grade getByStudentAndClass(int studentId, int classId) {
        String sql = "SELECT * FROM GRADES WHERE student_id = ? AND class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, classId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Grade g = new Grade();
                g.setGradeId(rs.getInt("grade_id"));
                g.setStudentId(rs.getInt("student_id"));
                g.setClassId(rs.getInt("class_id"));
                g.setScore(rs.getDouble("score"));
                return g;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Grade> getByClassId(int classId) {
        List<Grade> list = new ArrayList<>();
        String sql = "SELECT * FROM GRADES WHERE class_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, classId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Grade g = new Grade();
                g.setGradeId(rs.getInt("grade_id"));
                g.setStudentId(rs.getInt("student_id"));
                g.setClassId(rs.getInt("class_id"));
                g.setScore(rs.getDouble("score"));
                list.add(g);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
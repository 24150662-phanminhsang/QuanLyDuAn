package dao;

import dao.GradeDAO;
import model.Grade;
import java.util.List;

public interface GradeDAO {
    boolean insert(Grade grade);
    boolean update(Grade grade);
    Grade getByStudentAndClass(int studentId, int classId);
    List<Grade> getByClassId(int classId);
}
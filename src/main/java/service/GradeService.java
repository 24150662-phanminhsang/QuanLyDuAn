package service;

import dao.GradeDAO;
import dao.impl.GradeDAOImpl;
import model.Grade;

import java.util.List;

public class GradeService {
    private final GradeDAO gradeDAO = new GradeDAOImpl();

    // Tự động kiểm tra: Nếu học viên đã có điểm thì UPDATE, chưa có thì INSERT
    public boolean saveOrUpdateGrade(int studentId, int classId, double score) {
        if (score < 0 || score > 10) return false; // Khống chế thang điểm từ 0 đến 10

        Grade existingGrade = gradeDAO.getByStudentAndClass(studentId, classId);
        if (existingGrade == null) {
            Grade newGrade = new Grade();
            newGrade.setStudentId(studentId);
            newGrade.setClassId(classId);
            newGrade.setScore(score);
            return gradeDAO.insert(newGrade);
        } else {
            existingGrade.setScore(score);
            return gradeDAO.update(existingGrade);
        }
    }

    public List<Grade> getGradesByClass(int classId) {
        return gradeDAO.getByClassId(classId);
    }
}
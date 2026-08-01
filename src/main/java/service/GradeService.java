package service;

import dao.GradeDAO;
import dao.impl.GradeDAOImpl;
import model.Grade;

import java.util.Collections;
import java.util.List;

public class GradeService {

    private final GradeDAO gradeDAO;

    public GradeService() {
        this(new GradeDAOImpl());
    }

    public GradeService(GradeDAO gradeDAO) {
        this.gradeDAO = gradeDAO;
    }

    /**
     * Thêm mới hoặc cập nhật điểm.
     */
    public boolean saveOrUpdateGrade(
            int studentId,
            int classId,
            double score
    ) {
        if (studentId <= 0 || classId <= 0) {
            return false;
        }

        if (score < 0 || score > 10) {
            return false;
        }

        Grade existingGrade =
                gradeDAO.getByStudentAndClass(
                        studentId,
                        classId
                );

        if (existingGrade == null) {
            Grade newGrade = new Grade();

            newGrade.setStudentId(studentId);
            newGrade.setClassId(classId);
            newGrade.setScore(score);

            return gradeDAO.insert(newGrade);
        }

        existingGrade.setScore(score);

        return gradeDAO.update(existingGrade);
    }

    /**
     * Lấy danh sách điểm của một lớp.
     */
    public List<Grade> getGradesByClass(int classId) {
        if (classId <= 0) {
            return Collections.emptyList();
        }

        List<Grade> grades =
                gradeDAO.getByClassId(classId);

        return grades == null
                ? Collections.emptyList()
                : grades;
    }
}
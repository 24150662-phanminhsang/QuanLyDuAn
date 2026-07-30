package controller;

import service.GradeService;
import model.Grade;

public class GradeController {
    private GradeService gradeService = new GradeService();

    public boolean enterGrade(int studentId, int classId, double score) {
        Grade g = new Grade();
        g.setStudentId(studentId);
        g.setClassId(classId);
        g.setScore(score);
        return gradeService.saveOrUpdateGrade(studentId, classId, score);    }
}


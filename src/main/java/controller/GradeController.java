package controller;

import service.GradeService;
import model.Grade;
import java.util.List;

public class GradeController {
    private final GradeService gradeService = new GradeService();

    public boolean enterGrade(int studentId, int classId, double score) {
        return gradeService.saveOrUpdateGrade(studentId, classId, score);
    }

    public List<Grade> getGradesByClass(int classId) {
        return gradeService.getGradesByClass(classId);
    }
}
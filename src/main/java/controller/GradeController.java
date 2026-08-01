package controller;

import model.Grade;
import service.GradeService;

import java.util.Collections;
import java.util.List;

public class GradeController {

    private final GradeService gradeService;

    public GradeController() {
        this.gradeService = new GradeService();
    }

    public boolean enterGrade(
            int studentId,
            int classId,
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        try {
            return gradeService.saveOrUpdateGrade(
                    studentId,
                    classId,
                    attendanceScore,
                    midtermScore,
                    finalScore
            );

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể lưu điểm học viên.",
                    e
            );
        }
    }

    public Grade getGradeByStudentAndClass(
            int studentId,
            int classId
    ) {
        try {
            return gradeService.getGradeByStudentAndClass(
                    studentId,
                    classId
            );

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể lấy điểm của học viên.",
                    e
            );
        }
    }

    public List<Grade> getGradesByStudent(
            int studentId
    ) {
        try {
            List<Grade> grades =
                    gradeService.getGradesByStudent(studentId);

            return grades == null
                    ? Collections.emptyList()
                    : grades;

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tải kết quả học tập.",
                    e
            );
        }
    }

    public List<Grade> getGradesByClass(
            int classId
    ) {
        try {
            List<Grade> grades =
                    gradeService.getGradesByClass(classId);

            return grades == null
                    ? Collections.emptyList()
                    : grades;

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách điểm của lớp.",
                    e
            );
        }
    }

    public boolean deleteGrade(int gradeId) {
        try {
            return gradeService.deleteGrade(gradeId);

        } catch (IllegalArgumentException e) {
            throw e;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể xóa điểm.",
                    e
            );
        }
    }
}
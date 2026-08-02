package controller;

import model.dto.StudentGradeDTO;
import service.StudentGradeService;

import java.util.Collections;
import java.util.List;

public class StudentGradeController {

    private final StudentGradeService studentGradeService;

    public StudentGradeController() {
        this.studentGradeService =
                new StudentGradeService();
    }

    public StudentGradeController(
            StudentGradeService studentGradeService
    ) {
        if (studentGradeService == null) {
            throw new IllegalArgumentException(
                    "StudentGradeService không được null."
            );
        }

        this.studentGradeService =
                studentGradeService;
    }

    public List<StudentGradeDTO> getGrades(
            int studentId
    ) {
        try {
            List<StudentGradeDTO> grades =
                    studentGradeService
                            .getGradesByStudentId(
                                    studentId
                            );

            return grades == null
                    ? Collections.emptyList()
                    : grades;

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải kết quả học tập.",
                    exception
            );
        }
    }

    public List<StudentGradeDTO> searchGrades(
            int studentId,
            String keyword
    ) {
        try {
            List<StudentGradeDTO> grades =
                    studentGradeService
                            .searchGrades(
                                    studentId,
                                    keyword
                            );

            return grades == null
                    ? Collections.emptyList()
                    : grades;

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm kết quả học tập.",
                    exception
            );
        }
    }

    public StudentGradeDTO getByGradeId(
            int gradeId
    ) {
        try {
            return studentGradeService
                    .getByGradeId(
                            gradeId
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải chi tiết điểm.",
                    exception
            );
        }
    }

    public int countPassedSubjects(
            int studentId
    ) {
        return studentGradeService
                .countPassedSubjects(
                        studentId
                );
    }

    public int countFailedSubjects(
            int studentId
    ) {
        return studentGradeService
                .countFailedSubjects(
                        studentId
                );
    }

    public double getAverageScore(
            int studentId
    ) {
        return studentGradeService
                .calculateAverageScore(
                        studentId
                );
    }
}
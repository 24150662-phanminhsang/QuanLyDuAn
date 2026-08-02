package service;

import dao.StudentGradeDAO;
import model.dto.StudentGradeDTO;

import java.util.Collections;
import java.util.List;

public class StudentGradeService {

    private final StudentGradeDAO studentGradeDAO;

    public StudentGradeService() {
        this.studentGradeDAO =
                new StudentGradeDAO();
    }

    public StudentGradeService(
            StudentGradeDAO studentGradeDAO
    ) {
        if (studentGradeDAO == null) {
            throw new IllegalArgumentException(
                    "StudentGradeDAO không được null."
            );
        }

        this.studentGradeDAO =
                studentGradeDAO;
    }

    public List<StudentGradeDTO> getGradesByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<StudentGradeDTO> grades =
                studentGradeDAO.getByStudentId(
                        studentId
                );

        return grades == null
                ? Collections.emptyList()
                : grades;
    }

    public List<StudentGradeDTO> searchGrades(
            int studentId,
            String keyword
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<StudentGradeDTO> grades =
                studentGradeDAO.searchByStudentId(
                        studentId,
                        keyword
                );

        return grades == null
                ? Collections.emptyList()
                : grades;
    }

    public StudentGradeDTO getByGradeId(
            int gradeId
    ) {
        validatePositiveId(
                gradeId,
                "ID điểm"
        );

        return studentGradeDAO.getByGradeId(
                gradeId
        );
    }

    public int countPassedSubjects(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        return studentGradeDAO.countPassedSubjects(
                studentId
        );
    }

    public int countFailedSubjects(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        return studentGradeDAO.countFailedSubjects(
                studentId
        );
    }

    public double calculateAverageScore(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        double average =
                studentGradeDAO.calculateAverageScore(
                        studentId
                );

        return Math.round(
                average * 100.0
        ) / 100.0;
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }
}
package service;

import dao.EnrollmentDAO;
import dao.GradeDAO;
import dao.impl.GradeDAOImpl;
import model.Grade;

import java.util.Collections;
import java.util.List;

public class GradeService {

    private final GradeDAO gradeDAO;
    private final EnrollmentDAO enrollmentDAO;

    public GradeService() {
        this.gradeDAO = new GradeDAOImpl();
        this.enrollmentDAO = new EnrollmentDAO();
    }

    public boolean saveOrUpdateGrade(
            int studentId,
            int classId,
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        validateId(studentId, "ID sinh viên");
        validateId(classId, "ID lớp học");

        validateScore(
                attendanceScore,
                "Điểm chuyên cần"
        );

        validateScore(
                midtermScore,
                "Điểm giữa kỳ"
        );

        validateScore(
                finalScore,
                "Điểm cuối kỳ"
        );

        int enrollmentId =
                enrollmentDAO.findEnrollmentId(
                        studentId,
                        classId
                );

        if (enrollmentId <= 0) {
            throw new IllegalArgumentException(
                    "Sinh viên chưa đăng ký lớp học này."
            );
        }

        double averageScore =
                calculateAverage(
                        attendanceScore,
                        midtermScore,
                        finalScore
                );

        String result =
                averageScore >= 5
                        ? "PASSED"
                        : "FAILED";

        Grade existingGrade =
                gradeDAO.getByEnrollmentId(
                        enrollmentId
                );

        if (existingGrade == null) {
            Grade grade = new Grade();

            grade.setEnrollmentId(enrollmentId);
            grade.setAttendanceScore(attendanceScore);
            grade.setMidtermScore(midtermScore);
            grade.setFinalScore(finalScore);
            grade.setAverageScore(averageScore);
            grade.setResult(result);

            return gradeDAO.insert(grade);
        }

        existingGrade.setAttendanceScore(
                attendanceScore
        );

        existingGrade.setMidtermScore(
                midtermScore
        );

        existingGrade.setFinalScore(
                finalScore
        );

        existingGrade.setAverageScore(
                averageScore
        );

        existingGrade.setResult(result);

        return gradeDAO.update(existingGrade);
    }

    public Grade getGradeByStudentAndClass(
            int studentId,
            int classId
    ) {
        validateId(studentId, "ID sinh viên");
        validateId(classId, "ID lớp học");

        return gradeDAO.getByStudentAndClass(
                studentId,
                classId
        );
    }

    public List<Grade> getGradesByStudent(
            int studentId
    ) {
        validateId(studentId, "ID sinh viên");

        List<Grade> grades =
                gradeDAO.getByStudentId(studentId);

        return grades == null
                ? Collections.emptyList()
                : grades;
    }

    public List<Grade> getGradesByClass(
            int classId
    ) {
        validateId(classId, "ID lớp học");

        List<Grade> grades =
                gradeDAO.getByClassId(classId);

        return grades == null
                ? Collections.emptyList()
                : grades;
    }

    public List<Grade> getAllGrades() {
        List<Grade> grades =
                gradeDAO.getAll();

        return grades == null
                ? Collections.emptyList()
                : grades;
    }

    public boolean deleteGrade(int gradeId) {
        validateId(gradeId, "ID điểm");

        return gradeDAO.delete(gradeId);
    }

    private void validateId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " phải lớn hơn 0."
            );
        }
    }

    private void validateScore(
            double score,
            String fieldName
    ) {
        if (Double.isNaN(score)
                || Double.isInfinite(score)
                || score < 0
                || score > 10) {

            throw new IllegalArgumentException(
                    fieldName
                            + " phải nằm trong khoảng từ 0 đến 10."
            );
        }
    }

    private double calculateAverage(
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        double average =
                attendanceScore * 0.1
                        + midtermScore * 0.3
                        + finalScore * 0.6;

        return Math.round(
                average * 100.0
        ) / 100.0;
    }
}
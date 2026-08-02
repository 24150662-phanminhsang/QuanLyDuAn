package service;

import dao.StudentCourseDAO;
import model.dto.StudentCourseDTO;

import java.util.Collections;
import java.util.List;

public class StudentCourseService {

    private final StudentCourseDAO studentCourseDAO;

    public StudentCourseService() {
        this.studentCourseDAO =
                new StudentCourseDAO();
    }

    public StudentCourseService(
            StudentCourseDAO studentCourseDAO
    ) {
        if (studentCourseDAO == null) {
            throw new IllegalArgumentException(
                    "StudentCourseDAO không được null."
            );
        }

        this.studentCourseDAO =
                studentCourseDAO;
    }

    /* =====================================================
       LẤY TOÀN BỘ KHÓA HỌC CỦA SINH VIÊN
       ===================================================== */

    public List<StudentCourseDTO> getCoursesByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<StudentCourseDTO> courses =
                studentCourseDAO.getByStudentId(
                        studentId
                );

        return courses == null
                ? Collections.emptyList()
                : courses;
    }

    /* =====================================================
       LẤY KHÓA HỌC ĐANG HỌC
       ===================================================== */

    public List<StudentCourseDTO> getActiveCoursesByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<StudentCourseDTO> courses =
                studentCourseDAO.getActiveByStudentId(
                        studentId
                );

        return courses == null
                ? Collections.emptyList()
                : courses;
    }

    /* =====================================================
       TÌM THEO ENROLLMENT
       ===================================================== */

    public StudentCourseDTO getByEnrollmentId(
            int enrollmentId
    ) {
        validatePositiveId(
                enrollmentId,
                "ID đăng ký học"
        );

        return studentCourseDAO.getByEnrollmentId(
                enrollmentId
        );
    }

    /* =====================================================
       TÌM THEO SINH VIÊN VÀ LỚP
       ===================================================== */

    public StudentCourseDTO getByStudentAndClass(
            int studentId,
            int classId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        validatePositiveId(
                classId,
                "ID lớp học"
        );

        return studentCourseDAO
                .getByStudentAndClass(
                        studentId,
                        classId
                );
    }

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

    public List<StudentCourseDTO> searchCourses(
            int studentId,
            String keyword
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        List<StudentCourseDTO> courses =
                studentCourseDAO.searchByStudentId(
                        studentId,
                        normalizedKeyword
                );

        return courses == null
                ? Collections.emptyList()
                : courses;
    }

    /* =====================================================
       ĐẾM SỐ KHÓA HỌC ĐANG HỌC
       ===================================================== */

    public int countActiveCourses(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        return studentCourseDAO.countActiveCourses(
                studentId
        );
    }

    /* =====================================================
       KIỂM TRA QUYỀN XEM LỚP
       ===================================================== */

    public boolean isStudentEnrolledInClass(
            int studentId,
            int classId
    ) {
        if (studentId <= 0
                || classId <= 0) {

            return false;
        }

        return studentCourseDAO
                .existsByStudentAndClass(
                        studentId,
                        classId
                );
    }

    /* =====================================================
       TÍNH TIẾN ĐỘ TRUNG BÌNH
       ===================================================== */

    public double calculateAverageProgress(
            int studentId
    ) {
        List<StudentCourseDTO> courses =
                getActiveCoursesByStudentId(
                        studentId
                );

        if (courses.isEmpty()) {
            return 0.0;
        }

        return courses.stream()
                .mapToInt(
                        StudentCourseDTO::getProgressPercent
                )
                .average()
                .orElse(0.0);
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

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
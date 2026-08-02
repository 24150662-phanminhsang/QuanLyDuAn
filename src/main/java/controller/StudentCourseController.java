package controller;

import model.dto.StudentCourseDTO;
import service.StudentCourseService;

import java.util.Collections;
import java.util.List;

public class StudentCourseController {

    private final StudentCourseService studentCourseService;

    public StudentCourseController() {
        this.studentCourseService =
                new StudentCourseService();
    }

    public StudentCourseController(
            StudentCourseService studentCourseService
    ) {
        if (studentCourseService == null) {
            throw new IllegalArgumentException(
                    "StudentCourseService không được null."
            );
        }

        this.studentCourseService =
                studentCourseService;
    }

    public List<StudentCourseDTO> getCourses(
            int studentId
    ) {
        try {
            List<StudentCourseDTO> courses =
                    studentCourseService
                            .getCoursesByStudentId(
                                    studentId
                            );

            return courses == null
                    ? Collections.emptyList()
                    : courses;

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải khóa học của sinh viên.",
                    exception
            );
        }
    }

    public List<StudentCourseDTO> getActiveCourses(
            int studentId
    ) {
        try {
            List<StudentCourseDTO> courses =
                    studentCourseService
                            .getActiveCoursesByStudentId(
                                    studentId
                            );

            return courses == null
                    ? Collections.emptyList()
                    : courses;

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải khóa học đang học.",
                    exception
            );
        }
    }

    public List<StudentCourseDTO> searchCourses(
            int studentId,
            String keyword
    ) {
        try {
            List<StudentCourseDTO> courses =
                    studentCourseService
                            .searchCourses(
                                    studentId,
                                    keyword
                            );

            return courses == null
                    ? Collections.emptyList()
                    : courses;

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm khóa học.",
                    exception
            );
        }
    }

    public StudentCourseDTO getByEnrollmentId(
            int enrollmentId
    ) {
        try {
            return studentCourseService
                    .getByEnrollmentId(
                            enrollmentId
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải chi tiết đăng ký học.",
                    exception
            );
        }
    }

    public StudentCourseDTO getByStudentAndClass(
            int studentId,
            int classId
    ) {
        try {
            return studentCourseService
                    .getByStudentAndClass(
                            studentId,
                            classId
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải thông tin lớp học.",
                    exception
            );
        }
    }

    public int countActiveCourses(
            int studentId
    ) {
        try {
            return studentCourseService
                    .countActiveCourses(
                            studentId
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể đếm số khóa học.",
                    exception
            );
        }
    }

    public double getAverageProgress(
            int studentId
    ) {
        try {
            return studentCourseService
                    .calculateAverageProgress(
                            studentId
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tính tiến độ trung bình.",
                    exception
            );
        }
    }

    public boolean isStudentEnrolledInClass(
            int studentId,
            int classId
    ) {
        try {
            return studentCourseService
                    .isStudentEnrolledInClass(
                            studentId,
                            classId
                    );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra đăng ký học.",
                    exception
            );
        }
    }
}
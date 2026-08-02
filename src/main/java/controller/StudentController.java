package controller;

import model.Student;
import service.StudentService;

import java.util.Collections;
import java.util.List;

public class StudentController {

    private final StudentService studentService;

    public StudentController() {
        this(
                new StudentService()
        );
    }

    public StudentController(
            StudentService studentService
    ) {
        if (studentService == null) {
            throw new IllegalArgumentException(
                    "StudentService không được null."
            );
        }

        this.studentService =
                studentService;
    }

    /* =====================================================
       TẠO TÀI KHOẢN VÀ HỒ SƠ HỌC VIÊN
       ===================================================== */

    public boolean createStudentAccount(
            Student student,
            String username,
            String password,
            String confirmPassword
    ) {
        try {
            return studentService
                    .createStudentAccount(
                            student,
                            username,
                            password,
                            confirmPassword
                    );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tạo tài khoản học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       THÊM HỒ SƠ CŨ
       ===================================================== */

    public boolean addStudent(
            Student student
    ) {
        try {
            return studentService.addStudent(
                    student
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể thêm học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       DANH SÁCH VÀ TÌM KIẾM
       ===================================================== */

    public List<Student> getAllStudents() {
        try {
            List<Student> students =
                    studentService.getAllStudents();

            return students == null
                    ? Collections.emptyList()
                    : students;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách học viên.",
                    exception
            );
        }
    }

    public List<Student> searchStudents(
            String keyword
    ) {
        try {
            List<Student> students =
                    studentService.searchStudents(
                            keyword
                    );

            return students == null
                    ? Collections.emptyList()
                    : students;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tìm kiếm học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       TÌM HỌC VIÊN
       ===================================================== */

    public Student getStudentByID(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID học viên"
        );

        try {
            return studentService.getStudentByID(
                    studentId
            );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể lấy thông tin học viên.",
                    exception
            );
        }
    }

    public Student getStudentById(
            int studentId
    ) {
        return getStudentByID(
                studentId
        );
    }

    public Student getStudentByCode(
            String studentCode
    ) {
        if (
                studentCode == null
                        || studentCode.isBlank()
        ) {
            return null;
        }

        try {
            return studentService.getStudentByCode(
                    studentCode.trim()
            );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể lấy học viên theo mã.",
                    exception
            );
        }
    }

    public Student getStudentByUserId(
            int userId
    ) {
        validatePositiveId(
                userId,
                "ID tài khoản"
        );

        try {
            return studentService.getStudentByUserId(
                    userId
            );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể lấy học viên theo tài khoản.",
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT HỌC VIÊN
       ===================================================== */

    public boolean updateStudent(
            Student student
    ) {
        try {
            return studentService.updateStudent(
                    student
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật học viên.",
                    exception
            );
        }
    }

    public boolean updateStudentAndUser(
            Student student
    ) {
        try {
            return studentService
                    .updateStudentAndUser(
                            student
                    );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật học viên và tài khoản.",
                    exception
            );
        }
    }

    /* =====================================================
       ĐỔI MẬT KHẨU
       ===================================================== */

    public boolean resetStudentPassword(
            int studentId,
            String newPassword,
            String confirmPassword
    ) {
        validatePositiveId(
                studentId,
                "ID học viên"
        );

        try {
            return studentService
                    .resetStudentPassword(
                            studentId,
                            newPassword,
                            confirmPassword
                    );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể đổi mật khẩu học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       XÓA HỌC VIÊN
       ===================================================== */

    public boolean deleteStudent(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID học viên"
        );

        try {
            return studentService.deleteStudent(
                    studentId
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể xóa học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       KIỂM TRA TỒN TẠI
       ===================================================== */

    public boolean existsByStudentCode(
            String studentCode
    ) {
        if (
                studentCode == null
                        || studentCode.isBlank()
        ) {
            return false;
        }

        return studentService.existsByStudentCode(
                studentCode.trim()
        );
    }

    public boolean existsByUserId(
            int userId
    ) {
        if (userId <= 0) {
            return false;
        }

        return studentService.existsByUserId(
                userId
        );
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
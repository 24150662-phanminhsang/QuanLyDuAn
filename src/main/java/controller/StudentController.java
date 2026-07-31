package controller;

import dao.StudentDAO;
import model.Student;

import java.util.Collections;
import java.util.List;

public class StudentController {

    private final StudentDAO studentDAO;

    public StudentController() {
        this.studentDAO = new StudentDAO();
    }

    public List<Student> getAllStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();

            return students == null
                    ? Collections.emptyList()
                    : students;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách học viên.",
                    e
            );
        }
    }

    public Student getStudentById(int studentId) {
        validateStudentId(studentId);

        try {
            return studentDAO.getStudentById(studentId);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể lấy thông tin học viên.",
                    e
            );
        }
    }

    public Student getStudentByCode(String studentCode) {
        if (studentCode == null || studentCode.isBlank()) {
            throw new IllegalArgumentException(
                    "Mã học viên không được để trống."
            );
        }

        try {
            return studentDAO.getStudentByCode(
                    studentCode.trim()
            );

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tìm học viên theo mã.",
                    e
            );
        }
    }

    public List<Student> searchStudents(String keyword) {
        try {
            List<Student> students =
                    studentDAO.searchStudents(keyword);

            return students == null
                    ? Collections.emptyList()
                    : students;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tìm kiếm học viên.",
                    e
            );
        }
    }

    public boolean addStudent(Student student) {
        validateStudent(student, false);

        try {
            return studentDAO.addStudent(student);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể thêm học viên.",
                    e
            );
        }
    }

    public boolean updateStudent(Student student) {
        validateStudent(student, true);

        try {
            return studentDAO.updateStudent(student);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể cập nhật học viên.",
                    e
            );
        }
    }

    public boolean deleteStudent(int studentId) {
        validateStudentId(studentId);

        try {
            return studentDAO.deleteStudent(studentId);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể xóa học viên.",
                    e
            );
        }
    }

    private void validateStudent(
            Student student,
            boolean requireId
    ) {
        if (student == null) {
            throw new IllegalArgumentException(
                    "Thông tin học viên không được null."
            );
        }

        if (requireId && student.getStudentID() <= 0) {
            throw new IllegalArgumentException(
                    "ID học viên không hợp lệ."
            );
        }

        if (
                student.getStudentCode() == null
                        || student.getStudentCode().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mã học viên không được để trống."
            );
        }

        if (
                student.getFullName() == null
                        || student.getFullName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Họ và tên học viên không được để trống."
            );
        }
    }

    private void validateStudentId(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID học viên phải lớn hơn 0."
            );
        }
    }
}
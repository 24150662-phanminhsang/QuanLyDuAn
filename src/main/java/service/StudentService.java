package service;

import dao.StudentDAO;
import model.Student;

import java.util.List;

/**
 * StudentService
 */
public class StudentService {

    private final StudentDAO studentDAO;

    public StudentService() {
        this.studentDAO = new StudentDAO();
    }

    // Thêm sinh viên
    public boolean addStudent(Student student) {
        if (student == null) {
            return false;
        }

        if (student.getStudentCode() == null
                || student.getStudentCode().isBlank()) {
            return false;
        }

        if (student.getFullName() == null
                || student.getFullName().isBlank()) {
            return false;
        }

        return studentDAO.addStudent(student);
    }

    // Lấy danh sách sinh viên
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // Tìm sinh viên theo ID
    public Student getStudentByID(int id) {
        if (id <= 0) {
            return null;
        }

        return studentDAO.getStudentByID(id);
    }

    // Tên hàm tương thích với cách viết getStudentById
    public Student getStudentById(int id) {
        return getStudentByID(id);
    }

    // Tìm sinh viên theo mã sinh viên
    public Student getStudentByCode(String studentCode) {
        if (studentCode == null || studentCode.isBlank()) {
            return null;
        }

        return studentDAO.getStudentByCode(studentCode.trim());
    }

    // Tìm kiếm sinh viên
    public List<Student> searchStudents(String keyword) {
        return studentDAO.searchStudents(keyword);
    }

    // Cập nhật sinh viên
    public boolean updateStudent(Student student) {
        if (student == null || student.getStudentID() <= 0) {
            return false;
        }

        if (student.getStudentCode() == null
                || student.getStudentCode().isBlank()) {
            return false;
        }

        if (student.getFullName() == null
                || student.getFullName().isBlank()) {
            return false;
        }

        return studentDAO.updateStudent(student);
    }

    // Xóa sinh viên
    public boolean deleteStudent(int id) {
        if (id <= 0) {
            return false;
        }

        return studentDAO.deleteStudent(id);
    }
}
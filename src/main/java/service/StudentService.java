package service;

import dao.StudentDAO;
import model.Student;

import java.util.List;
/**
 * StudentService
 */
public class StudentService {
    private StudentDAO studentDAO;

    public StudentService() {
        studentDAO = new StudentDAO();
    }

    // Thêm sinh viên
    public boolean addStudent(Student student) {
        return studentDAO.addStudent(student);
    }

    // Lấy danh sách sinh viên
    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    // Tìm sinh viên theo ID
    public Student getStudentByID(int id) {
        return studentDAO.getStudentByID(id);
    }

    // Cập nhật sinh viên
    public boolean updateStudent(Student student) {
        return studentDAO.updateStudent(student);
    }

    // Xóa sinh viên
    public boolean deleteStudent(int id) {
        return studentDAO.deleteStudent(id);
    }

}



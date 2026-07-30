package controller;
import model.Student;
import service.StudentService;

import java.util.List;

/**
 * Điều phối xử lý cho module StudentController.
 *
 * Luồng:
 * View -> Controller -> Service -> DAO -> SQL Server
 */
public class StudentController {
    private StudentService studentService;

    public StudentController() {
        studentService = new StudentService();
    }

    // Thêm sinh viên
    public boolean addStudent(Student student) {
        return studentService.addStudent(student);
    }

    // Lấy danh sách sinh viên
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    // Tìm sinh viên theo ID
    public Student getStudentByID(int id) {
        return studentService.getStudentByID(id);
    }

    // Cập nhật sinh viên
    public boolean updateStudent(Student student) {
        return studentService.updateStudent(student);
    }

    // Xóa sinh viên
    public boolean deleteStudent(int id) {
        return studentService.deleteStudent(id);
    }

}

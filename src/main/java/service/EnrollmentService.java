package service;
import dao.EnrollmentDAO;
import model.Enrollment;

import java.util.List;

/**
 * EnrollmentService
 */
public class EnrollmentService {
    private EnrollmentDAO enrollmentDAO;

    public EnrollmentService() {
        enrollmentDAO = new EnrollmentDAO();
    }

    // Thêm đăng ký
    public boolean addEnrollment(Enrollment enrollment) {
        return enrollmentDAO.addEnrollment(enrollment);
    }

    // Lấy tất cả đăng ký
    public List<Enrollment> getAllEnrollments() {
        return enrollmentDAO.getAllEnrollments();
    }

    // Tìm đăng ký theo ID
    public Enrollment getEnrollmentById(int id) {
        return enrollmentDAO.getEnrollmentById(id);
    }

    // Cập nhật đăng ký
    public boolean updateEnrollment(Enrollment enrollment) {
        return enrollmentDAO.updateEnrollment(enrollment);
    }

    // Xóa đăng ký
    public boolean deleteEnrollment(int id) {
        return enrollmentDAO.deleteEnrollment(id);
    }

}

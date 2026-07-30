package controller;
import model.Enrollment;
import service.EnrollmentService;

import java.util.List;

/**
 * Điều phối xử lý cho module EnrollmentController.
 *
 * Luồng:
 * View -> Controller -> Service -> DAO -> SQL Server
 */
public class EnrollmentController {
    private EnrollmentService enrollmentService;

    public EnrollmentController() {
        enrollmentService = new EnrollmentService();
    }

    // Thêm đăng ký
    public boolean addEnrollment(Enrollment enrollment) {
        return enrollmentService.addEnrollment(enrollment);
    }

    // Lấy danh sách đăng ký
    public List<Enrollment> getAllEnrollments() {
        return enrollmentService.getAllEnrollments();
    }

    // Tìm đăng ký theo ID
    public Enrollment getEnrollmentById(int id) {
        return enrollmentService.getEnrollmentById(id);
    }

    // Cập nhật đăng ký
    public boolean updateEnrollment(Enrollment enrollment) {
        return enrollmentService.updateEnrollment(enrollment);
    }

    // Xóa đăng ký
    public boolean deleteEnrollment(int id) {
        return enrollmentService.deleteEnrollment(id);
    }

}

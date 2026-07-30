package dao;

import model.Enrollment;

import java.util.ArrayList;
import java.util.List;

/**
 * EnrollmentDAO
 * Phiên bản tạm thời.
 * Sau này sẽ thay bằng SQL Server.
 */
public class EnrollmentDAO {

    private final List<Enrollment> enrollmentList = new ArrayList<>();

    // Thêm đăng ký
    public boolean addEnrollment(Enrollment enrollment) {
        return enrollmentList.add(enrollment);
    }

    // Lấy tất cả đăng ký
    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollmentList);
    }

    // Tìm theo ID
    public Enrollment getEnrollmentById(int id) {
        for (Enrollment enrollment : enrollmentList) {
            if (enrollment.getId() == id) {
                return enrollment;
            }
        }
        return null;
    }

    // Cập nhật
    public boolean updateEnrollment(Enrollment enrollment) {
        for (int i = 0; i < enrollmentList.size(); i++) {
            if (enrollmentList.get(i).getId() == enrollment.getId()) {
                enrollmentList.set(i, enrollment);
                return true;
            }
        }
        return false;
    }

    // Xóa
    public boolean deleteEnrollment(int id) {
        Enrollment enrollment = getEnrollmentById(id);

        if (enrollment != null) {
            enrollmentList.remove(enrollment);
            return true;
        }

        return false;
    }
}
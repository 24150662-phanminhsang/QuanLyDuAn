package dao;
import model.Enrollment;

import java.util.ArrayList;
import java.util.List;

/**
 * EnrollmentDAO
 *
 * TODO: Implement later.
 */
public interface EnrollmentDAO {
    List<Enrollment> enrollmentList = List.of();

    // Thêm đăng ký
    public default boolean addEnrollment(Enrollment enrollment) {
        return enrollmentList.add(enrollment);
    }

    // Lấy tất cả đăng ký
    public default List<Enrollment> getAllEnrollments() {
        return enrollmentList;
    }

    // Tìm theo ID
    public default Enrollment getEnrollmentById(int id) {
        for (Enrollment enrollment : enrollmentList) {
            if (enrollment.getId() == id) {
                return enrollment;
            }
        }
        return null;
    }

    // Cập nhật
     public default boolean updateEnrollment(Enrollment enrollment) {
        for (int i = 0; i < enrollmentList.size(); i++) {
            if (enrollmentList.get(i).getId() == enrollment.getId()) {
                enrollmentList.set(i, enrollment);
                return true;
            }
        }
        return false;
    }

    // Xóa
    public default boolean deleteEnrollment(int id) {
        Enrollment enrollment = getEnrollmentById(id);
        if (enrollment != null) {
            enrollmentList.remove(enrollment);
            return true;
        }
        return false;
    }

}

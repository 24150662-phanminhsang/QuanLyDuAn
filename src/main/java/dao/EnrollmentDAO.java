package dao;

import model.Enrollment;

import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

    private final List<Enrollment> enrollmentList = new ArrayList<>();

    public boolean addEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return false;
        }

        return enrollmentList.add(enrollment);
    }

    public List<Enrollment> getAllEnrollments() {
        return new ArrayList<>(enrollmentList);
    }

    public Enrollment getEnrollmentById(int id) {
        for (Enrollment enrollment : enrollmentList) {
            if (enrollment.getEnrollmentId() == id) {
                return enrollment;
            }
        }

        return null;
    }

    public boolean updateEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            return false;
        }

        for (int i = 0; i < enrollmentList.size(); i++) {
            Enrollment current = enrollmentList.get(i);

            if (
                    current.getEnrollmentId()
                            == enrollment.getEnrollmentId()
            ) {
                enrollmentList.set(i, enrollment);
                return true;
            }
        }

        return false;
    }

    public boolean deleteEnrollment(int id) {
        Enrollment enrollment =
                getEnrollmentById(id);

        if (enrollment == null) {
            return false;
        }

        return enrollmentList.remove(enrollment);
    }
}
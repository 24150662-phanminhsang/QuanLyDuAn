package service;
<<<<<<< HEAD
=======

import dao.CourseDAO;
import model.Course;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
>>>>>>> 1728579b857bfb79834ec45188e9749d64f3821e

public class CourseService {
<<<<<<< HEAD

}
=======

    private final CourseDAO courseDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
    }

    public List<Course> getAllCourses()
            throws SQLException {

        return courseDAO.findAll();
    }

    public List<Course> getActiveCourses()
            throws SQLException {

        return courseDAO.findActiveCourses();
    }

    public List<Course> getFeaturedCourses()
            throws SQLException {

        return courseDAO.findFeaturedCourses(4);
    }

    public Optional<Course> getCourseById(
            int courseId
    ) throws SQLException {

        if (courseId <= 0) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                courseDAO.findById(courseId)
        );
    }

    public boolean createCourse(Course course)
            throws SQLException {

        validateCourse(course);

        course.setCourseId(0);

        if (
                course.getStatus() == null
                        || course.getStatus().isBlank()
        ) {
            course.setStatus("ACTIVE");
        }

        return courseDAO.insert(course);
    }

    public boolean updateCourse(Course course)
            throws SQLException {

        if (
                course == null
                        || course.getCourseId() <= 0
        ) {
            throw new IllegalArgumentException(
                    "Khóa học không hợp lệ."
            );
        }

        validateCourse(course);

        return courseDAO.update(course);
    }

    public boolean deleteCourse(int courseId)
            throws SQLException {

        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "Mã khóa học không hợp lệ."
            );
        }

        return courseDAO.deleteById(courseId);
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException(
                    "Thông tin khóa học không hợp lệ."
            );
        }

        if (
                course.getCourseCode() == null
                        || course.getCourseCode().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Mã khóa học không được để trống."
            );
        }

        if (
                course.getCourseName() == null
                        || course.getCourseName().isBlank()
        ) {
            throw new IllegalArgumentException(
                    "Tên khóa học không được để trống."
            );
        }

        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException(
                    "Số tín chỉ phải lớn hơn 0."
            );
        }

        if (
                course.getTuitionFee() == null
                        || course.getTuitionFee().signum() < 0
        ) {
            throw new IllegalArgumentException(
                    "Học phí không hợp lệ."
            );
        }
    }
}
>>>>>>> 1728579b857bfb79834ec45188e9749d64f3821e

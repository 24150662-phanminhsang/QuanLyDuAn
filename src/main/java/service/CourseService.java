package service;

import dao.CourseDAO;
import model.Course;

import java.util.Collections;
import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;

    public CourseService() {
        this(new CourseDAO());
    }

    public CourseService(CourseDAO courseDAO) {
        this.courseDAO = courseDAO;
    }

    public boolean addCourse(Course course) {
        validateCourse(course);
        normalizeCourse(course);

        return courseDAO.addCourse(course);
    }

    public List<Course> getAllCourses() {
        return safe(courseDAO.getAllCourses());
    }

    public Course getCourseById(int id) {
        if (id <= 0) {
            return null;
        }

        return courseDAO.getCourseByID(id);
    }

    /*
     * Giữ lại để tương thích với code cũ.
     */
    public Course getCourseByID(int id) {
        return getCourseById(id);
    }

    public boolean updateCourse(Course course) {
        validateCourse(course);

        if (course.getCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        normalizeCourse(course);

        return courseDAO.updateCourse(course);
    }

    public boolean deleteCourse(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        return courseDAO.deleteCourse(id);
    }

    public List<Course> getActiveCourses() {
        return safe(
                courseDAO.getActiveCourses()
        );
    }

    public List<Course> getFeaturedCourses() {
        return getFeaturedCourses(6);
    }

    public List<Course> getFeaturedCourses(int limit) {
        if (limit <= 0) {
            return Collections.emptyList();
        }

        return safe(
                courseDAO.getFeaturedCourses(limit)
        );
    }

    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCourses();
        }

        return safe(
                courseDAO.searchCourses(
                        keyword.trim()
                )
        );
    }

    private void validateCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException(
                    "Thông tin khóa học không được để trống."
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

        if (course.getCredits() < 0) {
            throw new IllegalArgumentException(
                    "Số tín chỉ không được âm."
            );
        }

        if (
                course.getTuitionFee() != null
                        && course.getTuitionFee()
                        .signum() < 0
        ) {
            throw new IllegalArgumentException(
                    "Học phí không được âm."
            );
        }
    }

    private void normalizeCourse(Course course) {
        course.setCourseCode(
                course.getCourseCode().trim()
        );

        course.setCourseName(
                course.getCourseName().trim()
        );

        /*
         * Nếu model Course có setter cho mô tả và trạng thái,
         * có thể chuẩn hóa thêm tại đây.
         */
    }

    private List<Course> safe(
            List<Course> courses
    ) {
        return courses == null
                ? Collections.emptyList()
                : courses;
    }
}
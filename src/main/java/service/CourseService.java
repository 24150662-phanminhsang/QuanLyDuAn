package service;
import dao.CourseDAO;
import model.Course;

import java.util.List;

/**
 * CourseService
 */
public class CourseService {
    private CourseDAO courseDAO;

    public CourseService() {
        courseDAO = new CourseDAO();
    }

    // Thêm khóa học
    public boolean addCourse(Course course) {
        return courseDAO.addCourse(course);
    }

    // Lấy danh sách khóa học
    public List<Course> getAllCourses() {
        return courseDAO.getAllCourses();
    }

    // Tìm khóa học theo ID
    public Course getCourseByID(int id) {
        return courseDAO.getCourseByID(id);
    }

    // Cập nhật khóa học
    public boolean updateCourse(Course course) {
        return courseDAO.updateCourse(course);
    }

    // Xóa khóa học
    public boolean deleteCourse(int id) {
        return courseDAO.deleteCourse(id);
    }

}




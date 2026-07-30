package controller;
import model.Course;
import service.CourseService;

import java.util.List;

/**
 * Điều phối xử lý cho module CourseController.
 *
 * Luồng:
 * View -> Controller -> Service -> DAO -> SQL Server
 */
public class CourseController {
    private CourseService courseService;

    public CourseController() {
        courseService = new CourseService();
    }

    // Thêm khóa học
    public boolean addCourse(Course course) {
        return courseService.addCourse(course);
    }

    // Lấy danh sách khóa học
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Tìm khóa học theo ID
    public Course getCourseByID(int id) {
        return courseService.getCourseByID(id);
    }

    // Cập nhật khóa học
    public boolean updateCourse(Course course) {
        return courseService.updateCourse(course);
    }

    // Xóa khóa học
    public boolean deleteCourse(int id) {
        return courseService.deleteCourse(id);
    }

}

package controller;

import model.Course;
import service.CourseService;

import java.util.List;

public class CourseController {

    private final CourseService courseService;

    public CourseController() {
        this.courseService = new CourseService();
    }

    /**
     * Thêm khóa học
     */
    public boolean addCourse(Course course) {
        return courseService.addCourse(course);
    }

    /**
     * Lấy toàn bộ khóa học
     */
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    /**
     * Lấy khóa học theo ID
     */
    public Course getCourseById(int id) {
        return courseService.getCourseById(id);
    }

    /*
     * Giữ lại để tương thích với các file cũ.
     */
    public Course getCourseByID(int id) {
        return getCourseById(id);
    }

    /**
     * Cập nhật khóa học
     */
    public boolean updateCourse(Course course) {
        return courseService.updateCourse(course);
    }

    /**
     * Xóa khóa học
     */
    public boolean deleteCourse(int id) {
        return courseService.deleteCourse(id);
    }

    /**
     * Danh sách khóa học đang mở
     */
    public List<Course> getActiveCourses() {
        return courseService.getActiveCourses();
    }

    /**
     * Danh sách khóa học nổi bật
     */
    public List<Course> getFeaturedCourses() {
        return courseService.getFeaturedCourses();
    }

    /**
     * Tìm kiếm khóa học
     */
    public List<Course> searchCourses(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllCourses();
        }

        return courseService.searchCourses(keyword.trim());
    }
}
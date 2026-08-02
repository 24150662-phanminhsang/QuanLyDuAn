package controller;

import model.Course;
import service.CourseService;

import java.util.List;

public class CourseController {

    private final CourseService courseService;

    public CourseController() {
        this.courseService = new CourseService();
    }

    /* =====================================================
       THÊM KHÓA HỌC
       ===================================================== */

    public boolean addCourse(Course course) {
        return courseService.addCourse(course);
    }

    /* =====================================================
       DANH SÁCH KHÓA HỌC
       ===================================================== */

    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    public List<Course> getActiveCourses() {
        return courseService.getActiveCourses();
    }

    public List<Course> getInactiveCourses() {
        return courseService.getInactiveCourses();
    }

    public List<Course> getArchivedCourses() {
        return courseService.getArchivedCourses();
    }

    public List<Course> getCoursesByStatus(
            String status
    ) {
        return courseService.getCoursesByStatus(
                status
        );
    }

    public List<Course> getFeaturedCourses() {
        return courseService.getFeaturedCourses();
    }

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

    public Course getCourseById(
            int courseId
    ) {
        return courseService.getCourseById(
                courseId
        );
    }

    /*
     * Giữ tương thích code cũ.
     */
    public Course getCourseByID(
            int courseId
    ) {
        return getCourseById(
                courseId
        );
    }

    public Course getCourseByCode(
            String code
    ) {
        return courseService.getCourseByCode(
                code
        );
    }

    public List<Course> searchCourses(
            String keyword
    ) {
        return courseService.searchCourses(
                keyword
        );
    }

    /* =====================================================
       CẬP NHẬT
       ===================================================== */

    public boolean updateCourse(
            Course course
    ) {
        return courseService.updateCourse(
                course
        );
    }

    /* =====================================================
       TRẠNG THÁI
       ===================================================== */

    public boolean activateCourse(
            int courseId
    ) {
        return courseService.activateCourse(
                courseId
        );
    }

    public boolean deactivateCourse(
            int courseId
    ) {
        return courseService.deactivateCourse(
                courseId
        );
    }

    public boolean archiveCourse(
            int courseId
    ) {
        return courseService.archiveCourse(
                courseId
        );
    }

    public boolean updateCourseStatus(
            int courseId,
            String status
    ) {
        return courseService.updateCourseStatus(
                courseId,
                status
        );
    }

    /* =====================================================
       XÓA
       ===================================================== */

    public boolean canDeleteCourse(
            int courseId
    ) {
        return courseService.canDeleteCourse(
                courseId
        );
    }

    public boolean deleteCourse(
            int courseId
    ) {
        return courseService.deleteCourse(
                courseId
        );
    }

    public int countClasses(
            int courseId
    ) {
        return courseService.countClasses(
                courseId
        );
    }
}
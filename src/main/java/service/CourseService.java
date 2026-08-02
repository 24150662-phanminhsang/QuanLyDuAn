package service;

import dao.CourseDAO;
import model.Course;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CourseService {

    private static final String STATUS_ACTIVE =
            "ACTIVE";

    private static final String STATUS_INACTIVE =
            "INACTIVE";

    private static final String STATUS_ARCHIVED =
            "ARCHIVED";

    private final CourseDAO courseDAO;

    public CourseService() {
        this(
                new CourseDAO()
        );
    }

    public CourseService(
            CourseDAO courseDAO
    ) {
        if (courseDAO == null) {
            throw new IllegalArgumentException(
                    "CourseDAO không được null."
            );
        }

        this.courseDAO =
                courseDAO;
    }

    /* =====================================================
       THÊM KHÓA HỌC
       ===================================================== */

    public boolean addCourse(
            Course course
    ) {
        validateCourseForCreate(
                course
        );

        normalizeCourse(
                course
        );

        if (
                courseDAO.existsByCourseCode(
                        course.getCourseCode()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã khóa học đã tồn tại."
            );
        }

        return courseDAO.addCourse(
                course
        );
    }

    /* =====================================================
       DANH SÁCH KHÓA HỌC
       ===================================================== */

    public List<Course> getAllCourses() {
        return safe(
                courseDAO.getAllCourses()
        );
    }

    public List<Course> getActiveCourses() {
        return safe(
                courseDAO.getActiveCourses()
        );
    }

    public List<Course> getInactiveCourses() {
        return safe(
                courseDAO.getInactiveCourses()
        );
    }

    public List<Course> getArchivedCourses() {
        return safe(
                courseDAO.getArchivedCourses()
        );
    }

    public List<Course> getCoursesByStatus(
            String status
    ) {
        String normalizedStatus =
                normalizeStatus(
                        status
                );

        return safe(
                courseDAO.getCoursesByStatus(
                        normalizedStatus
                )
        );
    }

    public List<Course> getFeaturedCourses() {
        return getFeaturedCourses(6);
    }

    public List<Course> getFeaturedCourses(
            int limit
    ) {
        if (limit <= 0) {
            return Collections.emptyList();
        }

        return safe(
                courseDAO.getFeaturedCourses(
                        limit
                )
        );
    }

    /* =====================================================
       TÌM KHÓA HỌC
       ===================================================== */

    public Course getCourseById(
            int courseId
    ) {
        if (courseId <= 0) {
            return null;
        }

        return courseDAO.getCourseById(
                courseId
        );
    }

    public Course getCourseByID(
            int courseId
    ) {
        return getCourseById(
                courseId
        );
    }

    public Course getCourseByCode(
            String courseCode
    ) {
        if (
                courseCode == null
                        || courseCode.isBlank()
        ) {
            return null;
        }

        return courseDAO.getCourseByCode(
                courseCode.trim()
        );
    }

    public List<Course> searchCourses(
            String keyword
    ) {
        if (
                keyword == null
                        || keyword.isBlank()
        ) {
            return getAllCourses();
        }

        return safe(
                courseDAO.searchCourses(
                        keyword.trim()
                )
        );
    }

    /* =====================================================
       CẬP NHẬT KHÓA HỌC
       ===================================================== */

    public boolean updateCourse(
            Course course
    ) {
        validateCourseForUpdate(
                course
        );

        normalizeCourse(
                course
        );

        if (
                courseDAO.existsByCourseCodeExceptId(
                        course.getCourseCode(),
                        course.getCourseId()
                )
        ) {
            throw new IllegalArgumentException(
                    "Mã khóa học đã được sử dụng "
                            + "bởi khóa học khác."
            );
        }

        return courseDAO.updateCourse(
                course
        );
    }

    /* =====================================================
       TRẠNG THÁI KHÓA HỌC
       ===================================================== */

    public boolean activateCourse(
            int courseId
    ) {
        requireExistingCourse(
                courseId
        );

        return courseDAO.activateCourse(
                courseId
        );
    }

    public boolean deactivateCourse(
            int courseId
    ) {
        Course course =
                requireExistingCourse(
                        courseId
                );

        if (
                STATUS_ARCHIVED.equals(
                        normalizeStatus(
                                course.getStatus()
                        )
                )
        ) {
            throw new IllegalStateException(
                    "Khóa học đã được lưu trữ, "
                            + "không thể chuyển sang tạm ngưng."
            );
        }

        return courseDAO.deactivateCourse(
                courseId
        );
    }

    public boolean archiveCourse(
            int courseId
    ) {
        Course course =
                requireExistingCourse(
                        courseId
                );

        if (
                STATUS_ARCHIVED.equals(
                        normalizeStatus(
                                course.getStatus()
                        )
                )
        ) {
            throw new IllegalStateException(
                    "Khóa học đã ở trạng thái lưu trữ."
            );
        }

        return courseDAO.archiveCourse(
                courseId
        );
    }

    public boolean updateCourseStatus(
            int courseId,
            String status
    ) {
        requireExistingCourse(
                courseId
        );

        String normalizedStatus =
                normalizeStatus(
                        status
                );

        return switch (normalizedStatus) {
            case STATUS_ACTIVE ->
                    courseDAO.activateCourse(
                            courseId
                    );

            case STATUS_INACTIVE ->
                    courseDAO.deactivateCourse(
                            courseId
                    );

            case STATUS_ARCHIVED ->
                    courseDAO.archiveCourse(
                            courseId
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Trạng thái khóa học không hợp lệ."
                    );
        };
    }

    /* =====================================================
       XÓA KHÓA HỌC
       ===================================================== */

    public boolean canDeleteCourse(
            int courseId
    ) {
        if (courseId <= 0) {
            return false;
        }

        Course course =
                courseDAO.getCourseById(
                        courseId
                );

        if (course == null) {
            return false;
        }

        return !courseDAO.hasClasses(
                courseId
        );
    }

    public boolean deleteCourse(
            int courseId
    ) {
        Course course =
                requireExistingCourse(
                        courseId
                );

        if (
                courseDAO.hasClasses(
                        courseId
                )
        ) {
            throw new IllegalStateException(
                    "Không thể xóa khóa học \""
                            + safeText(
                            course.getCourseName()
                    )
                            + "\" vì khóa học đã có lớp học. "
                            + "Hãy chuyển khóa học sang trạng thái ARCHIVED."
            );
        }

        return courseDAO.deleteCourse(
                courseId
        );
    }

    public int countClasses(
            int courseId
    ) {
        if (courseId <= 0) {
            return 0;
        }

        return courseDAO.countClasses(
                courseId
        );
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateCourseForCreate(
            Course course
    ) {
        validateCourseCommon(
                course
        );
    }

    private void validateCourseForUpdate(
            Course course
    ) {
        validateCourseCommon(
                course
        );

        if (course.getCourseId() <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        if (
                courseDAO.getCourseById(
                        course.getCourseId()
                ) == null
        ) {
            throw new IllegalArgumentException(
                    "Không tìm thấy khóa học cần cập nhật."
            );
        }
    }

    private void validateCourseCommon(
            Course course
    ) {
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

        if (course.getCredits() <= 0) {
            throw new IllegalArgumentException(
                    "Số tín chỉ phải lớn hơn 0."
            );
        }

        BigDecimal tuitionFee =
                course.getTuitionFee();

        if (
                tuitionFee != null
                        && tuitionFee.signum() < 0
        ) {
            throw new IllegalArgumentException(
                    "Học phí không được âm."
            );
        }

        normalizeStatus(
                course.getStatus()
        );
    }

    private Course requireExistingCourse(
            int courseId
    ) {
        if (courseId <= 0) {
            throw new IllegalArgumentException(
                    "ID khóa học không hợp lệ."
            );
        }

        Course course =
                courseDAO.getCourseById(
                        courseId
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy khóa học."
            );
        }

        return course;
    }

    /* =====================================================
       CHUẨN HÓA DỮ LIỆU
       ===================================================== */

    private void normalizeCourse(
            Course course
    ) {
        course.setCourseCode(
                course.getCourseCode()
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
        );

        course.setCourseName(
                course.getCourseName()
                        .trim()
        );

        if (
                course.getDescription() != null
                        && !course.getDescription()
                        .isBlank()
        ) {
            course.setDescription(
                    course.getDescription()
                            .trim()
            );
        } else {
            course.setDescription(
                    null
            );
        }

        if (course.getTuitionFee() == null) {
            course.setTuitionFee(
                    BigDecimal.ZERO
            );
        }

        course.setStatus(
                normalizeStatus(
                        course.getStatus()
                )
        );
    }

    private String normalizeStatus(
            String status
    ) {
        if (
                status == null
                        || status.isBlank()
        ) {
            return STATUS_ACTIVE;
        }

        String normalized =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "ACTIVE",
                 "OPEN" ->
                    STATUS_ACTIVE;

            case "INACTIVE",
                 "CLOSED" ->
                    STATUS_INACTIVE;

            case "ARCHIVED" ->
                    STATUS_ARCHIVED;

            default ->
                    throw new IllegalArgumentException(
                            "Trạng thái khóa học không hợp lệ: "
                                    + status
                    );
        };
    }

    private String safeText(
            String value
    ) {
        return value == null
                || value.isBlank()
                ? "không xác định"
                : value.trim();
    }

    private List<Course> safe(
            List<Course> courses
    ) {
        return courses == null
                ? Collections.emptyList()
                : courses;
    }
}
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Locale;

public class Course {

    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int credits;
    private BigDecimal tuitionFee;
    private String status;
    private Timestamp createdAt;

    /**
     * Constructor mặc định.
     */
    public Course() {
        this.courseId = 0;
        this.credits = 0;
        this.tuitionFee = BigDecimal.ZERO;
        this.status = "ACTIVE";
    }

    /**
     * Constructor đầy đủ.
     */
    public Course(
            int courseId,
            String courseCode,
            String courseName,
            String description,
            int credits,
            BigDecimal tuitionFee,
            String status,
            Timestamp createdAt
    ) {
        this.courseId = courseId;
        this.courseCode = normalizeText(courseCode);
        this.courseName = normalizeText(courseName);
        this.description = normalizeNullableText(description);
        this.credits = credits;
        this.tuitionFee = normalizeTuitionFee(tuitionFee);
        this.status = normalizeStatus(status);
        this.createdAt = createdAt;
    }

    /**
     * Constructor dùng khi thêm khóa học mới.
     */
    public Course(
            String courseCode,
            String courseName,
            String description,
            int credits,
            BigDecimal tuitionFee,
            String status
    ) {
        this(
                0,
                courseCode,
                courseName,
                description,
                credits,
                tuitionFee,
                status,
                null
        );
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = normalizeText(courseCode);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = normalizeText(courseName);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = normalizeNullableText(description);
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = normalizeTuitionFee(tuitionFee);
    }

    public void setTuitionFee(double tuitionFee) {
        this.tuitionFee = BigDecimal.valueOf(tuitionFee);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = normalizeStatus(status);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /*
     * Các hàm giữ lại để tương thích với code cũ.
     */

    public int getCourseID() {
        return getCourseId();
    }

    public void setCourseID(int courseID) {
        setCourseId(courseID);
    }

    public double getFee() {
        return tuitionFee == null
                ? 0.0
                : tuitionFee.doubleValue();
    }

    public void setFee(double fee) {
        setTuitionFee(fee);
    }

    public int getDuration() {
        return getCredits();
    }

    public void setDuration(int duration) {
        setCredits(duration);
    }

    /**
     * Chuẩn hóa chuỗi bắt buộc.
     */
    private String normalizeText(String value) {
        return value == null
                ? ""
                : value.trim();
    }

    /**
     * Chuẩn hóa chuỗi có thể để trống.
     */
    private String normalizeNullableText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    /**
     * Học phí null được chuyển thành 0.
     */
    private BigDecimal normalizeTuitionFee(
            BigDecimal tuitionFee
    ) {
        return tuitionFee == null
                ? BigDecimal.ZERO
                : tuitionFee;
    }

    /**
     * Trạng thái mặc định là ACTIVE.
     */
    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }

        return status
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        String name = courseName == null
                ? ""
                : courseName;

        if (courseCode == null || courseCode.isBlank()) {
            return name;
        }

        return courseCode + " - " + name;
    }
}
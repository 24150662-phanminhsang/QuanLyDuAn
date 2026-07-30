package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Course {
    private int courseID;
    private String courseName;
    private double fee;
    private int duration;

    // Constructor rỗng
    public Course() {
    }

    // Constructor đầy đủ
    public Course(int courseID, String courseName, double fee, int duration) {
        this.courseID = courseID;
        this.courseName = courseName;
        this.fee = fee;
        this.duration = duration;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return courseID + " - " + courseName;
    }

    private int courseId;
    private String courseCode;
    private String courseName;
    private String description;
    private int credits;
    private BigDecimal tuitionFee;
    private String status;
    private LocalDateTime createdAt;

    public Course() {
    }

    public Course(
            int courseId,
            String courseCode,
            String courseName,
            String description,
            int credits,
            BigDecimal tuitionFee,
            String status,
            LocalDateTime createdAt
    ) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.tuitionFee = tuitionFee;
        this.status = status;
        this.createdAt = createdAt;
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
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        this.tuitionFee = tuitionFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return courseName;
    }
}
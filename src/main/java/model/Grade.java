package model;

import java.sql.Timestamp;

public class Grade {

    private int gradeId;

    private int enrollmentId;

    // dùng cho View
    private int studentId;
    private int classId;

    private Double attendanceScore;
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;

    private String result;

    private Timestamp updatedAt;

    public Grade() {
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    //=========================
    // Student
    //=========================

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    //=========================
    // Class
    //=========================

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    //=========================
    // Attendance
    //=========================

    public Double getAttendanceScore() {
        return attendanceScore;
    }

    public void setAttendanceScore(Double attendanceScore) {
        this.attendanceScore = attendanceScore;
    }

    //=========================
    // Midterm
    //=========================

    public Double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(Double midtermScore) {
        this.midtermScore = midtermScore;
    }

    //=========================
    // Final
    //=========================

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    //=========================
    // Average
    //=========================

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    //=========================
    // Result
    //=========================

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    //=========================
    // Updated At
    //=========================

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
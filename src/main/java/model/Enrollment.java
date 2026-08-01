package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Enrollment {

    private int enrollmentId;
    private int studentId;
    private int classId;
    private Date enrollmentDate;
    private int progressPercent;
    private String status;
    private Timestamp createdAt;

    public Enrollment() {
    }

    public Enrollment(
            int enrollmentId,
            int studentId,
            int classId,
            Date enrollmentDate,
            int progressPercent,
            String status,
            Timestamp createdAt
    ) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.classId = classId;
        this.enrollmentDate = enrollmentDate;
        this.progressPercent = progressPercent;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public Date getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(Date enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = Math.max(
                0,
                Math.min(progressPercent, 100)
        );
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status =
                status == null || status.isBlank()
                        ? "ENROLLED"
                        : status.trim().toUpperCase();
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
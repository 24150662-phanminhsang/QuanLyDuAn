package model;

import java.sql.Timestamp;

public class Grade {

    private int gradeId;
    private int enrollmentId;

    private Double attendanceScore;
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;

    private String result;
    private Timestamp updatedAt;

    public Grade() {
    }

    public Grade(
            int gradeId,
            int enrollmentId,
            Double attendanceScore,
            Double midtermScore,
            Double finalScore,
            Double averageScore,
            String result,
            Timestamp updatedAt
    ) {
        this.gradeId = gradeId;
        this.enrollmentId = enrollmentId;
        this.attendanceScore = attendanceScore;
        this.midtermScore = midtermScore;
        this.finalScore = finalScore;
        this.averageScore = averageScore;
        this.result = result;
        this.updatedAt = updatedAt;
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

    public Double getAttendanceScore() {
        return attendanceScore;
    }

    public void setAttendanceScore(Double attendanceScore) {
        this.attendanceScore = validateScore(attendanceScore);
    }

    public Double getMidtermScore() {
        return midtermScore;
    }

    public void setMidtermScore(Double midtermScore) {
        this.midtermScore = validateScore(midtermScore);
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = validateScore(finalScore);
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = validateScore(averageScore);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        if (result == null || result.isBlank()) {
            this.result = null;
        } else {
            this.result = result.trim().toUpperCase();
        }
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    private Double validateScore(Double score) {
        if (score == null) {
            return null;
        }

        if (score < 0 || score > 10) {
            throw new IllegalArgumentException(
                    "Điểm phải nằm trong khoảng từ 0 đến 10."
            );
        }

        return score;
    }
}
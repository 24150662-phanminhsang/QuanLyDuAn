package model;

public class Grade {
    private int gradeId;
    private int studentId;
    private int classId;
    private double score;

    public Grade() {}

    public int getGradeId() { return gradeId; }
    public void setGradeId(int gradeId) { this.gradeId = gradeId; }
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
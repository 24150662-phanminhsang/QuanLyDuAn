package model;

/**
 * Enrollment
 */
public class Enrollment {
    private int id;
    private int studentId;
    private int courseId;
    private String enrollDate;

    public Enrollment() {
    }

    public Enrollment(int id, int studentId, int courseId, String enrollDate) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollDate = enrollDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(String enrollDate) {
        this.enrollDate = enrollDate;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", enrollDate='" + enrollDate + '\'' +
                '}';
    }

}

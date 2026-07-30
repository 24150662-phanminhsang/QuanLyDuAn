package model;

/**
 * Course
 */
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

}

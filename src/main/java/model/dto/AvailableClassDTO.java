package model.dto;

import java.math.BigDecimal;
import java.sql.Date;

public class AvailableClassDTO {

    private int classId;
    private int courseId;
    private Integer teacherId;

    private String classCode;
    private String courseCode;
    private String courseName;
    private String teacherName;

    private String semester;
    private String schoolYear;
    private String room;
    private String scheduleText;

    private int maximumStudents;
    private int enrolledStudents;
    private int remainingSlots;

    private int credits;
    private BigDecimal tuitionFee;

    private Date startDate;
    private Date endDate;
    private String classStatus;

    public AvailableClassDTO() {
        this.tuitionFee = BigDecimal.ZERO;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = normalize(classCode);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = normalize(courseCode);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = normalize(courseName);
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = normalize(teacherName);
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = normalize(semester);
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = normalize(schoolYear);
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = normalize(room);
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = normalize(scheduleText);
    }

    public int getMaximumStudents() {
        return maximumStudents;
    }

    public void setMaximumStudents(int maximumStudents) {
        this.maximumStudents = Math.max(0, maximumStudents);
    }

    public int getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(int enrolledStudents) {
        this.enrolledStudents = Math.max(0, enrolledStudents);
    }

    public int getRemainingSlots() {
        return remainingSlots;
    }

    public void setRemainingSlots(int remainingSlots) {
        this.remainingSlots = Math.max(0, remainingSlots);
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = Math.max(0, credits);
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee == null
                ? BigDecimal.ZERO
                : tuitionFee.max(BigDecimal.ZERO);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getClassStatus() {
        return classStatus;
    }

    public void setClassStatus(String classStatus) {
        this.classStatus = normalize(classStatus);
    }

    public boolean hasAvailableSlot() {
        return remainingSlots > 0;
    }

    public String getDisplayCourseName() {
        return courseName == null ? "--" : courseName;
    }

    public String getDisplayTeacherName() {
        return teacherName == null ? "Chưa phân công" : teacherName;
    }

    public String getDisplaySchedule() {
        return scheduleText == null ? "Chưa có lịch" : scheduleText;
    }

    private String normalize(String value) {
        return value == null || value.isBlank()
                ? null
                : value.trim();
    }
}

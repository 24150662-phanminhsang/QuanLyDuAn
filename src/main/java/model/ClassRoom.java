package model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Locale;

public class ClassRoom {

    private int classId;
    private String classCode;

    private int courseId;
    private int teacherId;

    private String semester;
    private String schoolYear;

    private String room;
    private String scheduleText;

    private int maximumStudents;

    private Date startDate;
    private Date endDate;

    private String status;
    private Timestamp createdAt;

    public ClassRoom() {
        this.maximumStudents = 30;
        this.status = "OPEN";
    }

    public ClassRoom(
            int classId,
            String classCode,
            int courseId,
            int teacherId,
            String semester,
            String schoolYear,
            String room,
            String scheduleText,
            int maximumStudents,
            Date startDate,
            Date endDate,
            String status,
            Timestamp createdAt
    ) {
        this.classId = classId;
        setClassCode(classCode);
        this.courseId = courseId;
        this.teacherId = teacherId;
        setSemester(semester);
        setSchoolYear(schoolYear);
        setRoom(room);
        setScheduleText(scheduleText);
        setMaximumStudents(maximumStudents);
        this.startDate = startDate;
        this.endDate = endDate;
        setStatus(status);
        this.createdAt = createdAt;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        if (classCode == null || classCode.isBlank()) {
            this.classCode = null;
        } else {
            this.classCode =
                    classCode.trim().toUpperCase(Locale.ROOT);
        }
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = normalizeNullable(semester);
    }

    public String getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear) {
        this.schoolYear = normalizeNullable(schoolYear);
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = normalizeNullable(room);
    }

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText =
                normalizeNullable(scheduleText);
    }

    public int getMaximumStudents() {
        return maximumStudents;
    }

    public void setMaximumStudents(
            int maximumStudents
    ) {
        if (maximumStudents < 0) {
            throw new IllegalArgumentException(
                    "Sĩ số tối đa không được âm."
            );
        }

        this.maximumStudents = maximumStudents;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            this.status = "OPEN";
        } else {
            this.status =
                    status.trim().toUpperCase(Locale.ROOT);
        }
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            Timestamp createdAt
    ) {
        this.createdAt = createdAt;
    }

    /*
     * =====================================================
     * CÁC HÀM GIỮ TƯƠNG THÍCH VỚI VIEW VÀ SERVICE CŨ
     * =====================================================
     */

    /**
     * Code cũ dùng className.
     * Database hiện tại dùng class_code.
     */
    public String getClassName() {
        return getClassCode();
    }

    public void setClassName(String className) {
        setClassCode(className);
    }

    /**
     * Code cũ dùng schedule.
     * Database hiện tại dùng schedule_text.
     */
    public String getSchedule() {
        return getScheduleText();
    }

    public void setSchedule(String schedule) {
        setScheduleText(schedule);
    }

    /**
     * Code cũ dùng maxStudents.
     * Database hiện tại dùng maximum_students.
     */
    public int getMaxStudents() {
        return getMaximumStudents();
    }

    public void setMaxStudents(int maxStudents) {
        setMaximumStudents(maxStudents);
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    @Override
    public String toString() {
        String displayCode =
                classCode == null || classCode.isBlank()
                        ? "Lớp #" + classId
                        : classCode;

        return classId + " - " + displayCode;
    }
}
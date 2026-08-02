package model.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Locale;

public class StudentCourseDTO {

    private int enrollmentId;
    private int studentId;

    private int classId;
    private String classCode;

    private int courseId;
    private String courseCode;
    private String courseName;

    private int teacherId;
    private String teacherName;

    private String semester;
    private String schoolYear;

    private String scheduleText;
    private String room;

    private int progressPercent;
    private String enrollmentStatus;

    private BigDecimal tuitionFee;

    private Date startDate;
    private Date endDate;

    public StudentCourseDTO() {
        this.progressPercent = 0;
        this.tuitionFee = BigDecimal.ZERO;
        this.enrollmentStatus = "ENROLLED";
    }

    public StudentCourseDTO(
            int enrollmentId,
            int studentId,
            int classId,
            String classCode,
            int courseId,
            String courseCode,
            String courseName,
            int teacherId,
            String teacherName,
            String semester,
            String schoolYear,
            String scheduleText,
            String room,
            int progressPercent,
            String enrollmentStatus,
            BigDecimal tuitionFee,
            Date startDate,
            Date endDate
    ) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.classId = classId;
        setClassCode(classCode);
        this.courseId = courseId;
        setCourseCode(courseCode);
        setCourseName(courseName);
        this.teacherId = teacherId;
        setTeacherName(teacherName);
        setSemester(semester);
        setSchoolYear(schoolYear);
        setScheduleText(scheduleText);
        setRoom(room);
        setProgressPercent(progressPercent);
        setEnrollmentStatus(enrollmentStatus);
        setTuitionFee(tuitionFee);
        this.startDate = startDate;
        this.endDate = endDate;
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

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = normalizeUpper(classCode);
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
        this.courseCode = normalizeUpper(courseCode);
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = normalizeNullable(courseName);
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = normalizeNullable(teacherName);
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

    public String getScheduleText() {
        return scheduleText;
    }

    public void setScheduleText(String scheduleText) {
        this.scheduleText = normalizeNullable(scheduleText);
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = normalizeNullable(room);
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent =
                Math.max(
                        0,
                        Math.min(progressPercent, 100)
                );
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        if (enrollmentStatus == null
                || enrollmentStatus.isBlank()) {

            this.enrollmentStatus = "ENROLLED";
        } else {
            this.enrollmentStatus =
                    enrollmentStatus
                            .trim()
                            .toUpperCase(Locale.ROOT);
        }
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee =
                tuitionFee == null
                        ? BigDecimal.ZERO
                        : tuitionFee;
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

    public String getDisplayCourseName() {
        String code =
                courseCode == null
                        ? ""
                        : courseCode;

        String name =
                courseName == null
                        ? ""
                        : courseName;

        if (code.isBlank()) {
            return name;
        }

        if (name.isBlank()) {
            return code;
        }

        return code + " - " + name;
    }

    public String getDisplayClassName() {
        if (classCode == null || classCode.isBlank()) {
            return "Lớp #" + classId;
        }

        return classCode;
    }

    public String getDisplaySemester() {
        String semesterValue =
                semester == null
                        ? ""
                        : semester;

        String schoolYearValue =
                schoolYear == null
                        ? ""
                        : schoolYear;

        if (semesterValue.isBlank()) {
            return schoolYearValue;
        }

        if (schoolYearValue.isBlank()) {
            return semesterValue;
        }

        return semesterValue + " - " + schoolYearValue;
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String normalizeUpper(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return getDisplayCourseName()
                + " | "
                + getDisplayClassName();
    }
}
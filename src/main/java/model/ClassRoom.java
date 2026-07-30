package model;

public class ClassRoom {
    private int classId;
    private int courseId;
    private int teacherId;
    private String className;
    private String schedule;
    private String room;
    private int maxStudents;

    public ClassRoom() {
    }

    public ClassRoom(int classId, int courseId, int teacherId, String className, String schedule, String room, int maxStudents) {
        this.classId = classId;
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.className = className;
        this.schedule = schedule;
        this.room = room;
        this.maxStudents = maxStudents;
    }

    // --- GETTER & SETTER ---
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

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }
}
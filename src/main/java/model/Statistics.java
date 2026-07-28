package model;

public class Statistics {

    private int totalUsers;
    private int totalStudents;
    private int totalTeachers;
    private int totalCourses;
    private int totalClasses;
    private int activeUsers;

    public Statistics() {
    }

    public Statistics(
            int totalUsers,
            int totalStudents,
            int totalTeachers,
            int totalCourses,
            int totalClasses,
            int activeUsers
    ) {
        this.totalUsers = totalUsers;
        this.totalStudents = totalStudents;
        this.totalTeachers = totalTeachers;
        this.totalCourses = totalCourses;
        this.totalClasses = totalClasses;
        this.activeUsers = activeUsers;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public int getTotalTeachers() {
        return totalTeachers;
    }

    public void setTotalTeachers(int totalTeachers) {
        this.totalTeachers = totalTeachers;
    }

    public int getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(int totalCourses) {
        this.totalCourses = totalCourses;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }
}
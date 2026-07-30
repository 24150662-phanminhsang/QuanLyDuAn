package model;

public class Teacher {
    // Các thuộc tính dùng cho DAO / Database
    private int teacherId;
    private int userId;
    private String teacherCode;
    private String specialization;

    // Các thuộc tính dùng cho Giao diện / View
    private String name;
    private String email;
    private String phone;

    public Teacher() {
    }

    public Teacher(int teacherId, int userId, String teacherCode, String specialization, String name, String email, String phone) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.teacherCode = teacherCode;
        this.specialization = specialization;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // --- GETTER & SETTER CHO DAO (DATABASE) ---
    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    // --- GETTER & SETTER CHO VIEW (GIAO DIỆN) ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
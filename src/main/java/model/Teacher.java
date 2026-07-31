package model;

public class Teacher {
    private int teacherId;
    private String name;          // Khớp với getName() / setName() trong View
    private String email;
    private String phone;
    private String specialization;
    private String status;
    private int userId;           // Thêm nếu dùng trong handleSaveTeacher()
    private String teacherCode;   // Thêm nếu dùng trong handleSaveTeacher()

    public Teacher() {}

    // Các Getter và Setter đầy đủ:
    public int getTeacherId() { return teacherId; }
    public void setTeacherId(int teacherId) { this.teacherId = teacherId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTeacherCode() { return teacherCode; }
    public void setTeacherCode(String teacherCode) { this.teacherCode = teacherCode; }
}
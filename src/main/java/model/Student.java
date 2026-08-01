package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Student {

    private int studentId;
    private String studentCode;

    // Liên kết sang bảng Users
    private Integer userId;

    private String fullName;
    private Date dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String status;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Student() {
        this.status = "ACTIVE";
    }

    public Student(
            int studentId,
            String studentCode,
            Integer userId,
            String fullName,
            Date dateOfBirth,
            String gender,
            String email,
            String phone,
            String address,
            String status,
            Timestamp createdAt,
            Timestamp updatedAt
    ) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        setStatus(status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // ===========================
    // Student ID
    // ===========================

    public int getStudentId() {
        return studentId;
    }

    public int getStudentID() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public void setStudentID(int studentId) {
        this.studentId = studentId;
    }

    // ===========================
    // Student Code
    // ===========================

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        if (studentCode == null || studentCode.isBlank()) {
            this.studentCode = null;
        } else {
            this.studentCode = studentCode.trim().toUpperCase();
        }
    }

    // ===========================
    // User ID
    // ===========================

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // ===========================
    // Full Name
    // ===========================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName =
                fullName == null
                        ? null
                        : fullName.trim();
    }

    // ===========================
    // Date Of Birth
    // ===========================

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // ===========================
    // Gender
    // ===========================

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender =
                gender == null || gender.isBlank()
                        ? null
                        : gender.trim().toUpperCase();
    }

    // ===========================
    // Email
    // ===========================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email =
                email == null || email.isBlank()
                        ? null
                        : email.trim().toLowerCase();
    }

    // ===========================
    // Phone
    // ===========================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone =
                phone == null || phone.isBlank()
                        ? null
                        : phone.trim();
    }

    // ===========================
    // Address
    // ===========================

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address =
                address == null || address.isBlank()
                        ? null
                        : address.trim();
    }

    // ===========================
    // Status
    // ===========================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.isBlank()) {
            this.status = "ACTIVE";
        } else {
            this.status = status.trim().toUpperCase();
        }
    }

    // ===========================
    // Created At
    // ===========================

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    // ===========================
    // Updated At
    // ===========================

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        if (studentCode == null || studentCode.isBlank()) {
            return fullName;
        }

        return studentCode + " - " + fullName;
    }
}
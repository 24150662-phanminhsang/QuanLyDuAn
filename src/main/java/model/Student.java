package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Student {

    private int studentID;
    private String studentCode;
    private Integer userId;
    private String fullName;
    private Date dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String status;
    private Timestamp createdAt;

    // Constructor rỗng
    public Student() {
        this.status = "ACTIVE";
    }

    // Constructor đầy đủ
    public Student(int studentID,
                   String studentCode,
                   Integer userId,
                   String fullName,
                   Date dateOfBirth,
                   String gender,
                   String email,
                   String phone,
                   String address,
                   String status,
                   Timestamp createdAt) {

        this.studentID = studentID;
        this.studentCode = studentCode;
        this.userId = userId;
        this.fullName = fullName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ================= ID =================

    public int getStudentID() {
        return studentID;
    }

    public void setStudentID(int studentID) {
        this.studentID = studentID;
    }

    // Tương thích nếu nơi khác dùng StudentId
    public int getStudentId() {
        return studentID;
    }

    public void setStudentId(int studentID) {
        this.studentID = studentID;
    }

    // ================= Student Code =================

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    // ================= User =================

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // ================= Full Name =================

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // ================= Date Of Birth =================

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    // ================= Gender =================

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // ================= Email =================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ================= Phone =================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // ================= Address =================

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ================= Status =================

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

    // ================= Created At =================

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {

        if (studentCode == null || studentCode.isBlank()) {
            return fullName;
        }

        return studentCode + " - " + fullName;
    }

}
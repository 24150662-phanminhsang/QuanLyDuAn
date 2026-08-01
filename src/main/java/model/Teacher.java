package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Teacher {

    private int teacherId;
    private String teacherCode;
    private Integer userId;

    private String fullName;
    private Date dateOfBirth;
    private String gender;

    private String email;
    private String phone;
    private String address;

    private String specialization;
    private String status;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Teacher() {
        this.status = "ACTIVE";
    }

    public Teacher(
            int teacherId,
            String teacherCode,
            Integer userId,
            String fullName,
            Date dateOfBirth,
            String gender,
            String email,
            String phone,
            String address,
            String specialization,
            String status,
            Timestamp createdAt,
            Timestamp updatedAt
    ) {
        this.teacherId = teacherId;
        setTeacherCode(teacherCode);
        this.userId = userId;
        setFullName(fullName);
        this.dateOfBirth = dateOfBirth;
        setGender(gender);
        setEmail(email);
        setPhone(phone);
        setAddress(address);
        setSpecialization(specialization);
        setStatus(status);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    // Giữ tương thích nếu code cũ dùng getId()/setId()
    public int getId() {
        return teacherId;
    }

    public void setId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        if (teacherCode == null || teacherCode.isBlank()) {
            this.teacherCode = null;
        } else {
            this.teacherCode =
                    teacherCode.trim().toUpperCase();
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = normalizeNullable(fullName);
    }

    // Giữ tương thích với View cũ đang dùng getName()/setName()
    public String getName() {
        return fullName;
    }

    public void setName(String name) {
        setFullName(name);
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = normalizeGender(gender);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = normalizeNullable(email);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = normalizeNullable(phone);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = normalizeNullable(address);
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization =
                normalizeNullable(specialization);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = normalizeStatus(status);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    private String normalizeGender(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized =
                value.trim().toUpperCase();

        return switch (normalized) {
            case "MALE", "NAM" -> "MALE";
            case "FEMALE", "NỮ", "NU" -> "FEMALE";
            case "OTHER", "KHÁC", "KHAC" -> "OTHER";

            default -> throw new IllegalArgumentException(
                    "Giới tính không hợp lệ: " + value
            );
        };
    }

    private String normalizeStatus(String value) {
        if (value == null || value.isBlank()) {
            return "ACTIVE";
        }

        String normalized =
                value.trim().toUpperCase();

        if (!normalized.equals("ACTIVE")
                && !normalized.equals("INACTIVE")) {

            throw new IllegalArgumentException(
                    "Trạng thái giảng viên không hợp lệ: "
                            + value
            );
        }

        return normalized;
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    @Override
    public String toString() {
        String displayName =
                fullName == null || fullName.isBlank()
                        ? "Chưa có họ tên"
                        : fullName;

        if (teacherCode == null
                || teacherCode.isBlank()) {

            return displayName;
        }

        return teacherCode
                + " - "
                + displayName;
    }
}
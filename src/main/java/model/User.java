package model;

import java.time.LocalDateTime;

public class User {

    private int userId;

    private String username;
    private String passwordHash;

    private String fullName;
    private String email;
    private String phone;

    private int roleId;
    private Role role;

    private AccountStatus status;

    // ======= V2 =======

    // Đã xác minh email chưa
    private boolean emailVerified;

    // Thời gian xác minh email
    private LocalDateTime emailVerifiedAt;

    // ADMIN hoặc SELF_REGISTER
    private String registrationSource;

    // Đăng nhập gần nhất
    private LocalDateTime lastLogin;

    // Số lần nhập sai mật khẩu
    private int loginAttempts;

    // Bị khóa đến thời điểm nào
    private LocalDateTime lockedUntil;

    // Admin duyệt
    private Integer approvedBy;
    private LocalDateTime approvedAt;

    // Admin từ chối
    private Integer rejectedBy;
    private LocalDateTime rejectedAt;
    private String rejectionReason;

    // ==================

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    /*======================
            Getter
     ======================*/

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public int getRoleId() {
        return roleId;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public LocalDateTime getEmailVerifiedAt() {
        return emailVerifiedAt;
    }

    public String getRegistrationSource() {
        return registrationSource;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public int getLoginAttempts() {
        return loginAttempts;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public Integer getApprovedBy() {
        return approvedBy;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public Integer getRejectedBy() {
        return rejectedBy;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /*======================
            Setter
     ======================*/

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) {
        this.emailVerifiedAt = emailVerifiedAt;
    }

    public void setRegistrationSource(String registrationSource) {
        this.registrationSource = registrationSource;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setLoginAttempts(int loginAttempts) {
        this.loginAttempts = loginAttempts;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public void setApprovedBy(Integer approvedBy) {
        this.approvedBy = approvedBy;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void setRejectedBy(Integer rejectedBy) {
        this.rejectedBy = rejectedBy;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return username + " - " + fullName;
    }
}
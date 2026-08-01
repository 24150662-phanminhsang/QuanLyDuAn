package model;

import java.time.LocalDateTime;

public class EmailVerification {

    private long verificationId;
    private int userId;

    private String otpHash;
    private String purpose;

    private LocalDateTime expiresAt;
    private LocalDateTime verifiedAt;
    private LocalDateTime invalidatedAt;

    private int attemptCount;
    private int resendCount;

    private LocalDateTime createdAt;

    public EmailVerification() {
        this.purpose = "EMAIL_VERIFICATION";
        this.attemptCount = 0;
        this.resendCount = 0;
    }

    public EmailVerification(
            long verificationId,
            int userId,
            String otpHash,
            String purpose,
            LocalDateTime expiresAt,
            LocalDateTime verifiedAt,
            LocalDateTime invalidatedAt,
            int attemptCount,
            int resendCount,
            LocalDateTime createdAt
    ) {
        this.verificationId = verificationId;
        this.userId = userId;
        this.otpHash = otpHash;
        setPurpose(purpose);
        this.expiresAt = expiresAt;
        this.verifiedAt = verifiedAt;
        this.invalidatedAt = invalidatedAt;
        this.attemptCount = Math.max(0, attemptCount);
        this.resendCount = Math.max(0, resendCount);
        this.createdAt = createdAt;
    }

    public long getVerificationId() {
        return verificationId;
    }

    public void setVerificationId(long verificationId) {
        this.verificationId = verificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public void setOtpHash(String otpHash) {
        this.otpHash = normalizeRequired(
                otpHash,
                "Mã OTP đã băm"
        );
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        if (purpose == null || purpose.isBlank()) {
            this.purpose = "EMAIL_VERIFICATION";
            return;
        }

        String normalized =
                purpose.trim().toUpperCase();

        if (!normalized.equals("EMAIL_VERIFICATION")
                && !normalized.equals("PASSWORD_RESET")) {

            throw new IllegalArgumentException(
                    "Mục đích OTP không hợp lệ: "
                            + purpose
            );
        }

        this.purpose = normalized;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getInvalidatedAt() {
        return invalidatedAt;
    }

    public void setInvalidatedAt(
            LocalDateTime invalidatedAt
    ) {
        this.invalidatedAt = invalidatedAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = Math.max(
                0,
                attemptCount
        );
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = Math.max(
                0,
                resendCount
        );
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isInvalidated() {
        return invalidatedAt != null;
    }

    public boolean isExpired() {
        return expiresAt == null
                || LocalDateTime.now().isAfter(
                expiresAt
        );
    }

    public boolean isActive() {
        return !isVerified()
                && !isInvalidated()
                && !isExpired();
    }

    private String normalizeRequired(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        return value.trim();
    }
}
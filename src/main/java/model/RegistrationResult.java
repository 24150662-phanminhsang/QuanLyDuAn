package model;

public class RegistrationResult {

    private final boolean success;
    private final int userId;
    private final int profileId;

    private final Role role;
    private final AccountStatus status;

    private final String email;
    private final String otp;
    private final String message;

    private RegistrationResult(
            boolean success,
            int userId,
            int profileId,
            Role role,
            AccountStatus status,
            String email,
            String otp,
            String message
    ) {
        this.success = success;
        this.userId = userId;
        this.profileId = profileId;
        this.role = role;
        this.status = status;
        this.email = email;
        this.otp = otp;
        this.message = message;
    }

    public static RegistrationResult success(
            int userId,
            int profileId,
            Role role,
            AccountStatus status,
            String email,
            String otp,
            String message
    ) {
        return new RegistrationResult(
                true,
                userId,
                profileId,
                role,
                status,
                email,
                otp,
                message
        );
    }

    public static RegistrationResult failure(
            String message
    ) {
        return new RegistrationResult(
                false,
                0,
                0,
                null,
                null,
                null,
                null,
                message
        );
    }

    public boolean isSuccess() {
        return success;
    }

    public int getUserId() {
        return userId;
    }

    public int getProfileId() {
        return profileId;
    }

    public Role getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public String getEmail() {
        return email;
    }

    public String getOtp() {
        return otp;
    }

    public String getMessage() {
        return message;
    }
}
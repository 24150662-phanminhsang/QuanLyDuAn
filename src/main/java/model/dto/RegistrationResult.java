package model.dto;

public class RegistrationResult {

    private final boolean successful;
    private final String message;
    private final int enrollmentId;
    private final int paymentId;

    private RegistrationResult(
            boolean successful,
            String message,
            int enrollmentId,
            int paymentId
    ) {
        this.successful = successful;
        this.message = message;
        this.enrollmentId = enrollmentId;
        this.paymentId = paymentId;
    }

    public static RegistrationResult success(
            String message,
            int enrollmentId,
            int paymentId
    ) {
        return new RegistrationResult(
                true,
                message,
                enrollmentId,
                paymentId
        );
    }

    public static RegistrationResult failure(String message) {
        return new RegistrationResult(
                false,
                message,
                0,
                0
        );
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getMessage() {
        return message;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public int getPaymentId() {
        return paymentId;
    }
}

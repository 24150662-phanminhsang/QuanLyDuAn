import com.formdev.flatlaf.FlatLightLaf;
import service.EmailService;

public class TestEmail {

    public static void main(String[] args) {
        FlatLightLaf.setup();

        EmailService emailService =
                new EmailService();

        try {
            System.out.println(
                    "===== KIỂM TRA SMTP ====="
            );

            boolean connected =
                    emailService.testConnection();

            if (!connected) {
                System.err.println(
                        "Không thể kết nối Gmail SMTP."
                );

                return;
            }

            emailService.sendVerificationOtp(
                    "EMAIL_NHAN_THAT@gmail.com",
                    "Nguyễn Trác Bắc",
                    "123456"
            );

            System.out.println(
                    "Đã gửi email OTP thành công."
            );

        } catch (Exception exception) {
            System.err.println(
                    "Gửi email thất bại: "
                            + exception.getMessage()
            );

            exception.printStackTrace();
        }
    }
}
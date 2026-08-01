package util;

public final class EmailTemplate {

    private EmailTemplate() {
    }

    public static String buildOtpTemplate(
            String fullName,
            String otp,
            int expirationMinutes
    ) {
        String safeName =
                escapeHtml(
                        fullName == null
                                || fullName.isBlank()
                                ? "bạn"
                                : fullName.trim()
                );

        String safeOtp =
                escapeHtml(otp);

        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f4f6f8;
                    font-family: Arial, Helvetica, sans-serif;
                ">

                    <table width="100%%"
                           cellpadding="0"
                           cellspacing="0"
                           style="padding: 32px 12px;">

                        <tr>
                            <td align="center">

                                <table width="100%%"
                                       cellpadding="0"
                                       cellspacing="0"
                                       style="
                                           max-width: 560px;
                                           background-color: #ffffff;
                                           border-radius: 14px;
                                           overflow: hidden;
                                           box-shadow:
                                               0 8px 28px
                                               rgba(0, 0, 0, 0.08);
                                       ">

                                    <tr>
                                        <td style="
                                            background-color: #2563eb;
                                            color: #ffffff;
                                            text-align: center;
                                            padding: 28px 24px;
                                        ">

                                            <h1 style="
                                                margin: 0;
                                                font-size: 24px;
                                            ">
                                                Course Management System
                                            </h1>

                                            <p style="
                                                margin: 8px 0 0;
                                                opacity: 0.9;
                                            ">
                                                Xác nhận địa chỉ email
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            padding: 32px 30px;
                                            color: #1f2937;
                                        ">

                                            <p style="
                                                margin: 0 0 16px;
                                                font-size: 16px;
                                            ">
                                                Xin chào <strong>%s</strong>,
                                            </p>

                                            <p style="
                                                margin: 0 0 24px;
                                                line-height: 1.6;
                                            ">
                                                Mã xác nhận tài khoản của bạn là:
                                            </p>

                                            <div style="
                                                text-align: center;
                                                margin: 24px 0;
                                            ">

                                                <span style="
                                                    display: inline-block;
                                                    padding: 16px 28px;
                                                    border-radius: 12px;
                                                    background-color: #eff6ff;
                                                    color: #1d4ed8;
                                                    font-size: 32px;
                                                    font-weight: bold;
                                                    letter-spacing: 8px;
                                                ">
                                                    %s
                                                </span>

                                            </div>

                                            <p style="
                                                margin: 24px 0 10px;
                                                line-height: 1.6;
                                            ">
                                                Mã có hiệu lực trong
                                                <strong>%d phút</strong>.
                                            </p>

                                            <p style="
                                                margin: 0;
                                                color: #6b7280;
                                                font-size: 14px;
                                                line-height: 1.6;
                                            ">
                                                Không chia sẻ mã này cho người khác.
                                                Nếu bạn không thực hiện đăng ký,
                                                hãy bỏ qua email này.
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            padding: 20px 24px;
                                            background-color: #f9fafb;
                                            color: #6b7280;
                                            text-align: center;
                                            font-size: 13px;
                                        ">
                                            Đây là email được gửi tự động.
                                            Vui lòng không trả lời email này.
                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>

                    </table>

                </body>
                </html>
                """.formatted(
                safeName,
                safeOtp,
                expirationMinutes
        );
    }

    public static String buildTeacherApprovedTemplate(
            String fullName
    ) {
        String safeName =
                escapeHtml(
                        fullName == null
                                || fullName.isBlank()
                                ? "giảng viên"
                                : fullName.trim()
                );

        return """
                <html>
                <body style="
                    font-family: Arial, sans-serif;
                    background: #f4f6f8;
                    padding: 30px;
                ">

                    <div style="
                        max-width: 560px;
                        margin: auto;
                        background: white;
                        padding: 30px;
                        border-radius: 12px;
                    ">

                        <h2 style="color: #15803d;">
                            Tài khoản đã được duyệt
                        </h2>

                        <p>Xin chào <strong>%s</strong>,</p>

                        <p>
                            Tài khoản giảng viên của bạn đã được
                            quản trị viên xét duyệt.
                        </p>

                        <p>
                            Bạn có thể đăng nhập và sử dụng hệ thống.
                        </p>

                    </div>

                </body>
                </html>
                """.formatted(safeName);
    }

    public static String buildTeacherRejectedTemplate(
            String fullName,
            String reason
    ) {
        String safeName =
                escapeHtml(
                        fullName == null
                                || fullName.isBlank()
                                ? "giảng viên"
                                : fullName.trim()
                );

        String safeReason =
                escapeHtml(
                        reason == null
                                || reason.isBlank()
                                ? "Không có lý do cụ thể."
                                : reason.trim()
                );

        return """
                <html>
                <body style="
                    font-family: Arial, sans-serif;
                    background: #f4f6f8;
                    padding: 30px;
                ">

                    <div style="
                        max-width: 560px;
                        margin: auto;
                        background: white;
                        padding: 30px;
                        border-radius: 12px;
                    ">

                        <h2 style="color: #b91c1c;">
                            Yêu cầu đăng ký chưa được duyệt
                        </h2>

                        <p>Xin chào <strong>%s</strong>,</p>

                        <p>
                            Yêu cầu đăng ký tài khoản giảng viên
                            của bạn chưa được chấp nhận.
                        </p>

                        <p>
                            <strong>Lý do:</strong> %s
                        </p>

                    </div>

                </body>
                </html>
                """.formatted(
                safeName,
                safeReason
        );
    }

    private static String escapeHtml(
            String value
    ) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
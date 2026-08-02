package controller;

import model.Payment;
import service.PaymentService;
import service.impl.PaymentServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController() {
        this.paymentService =
                new PaymentServiceImpl();
    }

    /* =====================================================
       DANH SÁCH TOÀN BỘ THANH TOÁN
       ===================================================== */

    public List<Payment> getAllPayments() {
        try {
            List<Payment> payments =
                    paymentService.getAll();

            return payments == null
                    ? Collections.emptyList()
                    : payments;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       DANH SÁCH THANH TOÁN THEO SINH VIÊN
       Chỉ hiển thị các khoản chưa thanh toán nếu Service/DAO
       đang được cấu hình theo nghiệp vụ đó.
       ===================================================== */

    public List<Payment> getPaymentsByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        try {
            List<Payment> payments =
                    paymentService.getByStudentId(
                            studentId
                    );

            return payments == null
                    ? Collections.emptyList()
                    : payments;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải thanh toán của sinh viên.",
                    exception
            );
        }
    }

    /* =====================================================
       TÌM THANH TOÁN THEO ID
       ===================================================== */

    public Payment getPayment(
            int paymentId
    ) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        try {
            return paymentService.getById(
                    paymentId
            );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải thông tin thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       TÌM THANH TOÁN THEO ENROLLMENT
       ===================================================== */

    public Payment getPaymentByEnrollment(
            int enrollmentId
    ) {
        validatePositiveId(
                enrollmentId,
                "ID đăng ký học"
        );

        try {
            return paymentService
                    .getByEnrollmentId(
                            enrollmentId
                    );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tải thanh toán theo đăng ký học.",
                    exception
            );
        }
    }

    /* =====================================================
       TẠO KHOẢN THANH TOÁN
       ===================================================== */

    public boolean processPayment(
            Payment payment
    ) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Thông tin thanh toán không được null."
            );
        }

        try {
            return paymentService
                    .createPayment(
                            payment
                    );

        } catch (IllegalArgumentException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tạo khoản thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       XÁC NHẬN THANH TOÁN
       ===================================================== */

    public boolean confirmPayment(
            int paymentId,
            double amount,
            String paymentMethod,
            String transactionCode
    ) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Số tiền thanh toán phải lớn hơn 0."
            );
        }

        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            throw new IllegalArgumentException(
                    "Phương thức thanh toán không được để trống."
            );
        }

        try {
            return paymentService.makePayment(
                    paymentId,
                    amount,
                    paymentMethod,
                    transactionCode
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể xác nhận thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       HỦY THANH TOÁN
       ===================================================== */

    public boolean cancelPayment(
            int paymentId
    ) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        try {
            return paymentService.cancelPayment(
                    paymentId
            );

        } catch (IllegalStateException exception) {
            throw exception;

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể hủy khoản thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       TỔNG HỌC PHÍ

       Phải tính trên tất cả enrollment hiện tại:
       PAID + UNPAID.
       ===================================================== */

    public double getTotalAmount(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        try {
            return paymentService
                    .getTotalAmountByStudent(
                            studentId
                    );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tính tổng học phí.",
                    exception
            );
        }
    }

    /* =====================================================
       TỔNG ĐÃ THANH TOÁN
       ===================================================== */

    public double getPaidAmount(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        try {
            return paymentService
                    .getTotalPaidByStudent(
                            studentId
                    );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tính số tiền đã thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       TỔNG CHƯA THANH TOÁN
       ===================================================== */

    public double getRemainingAmount(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        try {
            return paymentService
                    .getTotalRemainingByStudent(
                            studentId
                    );

        } catch (RuntimeException exception) {
            throw new RuntimeException(
                    "Không thể tính số tiền chưa thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       SINH MÃ GIAO DỊCH MÔ PHỎNG
       ===================================================== */

    public String generateTransactionCode() {
        return "PAY-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }
    public boolean removeEnrollment(
            int studentId,
            int enrollmentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        validatePositiveId(
                enrollmentId,
                "ID đăng ký học"
        );

        return paymentService
                .removeUnpaidEnrollment(
                        studentId,
                        enrollmentId
                );
    }
}
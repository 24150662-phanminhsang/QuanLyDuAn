package service.impl;

import dao.PaymentDAO;
import dao.impl.PaymentDAOImpl;
import model.Discount;
import model.Payment;
import service.DiscountService;
import service.PaymentService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PaymentServiceImpl implements PaymentService {

    private static final String STATUS_UNPAID = "UNPAID";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CANCELLED = "CANCELLED";

    private final PaymentDAO paymentDAO;
    private final DiscountService discountService;

    public PaymentServiceImpl() {
        this(
                new PaymentDAOImpl(),
                new DiscountServiceImpl()
        );
    }

    public PaymentServiceImpl(
            PaymentDAO paymentDAO,
            DiscountService discountService
    ) {
        if (paymentDAO == null) {
            throw new IllegalArgumentException(
                    "PaymentDAO không được null."
            );
        }

        if (discountService == null) {
            throw new IllegalArgumentException(
                    "DiscountService không được null."
            );
        }

        this.paymentDAO = paymentDAO;
        this.discountService = discountService;
    }

    /* =====================================================
       TẠO KHOẢN THANH TOÁN
       ===================================================== */

    @Override
    public boolean createPayment(Payment payment) {
        validatePaymentForCreate(payment);

        double amount = resolveAmount(payment);

        payment.setAmount(amount);
        payment.setOriginalAmount(amount);
        payment.setDiscountAmount(0);
        payment.setFinalAmount(amount);
        payment.setPaidAmount(0);
        payment.setStatus(STATUS_UNPAID);

        return paymentDAO.insert(payment);
    }

    /* =====================================================
       ÁP DỤNG GIẢM GIÁ
       ===================================================== */

    @Override
    public boolean applyDiscount(
            Payment payment,
            Discount discount
    ) {
        if (payment == null || discount == null) {
            return false;
        }

        double originalAmount = resolveAmount(payment);

        if (originalAmount <= 0) {
            return false;
        }

        if (!discountService.isValid(
                discount,
                originalAmount
        )) {
            return false;
        }

        double discountAmount =
                discountService.calculateDiscount(
                        discount,
                        originalAmount
                );

        discountAmount = Math.max(
                0,
                Math.min(
                        discountAmount,
                        originalAmount
                )
        );

        /*
         * Database hiện tại chưa có các cột lưu giảm giá.
         * Các giá trị này chỉ được cập nhật trên object.
         */
        payment.setDiscountId(
                discount.getDiscountId()
        );

        payment.setOriginalAmount(
                originalAmount
        );

        payment.setDiscountAmount(
                discountAmount
        );

        payment.setFinalAmount(
                originalAmount - discountAmount
        );

        return true;
    }

    /* =====================================================
       THANH TOÁN TOÀN BỘ
       ===================================================== */

    @Override
    public boolean makePayment(
            int paymentId,
            double amount,
            String paymentMethod,
            String transactionCode
    ) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        Payment payment =
                paymentDAO.getById(paymentId);

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy khoản thanh toán."
            );
        }

        String currentStatus =
                normalizeStatus(
                        payment.getStatus()
                );

        if (STATUS_CANCELLED.equals(
                currentStatus
        )) {
            throw new IllegalStateException(
                    "Khoản thanh toán đã bị hủy."
            );
        }

        if (STATUS_PAID.equals(
                currentStatus
        )) {
            throw new IllegalStateException(
                    "Khoản học phí đã được thanh toán."
            );
        }

        double requiredAmount =
                resolveAmount(payment);

        if (requiredAmount <= 0) {
            throw new IllegalStateException(
                    "Số tiền học phí không hợp lệ."
            );
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Số tiền thanh toán phải lớn hơn 0."
            );
        }

        /*
         * Database hiện tại không có paid_amount,
         * nên chỉ hỗ trợ thanh toán toàn bộ.
         */
        if (Math.abs(amount - requiredAmount) > 0.01) {
            throw new IllegalArgumentException(
                    "Vui lòng thanh toán đúng toàn bộ số tiền: "
                            + requiredAmount
            );
        }

        String normalizedMethod =
                normalizePaymentMethod(
                        paymentMethod
                );

        payment.setPaymentMethod(
                normalizedMethod
        );

        payment.setStatus(
                STATUS_PAID
        );

        payment.setPaymentDate(
                new java.sql.Timestamp(
                        System.currentTimeMillis()
                )
        );

        /*
         * transaction_code chưa có trong bảng Payments.
         * Chỉ giữ giá trị trong model để mô phỏng.
         */
        payment.setTransactionCode(
                normalizeOptionalText(
                        transactionCode
                )
        );

        payment.setPaidAmount(
                requiredAmount
        );

        payment.setOriginalAmount(
                requiredAmount
        );

        payment.setFinalAmount(
                requiredAmount
        );

        return paymentDAO.update(payment);
    }

    /* =====================================================
       HỦY THANH TOÁN
       ===================================================== */

    @Override
    public boolean cancelPayment(int paymentId) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        Payment payment =
                paymentDAO.getById(paymentId);

        if (payment == null) {
            return false;
        }

        if (STATUS_PAID.equals(
                normalizeStatus(
                        payment.getStatus()
                )
        )) {
            throw new IllegalStateException(
                    "Không thể hủy khoản học phí đã thanh toán."
            );
        }

        return paymentDAO.updateStatus(
                paymentId,
                STATUS_CANCELLED
        );
    }

    /* =====================================================
       TRUY VẤN
       ===================================================== */

    @Override
    public Payment getById(int paymentId) {
        validatePositiveId(
                paymentId,
                "ID thanh toán"
        );

        return paymentDAO.getById(paymentId);
    }

    @Override
    public Payment getByEnrollmentId(
            int enrollmentId
    ) {
        validatePositiveId(
                enrollmentId,
                "ID đăng ký học"
        );

        return paymentDAO.getByEnrollmentId(
                enrollmentId
        );
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> payments =
                paymentDAO.getAll();

        return payments == null
                ? Collections.emptyList()
                : payments;
    }

    /**
     * Dùng để hiển thị bảng Thanh toán.
     * DAO chỉ trả về các khoản UNPAID/PENDING.
     */
    @Override
    public List<Payment> getByStudentId(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<Payment> payments =
                paymentDAO.getByStudentId(
                        studentId
                );

        return payments == null
                ? Collections.emptyList()
                : payments;
    }

    /**
     * Dùng riêng cho thống kê.
     *
     * Danh sách này gồm tất cả Enrollment hiện tại:
     * - PAID
     * - UNPAID/PENDING
     *
     * Mỗi Enrollment chỉ lấy Payment mới nhất.
     */
    private List<Payment> getStatisticPayments(
            int studentId
    ) {
        validatePositiveId(
                studentId,
                "ID sinh viên"
        );

        List<Payment> payments =
                paymentDAO.getLatestByStudentId(
                        studentId
                );

        return payments == null
                ? Collections.emptyList()
                : payments;
    }

    /* =====================================================
       THỐNG KÊ
       ===================================================== */

    /**
     * Tổng học phí =
     * tổng học phí của tất cả môn đã đăng ký,
     * gồm PAID + UNPAID.
     */
    @Override
    public double getTotalAmountByStudent(
            int studentId
    ) {
        return getStatisticPayments(studentId)
                .stream()
                .filter(this::isActivePayment)
                .mapToDouble(
                        this::resolveAmount
                )
                .sum();
    }

    /**
     * Đã thanh toán =
     * tổng học phí của các môn có trạng thái PAID.
     */
    @Override
    public double getTotalPaidByStudent(
            int studentId
    ) {
        return getStatisticPayments(studentId)
                .stream()
                .filter(this::isActivePayment)
                .filter(
                        payment ->
                                STATUS_PAID.equals(
                                        normalizeStatus(
                                                payment.getStatus()
                                        )
                                )
                )
                .mapToDouble(
                        this::resolveAmount
                )
                .sum();
    }

    /**
     * Chưa thanh toán =
     * tổng học phí của các môn có trạng thái
     * UNPAID hoặc PENDING.
     */
    @Override
    public double getTotalRemainingByStudent(
            int studentId
    ) {
        return getStatisticPayments(studentId)
                .stream()
                .filter(this::isActivePayment)
                .filter(
                        payment ->
                                STATUS_UNPAID.equals(
                                        normalizeStatus(
                                                payment.getStatus()
                                        )
                                )
                )
                .mapToDouble(
                        this::resolveAmount
                )
                .sum();
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validatePaymentForCreate(
            Payment payment
    ) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Thông tin thanh toán không được null."
            );
        }

        validatePositiveId(
                payment.getStudentId(),
                "ID sinh viên"
        );

        if (payment.getEnrollmentId() == null
                || payment.getEnrollmentId() <= 0) {

            throw new IllegalArgumentException(
                    "ID đăng ký học không hợp lệ."
            );
        }

        if (resolveAmount(payment) < 0) {
            throw new IllegalArgumentException(
                    "Số tiền học phí không hợp lệ."
            );
        }
    }

    private void validatePositiveId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName + " phải lớn hơn 0."
            );
        }
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private double resolveAmount(
            Payment payment
    ) {
        if (payment == null) {
            return 0;
        }

        if (payment.getFinalAmount() > 0) {
            return payment.getFinalAmount();
        }

        if (payment.getOriginalAmount() > 0) {
            return payment.getOriginalAmount();
        }

        return Math.max(
                0,
                payment.getAmount()
        );
    }

    private boolean isActivePayment(
            Payment payment
    ) {
        return payment != null
                && !STATUS_CANCELLED.equals(
                normalizeStatus(
                        payment.getStatus()
                )
        );
    }

    private String normalizeStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            return STATUS_UNPAID;
        }

        String normalized =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "PENDING",
                 "PARTIAL",
                 "PARTIALLY_PAID",
                 "UNPAID" ->
                    STATUS_UNPAID;

            case "PAID" ->
                    STATUS_PAID;

            case "CANCELLED" ->
                    STATUS_CANCELLED;

            default ->
                    normalized;
        };
    }

    private String normalizePaymentMethod(
            String paymentMethod
    ) {
        if (paymentMethod == null
                || paymentMethod.isBlank()) {

            throw new IllegalArgumentException(
                    "Vui lòng chọn phương thức thanh toán."
            );
        }

        String normalized =
                paymentMethod.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "CASH",
                 "TIỀN MẶT",
                 "TIEN MAT" ->
                    "CASH";

            case "BANK_TRANSFER",
                 "CHUYỂN KHOẢN",
                 "CHUYEN KHOAN" ->
                    "BANK_TRANSFER";

            case "CARD",
                 "THẺ",
                 "THE" ->
                    "CARD";

            case "E_WALLET",
                 "VÍ ĐIỆN TỬ",
                 "VI DIEN TU" ->
                    "E_WALLET";

            default ->
                    throw new IllegalArgumentException(
                            "Phương thức thanh toán không hợp lệ."
                    );
        };
    }

    private String normalizeOptionalText(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
    @Override
    public boolean removeUnpaidEnrollment(
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

        Payment payment =
                paymentDAO.getByEnrollmentId(
                        enrollmentId
                );

        if (payment == null) {
            throw new IllegalArgumentException(
                    "Không tìm thấy khoản thanh toán."
            );
        }

        if (payment.getStudentId() != studentId) {
            throw new IllegalStateException(
                    "Khoản thanh toán không thuộc sinh viên hiện tại."
            );
        }

        String status =
                normalizeStatus(
                        payment.getStatus()
                );

        if (STATUS_PAID.equals(status)) {
            throw new IllegalStateException(
                    "Không thể xóa môn đã thanh toán."
            );
        }

        return paymentDAO.deleteUnpaidEnrollment(
                studentId,
                enrollmentId
        );
    }
}
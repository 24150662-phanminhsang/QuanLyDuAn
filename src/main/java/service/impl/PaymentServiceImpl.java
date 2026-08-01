package service.impl;

import dao.PaymentDAO;
import dao.impl.PaymentDAOImpl;
import model.Discount;
import model.Payment;
import service.DiscountService;
import service.PaymentService;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private final PaymentDAO paymentDAO;
    private final DiscountService discountService;

    public PaymentServiceImpl() {
        this(new PaymentDAOImpl(), new DiscountServiceImpl());
    }

    public PaymentServiceImpl(PaymentDAO paymentDAO, DiscountService discountService) {
        this.paymentDAO = paymentDAO;
        this.discountService = discountService;
    }

    @Override
    public boolean createPayment(Payment payment) {
        if (payment == null || payment.getOriginalAmount() <= 0) {
            return false;
        }

        // Tự động tính số tiền phải trả nếu chưa được thiết lập
        if (payment.getFinalAmount() <= 0) {
            double finalAmt = Math.max(0, payment.getOriginalAmount() - payment.getDiscountAmount());
            payment.setFinalAmount(finalAmt);
        }

        // Đặt trạng thái mặc định nếu chưa truyền vào
        if (payment.getStatus() == null || payment.getStatus().isBlank()) {
            payment.setStatus("PENDING");
        }

        return paymentDAO.insert(payment);
    }

    @Override
    public boolean applyDiscount(Payment payment, Discount discount) {
        if (payment == null || discount == null) {
            return false;
        }

        double originalAmount = payment.getOriginalAmount();

        // Kiểm tra tính hợp lệ của mã giảm giá
        if (!discountService.isValid(discount, originalAmount)) {
            return false;
        }

        // Tính số tiền được giảm và cập nhật lại Payment
        double discountAmount = discountService.calculateDiscount(discount, originalAmount);
        double finalAmount = Math.max(0, originalAmount - discountAmount);

        payment.setDiscountAmount(discountAmount);
        payment.setFinalAmount(finalAmount);
        payment.setDiscountId(discount.getDiscountId());

        return true;
    }

    @Override
    public boolean makePayment(int paymentId, double amount, String paymentMethod, String transactionCode) {
        if (paymentId <= 0 || amount <= 0) {
            return false;
        }

        Payment payment = paymentDAO.getById(paymentId);
        if (payment == null || "PAID".equalsIgnoreCase(payment.getStatus())) {
            return false;
        }

        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PAID");
        payment.setPaymentDate(new Date());

        return paymentDAO.update(payment);
    }

    @Override
    public boolean cancelPayment(int paymentId) {
        if (paymentId <= 0) {
            return false;
        }

        Payment payment = paymentDAO.getById(paymentId);
        if (payment == null) {
            return false;
        }

        payment.setStatus("CANCELLED");
        return paymentDAO.update(payment);
    }

    @Override
    public Payment getById(int paymentId) {
        if (paymentId <= 0) {
            return null;
        }
        return paymentDAO.getById(paymentId);
    }

    @Override
    public Payment getByEnrollmentId(int enrollmentId) {
        if (enrollmentId <= 0) {
            return null;
        }
        return paymentDAO.getByEnrollmentId(enrollmentId);
    }

    @Override
    public List<Payment> getAll() {
        List<Payment> list = paymentDAO.getAll();
        return list != null ? list : Collections.emptyList();
    }
}
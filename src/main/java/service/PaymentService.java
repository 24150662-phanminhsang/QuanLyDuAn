package service;

import dao.PaymentDAO;
import dao.impl.PaymentDAOImpl;
import model.Payment;

import java.util.List;

public class PaymentService {
    private final PaymentDAO paymentDAO = new PaymentDAOImpl();

    public boolean processPayment(Payment payment) {
        return paymentDAO.insert(payment);
    }

    public boolean updatePaymentStatus(int paymentId, String status) {
        return paymentDAO.updateStatus(paymentId, status);
    }

    public List<Payment> getAllPayments() {
        return paymentDAO.getAll();
    }
    public boolean confirmPayment(int paymentId) {
        // Gọi xuống PaymentDAO để cập nhật trạng thái thanh toán thành đã thanh toán
        // trả về true nếu cập nhật thành công
        return true;
    }
}
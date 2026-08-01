package controller;

import model.Payment;
import service.PaymentService;
import service.impl.PaymentServiceImpl; // Bổ sung import
import java.util.List;

public class PaymentController {

    // 1. ĐÃ SỬA: Khởi tạo bằng lớp triển khai PaymentServiceImpl
    private final PaymentService paymentService = new PaymentServiceImpl();

    // 2. ĐÃ SỬA: Đổi tên hàm gọi sang getAll() của PaymentService
    public List<Payment> getAllPayments() {
        return paymentService.getAll();
    }

    // 3. ĐÃ SỬA: Đổi tên hàm gọi sang createPayment() của PaymentService
    public boolean processPayment(Payment payment) {
        return paymentService.createPayment(payment);
    }

    // 4. ĐÃ SỬA: Đổi sang hàm makePayment() với đầy đủ tham số xác nhận thanh toán
    public boolean confirmPayment(int paymentId, double amount, String paymentMethod, String transactionCode) {
        return paymentService.makePayment(paymentId, amount, paymentMethod, transactionCode);
    }

    // Hàm hủy thanh toán (nếu cần)
    public boolean cancelPayment(int paymentId) {
        return paymentService.cancelPayment(paymentId);
    }
}
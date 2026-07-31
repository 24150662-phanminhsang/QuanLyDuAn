package controller;

import model.Payment;
import service.PaymentService;
import java.util.List;

public class PaymentController {
    private final PaymentService paymentService = new PaymentService();

    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    public boolean processPayment(Payment payment) {
        return paymentService.processPayment(payment);
    }

    public boolean confirmPayment(int paymentId) {
        return paymentService.updatePaymentStatus(paymentId, "COMPLETED");
    }
}
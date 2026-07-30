package controller;

import service.PaymentService;

public class PaymentController {
    private PaymentService paymentService = new PaymentService();

    public void confirmPayment(int paymentId) {

        paymentService.confirmPayment(paymentId);
    }
}
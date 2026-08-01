package service;

import model.Discount;
import model.Payment;

import java.util.List;

public interface PaymentService {

    boolean createPayment(Payment payment);

    boolean applyDiscount(
            Payment payment,
            Discount discount
    );

    boolean makePayment(
            int paymentId,
            double amount,
            String paymentMethod,
            String transactionCode
    );

    boolean cancelPayment(int paymentId);

    Payment getById(int paymentId);

    Payment getByEnrollmentId(int enrollmentId);

    List<Payment> getAll();
}
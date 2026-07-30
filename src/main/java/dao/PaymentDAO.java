package dao;

import model.Payment;
import java.util.List;

public interface PaymentDAO {
    boolean insert(Payment payment);
    boolean updateStatus(int paymentId, String status);
    Payment getByEnrollmentId(int enrollmentId);
    List<Payment> getAll();
}
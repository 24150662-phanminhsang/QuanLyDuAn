package dao;

import model.Payment;

import java.util.List;

public interface PaymentDAO {

    boolean insert(Payment payment);

    boolean update(Payment payment);

    boolean updateStatus(int paymentId, String status);

    boolean updatePaidAmount(
            int paymentId,
            double paidAmount,
            String status
    );
    boolean deleteUnpaidEnrollment(
            int studentId,
            int enrollmentId
    );

    Payment getById(int paymentId);

    Payment getByEnrollmentId(int enrollmentId);

    List<Payment> getAll();

    List<Payment> getByStudentId(int studentId);

    List<Payment> getLatestByStudentId(int studentId);

}
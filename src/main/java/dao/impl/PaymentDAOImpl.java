package dao.impl;

import dao.PaymentDAO;
import model.Payment;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {

    @Override
    public boolean insert(Payment payment) {

        String sql = """
                INSERT INTO PAYMENTS
                (
                    student_id,
                    enrollment_id,
                    amount,
                    original_amount,
                    discount_amount,
                    final_amount,
                    paid_amount,
                    discount_id,
                    payment_date,
                    due_date,
                    payment_method,
                    status,
                    note,
                    transaction_code
                )
                VALUES
                (
                    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
                )
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {

            ps.setInt(1, payment.getStudentId());

            if (payment.getEnrollmentId() != null) {
                ps.setInt(2, payment.getEnrollmentId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setDouble(3, payment.getAmount());
            ps.setDouble(4, payment.getOriginalAmount());
            ps.setDouble(5, payment.getDiscountAmount());
            ps.setDouble(6, payment.getFinalAmount());
            ps.setDouble(7, payment.getPaidAmount());

            if (payment.getDiscountId() != null) {
                ps.setInt(8, payment.getDiscountId());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            if (payment.getPaymentDate() != null) {
                ps.setTimestamp(
                        9,
                        new Timestamp(
                                payment.getPaymentDate().getTime()
                        )
                );
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            if (payment.getDueDate() != null) {
                ps.setTimestamp(
                        10,
                        new Timestamp(
                                payment.getDueDate().getTime()
                        )
                );
            } else {
                ps.setNull(10, Types.TIMESTAMP);
            }

            ps.setString(11, payment.getPaymentMethod());
            ps.setString(12, payment.getStatus());
            ps.setString(13, payment.getNote());
            ps.setString(14, payment.getTransactionCode());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    payment.setPaymentId(
                            rs.getInt(1)
                    );
                }
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean update(Payment payment) {

        String sql = """
                UPDATE PAYMENTS
                SET
                    student_id = ?,
                    enrollment_id = ?,
                    amount = ?,
                    original_amount = ?,
                    discount_amount = ?,
                    final_amount = ?,
                    paid_amount = ?,
                    discount_id = ?,
                    payment_date = ?,
                    due_date = ?,
                    payment_method = ?,
                    status = ?,
                    note = ?,
                    transaction_code = ?
                WHERE payment_id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(1, payment.getStudentId());

            if (payment.getEnrollmentId() != null) {
                ps.setInt(
                        2,
                        payment.getEnrollmentId()
                );
            } else {
                ps.setNull(
                        2,
                        Types.INTEGER
                );
            }

            ps.setDouble(
                    3,
                    payment.getAmount()
            );

            ps.setDouble(
                    4,
                    payment.getOriginalAmount()
            );

            ps.setDouble(
                    5,
                    payment.getDiscountAmount()
            );

            ps.setDouble(
                    6,
                    payment.getFinalAmount()
            );

            ps.setDouble(
                    7,
                    payment.getPaidAmount()
            );

            if (payment.getDiscountId() != null) {
                ps.setInt(
                        8,
                        payment.getDiscountId()
                );
            } else {
                ps.setNull(
                        8,
                        Types.INTEGER
                );
            }

            if (payment.getPaymentDate() != null) {
                ps.setTimestamp(
                        9,
                        new Timestamp(
                                payment.getPaymentDate().getTime()
                        )
                );
            } else {
                ps.setNull(
                        9,
                        Types.TIMESTAMP
                );
            }

            if (payment.getDueDate() != null) {
                ps.setTimestamp(
                        10,
                        new Timestamp(
                                payment.getDueDate().getTime()
                        )
                );
            } else {
                ps.setNull(
                        10,
                        Types.TIMESTAMP
                );
            }

            ps.setString(
                    11,
                    payment.getPaymentMethod()
            );

            ps.setString(
                    12,
                    payment.getStatus()
            );

            ps.setString(
                    13,
                    payment.getNote()
            );

            ps.setString(
                    14,
                    payment.getTransactionCode()
            );

            ps.setInt(
                    15,
                    payment.getPaymentId()
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updateStatus(
            int paymentId,
            String status
    ) {

        String sql = """
                UPDATE PAYMENTS
                SET status = ?
                WHERE payment_id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setString(1, status);
            ps.setInt(2, paymentId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean updatePaidAmount(
            int paymentId,
            double paidAmount,
            String status
    ) {

        String sql = """
                UPDATE PAYMENTS
                SET
                    paid_amount = ?,
                    status = ?,
                    payment_date = GETDATE()
                WHERE payment_id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setDouble(
                    1,
                    paidAmount
            );

            ps.setString(
                    2,
                    status
            );

            ps.setInt(
                    3,
                    paymentId
            );

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public Payment getById(
            int paymentId
    ) {

        String sql =
                "SELECT * FROM PAYMENTS WHERE payment_id = ?";

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    paymentId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {
                    return mapPayment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public Payment getByEnrollmentId(
            int enrollmentId
    ) {

        String sql = """
                SELECT *
                FROM PAYMENTS
                WHERE enrollment_id = ?
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    enrollmentId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                if (rs.next()) {
                    return mapPayment(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public List<Payment> getAll() {

        List<Payment> list =
                new ArrayList<>();

        String sql =
                "SELECT * FROM PAYMENTS ORDER BY payment_id DESC";

        try (
                Connection conn = DBConnection.getConnection();
                Statement stmt =
                        conn.createStatement();
                ResultSet rs =
                        stmt.executeQuery(sql)
        ) {

            while (rs.next()) {
                list.add(
                        mapPayment(rs)
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    @Override
    public List<Payment> getByStudentId(
            int studentId
    ) {

        List<Payment> list =
                new ArrayList<>();

        String sql = """
                SELECT *
                FROM PAYMENTS
                WHERE student_id = ?
                ORDER BY payment_id DESC
                """;

        try (
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps =
                        conn.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    studentId
            );

            try (ResultSet rs =
                         ps.executeQuery()) {

                while (rs.next()) {
                    list.add(
                            mapPayment(rs)
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }


    private Payment mapPayment(
            ResultSet rs
    ) throws SQLException {

        Payment p =
                new Payment();

        p.setPaymentId(
                rs.getInt("payment_id")
        );

        p.setStudentId(
                rs.getInt("student_id")
        );

        int enrollmentId =
                rs.getInt("enrollment_id");

        if (!rs.wasNull()) {
            p.setEnrollmentId(
                    enrollmentId
            );
        }

        p.setAmount(
                rs.getDouble("amount")
        );

        p.setOriginalAmount(
                rs.getDouble("original_amount")
        );

        p.setDiscountAmount(
                rs.getDouble("discount_amount")
        );

        p.setFinalAmount(
                rs.getDouble("final_amount")
        );

        p.setPaidAmount(
                rs.getDouble("paid_amount")
        );

        int discountId =
                rs.getInt("discount_id");

        if (!rs.wasNull()) {
            p.setDiscountId(
                    discountId
            );
        }

        p.setPaymentDate(
                rs.getTimestamp(
                        "payment_date"
                )
        );

        p.setDueDate(
                rs.getTimestamp(
                        "due_date"
                )
        );

        p.setPaymentMethod(
                rs.getString(
                        "payment_method"
                )
        );

        p.setStatus(
                rs.getString(
                        "status"
                )
        );

        p.setNote(
                rs.getString(
                        "note"
                )
        );

        p.setTransactionCode(
                rs.getString(
                        "transaction_code"
                )
        );

        return p;
    }
}
package dao.impl;

import dao.PaymentDAO;
import model.Payment;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * DAO thanh toán tương thích với cấu trúc bảng Payments hiện tại:
 *
 * payment_id
 * student_id
 * enrollment_id
 * amount
 * payment_date
 * payment_method
 * status
 * note
 * created_at
 *
 * Quy ước trạng thái:
 * - UNPAID: Chưa thanh toán
 * - PAID: Đã thanh toán
 * - CANCELLED: Đã hủy
 *
 * Mỗi enrollment chỉ lấy khoản thanh toán mới nhất khi hiển thị cho Student.
 */
public class PaymentDAOImpl implements PaymentDAO {

    private static final String STATUS_UNPAID =
            "UNPAID";

    private static final String STATUS_PAID =
            "PAID";

    private static final String STATUS_CANCELLED =
            "CANCELLED";

    /* =====================================================
       THÊM KHOẢN THANH TOÁN
       ===================================================== */

    @Override
    public boolean insert(Payment payment) {
        validatePaymentForInsert(payment);

        /*
         * Không tạo trùng khoản học phí cho cùng enrollment.
         */
        if (payment.getEnrollmentId() != null
                && existsByEnrollmentId(
                payment.getEnrollmentId()
        )) {
            return false;
        }

        String sql =
                """
                INSERT INTO dbo.Payments
                (
                    student_id,
                    enrollment_id,
                    amount,
                    payment_date,
                    payment_method,
                    status,
                    note,
                    created_at
                )
                VALUES
                (
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    ?,
                    SYSDATETIME()
                )
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql,
                                Statement.RETURN_GENERATED_KEYS
                        )
        ) {
            statement.setInt(
                    1,
                    payment.getStudentId()
            );

            setNullableInteger(
                    statement,
                    2,
                    payment.getEnrollmentId()
            );

            statement.setDouble(
                    3,
                    resolveAmount(payment)
            );

            setNullableTimestamp(
                    statement,
                    4,
                    payment.getPaymentDate()
            );

            setNullableString(
                    statement,
                    5,
                    payment.getPaymentMethod()
            );

            statement.setString(
                    6,
                    normalizeStatusForDatabase(
                            payment.getStatus()
                    )
            );

            setNullableString(
                    statement,
                    7,
                    payment.getNote()
            );

            int affectedRows =
                    statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (
                    ResultSet generatedKeys =
                            statement.getGeneratedKeys()
            ) {
                if (generatedKeys.next()) {
                    payment.setPaymentId(
                            generatedKeys.getInt(1)
                    );
                }
            }

            return true;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể thêm khoản thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT THANH TOÁN
       ===================================================== */

    @Override
    public boolean update(Payment payment) {
        if (payment == null
                || payment.getPaymentId() <= 0) {

            return false;
        }

        String sql =
                """
                UPDATE dbo.Payments
                SET
                    student_id = ?,
                    enrollment_id = ?,
                    amount = ?,
                    payment_date = ?,
                    payment_method = ?,
                    status = ?,
                    note = ?
                WHERE payment_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    payment.getStudentId()
            );

            setNullableInteger(
                    statement,
                    2,
                    payment.getEnrollmentId()
            );

            statement.setDouble(
                    3,
                    resolveAmount(payment)
            );

            setNullableTimestamp(
                    statement,
                    4,
                    payment.getPaymentDate()
            );

            setNullableString(
                    statement,
                    5,
                    payment.getPaymentMethod()
            );

            statement.setString(
                    6,
                    normalizeStatusForDatabase(
                            payment.getStatus()
                    )
            );

            setNullableString(
                    statement,
                    7,
                    payment.getNote()
            );

            statement.setInt(
                    8,
                    payment.getPaymentId()
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật khoản thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT TRẠNG THÁI
       ===================================================== */

    @Override
    public List<Payment> getLatestByStudentId(
            int studentId
    ) {
        if (studentId <= 0) {
            return new ArrayList<>();
        }

        String sql =
                """
                SELECT
                    latestPayment.payment_id,
                    e.student_id,
                    e.enrollment_id,
    
                    /*
                     * Tổng học phí luôn lấy từ khóa học,
                     * không lấy từ lịch sử thanh toán cũ.
                     */
                    c.tuition_fee AS amount,
    
                    latestPayment.payment_date,
                    latestPayment.payment_method,
    
                    /*
                     * Chưa có Payment thì xem là UNPAID.
                     */
                    COALESCE(
                        latestPayment.status,
                        'UNPAID'
                    ) AS status,
    
                    latestPayment.note,
                    latestPayment.created_at
    
                FROM dbo.Enrollments e
    
                INNER JOIN dbo.CourseClasses cc
                    ON cc.class_id = e.class_id
    
                INNER JOIN dbo.Courses c
                    ON c.course_id = cc.course_id
    
                OUTER APPLY
                (
                    SELECT TOP 1
                        p.payment_id,
                        p.payment_date,
                        p.payment_method,
                        p.status,
                        p.note,
                        p.created_at
    
                    FROM dbo.Payments p
    
                    WHERE p.enrollment_id =
                          e.enrollment_id
    
                    ORDER BY p.payment_id DESC
                ) latestPayment
    
                WHERE e.student_id = ?
                  AND UPPER(e.status) IN
                      (
                          'ENROLLED',
                          'COMPLETED'
                      )
    
                ORDER BY e.enrollment_id DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    payments.add(
                            mapPayment(resultSet)
                    );
                }
            }

            return payments;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải thống kê học phí của sinh viên: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
    @Override
    public boolean updateStatus(
            int paymentId,
            String status
    ) {
        if (paymentId <= 0) {
            return false;
        }

        String normalizedStatus =
                normalizeStatusForDatabase(
                        status
                );

        String sql;

        if (STATUS_PAID.equals(
                normalizedStatus
        )) {
            sql =
                    """
                    UPDATE dbo.Payments
                    SET
                        status = ?,
                        payment_date =
                            COALESCE(
                                payment_date,
                                SYSDATETIME()
                            )
                    WHERE payment_id = ?
                    """;
        } else {
            sql =
                    """
                    UPDATE dbo.Payments
                    SET status = ?
                    WHERE payment_id = ?
                    """;
        }

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setString(
                    1,
                    normalizedStatus
            );

            statement.setInt(
                    2,
                    paymentId
            );

            return statement.executeUpdate() > 0;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể cập nhật trạng thái thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT SỐ TIỀN ĐÃ TRẢ
       ===================================================== */

    @Override
    public boolean updatePaidAmount(
            int paymentId,
            double paidAmount,
            String status
    ) {
        if (paymentId <= 0
                || paidAmount < 0) {

            return false;
        }

        /*
         * Bảng Payments hiện chưa có paid_amount.
         * Chỉ hỗ trợ thanh toán toàn bộ.
         */
        Payment payment =
                getById(paymentId);

        if (payment == null) {
            return false;
        }

        double requiredAmount =
                payment.getFinalAmount() > 0
                        ? payment.getFinalAmount()
                        : payment.getAmount();

        if (paidAmount + 0.01
                < requiredAmount) {

            throw new IllegalArgumentException(
                    "Hệ thống hiện chỉ hỗ trợ "
                            + "thanh toán toàn bộ học phí."
            );
        }

        return updateStatus(
                paymentId,
                STATUS_PAID
        );
    }

    /* =====================================================
       TÌM THEO PAYMENT ID
       ===================================================== */

    @Override
    public Payment getById(
            int paymentId
    ) {
        if (paymentId <= 0) {
            return null;
        }

        String sql =
                baseSelect()
                        + """
                        WHERE p.payment_id = ?
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    paymentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapPayment(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải khoản thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       TÌM THEO ENROLLMENT ID
       ===================================================== */

    @Override
    public Payment getByEnrollmentId(
            int enrollmentId
    ) {
        if (enrollmentId <= 0) {
            return null;
        }

        String sql =
                baseSelect()
                        + """
                        WHERE p.enrollment_id = ?
                        ORDER BY p.payment_id DESC
                        """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    enrollmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                if (resultSet.next()) {
                    return mapPayment(
                            resultSet
                    );
                }
            }

            return null;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải thanh toán theo đăng ký học: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       DANH SÁCH TOÀN BỘ
       ===================================================== */

    @Override
    public List<Payment> getAll() {
        String sql =
                baseSelect()
                        + """
                        ORDER BY p.payment_id DESC
                        """;

        List<Payment> payments =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {
            while (resultSet.next()) {
                payments.add(
                        mapPayment(resultSet)
                );
            }

            return payments;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải danh sách thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       DANH SÁCH THEO SINH VIÊN
       MỖI ENROLLMENT CHỈ HIỆN MỘT DÒNG
       ===================================================== */

    @Override
    public List<Payment> getByStudentId(
            int studentId
    ) {
        if (studentId <= 0) {
            return new ArrayList<>();
        }

        String sql =
                """
                SELECT
                    latestPayment.payment_id,
                    e.student_id,
                    e.enrollment_id,
                    c.tuition_fee AS amount,
                    latestPayment.payment_date,
                    latestPayment.payment_method,
    
                    COALESCE(
                        latestPayment.status,
                        'UNPAID'
                    ) AS status,
    
                    latestPayment.note,
                    latestPayment.created_at
    
                FROM dbo.Enrollments e
    
                INNER JOIN dbo.CourseClasses cc
                    ON cc.class_id = e.class_id
    
                INNER JOIN dbo.Courses c
                    ON c.course_id = cc.course_id
    
                OUTER APPLY
                (
                    SELECT TOP 1
                        p.payment_id,
                        p.payment_date,
                        p.payment_method,
                        p.status,
                        p.note,
                        p.created_at
    
                    FROM dbo.Payments p
    
                    WHERE p.enrollment_id =
                          e.enrollment_id
    
                    ORDER BY p.payment_id DESC
                ) latestPayment
    
                WHERE e.student_id = ?
                  AND UPPER(e.status) IN
                      (
                          'ENROLLED',
                          'COMPLETED'
                      )
    
                  AND UPPER(
                        COALESCE(
                            latestPayment.status,
                            'UNPAID'
                        )
                      ) IN
                      (
                          'UNPAID',
                          'PENDING'
                      )
    
                ORDER BY e.enrollment_id DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                while (resultSet.next()) {
                    payments.add(
                            mapPayment(resultSet)
                    );
                }
            }

            return payments;

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể tải các khoản chưa thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       KIỂM TRA TỒN TẠI
       ===================================================== */

    public boolean existsByEnrollmentId(
            int enrollmentId
    ) {
        if (enrollmentId <= 0) {
            return false;
        }

        String sql =
                """
                SELECT COUNT(*) AS total
                FROM dbo.Payments
                WHERE enrollment_id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {
            statement.setInt(
                    1,
                    enrollmentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {
                return resultSet.next()
                        && resultSet.getInt(
                        "total"
                ) > 0;
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể kiểm tra khoản thanh toán: "
                            + exception.getMessage(),
                    exception
            );
        }
    }

    /* =====================================================
       SELECT CHUNG
       ===================================================== */

    private String baseSelect() {
        return """
                SELECT
                    p.payment_id,
                    p.student_id,
                    p.enrollment_id,
                    p.amount,
                    p.payment_date,
                    p.payment_method,
                    p.status,
                    p.note,
                    p.created_at
                FROM dbo.Payments p
                """;
    }

    /* =====================================================
       MAPPING
       ===================================================== */

    private Payment mapPayment(
            ResultSet resultSet
    ) throws SQLException {
        Payment payment =
                new Payment();

        int paymentId =
                resultSet.getInt("payment_id");

        payment.setPaymentId(
                resultSet.wasNull()
                        ? 0
                        : paymentId
        );

        payment.setStudentId(
                resultSet.getInt(
                        "student_id"
                )
        );

        int enrollmentId =
                resultSet.getInt(
                        "enrollment_id"
                );

        payment.setEnrollmentId(
                resultSet.wasNull()
                        ? null
                        : enrollmentId
        );

        double amount =
                resultSet.getDouble(
                        "amount"
                );

        String status =
                normalizeStatusForModel(
                        resultSet.getString(
                                "status"
                        )
                );

        /*
         * Đồng bộ database cũ với model Payment mới.
         */
        payment.setAmount(amount);
        payment.setOriginalAmount(amount);
        payment.setDiscountAmount(0);
        payment.setFinalAmount(amount);

        if (STATUS_PAID.equals(status)) {
            payment.setPaidAmount(amount);
        } else {
            payment.setPaidAmount(0);
        }

        payment.setDiscountId(null);
        payment.setDueDate(null);
        payment.setTransactionCode(null);

        payment.setPaymentDate(
                resultSet.getTimestamp(
                        "payment_date"
                )
        );

        payment.setPaymentMethod(
                resultSet.getString(
                        "payment_method"
                )
        );

        payment.setStatus(status);

        payment.setNote(
                resultSet.getString(
                        "note"
                )
        );

        return payment;
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validatePaymentForInsert(
            Payment payment
    ) {
        if (payment == null) {
            throw new IllegalArgumentException(
                    "Thông tin thanh toán không được null."
            );
        }

        if (payment.getStudentId() <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        if (payment.getEnrollmentId() == null
                || payment.getEnrollmentId() <= 0) {

            throw new IllegalArgumentException(
                    "ID đăng ký học không hợp lệ."
            );
        }

        if (resolveAmount(payment) < 0) {
            throw new IllegalArgumentException(
                    "Số tiền thanh toán không hợp lệ."
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

    private String normalizeStatusForDatabase(
            String status
    ) {
        if (status == null
                || status.isBlank()) {

            return STATUS_UNPAID;
        }

        String normalized =
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch (normalized) {
            case "PENDING",
                 "UNPAID",
                 "PARTIAL",
                 "PARTIALLY_PAID" ->
                    STATUS_UNPAID;

            case "PAID" ->
                    STATUS_PAID;

            case "CANCELLED" ->
                    STATUS_CANCELLED;

            default ->
                    normalized;
        };
    }

    private String normalizeStatusForModel(
            String status
    ) {
        if (status == null
                || status.isBlank()) {

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
                 "PARTIALLY_PAID" ->
                    STATUS_UNPAID;

            default ->
                    normalized;
        };
    }

    private void setNullableInteger(
            PreparedStatement statement,
            int parameterIndex,
            Integer value
    ) throws SQLException {
        if (value == null) {
            statement.setNull(
                    parameterIndex,
                    Types.INTEGER
            );
        } else {
            statement.setInt(
                    parameterIndex,
                    value
            );
        }
    }

    private void setNullableTimestamp(
            PreparedStatement statement,
            int parameterIndex,
            java.util.Date value
    ) throws SQLException {
        if (value == null) {
            statement.setNull(
                    parameterIndex,
                    Types.TIMESTAMP
            );
        } else {
            statement.setTimestamp(
                    parameterIndex,
                    new Timestamp(
                            value.getTime()
                    )
            );
        }
    }

    private void setNullableString(
            PreparedStatement statement,
            int parameterIndex,
            String value
    ) throws SQLException {
        if (value == null
                || value.isBlank()) {

            statement.setNull(
                    parameterIndex,
                    Types.VARCHAR
            );
        } else {
            statement.setString(
                    parameterIndex,
                    value.trim()
            );
        }
    }
    @Override
    public boolean deleteUnpaidEnrollment(
            int studentId,
            int enrollmentId
    ) {
        if (studentId <= 0 || enrollmentId <= 0) {
            return false;
        }

        String checkSql =
                """
                SELECT TOP 1
                    p.payment_id,
                    p.status
                FROM dbo.Payments p
                INNER JOIN dbo.Enrollments e
                    ON e.enrollment_id = p.enrollment_id
                WHERE p.student_id = ?
                  AND p.enrollment_id = ?
                  AND e.student_id = ?
                ORDER BY p.payment_id DESC
                """;

        String deletePaymentsSql =
                """
                DELETE FROM dbo.Payments
                WHERE student_id = ?
                  AND enrollment_id = ?
                """;

        String deleteEnrollmentSql =
                """
                DELETE FROM dbo.Enrollments
                WHERE enrollment_id = ?
                  AND student_id = ?
                  AND UPPER(status) = 'ENROLLED'
                """;

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {
            boolean oldAutoCommit =
                    connection.getAutoCommit();

            try {
                connection.setAutoCommit(false);

                String paymentStatus = null;

                try (
                        PreparedStatement checkStatement =
                                connection.prepareStatement(
                                        checkSql
                                )
                ) {
                    checkStatement.setInt(1, studentId);
                    checkStatement.setInt(2, enrollmentId);
                    checkStatement.setInt(3, studentId);

                    try (
                            ResultSet resultSet =
                                    checkStatement.executeQuery()
                    ) {
                        if (resultSet.next()) {
                            paymentStatus =
                                    resultSet.getString(
                                            "status"
                                    );
                        }
                    }
                }

                if (paymentStatus == null) {
                    connection.rollback();

                    throw new IllegalStateException(
                            "Không tìm thấy khoản học phí của môn này."
                    );
                }

                String normalizedStatus =
                        paymentStatus.trim()
                                .toUpperCase(
                                        Locale.ROOT
                                );

                if ("PAID".equals(normalizedStatus)) {
                    connection.rollback();

                    throw new IllegalStateException(
                            "Không thể xóa môn đã thanh toán."
                    );
                }

                if ("CANCELLED".equals(normalizedStatus)) {
                    connection.rollback();

                    throw new IllegalStateException(
                            "Khoản thanh toán đã bị hủy."
                    );
                }

                try (
                        PreparedStatement deletePaymentStatement =
                                connection.prepareStatement(
                                        deletePaymentsSql
                                )
                ) {
                    deletePaymentStatement.setInt(
                            1,
                            studentId
                    );

                    deletePaymentStatement.setInt(
                            2,
                            enrollmentId
                    );

                    deletePaymentStatement.executeUpdate();
                }

                int deletedEnrollments;

                try (
                        PreparedStatement deleteEnrollmentStatement =
                                connection.prepareStatement(
                                        deleteEnrollmentSql
                                )
                ) {
                    deleteEnrollmentStatement.setInt(
                            1,
                            enrollmentId
                    );

                    deleteEnrollmentStatement.setInt(
                            2,
                            studentId
                    );

                    deletedEnrollments =
                            deleteEnrollmentStatement
                                    .executeUpdate();
                }

                if (deletedEnrollments == 0) {
                    connection.rollback();

                    throw new IllegalStateException(
                            "Không thể xóa đăng ký khóa học."
                    );
                }

                connection.commit();
                return true;

            } catch (SQLException | RuntimeException exception) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    exception.addSuppressed(
                            rollbackException
                    );
                }

                throw exception;

            } finally {
                try {
                    connection.setAutoCommit(
                            oldAutoCommit
                    );
                } catch (SQLException ignored) {
                }
            }

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Không thể xóa đăng ký khóa học: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}
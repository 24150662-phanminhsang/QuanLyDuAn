package view;

import model.Discount;
import model.Payment;
import net.miginfocom.swing.MigLayout;
import service.DiscountService;
import service.impl.DiscountServiceImpl;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PaymentManagementView extends JPanel {

    private JTextField txtEnrollmentId;
    private JTextField txtStudentId;
    private JTextField txtCourseName;
    private JTextField txtOriginalAmount;
    private JTextField txtDiscountCode;

    private JLabel lblDiscount;
    private JLabel lblFinalAmount;

    private JComboBox<String> cboPaymentMethod;

    private JButton btnApplyDiscount;
    private JButton btnPayment;
    private JButton btnClear;

    private JTable paymentTable;
    private DefaultTableModel tableModel;

    // ĐÃ SỬA: Đổi kiểu khai báo sang Interface DiscountService
    private final DiscountService discountService = new DiscountServiceImpl();

    private Discount currentDiscount;

    private double originalAmount = 0;
    private double discountAmount = 0;
    private double finalAmount = 0;

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public PaymentManagementView() {

        setLayout(new MigLayout("fill, insets 15", "[380pt][grow]", "[][grow]"));

        // ==========================================
        // TITLE
        // ==========================================

        JLabel lblTitle = new JLabel("QUẢN LÝ THANH TOÁN & HỌC PHÍ");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblTitle, "span 2, wrap 15");

        // ==========================================
        // FORM THANH TOÁN
        // ==========================================

        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));

        // Enrollment ID
        formPanel.add(new JLabel("Mã đăng ký:"));
        txtEnrollmentId = new JTextField();
        formPanel.add(txtEnrollmentId);

        // Student ID
        formPanel.add(new JLabel("Mã học viên:"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        // Course
        formPanel.add(new JLabel("Khóa học:"));
        txtCourseName = new JTextField();
        formPanel.add(txtCourseName);

        // Original amount
        formPanel.add(new JLabel("Học phí gốc:"));
        txtOriginalAmount = new JTextField();
        formPanel.add(txtOriginalAmount);

        // Discount code
        formPanel.add(new JLabel("Mã giảm giá:"));
        JPanel discountPanel = new JPanel(new MigLayout("insets 0", "[grow][100!]"));
        txtDiscountCode = new JTextField();
        btnApplyDiscount = new JButton("Áp dụng");
        discountPanel.add(txtDiscountCode, "growx");
        discountPanel.add(btnApplyDiscount);
        formPanel.add(discountPanel, "growx");

        // Discount amount
        formPanel.add(new JLabel("Số tiền giảm:"));
        lblDiscount = new JLabel(currency.format(0));
        lblDiscount.setFont(new Font("SansSerif", Font.BOLD, 14));
        formPanel.add(lblDiscount);

        // Final amount
        formPanel.add(new JLabel("Thành tiền:"));
        lblFinalAmount = new JLabel(currency.format(0));
        lblFinalAmount.setFont(new Font("SansSerif", Font.BOLD, 18));
        formPanel.add(lblFinalAmount);

        // Payment method
        formPanel.add(new JLabel("Phương thức:"));
        cboPaymentMethod = new JComboBox<>(new String[]{
                "Tiền mặt",
                "Chuyển khoản",
                "VNPay",
                "Momo"
        });
        formPanel.add(cboPaymentMethod);

        // Buttons
        btnPayment = new JButton("Xác nhận thanh toán");
        btnPayment.putClientProperty("JButton.buttonType", "accent");

        btnClear = new JButton("Làm mới");

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        buttonPanel.add(btnPayment, "growx");
        buttonPanel.add(btnClear, "growx");

        formPanel.add(buttonPanel, "span 2, growx, gaptop 15");

        add(formPanel, "top");

        // ==========================================
        // PAYMENT TABLE
        // ==========================================

        JPanel tablePanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Lịch sử thanh toán"));

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Mã ĐK",
                        "Học viên",
                        "Học phí gốc",
                        "Giảm giá",
                        "Thành tiền",
                        "Phương thức",
                        "Trạng thái"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(25);

        tablePanel.add(new JScrollPane(paymentTable), "grow");

        add(tablePanel, "grow");

        // ==========================================
        // EVENTS
        // ==========================================

        btnApplyDiscount.addActionListener(e -> applyDiscount());
        btnPayment.addActionListener(e -> processPayment());
        btnClear.addActionListener(e -> clearForm());
    }

    // ==========================================
    // ÁP DỤNG GIẢM GIÁ
    // ==========================================

    private void applyDiscount() {
        try {
            originalAmount = Double.parseDouble(txtOriginalAmount.getText().trim());

            if (originalAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Học phí phải lớn hơn 0!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String code = txtDiscountCode.getText().trim();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã giảm giá!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            currentDiscount = discountService.findByCode(code);

            if (currentDiscount == null) {
                JOptionPane.showMessageDialog(this, "Mã giảm giá không tồn tại hoặc đã hết hiệu lực!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                discountAmount = 0;
                updateAmount();
                return;
            }

            if (originalAmount < currentDiscount.getMinimumAmount()) {
                JOptionPane.showMessageDialog(this, "Đơn hàng chưa đạt giá trị tối thiểu để sử dụng mã!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                discountAmount = 0;
                updateAmount();
                return;
            }

            discountAmount = discountService.calculateDiscount(currentDiscount, originalAmount);
            updateAmount();

            JOptionPane.showMessageDialog(this, "Áp dụng mã giảm giá thành công!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Học phí phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // TÍNH TIỀN
    // ==========================================

    private void updateAmount() {
        // ĐÃ SỬA: Tính số tiền phải trả trực tiếp thay vì gọi phương thức không tồn tại
        finalAmount = Math.max(0, originalAmount - discountAmount);

        lblDiscount.setText(currency.format(discountAmount));
        lblFinalAmount.setText(currency.format(finalAmount));
    }

    // ==========================================
    // THANH TOÁN
    // ==========================================

    private void processPayment() {
        try {
            if (txtEnrollmentId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã đăng ký!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtStudentId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã học viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtCourseName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khóa học!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            originalAmount = Double.parseDouble(txtOriginalAmount.getText().trim());

            if (originalAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Học phí không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            updateAmount();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Xác nhận thanh toán " + currency.format(finalAmount) + "?",
                    "Xác nhận thanh toán",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            Payment payment = new Payment();
            payment.setEnrollmentId(Integer.parseInt(txtEnrollmentId.getText().trim()));
            payment.setOriginalAmount(originalAmount);
            payment.setDiscountAmount(discountAmount);
            payment.setFinalAmount(finalAmount);

            // ĐÃ SỬA: Lưu discountId từ currentDiscount thay vì setDiscountCode không tồn tại
            if (currentDiscount != null) {
                payment.setDiscountId(currentDiscount.getDiscountId());
            }

            payment.setPaymentMethod(cboPaymentMethod.getSelectedItem().toString());
            payment.setStatus("PAID");
            payment.setPaymentDate(new java.util.Date());

            addPaymentToTable(payment);

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nSố tiền: " + currency.format(finalAmount));

            clearForm();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã đăng ký và học phí phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================================
    // THÊM VÀO BẢNG
    // ==========================================

    private void addPaymentToTable(Payment payment) {
        tableModel.addRow(new Object[]{
                payment.getPaymentId(),
                payment.getEnrollmentId(),
                txtStudentId.getText(),
                currency.format(payment.getOriginalAmount()),
                currency.format(payment.getDiscountAmount()),
                currency.format(payment.getFinalAmount()),
                payment.getPaymentMethod(),
                payment.getStatus()
        });
    }

    // ==========================================
    // RESET
    // ==========================================

    private void clearForm() {
        txtEnrollmentId.setText("");
        txtStudentId.setText("");
        txtCourseName.setText("");
        txtOriginalAmount.setText("");
        txtDiscountCode.setText("");

        lblDiscount.setText(currency.format(0));
        lblFinalAmount.setText(currency.format(0));

        currentDiscount = null;
        originalAmount = 0;
        discountAmount = 0;
        finalAmount = 0;
    }
}
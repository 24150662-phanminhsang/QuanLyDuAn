package view;

import model.Discount;
import model.Payment;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.DiscountService;
import service.PaymentService;
import service.impl.DiscountServiceImpl;
import service.impl.PaymentServiceImpl;
import util.UIConstants;
import view.components.ContentCard;
import java.awt.Window;
import javax.swing.SwingUtilities;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PaymentManagementView extends JPanel {

    private final PaymentService paymentService;
    private final DiscountService discountService;

    private final NumberFormat currencyFormat =
            NumberFormat.getCurrencyInstance(
                    new Locale("vi", "VN")
            );

    private final JTextField paymentIdField;
    private final JTextField enrollmentIdField;
    private final JTextField studentIdField;
    private final JTextField originalAmountField;
    private final JTextField discountCodeField;
    private final JTextField paymentAmountField;
    private final JTextField transactionCodeField;
    private final JTextField noteField;

    private final JComboBox<String> paymentMethodComboBox;

    private final JLabel discountAmountLabel;
    private final JLabel finalAmountLabel;
    private final JLabel paidAmountLabel;
    private final JLabel remainingAmountLabel;
    private final JLabel statusLabel;
    private final JLabel totalPaymentLabel;

    private final JButton applyDiscountButton;
    private final JButton manageDiscountButton;
    private final JButton createPaymentButton;
    private final JButton makePaymentButton;
    private final JButton cancelPaymentButton;
    private final JButton refreshButton;

    private final DefaultTableModel tableModel;
    private final JTable paymentTable;

    private List<Payment> currentPayments =
            Collections.emptyList();

    private Payment selectedPayment;
    private Discount currentDiscount;

    private double originalAmount;
    private double discountAmount;
    private double finalAmount;

    private boolean loading;

    public PaymentManagementView() {
        paymentService =
                new PaymentServiceImpl();

        discountService =
                new DiscountServiceImpl();

        paymentIdField = new JTextField();
        enrollmentIdField = new JTextField();
        studentIdField = new JTextField();
        originalAmountField = new JTextField();
        discountCodeField = new JTextField();
        paymentAmountField = new JTextField();
        transactionCodeField = new JTextField();
        noteField = new JTextField();

        paymentMethodComboBox =
                new JComboBox<>(
                        new String[]{
                                "Tiền mặt",
                                "Chuyển khoản",
                                "VNPay",
                                "Momo"
                        }
                );

        discountAmountLabel =
                createMoneyLabel();

        finalAmountLabel =
                createMoneyLabel();

        paidAmountLabel =
                createMoneyLabel();

        remainingAmountLabel =
                createMoneyLabel();

        statusLabel =
                new JLabel("Chưa tạo");

        totalPaymentLabel =
                new JLabel("0 giao dịch");

        applyDiscountButton =
                createSecondaryButton(
                        "Áp dụng",
                        FontAwesomeSolid.PERCENTAGE
                );

        manageDiscountButton =
                createSecondaryButton(
                        "Quản lý mã",
                        FontAwesomeSolid.TAGS
                );

        createPaymentButton =
                createPrimaryButton(
                        "Tạo khoản thu",
                        FontAwesomeSolid.PLUS
                );

        makePaymentButton =
                createPrimaryButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD
                );

        cancelPaymentButton =
                createDangerButton(
                        "Hủy khoản thu",
                        FontAwesomeSolid.TIMES
                );

        refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Mã đăng ký",
                                "Mã học viên",
                                "Học phí gốc",
                                "Giảm giá",
                                "Thành tiền",
                                "Đã trả",
                                "Còn lại",
                                "Phương thức",
                                "Trạng thái",
                                "Mã giao dịch"
                        },
                        0
                ) {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        paymentTable =
                new JTable(tableModel);

        initializeView();
        registerEvents();
        clearForm();
        loadPayments();
    }

    /* =====================================================
       KHỞI TẠO GIAO DIỆN
       ===================================================== */

    private void initializeView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, insets 16",
                                "[365!, fill]16[grow, fill]",
                                "[grow, fill]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createFormCard(),
                "growy"
        );

        wrapper.add(
                createTableCard(),
                "grow, push"
        );

        add(
                wrapper,
                BorderLayout.CENTER
        );
    }

    /* =====================================================
       FORM THANH TOÁN
       ===================================================== */

    private ContentCard createFormCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20",
                        "[grow, fill]",
                        "[]5[]15[]9[]9[]9[]9[]9[]9[]9[]9[]15[]8[]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Thông tin thanh toán"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Tạo khoản thu và xác nhận học phí"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(titleLabel);
        card.add(descriptionLabel);

        paymentIdField.setEditable(false);
        transactionCodeField.setEditable(false);

        configureTextField(
                paymentIdField,
                "Tự động sinh"
        );

        configureTextField(
                enrollmentIdField,
                "Nhập ID đăng ký học"
        );

        configureTextField(
                studentIdField,
                "Nhập ID học viên"
        );

        configureTextField(
                originalAmountField,
                "Ví dụ: 5000000"
        );

        configureTextField(
                discountCodeField,
                "Nhập mã giảm giá"
        );

        configureTextField(
                paymentAmountField,
                "Nhập số tiền thanh toán"
        );

        configureTextField(
                transactionCodeField,
                "Tự động sinh"
        );

        configureTextField(
                noteField,
                "Ghi chú thanh toán"
        );

        configureComboBox();

        card.add(
                createFormField(
                        "ID thanh toán",
                        paymentIdField
                )
        );

        card.add(
                createFormField(
                        "Mã đăng ký",
                        enrollmentIdField
                )
        );

        card.add(
                createFormField(
                        "Mã học viên",
                        studentIdField
                )
        );

        card.add(
                createFormField(
                        "Học phí gốc",
                        originalAmountField
                )
        );

        card.add(
                createDiscountField()
        );

        card.add(
                createAmountSummaryPanel()
        );

        card.add(
                createComboFormField(
                        "Phương thức",
                        paymentMethodComboBox
                )
        );

        card.add(
                createFormField(
                        "Số tiền thanh toán",
                        paymentAmountField
                )
        );

        card.add(
                createFormField(
                        "Mã giao dịch",
                        transactionCodeField
                )
        );

        card.add(
                createFormField(
                        "Ghi chú",
                        noteField
                )
        );

        JPanel firstButtonPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill][grow, fill]",
                                "[]"
                        )
                );

        firstButtonPanel.setOpaque(false);

        firstButtonPanel.add(refreshButton);
        firstButtonPanel.add(createPaymentButton);

        card.add(
                firstButtonPanel,
                "growx"
        );

        JPanel secondButtonPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill][grow, fill]",
                                "[]"
                        )
                );

        secondButtonPanel.setOpaque(false);

        secondButtonPanel.add(cancelPaymentButton);
        secondButtonPanel.add(makePaymentButton);

        card.add(
                secondButtonPanel,
                "growx"
        );

        return card;
    }

    private JPanel createFormField(
            String labelText,
            JTextField textField
    ) {
        JPanel panel =
                createFieldPanel();

        panel.add(
                createFieldLabel(labelText)
        );

        panel.add(
                textField,
                "growx, height 39!"
        );

        return panel;
    }

    private JPanel createComboFormField(
            String labelText,
            JComboBox<String> comboBox
    ) {
        JPanel panel =
                createFieldPanel();

        panel.add(
                createFieldLabel(labelText)
        );

        panel.add(
                comboBox,
                "growx, height 39!"
        );

        return panel;
    }

    private JPanel createFieldPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow, fill]",
                                "[]5[]"
                        )
                );

        panel.setOpaque(false);

        return panel;
    }

    private JLabel createFieldLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text);

        label.setFont(
                UIConstants.FONT_MEDIUM
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return label;
    }

    private JPanel createDiscountField() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow, fill]",
                                "[]5[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                createFieldLabel(
                        "Mã giảm giá"
                )
        );

        JPanel inputPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]8[]8[]",
                                "[]"
                        )
                );

        inputPanel.setOpaque(false);

        inputPanel.add(
                discountCodeField,
                "growx, height 39!"
        );

        inputPanel.add(
                applyDiscountButton,
                "height 39!"
        );

        inputPanel.add(
                manageDiscountButton,
                "height 39!"
        );

        panel.add(
                inputPanel,
                "growx"
        );

        return panel;
    }

    private JPanel createAmountSummaryPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 10",
                                "[grow][right]",
                                "[]7[]7[]7[]7[]"
                        )
                );

        panel.setBackground(
                new Color(248, 250, 252)
        );

        panel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 1;
                borderColor: #E2E8F0;
                """
        );

        panel.add(
                createSummaryLabel(
                        "Số tiền giảm"
                )
        );

        panel.add(discountAmountLabel);

        panel.add(
                createSummaryLabel(
                        "Thành tiền"
                )
        );

        panel.add(finalAmountLabel);

        panel.add(
                createSummaryLabel(
                        "Đã thanh toán"
                )
        );

        panel.add(paidAmountLabel);

        panel.add(
                createSummaryLabel(
                        "Còn lại"
                )
        );

        panel.add(remainingAmountLabel);

        panel.add(
                createSummaryLabel(
                        "Trạng thái"
                )
        );

        panel.add(statusLabel);

        return panel;
    }

    private JLabel createSummaryLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text);

        label.setFont(
                UIConstants.FONT_NORMAL
        );

        label.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        return label;
    }

    private JLabel createMoneyLabel() {
        JLabel label =
                new JLabel(
                        formatMoney(0)
                );

        label.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return label;
    }

    /* =====================================================
       DANH SÁCH GIAO DỊCH
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]12[grow, fill]"
                )
        );

        JPanel titlePanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]",
                                "[][]"
                        )
                );

        titlePanel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Lịch sử thanh toán"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Chọn một giao dịch để xem hoặc cập nhật"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalPaymentLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        totalPaymentLabel.setForeground(
                UIConstants.PRIMARY
        );

        titlePanel.add(
                titleLabel,
                "cell 0 0"
        );

        titlePanel.add(
                descriptionLabel,
                "cell 0 1"
        );

        titlePanel.add(
                totalPaymentLabel,
                "cell 1 0 1 2, align right"
        );

        card.add(
                titlePanel,
                "growx"
        );

        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(
                        paymentTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.getHorizontalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        card.add(
                scrollPane,
                "grow, push"
        );

        return card;
    }

    private void configureTable() {
        paymentTable.setRowHeight(42);

        paymentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        paymentTable.setFillsViewportHeight(true);

        paymentTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        paymentTable.setShowVerticalLines(false);
        paymentTable.setShowHorizontalLines(true);

        paymentTable.setGridColor(
                UIConstants.BORDER
        );

        paymentTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        paymentTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        paymentTable.getTableHeader()
                .setReorderingAllowed(false);

        paymentTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 40)
                );

        paymentTable.getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(Font.BOLD)
                );

        int[] widths = {
                60,
                90,
                90,
                125,
                110,
                125,
                125,
                125,
                125,
                100,
                190
        };

        for (
                int column = 0;
                column < widths.length;
                column++
        ) {
            paymentTable
                    .getColumnModel()
                    .getColumn(column)
                    .setPreferredWidth(
                            widths[column]
                    );
        }

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        for (
                int column = 0;
                column < tableModel.getColumnCount();
                column++
        ) {
            paymentTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        paymentTable
                .getColumnModel()
                .getColumn(9)
                .setCellRenderer(
                        new StatusCellRenderer()
                );
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        applyDiscountButton.addActionListener(
                event -> applyDiscount()
        );

        manageDiscountButton.addActionListener(
                event -> openDiscountManager()
        );

        createPaymentButton.addActionListener(
                event -> createPayment()
        );

        makePaymentButton.addActionListener(
                event -> makePayment()
        );

        cancelPaymentButton.addActionListener(
                event -> cancelPayment()
        );

        refreshButton.addActionListener(
                event -> {
                    clearForm();
                    loadPayments();
                }
        );

        paymentTable.getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                loadSelectedPayment();
                            }
                        }
                );
    }


    private void openDiscountManager() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        DiscountManagementDialog dialog =
                new DiscountManagementDialog(owner);

        dialog.setVisible(true);

        /*
         * Sau khi đóng dialog, mã vừa thêm có thể dùng ngay.
         */
        String currentCode = discountCodeField.getText();
        discountCodeField.setText(currentCode == null ? "" : currentCode.trim());
    }

    /* =====================================================
       ÁP DỤNG GIẢM GIÁ
       ===================================================== */

    private void applyDiscount() {
        try {
            originalAmount =
                    parsePositiveMoney(
                            originalAmountField.getText(),
                            "Học phí gốc"
                    );

            String discountCode =
                    discountCodeField
                            .getText()
                            .trim();

            if (discountCode.isBlank()) {
                throw new IllegalArgumentException(
                        "Vui lòng nhập mã giảm giá."
                );
            }

            currentDiscount =
                    discountService.findByCode(
                            discountCode
                    );

            if (currentDiscount == null) {
                throw new IllegalArgumentException(
                        "Mã giảm giá không tồn tại "
                                + "hoặc đã hết hiệu lực."
                );
            }

            Payment temporaryPayment =
                    new Payment();

            temporaryPayment.setOriginalAmount(
                    originalAmount
            );

            boolean applied =
                    paymentService.applyDiscount(
                            temporaryPayment,
                            currentDiscount
                    );

            if (!applied) {
                throw new IllegalArgumentException(
                        "Mã giảm giá không đủ điều kiện áp dụng."
                );
            }

            discountAmount =
                    temporaryPayment
                            .getDiscountAmount();

            finalAmount =
                    temporaryPayment
                            .getFinalAmount();

            updateAmountLabels(
                    0,
                    finalAmount
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Áp dụng mã giảm giá thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException exception) {
            currentDiscount = null;
            discountAmount = 0;

            calculateAmountsWithoutDiscount();

            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể áp dụng mã giảm giá.",
                    exception
            );
        }
    }

    /* =====================================================
       TẠO KHOẢN THU
       ===================================================== */

    private void createPayment() {
        if (loading) {
            return;
        }

        try {
            setLoading(true);

            Payment payment =
                    readNewPaymentFromForm();

            boolean successful =
                    paymentService.createPayment(
                            payment
                    );

            if (!successful) {
                showWarning(
                        "Không thể tạo khoản thanh toán."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Tạo khoản thanh toán thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadPayments();

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tạo khoản thanh toán.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private Payment readNewPaymentFromForm() {
        int enrollmentId =
                parsePositiveInt(
                        enrollmentIdField.getText(),
                        "Mã đăng ký"
                );

        int studentId =
                parsePositiveInt(
                        studentIdField.getText(),
                        "Mã học viên"
                );

        originalAmount =
                parsePositiveMoney(
                        originalAmountField.getText(),
                        "Học phí gốc"
                );

        calculateAmountsWithoutDiscount();

        Payment payment =
                new Payment();

        payment.setEnrollmentId(
                enrollmentId
        );

        payment.setStudentId(
                studentId
        );

        payment.setOriginalAmount(
                originalAmount
        );

        payment.setDiscountAmount(
                discountAmount
        );

        payment.setFinalAmount(
                finalAmount
        );

        payment.setPaidAmount(0);

        if (currentDiscount != null) {
            payment.setDiscountId(
                    currentDiscount
                            .getDiscountId()
            );
        }

        payment.setStatus("PENDING");

        payment.setNote(
                normalizeNullable(
                        noteField.getText()
                )
        );

        return payment;
    }

    /* =====================================================
       THANH TOÁN KHOẢN ĐÃ TẠO
       ===================================================== */

    private void makePayment() {
        if (selectedPayment == null) {
            showWarning(
                    "Vui lòng chọn khoản thanh toán trong bảng."
            );
            return;
        }

        try {
            double amount =
                    parsePositiveMoney(
                            paymentAmountField.getText(),
                            "Số tiền thanh toán"
                    );

            String paymentMethod =
                    String.valueOf(
                            paymentMethodComboBox
                                    .getSelectedItem()
                    );

            int confirmation =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Xác nhận thanh toán "
                                    + formatMoney(amount)
                                    + "?",
                            "Xác nhận thanh toán",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (confirmation != JOptionPane.YES_OPTION) {
                return;
            }

            boolean successful =
                    paymentService.makePayment(
                            selectedPayment
                                    .getPaymentId(),
                            amount,
                            paymentMethod,
                            null
                    );

            if (!successful) {
                showWarning(
                        "Không thể cập nhật thanh toán."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadPayments();

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể thực hiện thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       HỦY KHOẢN THU
       ===================================================== */

    private void cancelPayment() {
        if (selectedPayment == null) {
            showWarning(
                    "Vui lòng chọn khoản thanh toán cần hủy."
            );
            return;
        }

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn hủy khoản thanh toán này?",
                        "Xác nhận hủy",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean successful =
                    paymentService.cancelPayment(
                            selectedPayment
                                    .getPaymentId()
                    );

            if (!successful) {
                showWarning(
                        "Không thể hủy khoản thanh toán."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Hủy khoản thanh toán thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearForm();
            loadPayments();

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể hủy khoản thanh toán.",
                    exception
            );
        }
    }

    /* =====================================================
       TẢI DỮ LIỆU SQL SERVER
       ===================================================== */

    public void loadPayments() {
        if (loading) {
            return;
        }

        try {
            loading = true;

            List<Payment> payments =
                    paymentService.getAll();

            currentPayments =
                    payments == null
                            ? Collections.emptyList()
                            : payments;

            displayPayments();

        } catch (RuntimeException exception) {
            currentPayments =
                    Collections.emptyList();

            displayPayments();

            showError(
                    "Không thể tải lịch sử thanh toán.",
                    exception
            );

        } finally {
            loading = false;
        }
    }

    private void displayPayments() {
        tableModel.setRowCount(0);

        for (Payment payment : currentPayments) {
            tableModel.addRow(
                    new Object[]{
                            payment.getPaymentId(),
                            payment.getEnrollmentId(),
                            payment.getStudentId(),
                            formatMoney(
                                    payment.getOriginalAmount()
                            ),
                            formatMoney(
                                    payment.getDiscountAmount()
                            ),
                            formatMoney(
                                    payment.getFinalAmount()
                            ),
                            formatMoney(
                                    payment.getPaidAmount()
                            ),
                            formatMoney(
                                    payment.getRemainingAmount()
                            ),
                            formatPaymentMethod(
                                    payment.getPaymentMethod()
                            ),
                            formatStatus(
                                    payment.getStatus()
                            ),
                            safeText(
                                    payment.getTransactionCode()
                            )
                    }
            );
        }

        totalPaymentLabel.setText(
                currentPayments.size()
                        + " giao dịch"
        );

        paymentTable.clearSelection();
        paymentTable.revalidate();
        paymentTable.repaint();
    }

    /* =====================================================
       CHỌN DÒNG TRONG BẢNG
       ===================================================== */

    private void loadSelectedPayment() {
        int selectedRow =
                paymentTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                paymentTable.convertRowIndexToModel(
                        selectedRow
                );

        if (modelRow < 0
                || modelRow >= currentPayments.size()) {
            return;
        }

        selectedPayment =
                currentPayments.get(modelRow);

        paymentIdField.setText(
                String.valueOf(
                        selectedPayment.getPaymentId()
                )
        );

        enrollmentIdField.setText(
                selectedPayment.getEnrollmentId() == null
                        ? ""
                        : String.valueOf(
                        selectedPayment.getEnrollmentId()
                )
        );

        studentIdField.setText(
                String.valueOf(
                        selectedPayment.getStudentId()
                )
        );

        originalAmountField.setText(
                formatPlainNumber(
                        selectedPayment.getOriginalAmount()
                )
        );

        transactionCodeField.setText(
                safeText(
                        selectedPayment
                                .getTransactionCode()
                )
        );

        noteField.setText(
                safeText(
                        selectedPayment.getNote()
                )
        );

        selectPaymentMethod(
                selectedPayment.getPaymentMethod()
        );

        originalAmount =
                selectedPayment.getOriginalAmount();

        discountAmount =
                selectedPayment.getDiscountAmount();

        finalAmount =
                selectedPayment.getFinalAmount();

        discountAmountLabel.setText(
                formatMoney(discountAmount)
        );

        finalAmountLabel.setText(
                formatMoney(finalAmount)
        );

        paidAmountLabel.setText(
                formatMoney(
                        selectedPayment.getPaidAmount()
                )
        );

        remainingAmountLabel.setText(
                formatMoney(
                        selectedPayment
                                .getRemainingAmount()
                )
        );

        statusLabel.setText(
                formatStatus(
                        selectedPayment.getStatus()
                )
        );

        paymentAmountField.setText(
                selectedPayment.getRemainingAmount() > 0
                        ? formatPlainNumber(
                        selectedPayment
                                .getRemainingAmount()
                )
                        : ""
        );

        boolean canPay =
                !"PAID".equalsIgnoreCase(
                        selectedPayment.getStatus()
                )
                        && !"CANCELLED".equalsIgnoreCase(
                        selectedPayment.getStatus()
                );

        makePaymentButton.setEnabled(canPay);

        cancelPaymentButton.setEnabled(
                !"PAID".equalsIgnoreCase(
                        selectedPayment.getStatus()
                )
                        && !"CANCELLED".equalsIgnoreCase(
                        selectedPayment.getStatus()
                )
        );

        createPaymentButton.setEnabled(false);
    }

    /* =====================================================
       HÀM TÍNH TIỀN
       ===================================================== */

    private void calculateAmountsWithoutDiscount() {
        if (originalAmount <= 0) {
            try {
                originalAmount =
                        parsePositiveMoney(
                                originalAmountField.getText(),
                                "Học phí gốc"
                        );

            } catch (IllegalArgumentException exception) {
                originalAmount = 0;
            }
        }

        if (currentDiscount == null) {
            discountAmount = 0;
        }

        finalAmount =
                Math.max(
                        0,
                        originalAmount - discountAmount
                );

        updateAmountLabels(
                selectedPayment == null
                        ? 0
                        : selectedPayment.getPaidAmount(),
                selectedPayment == null
                        ? finalAmount
                        : selectedPayment.getRemainingAmount()
        );
    }

    private void updateAmountLabels(
            double paidAmount,
            double remainingAmount
    ) {
        discountAmountLabel.setText(
                formatMoney(discountAmount)
        );

        finalAmountLabel.setText(
                formatMoney(finalAmount)
        );

        paidAmountLabel.setText(
                formatMoney(paidAmount)
        );

        remainingAmountLabel.setText(
                formatMoney(remainingAmount)
        );
    }

    /* =====================================================
       RESET FORM
       ===================================================== */

    private void clearForm() {
        selectedPayment = null;
        currentDiscount = null;

        originalAmount = 0;
        discountAmount = 0;
        finalAmount = 0;

        paymentIdField.setText("");
        enrollmentIdField.setText("");
        studentIdField.setText("");
        originalAmountField.setText("");
        discountCodeField.setText("");
        paymentAmountField.setText("");
        transactionCodeField.setText("");
        noteField.setText("");

        paymentMethodComboBox.setSelectedIndex(0);

        discountAmountLabel.setText(
                formatMoney(0)
        );

        finalAmountLabel.setText(
                formatMoney(0)
        );

        paidAmountLabel.setText(
                formatMoney(0)
        );

        remainingAmountLabel.setText(
                formatMoney(0)
        );

        statusLabel.setText(
                "Chưa tạo"
        );

        createPaymentButton.setEnabled(true);
        makePaymentButton.setEnabled(false);
        cancelPaymentButton.setEnabled(false);

        paymentTable.clearSelection();

        enrollmentIdField.requestFocusInWindow();
    }

    /* =====================================================
       STYLE
       ===================================================== */

    private void configureTextField(
            JTextField textField,
            String placeholder
    ) {
        textField.setFont(
                UIConstants.FONT_NORMAL
        );

        textField.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        textField.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    private void configureComboBox() {
        paymentMethodComboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        paymentMethodComboBox.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    private JButton createPrimaryButton(
            String text,
            FontAwesomeSolid icon
    ) {
        return createButton(
                text,
                icon,
                UIConstants.PRIMARY,
                Color.WHITE
        );
    }

    private JButton createSecondaryButton(
            String text,
            FontAwesomeSolid icon
    ) {
        JButton button =
                createButton(
                        text,
                        icon,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 1;
                borderColor: #2563EB;
                focusWidth: 0;
                margin: 7,12,7,12;
                """
        );

        return button;
    }

    private JButton createDangerButton(
            String text,
            FontAwesomeSolid icon
    ) {
        return createButton(
                text,
                icon,
                UIConstants.DANGER,
                Color.WHITE
        );
    }

    private JButton createButton(
            String text,
            FontAwesomeSolid icon,
            Color background,
            Color foreground
    ) {
        JButton button =
                new JButton(text);

        button.setIcon(
                FontIcon.of(
                        icon,
                        13,
                        foreground
                )
        );

        button.setBackground(background);
        button.setForeground(foreground);

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 0;
                focusWidth: 0;
                margin: 7,12,7,12;
                """
        );

        return button;
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private int parsePositiveInt(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        try {
            int number =
                    Integer.parseInt(
                            value.trim()
                    );

            if (number <= 0) {
                throw new IllegalArgumentException(
                        fieldName
                                + " phải lớn hơn 0."
                );
            }

            return number;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải là số nguyên."
            );
        }
    }

    private double parsePositiveMoney(
            String value,
            String fieldName
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        try {
            String normalized =
                    value.trim()
                            .replace(" ", "")
                            .replace(",", "");

            double amount =
                    Double.parseDouble(normalized);

            if (Double.isNaN(amount)
                    || Double.isInfinite(amount)
                    || amount <= 0) {
                throw new IllegalArgumentException(
                        fieldName
                                + " phải lớn hơn 0."
                );
            }

            return amount;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải là số hợp lệ."
            );
        }
    }

    private String formatMoney(
            double amount
    ) {
        return currencyFormat.format(amount);
    }

    private String formatPlainNumber(
            double amount
    ) {
        return String.format(
                Locale.US,
                "%.0f",
                amount
        );
    }

    private String formatStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            return "";
        }

        return switch (
                status.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "PENDING" -> "Chờ thanh toán";
            case "PARTIAL" -> "Thanh toán một phần";
            case "PAID" -> "Đã thanh toán";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String formatPaymentMethod(
            String method
    ) {
        if (method == null || method.isBlank()) {
            return "";
        }

        return switch (
                method.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "CASH" -> "Tiền mặt";
            case "BANK_TRANSFER" -> "Chuyển khoản";
            case "VNPAY" -> "VNPay";
            case "MOMO" -> "MoMo";
            default -> method;
        };
    }

    private void selectPaymentMethod(
            String method
    ) {
        String formatted =
                formatPaymentMethod(method);

        for (
                int index = 0;
                index < paymentMethodComboBox
                        .getItemCount();
                index++
        ) {
            if (paymentMethodComboBox
                    .getItemAt(index)
                    .equalsIgnoreCase(formatted)) {

                paymentMethodComboBox
                        .setSelectedIndex(index);

                return;
            }
        }
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }

    private void setLoading(
            boolean loading
    ) {
        this.loading = loading;

        createPaymentButton.setEnabled(
                !loading
                        && selectedPayment == null
        );

        makePaymentButton.setEnabled(
                !loading
                        && selectedPayment != null
                        && selectedPayment
                        .getRemainingAmount() > 0
        );

        cancelPaymentButton.setEnabled(
                !loading
                        && selectedPayment != null
        );

        applyDiscountButton.setEnabled(
                !loading
        );

        manageDiscountButton.setEnabled(
                !loading
        );

        refreshButton.setEnabled(
                !loading
        );

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    private void showWarning(
            String message
    ) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(
            String message,
            Exception exception
    ) {
        String detail =
                exception == null
                        || exception.getMessage() == null
                        || exception.getMessage().isBlank()
                        ? "Không xác định"
                        : exception.getMessage();

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nChi tiết: "
                        + detail,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /* =====================================================
       RENDERER TRẠNG THÁI
       ===================================================== */

    private static final class StatusCellRenderer
            extends DefaultTableCellRenderer {

        public StatusCellRenderer() {
            setHorizontalAlignment(
                    SwingConstants.CENTER
            );
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {
            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                String text =
                        value == null
                                ? ""
                                : value.toString();

                if ("Đã thanh toán"
                        .equalsIgnoreCase(text)) {

                    setForeground(
                            UIConstants.SUCCESS
                    );

                } else if ("Đã hủy"
                        .equalsIgnoreCase(text)) {

                    setForeground(
                            UIConstants.DANGER
                    );

                } else {
                    setForeground(
                            new Color(217, 119, 6)
                    );
                }

                setBackground(Color.WHITE);
            }

            setFont(
                    UIConstants.FONT_SMALL
                            .deriveFont(Font.BOLD)
            );

            return component;
        }
    }
}
package view.student;

import controller.PaymentController;
import model.Payment;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

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
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudentPaymentView extends JPanel {

    private static final String STATUS_UNPAID =
            "UNPAID";

    private static final String STATUS_PAID =
            "PAID";

    private static final String STATUS_CANCELLED =
            "CANCELLED";

    private final int studentId;

    private final PaymentController paymentController;

    private final JLabel totalAmountLabel;
    private final JLabel totalPaidLabel;
    private final JLabel totalRemainingLabel;

    private final JTextField searchField;

    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton deleteButton;
    private final JButton paymentButton;
    private final JButton detailButton;

    private final DefaultTableModel tableModel;
    private final JTable paymentTable;

    private final JLabel selectedPaymentLabel;

    private List<Payment> currentPayments =
            Collections.emptyList();

    private Payment selectedPayment;

    private boolean loading;

    public StudentPaymentView(
            int studentId
    ) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        this.studentId =
                studentId;

        this.paymentController =
                new PaymentController();

        this.totalAmountLabel =
                createSummaryValueLabel(
                        UIConstants.PRIMARY
                );

        this.totalPaidLabel =
                createSummaryValueLabel(
                        UIConstants.SUCCESS
                );

        this.totalRemainingLabel =
                createSummaryValueLabel(
                        UIConstants.DANGER
                );

        this.searchField =
                new JTextField();

        this.searchButton =
                createPrimaryButton(
                        "Tìm kiếm",
                        FontAwesomeSolid.SEARCH
                );

        this.refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        this.deleteButton =
                createDangerButton(
                        "Xóa đăng ký",
                        FontAwesomeSolid.TRASH_ALT
                );

        this.paymentButton =
                createPrimaryButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD
                );

        this.detailButton =
                createSecondaryButton(
                        "Xem chi tiết",
                        FontAwesomeSolid.EYE
                );

        this.selectedPaymentLabel =
                new JLabel(
                        "Chưa chọn khoản thanh toán"
                );

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "Mã thanh toán",
                                "Mã đăng ký",
                                "Học phí",
                                "Đã thanh toán",
                                "Còn lại",
                                "Phương thức",
                                "Ngày thanh toán",
                                "Trạng thái"
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

        this.paymentTable =
                new JTable(
                        tableModel
                );

        initializeView();
        configureSearchField();
        configureTable();
        registerEvents();
        clearSelection();

        loadData();
    }

    /* =====================================================
       KHỞI TẠO GIAO DIỆN
       ===================================================== */

    private void initializeView() {
        setLayout(
                new BorderLayout()
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 1, insets 18",
                                "[grow,fill]",
                                "[]14[]14[]14[grow,fill]12[]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        wrapper.add(
                createSummaryPanel(),
                "growx"
        );

        wrapper.add(
                createFilterPanel(),
                "growx"
        );

        wrapper.add(
                createTableCard(),
                "grow,push"
        );

        wrapper.add(
                createSelectedPaymentCard(),
                "growx"
        );

        add(
                wrapper,
                BorderLayout.CENTER
        );
    }

    /* =====================================================
       HEADER
       ===================================================== */

    private JPanel createHeaderPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Thanh toán học phí"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Theo dõi các khoản học phí "
                                + "của khóa học đã đăng ký."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel modeLabel =
                new JLabel(
                        "Thanh toán mô phỏng"
                );

        modeLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        modeLabel.setForeground(
                UIConstants.WARNING
        );

        panel.add(
                titleLabel,
                "cell 0 0"
        );

        panel.add(
                descriptionLabel,
                "cell 0 1"
        );

        panel.add(
                modeLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    /* =====================================================
       THỐNG KÊ
       ===================================================== */

    private JPanel createSummaryPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 12",
                                "[grow,fill][grow,fill][grow,fill]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                createSummaryCard(
                        "Tổng học phí",
                        totalAmountLabel,
                        FontAwesomeSolid.FILE_INVOICE_DOLLAR,
                        UIConstants.PRIMARY
                ),
                "growx"
        );

        panel.add(
                createSummaryCard(
                        "Đã thanh toán",
                        totalPaidLabel,
                        FontAwesomeSolid.CHECK_CIRCLE,
                        UIConstants.SUCCESS
                ),
                "growx"
        );

        panel.add(
                createSummaryCard(
                        "Chưa thanh toán",
                        totalRemainingLabel,
                        FontAwesomeSolid.EXCLAMATION_CIRCLE,
                        UIConstants.DANGER
                ),
                "growx"
        );

        return panel;
    }

    private ContentCard createSummaryCard(
            String title,
            JLabel valueLabel,
            FontAwesomeSolid icon,
            Color iconColor
    ) {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 16",
                        "[grow][]",
                        "[][]"
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel iconLabel =
                new JLabel(
                        FontIcon.of(
                                icon,
                                23,
                                iconColor
                        )
                );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                valueLabel,
                "cell 0 1"
        );

        card.add(
                iconLabel,
                "cell 1 0 1 2, align center"
        );

        return card;
    }

    private JLabel createSummaryValueLabel(
            Color color
    ) {
        JLabel label =
                new JLabel(
                        formatMoney(0)
                );

        label.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(
                                Font.BOLD,
                                21f
                        )
        );

        label.setForeground(color);

        return label;
    }

    /* =====================================================
       BỘ LỌC
       ===================================================== */

    private ContentCard createFilterPanel() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 16, gapx 10",
                        "[grow,fill][][]",
                        "[]"
                )
        );

        card.add(
                searchField,
                "height 40!"
        );

        card.add(
                searchButton,
                "height 40!"
        );

        card.add(
                refreshButton,
                "height 40!"
        );

        return card;
    }

    private void configureSearchField() {
        searchField.setFont(
                UIConstants.FONT_NORMAL
        );

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo mã thanh toán, mã đăng ký, "
                        + "phương thức hoặc trạng thái"
        );

        searchField.putClientProperty(
                "JTextField.leadingIcon",
                FontIcon.of(
                        FontAwesomeSolid.SEARCH,
                        14,
                        UIConstants.TEXT_SECONDARY
                )
        );

        searchField.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    /* =====================================================
       BẢNG THANH TOÁN
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, insets 16",
                        "[grow,fill]",
                        "[grow,fill]"
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        paymentTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane
                .getViewport()
                .setBackground(
                        Color.WHITE
                );

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane
                .getHorizontalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        card.add(
                scrollPane,
                "grow,push"
        );

        return card;
    }

    private void configureTable() {
        paymentTable.setRowHeight(42);

        paymentTable.setFillsViewportHeight(
                true
        );

        paymentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        paymentTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        paymentTable.setShowVerticalLines(
                false
        );

        paymentTable.setShowHorizontalLines(
                true
        );

        paymentTable.setGridColor(
                UIConstants.BORDER
        );

        paymentTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        paymentTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        paymentTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        paymentTable
                .getTableHeader()
                .setReorderingAllowed(false);

        paymentTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 42)
                );

        paymentTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(Font.BOLD)
                );

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
                .getColumn(8)
                .setCellRenderer(
                        new PaymentStatusRenderer()
                );

        setColumnWidth(0, 55);
        setColumnWidth(1, 110);
        setColumnWidth(2, 110);
        setColumnWidth(3, 130);
        setColumnWidth(4, 130);
        setColumnWidth(5, 130);
        setColumnWidth(6, 145);
        setColumnWidth(7, 155);
        setColumnWidth(8, 135);
    }

    private void setColumnWidth(
            int columnIndex,
            int width
    ) {
        paymentTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(width);

        paymentTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setMinWidth(width);
    }

    /* =====================================================
       THẺ THANH TOÁN ĐANG CHỌN
       ===================================================== */

    private ContentCard createSelectedPaymentCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 18, gapx 10",
                        "[grow][][][]",
                        "[][]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Khoản thanh toán đang chọn"
                );

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        selectedPaymentLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        selectedPaymentLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                selectedPaymentLabel,
                "cell 0 1"
        );

        card.add(
                deleteButton,
                "cell 1 0 1 2, height 38!"
        );

        card.add(
                detailButton,
                "cell 2 0 1 2, height 38!"
        );

        card.add(
                paymentButton,
                "cell 3 0 1 2, height 38!"
        );

        return card;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        searchButton.addActionListener(
                event -> searchPayments()
        );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    loadData();
                }
        );

        searchField.addActionListener(
                event -> searchPayments()
        );

        paymentTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateSelectedPayment();
                            }
                        }
                );

        paymentTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (event.getClickCount() == 2) {
                            showPaymentDetail();
                        }
                    }
                }
        );

        deleteButton.addActionListener(
                event -> deleteSelectedEnrollment()
        );

        detailButton.addActionListener(
                event -> showPaymentDetail()
        );

        paymentButton.addActionListener(
                event -> showPaymentDialog()
        );
    }

    /* =====================================================
       TẢI DỮ LIỆU
       ===================================================== */

    public void loadData() {
        if (loading) {
            return;
        }

        setLoading(true);

        try {
            List<Payment> payments =
                    paymentController
                            .getPaymentsByStudentId(
                                    studentId
                            );

            currentPayments =
                    payments == null
                            ? Collections.emptyList()
                            : payments;

            displayPayments(
                    currentPayments
            );

            updateSummary();

        } catch (RuntimeException exception) {
            currentPayments =
                    Collections.emptyList();

            displayPayments(
                    currentPayments
            );

            setSummaryToZero();

            showError(
                    "Không thể tải danh sách thanh toán.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

    private void searchPayments() {
        String keyword =
                searchField.getText() == null
                        ? ""
                        : searchField
                        .getText()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (keyword.isBlank()) {
            displayPayments(
                    currentPayments
            );
            return;
        }

        List<Payment> filteredPayments =
                currentPayments.stream()
                        .filter(
                                payment ->
                                        payment != null
                                                && matchesKeyword(
                                                payment,
                                                keyword
                                        )
                        )
                        .toList();

        displayPayments(
                filteredPayments
        );
    }

    private boolean matchesKeyword(
            Payment payment,
            String keyword
    ) {
        String enrollmentId =
                payment.getEnrollmentId() == null
                        ? ""
                        : String.valueOf(
                        payment.getEnrollmentId()
                );

        return String.valueOf(
                        payment.getPaymentId()
                )
                .contains(keyword)

                || enrollmentId.contains(keyword)

                || normalizeText(
                payment.getPaymentMethod()
        ).contains(keyword)

                || normalizeText(
                payment.getStatus()
        ).contains(keyword)

                || formatStatus(
                payment.getStatus()
        )
                .toLowerCase(
                        Locale.ROOT
                )
                .contains(keyword);
    }

    /* =====================================================
       HIỂN THỊ DỮ LIỆU
       ===================================================== */

    private void displayPayments(
            List<Payment> payments
    ) {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (Payment payment : payments) {
            if (payment == null) {
                continue;
            }

            double totalAmount =
                    resolveAmount(
                            payment
                    );

            boolean paid =
                    isPaid(
                            payment
                    );

            double paidAmount =
                    paid
                            ? totalAmount
                            : 0;

            double remainingAmount =
                    paid
                            ? 0
                            : totalAmount;

            tableModel.addRow(
                    new Object[]{
                            sequence++,

                            payment.getPaymentId(),

                            payment.getEnrollmentId()
                                    == null
                                    ? "--"
                                    : payment
                                    .getEnrollmentId(),

                            formatMoney(
                                    totalAmount
                            ),

                            formatMoney(
                                    paidAmount
                            ),

                            formatMoney(
                                    remainingAmount
                            ),

                            formatPaymentMethod(
                                    payment
                                            .getPaymentMethod()
                            ),

                            formatDate(
                                    payment
                                            .getPaymentDate()
                            ),

                            formatStatus(
                                    payment
                                            .getStatus()
                            )
                    }
            );
        }

        clearSelection();

        paymentTable.revalidate();
        paymentTable.repaint();
    }

    /* =====================================================
       THỐNG KÊ
       ===================================================== */

    private void updateSummary() {
        double totalAmount =
                paymentController.getTotalAmount(
                        studentId
                );

        double totalPaid =
                paymentController.getPaidAmount(
                        studentId
                );

        double totalRemaining =
                paymentController.getRemainingAmount(
                        studentId
                );

        totalAmountLabel.setText(
                formatMoney(totalAmount)
        );

        totalPaidLabel.setText(
                formatMoney(totalPaid)
        );

        totalRemainingLabel.setText(
                formatMoney(totalRemaining)
        );
    }

    private void setSummaryToZero() {
        totalAmountLabel.setText(
                formatMoney(0)
        );

        totalPaidLabel.setText(
                formatMoney(0)
        );

        totalRemainingLabel.setText(
                formatMoney(0)
        );
    }

    /* =====================================================
       CHỌN THANH TOÁN
       ===================================================== */

    private void updateSelectedPayment() {
        int selectedViewRow =
                paymentTable.getSelectedRow();

        if (selectedViewRow < 0) {
            clearSelection();
            return;
        }

        int selectedModelRow =
                paymentTable
                        .convertRowIndexToModel(
                                selectedViewRow
                        );

        int paymentId =
                Integer.parseInt(
                        String.valueOf(
                                tableModel.getValueAt(
                                        selectedModelRow,
                                        1
                                )
                        )
                );

        selectedPayment =
                currentPayments.stream()
                        .filter(
                                payment ->
                                        payment != null
                                                && payment
                                                .getPaymentId()
                                                == paymentId
                        )
                        .findFirst()
                        .orElse(null);

        if (selectedPayment == null) {
            clearSelection();
            return;
        }

        selectedPaymentLabel.setText(
                "Thanh toán #"
                        + selectedPayment
                        .getPaymentId()
                        + " | Mã đăng ký "
                        + (
                        selectedPayment
                                .getEnrollmentId()
                                == null
                                ? "--"
                                : selectedPayment
                                .getEnrollmentId()
                )
                        + " | "
                        + formatStatus(
                        selectedPayment
                                .getStatus()
                )
        );

        boolean canDelete =
                !isPaid(selectedPayment)
                        && !isCancelled(selectedPayment)
                        && selectedPayment
                        .getEnrollmentId()
                        != null;

        deleteButton.setEnabled(
                canDelete
        );

        detailButton.setEnabled(
                true
        );

        paymentButton.setEnabled(
                canMakePayment(
                        selectedPayment
                )
        );
    }

    private void clearSelection() {
        selectedPayment =
                null;

        paymentTable.clearSelection();

        selectedPaymentLabel.setText(
                "Chưa chọn khoản thanh toán"
        );

        deleteButton.setEnabled(
                false
        );

        detailButton.setEnabled(
                false
        );

        paymentButton.setEnabled(
                false
        );
    }

    /* =====================================================
       XÓA ĐĂNG KÝ
       ===================================================== */

    private void deleteSelectedEnrollment() {
        if (selectedPayment == null
                || selectedPayment
                .getEnrollmentId()
                == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn môn cần xóa.",
                    "Chưa chọn môn",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (isPaid(selectedPayment)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa môn đã thanh toán.",
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (isCancelled(selectedPayment)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Khoản thanh toán đã bị hủy.",
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Integer enrollmentId =
                selectedPayment
                        .getEnrollmentId();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn hủy đăng ký môn này?\n\n"
                                + "Mã đăng ký: "
                                + enrollmentId
                                + "\nKhoản học phí chưa thanh toán "
                                + "sẽ bị xóa."
                                + "\nMôn học sẽ xuất hiện lại "
                                + "trong danh sách có thể đăng ký.",
                        "Xác nhận xóa đăng ký",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {
            return;
        }

        setLoading(true);

        try {
            boolean deleted =
                    paymentController
                            .removeEnrollment(
                                    studentId,
                                    enrollmentId
                            );

            if (!deleted) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể xóa đăng ký khóa học.",
                        "Thất bại",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã xóa đăng ký khóa học.\n"
                            + "Môn học đã trở lại danh sách "
                            + "có thể đăng ký.",
                    "Xóa thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            selectedPayment = null;

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Không thể xóa",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể xóa đăng ký khóa học.",
                    exception
            );

        } finally {
            setLoading(false);
        }

        loadData();
    }

    /* =====================================================
       THANH TOÁN
       ===================================================== */

    private void showPaymentDialog() {
        if (selectedPayment == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn khoản học phí cần thanh toán.",
                    "Chưa chọn khoản thanh toán",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!canMakePayment(
                selectedPayment
        )) {
            JOptionPane.showMessageDialog(
                    this,
                    "Khoản học phí này đã được thanh toán "
                            + "hoặc không thể tiếp tục xử lý.",
                    "Không thể thanh toán",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double requiredAmount =
                resolveAmount(
                        selectedPayment
                );

        JTextField amountField =
                new JTextField(
                        formatNumberOnly(
                                requiredAmount
                        )
                );

        amountField.setEditable(
                false
        );

        JComboBox<String> paymentMethodComboBox =
                new JComboBox<>(
                        new String[]{
                                "Chuyển khoản",
                                "Thẻ",
                                "Ví điện tử",
                                "Tiền mặt"
                        }
                );

        JPanel formPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 10",
                                "[right]12[grow,fill]",
                                "[]10[]10[]"
                        )
                );

        formPanel.add(
                new JLabel(
                        "Số tiền học phí:"
                )
        );

        formPanel.add(
                new JLabel(
                        formatMoney(
                                requiredAmount
                        )
                )
        );

        formPanel.add(
                new JLabel(
                        "Số tiền thanh toán:"
                )
        );

        formPanel.add(
                amountField
        );

        formPanel.add(
                new JLabel(
                        "Phương thức:"
                )
        );

        formPanel.add(
                paymentMethodComboBox
        );

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        formPanel,
                        "Xác nhận thanh toán",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (answer != JOptionPane.OK_OPTION) {
            return;
        }

        String selectedMethod =
                String.valueOf(
                        paymentMethodComboBox
                                .getSelectedItem()
                );

        String paymentMethod =
                convertMethodToCode(
                        selectedMethod
                );

        String transactionCode =
                paymentController
                        .generateTransactionCode();

        int confirmation =
                JOptionPane.showConfirmDialog(
                        this,
                        "Xác nhận thanh toán "
                                + formatMoney(
                                requiredAmount
                        )
                                + "?\n\n"
                                + "Phương thức: "
                                + selectedMethod
                                + "\nMã giao dịch: "
                                + transactionCode
                                + "\n\n"
                                + "Đây là thanh toán mô phỏng.",
                        "Xác nhận giao dịch",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (confirmation
                != JOptionPane.YES_OPTION) {
            return;
        }

        setLoading(true);

        try {
            boolean successful =
                    paymentController
                            .confirmPayment(
                                    selectedPayment
                                            .getPaymentId(),
                                    requiredAmount,
                                    paymentMethod,
                                    transactionCode
                            );

            if (!successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Thanh toán không thành công.",
                        "Thất bại",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Thanh toán thành công.\n"
                            + "Mã giao dịch: "
                            + transactionCode,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Không thể thanh toán",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể thực hiện thanh toán.",
                    exception
            );

        } finally {
            setLoading(false);
        }

        loadData();
    }

    /* =====================================================
       CHI TIẾT THANH TOÁN
       ===================================================== */

    private void showPaymentDetail() {
        if (selectedPayment == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một khoản thanh toán.",
                    "Chưa chọn khoản thanh toán",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        double totalAmount =
                resolveAmount(
                        selectedPayment
                );

        double paidAmount =
                isPaid(
                        selectedPayment
                )
                        ? totalAmount
                        : 0;

        double remainingAmount =
                isPaid(
                        selectedPayment
                )
                        ? 0
                        : totalAmount;

        JPanel detailPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 8",
                                "[right]12[grow,fill]",
                                "[]8[]8[]8[]8[]8[]8[]8[]8[]"
                        )
                );

        addDetailRow(
                detailPanel,
                "Mã thanh toán:",
                String.valueOf(
                        selectedPayment
                                .getPaymentId()
                )
        );

        addDetailRow(
                detailPanel,
                "Mã đăng ký:",
                selectedPayment
                        .getEnrollmentId()
                        == null
                        ? "--"
                        : String.valueOf(
                        selectedPayment
                                .getEnrollmentId()
                )
        );

        addDetailRow(
                detailPanel,
                "Học phí:",
                formatMoney(
                        totalAmount
                )
        );

        addDetailRow(
                detailPanel,
                "Đã thanh toán:",
                formatMoney(
                        paidAmount
                )
        );

        addDetailRow(
                detailPanel,
                "Còn lại:",
                formatMoney(
                        remainingAmount
                )
        );

        addDetailRow(
                detailPanel,
                "Phương thức:",
                formatPaymentMethod(
                        selectedPayment
                                .getPaymentMethod()
                )
        );

        addDetailRow(
                detailPanel,
                "Ngày thanh toán:",
                formatDate(
                        selectedPayment
                                .getPaymentDate()
                )
        );

        addDetailRow(
                detailPanel,
                "Trạng thái:",
                formatStatus(
                        selectedPayment
                                .getStatus()
                )
        );

        addDetailRow(
                detailPanel,
                "Ghi chú:",
                safeText(
                        selectedPayment
                                .getNote()
                )
        );

        JOptionPane.showMessageDialog(
                this,
                detailPanel,
                "Chi tiết thanh toán",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void addDetailRow(
            JPanel panel,
            String label,
            String value
    ) {
        JLabel labelComponent =
                new JLabel(label);

        labelComponent.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        JLabel valueComponent =
                new JLabel(value);

        valueComponent.setFont(
                UIConstants.FONT_NORMAL
        );

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    /* =====================================================
       BUTTON
       ===================================================== */

    private JButton createPrimaryButton(
            String text,
            FontAwesomeSolid icon
    ) {
        JButton button =
                new JButton(text);

        button.setIcon(
                FontIcon.of(
                        icon,
                        14,
                        Color.WHITE
                )
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setBackground(
                UIConstants.PRIMARY
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFocusable(false);
        button.setBorderPainted(false);

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
                margin: 7,13,7,13;
                """
        );

        return button;
    }

    private JButton createSecondaryButton(
            String text,
            FontAwesomeSolid icon
    ) {
        JButton button =
                new JButton(text);

        button.setIcon(
                FontIcon.of(
                        icon,
                        14,
                        UIConstants.PRIMARY
                )
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setBackground(
                Color.WHITE
        );

        button.setForeground(
                UIConstants.PRIMARY
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
                borderWidth: 1;
                borderColor: #2563EB;
                focusWidth: 0;
                margin: 7,13,7,13;
                """
        );

        return button;
    }

    private JButton createDangerButton(
            String text,
            FontAwesomeSolid icon
    ) {
        JButton button =
                new JButton(text);

        button.setIcon(
                FontIcon.of(
                        icon,
                        14,
                        Color.WHITE
                )
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setBackground(
                UIConstants.DANGER
        );

        button.setForeground(
                Color.WHITE
        );

        button.setFocusable(false);
        button.setBorderPainted(false);

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
                margin: 7,13,7,13;
                """
        );

        return button;
    }

    /* =====================================================
       LOADING
       ===================================================== */

    private void setLoading(
            boolean loading
    ) {
        this.loading =
                loading;

        searchField.setEnabled(
                !loading
        );

        searchButton.setEnabled(
                !loading
        );

        refreshButton.setEnabled(
                !loading
        );

        paymentTable.setEnabled(
                !loading
        );

        if (loading) {
            deleteButton.setEnabled(
                    false
            );

            detailButton.setEnabled(
                    false
            );

            paymentButton.setEnabled(
                    false
            );

        } else {
            deleteButton.setEnabled(
                    selectedPayment != null
                            && !isPaid(selectedPayment)
                            && !isCancelled(selectedPayment)
                            && selectedPayment
                            .getEnrollmentId()
                            != null
            );

            detailButton.setEnabled(
                    selectedPayment != null
            );

            paymentButton.setEnabled(
                    selectedPayment != null
                            && canMakePayment(
                            selectedPayment
                    )
            );
        }

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    /* =====================================================
       TRẠNG THÁI
       ===================================================== */

    private boolean canMakePayment(
            Payment payment
    ) {
        if (payment == null) {
            return false;
        }

        String status =
                normalizeStatus(
                        payment.getStatus()
                );

        return STATUS_UNPAID.equals(
                status
        )
                && resolveAmount(
                payment
        ) > 0;
    }

    private boolean isPaid(
            Payment payment
    ) {
        return payment != null
                && STATUS_PAID.equals(
                normalizeStatus(
                        payment.getStatus()
                )
        );
    }

    private boolean isCancelled(
            Payment payment
    ) {
        return payment != null
                && STATUS_CANCELLED.equals(
                normalizeStatus(
                        payment.getStatus()
                )
        );
    }

    private String normalizeStatus(
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

    private String formatStatus(
            String status
    ) {
        return switch (
                normalizeStatus(
                        status
                )
                ) {
            case STATUS_UNPAID ->
                    "Chưa thanh toán";

            case STATUS_PAID ->
                    "Đã thanh toán";

            case STATUS_CANCELLED ->
                    "Đã hủy";

            default ->
                    safeText(status);
        };
    }

    /* =====================================================
       FORMAT
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

    private String formatMoney(
            double amount
    ) {
        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale(
                                "vi",
                                "VN"
                        )
                );

        return formatter.format(
                amount
        );
    }

    private String formatNumberOnly(
            double amount
    ) {
        return String.format(
                Locale.US,
                "%.0f",
                amount
        );
    }

    private String formatDate(
            java.util.Date date
    ) {
        if (date == null) {
            return "--";
        }

        return new SimpleDateFormat(
                "dd/MM/yyyy HH:mm"
        ).format(date);
    }

    private String formatPaymentMethod(
            String method
    ) {
        if (method == null
                || method.isBlank()) {

            return "--";
        }

        return switch (
                method.trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
                ) {
            case "CASH" ->
                    "Tiền mặt";

            case "BANK_TRANSFER" ->
                    "Chuyển khoản";

            case "CARD" ->
                    "Thẻ";

            case "E_WALLET" ->
                    "Ví điện tử";

            default ->
                    method.trim();
        };
    }

    private String convertMethodToCode(
            String method
    ) {
        return switch (method) {
            case "Tiền mặt" ->
                    "CASH";

            case "Chuyển khoản" ->
                    "BANK_TRANSFER";

            case "Thẻ" ->
                    "CARD";

            case "Ví điện tử" ->
                    "E_WALLET";

            default ->
                    throw new IllegalArgumentException(
                            "Phương thức thanh toán không hợp lệ."
                    );
        };
    }

    private String normalizeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim()
                .toLowerCase(
                        Locale.ROOT
                );
    }

    private String safeText(
            String value
    ) {
        return value == null
                || value.isBlank()
                ? "--"
                : value.trim();
    }

    /* =====================================================
       THÔNG BÁO
       ===================================================== */

    private void showError(
            String message,
            Throwable throwable
    ) {
        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nChi tiết: "
                        + getRootErrorMessage(
                        throwable
                ),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private String getRootErrorMessage(
            Throwable throwable
    ) {
        if (throwable == null) {
            return "Không xác định";
        }

        Throwable current =
                throwable;

        while (current.getCause() != null) {
            current =
                    current.getCause();
        }

        if (current.getMessage() != null
                && !current.getMessage().isBlank()) {

            return current.getMessage();
        }

        return current
                .getClass()
                .getSimpleName();
    }

    /* =====================================================
       RENDER TRẠNG THÁI
       ===================================================== */

    private static class PaymentStatusRenderer
            extends DefaultTableCellRenderer {

        public PaymentStatusRenderer() {
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
                setBackground(
                        Color.WHITE
                );

                String text =
                        value == null
                                ? ""
                                : value.toString();

                if ("Đã thanh toán".equals(text)) {
                    setForeground(
                            UIConstants.SUCCESS
                    );

                } else if ("Đã hủy".equals(text)) {
                    setForeground(
                            UIConstants.DANGER
                    );

                } else {
                    setForeground(
                            UIConstants.WARNING
                    );
                }
            }

            setFont(
                    UIConstants.FONT_SMALL
                            .deriveFont(
                                    Font.BOLD
                            )
            );

            return component;
        }
    }

    /* =====================================================
       GETTER
       ===================================================== */

    public int getStudentId() {
        return studentId;
    }

    public Payment getSelectedPayment() {
        return selectedPayment;
    }
}
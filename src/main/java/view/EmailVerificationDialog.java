package view;

import controller.TeacherController;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class EmailVerificationDialog extends JDialog {

    private final JLabel lblDescription;
    private final JLabel lblMessage;
    private final JTextField txtOtp;

    private final JButton btnVerify;
    private final JButton btnResend;
    private final JButton btnCancel;

    public EmailVerificationDialog(
            Window owner,
            String email
    ) {
        super(
                owner,
                "Xác nhận email",
                ModalityType.APPLICATION_MODAL
        );

        lblDescription = new JLabel();
        lblMessage = new JLabel(" ");

        txtOtp = new JTextField();

        btnVerify = new JButton("Xác nhận");
        btnResend = new JButton("Gửi lại mã");
        btnCancel = new JButton("Đóng");

        initializeFrame();
        initializeView(email);
    }

    private void initializeFrame() {
        setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        setResizable(false);

        setMinimumSize(
                new Dimension(500, 390)
        );
    }

    private void initializeView(String email) {
        JPanel rootPanel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 28 34 30 34",
                        "[grow, fill]",
                        "[]12[]24[]8[]18[]"
                )
        );

        rootPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("✉");
        iconLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        iconLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        42
                )
        );

        iconLabel.setForeground(
                new Color(37, 99, 235)
        );

        JLabel titleLabel =
                new JLabel("Xác nhận địa chỉ email");

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        22
                )
        );

        titleLabel.setForeground(
                new Color(17, 24, 39)
        );

        lblDescription.setText(
                createDescription(email)
        );

        lblDescription.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblDescription.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        lblDescription.setForeground(
                new Color(75, 85, 99)
        );

        txtOtp.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        txtOtp.setFont(
                new Font(
                        "Monospaced",
                        Font.BOLD,
                        25
                )
        );

        txtOtp.setToolTipText(
                "Nhập mã OTP gồm 6 chữ số"
        );

        txtOtp.setPreferredSize(
                new Dimension(300, 54)
        );

        txtOtp.putClientProperty(
                "JTextField.placeholderText",
                "000000"
        );

        txtOtp.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                margin: 8,14,8,14;
                """
        );

        configureOtpDocument();

        lblMessage.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        lblMessage.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        JPanel buttonPanel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill][grow, fill]",
                        "[]8[]"
                )
        );

        buttonPanel.setOpaque(false);

        configureButton(
                btnCancel,
                false
        );

        configureButton(
                btnVerify,
                true
        );

        configureButton(
                btnResend,
                false
        );

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnVerify);

        buttonPanel.add(
                btnResend,
                "span 2, growx"
        );

        rootPanel.add(
                iconLabel,
                "alignx center"
        );

        rootPanel.add(
                titleLabel,
                "alignx center"
        );

        rootPanel.add(
                lblDescription,
                "growx"
        );

        rootPanel.add(
                txtOtp,
                "width 280!, alignx center"
        );

        rootPanel.add(
                lblMessage,
                "growx"
        );

        rootPanel.add(
                buttonPanel,
                "growx"
        );

        setContentPane(rootPanel);

        getRootPane().setDefaultButton(
                btnVerify
        );

        pack();
        setLocationRelativeTo(getOwner());

        SwingUtilities.invokeLater(
                () -> txtOtp.requestFocusInWindow()
        );
    }

    private void configureButton(
            JButton button,
            boolean primary
    ) {
        button.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        13
                )
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
                arc: 11;
                focusWidth: 0;
                """
        );

        if (primary) {
            button.setForeground(Color.WHITE);
            button.setBackground(
                    new Color(37, 99, 235)
            );
        }
    }

    private void configureOtpDocument() {
        if (txtOtp.getDocument()
                instanceof AbstractDocument document) {

            document.setDocumentFilter(
                    new OtpDocumentFilter()
            );
        }
    }

    private String createDescription(
            String email
    ) {
        return "<html><div style='text-align:center'>"
                + "Hệ thống đã gửi mã OTP gồm 6 chữ số đến<br>"
                + "<b>"
                + escapeHtml(maskEmail(email))
                + "</b>."
                + "<br>Mã có hiệu lực trong 10 phút."
                + "</div></html>";
    }

    public String getOtp() {
        return txtOtp
                .getText()
                .trim();
    }

    public JButton getBtnVerify() {
        return btnVerify;
    }

    public JButton getBtnResend() {
        return btnResend;
    }

    public JButton getBtnCancel() {
        return btnCancel;
    }

    public void clearOtp() {
        txtOtp.setText("");
        txtOtp.requestFocusInWindow();
    }

    public void setLoading(boolean loading) {
        txtOtp.setEnabled(!loading);
        btnVerify.setEnabled(!loading);
        btnResend.setEnabled(!loading);
        btnCancel.setEnabled(!loading);

        btnVerify.setText(
                loading
                        ? "Đang xác nhận..."
                        : "Xác nhận"
        );

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    public void setMessage(
            String message,
            boolean success
    ) {
        lblMessage.setText(
                message == null || message.isBlank()
                        ? " "
                        : "<html><div style='text-align:center'>"
                        + escapeHtml(message)
                        + "</div></html>"
        );

        lblMessage.setForeground(
                success
                        ? new Color(21, 128, 61)
                        : new Color(185, 28, 28)
        );
    }

    private String maskEmail(String email) {
        if (email == null
                || !email.contains("@")) {

            return "***";
        }

        String[] parts =
                email.split("@", 2);

        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0)
                    + "***@"
                    + domain;
        }

        return localPart.substring(0, 2)
                + "***@"
                + domain;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static final class OtpDocumentFilter
            extends DocumentFilter {

        private static final int MAX_LENGTH = 6;

        @Override
        public void insertString(
                FilterBypass filterBypass,
                int offset,
                String text,
                AttributeSet attributeSet
        ) throws BadLocationException {

            replace(
                    filterBypass,
                    offset,
                    0,
                    text,
                    attributeSet
            );
        }

        @Override
        public void replace(
                FilterBypass filterBypass,
                int offset,
                int length,
                String text,
                AttributeSet attributeSet
        ) throws BadLocationException {

            String replacement =
                    text == null
                            ? ""
                            : text.replaceAll("\\D", "");

            int currentLength =
                    filterBypass.getDocument()
                            .getLength();

            int newLength =
                    currentLength
                            - length
                            + replacement.length();

            if (newLength <= MAX_LENGTH) {
                filterBypass.replace(
                        offset,
                        length,
                        replacement,
                        attributeSet
                );
            }
        }
    }

    public static class TeacherProfileView extends JPanel {
        private JTextField txtCode, txtName, txtEmail, txtPhone, txtSpecialization;
        private JButton btnUpdate;
        private final TeacherController teacherController = new TeacherController();

        public TeacherProfileView() {
            setLayout(new MigLayout("fill, insets 15", "[320pt][grow]", "[][grow]"));

            JLabel lblTitle = new JLabel("HỒ SƠ CÁ NHÂN GIẢNG VIÊN");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
            add(lblTitle, "span 2, wrap 15");

            JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
            formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chi tiết"));

            formPanel.add(new JLabel("Mã Giảng Viên:"));
            txtCode = new JTextField();
            txtCode.setEditable(false);
            formPanel.add(txtCode);

            formPanel.add(new JLabel("Họ và Tên:"));
            txtName = new JTextField();
            formPanel.add(txtName);

            formPanel.add(new JLabel("Email:"));
            txtEmail = new JTextField();
            formPanel.add(txtEmail);

            formPanel.add(new JLabel("Số Điện Thoại:"));
            txtPhone = new JTextField();
            formPanel.add(txtPhone);

            formPanel.add(new JLabel("Chuyên Môn:"));
            txtSpecialization = new JTextField();
            formPanel.add(txtSpecialization);

            btnUpdate = new JButton("Cập Nhật Thông Tin");
            btnUpdate.putClientProperty("JButton.buttonType", "accent");
            formPanel.add(btnUpdate, "span 2, growx, gaptop 15");

            add(formPanel, "top");

            btnUpdate.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, "Tính năng cập nhật thông tin cá nhân đang được phát triển!");
            });
        }
    }
}
package view;

import model.Role;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JFrame {

    private final JComboBox<Role> cboRole;

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JPasswordField txtConfirmPassword;

    private final JTextField txtCode;
    private final JTextField txtFullName;
    private final JTextField txtDateOfBirth;
    private final JComboBox<String> cboGender;
    private final JTextField txtEmail;
    private final JTextField txtPhone;
    private final JTextField txtAddress;

    private final JLabel lblSpecialization;
    private final JTextField txtSpecialization;

    private final JLabel lblCode;
    private final JLabel lblStatusMessage;

    private final JButton btnRegister;
    private final JButton btnBack;

    public RegisterView() {
        cboRole = new JComboBox<>(
                new Role[]{
                        Role.STUDENT,
                        Role.TEACHER
                }
        );

        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirmPassword = new JPasswordField();

        txtCode = new JTextField();
        txtFullName = new JTextField();
        txtDateOfBirth = new JTextField();

        cboGender = new JComboBox<>(
                new String[]{
                        "MALE",
                        "FEMALE",
                        "OTHER"
                }
        );

        txtEmail = new JTextField();
        txtPhone = new JTextField();
        txtAddress = new JTextField();

        lblSpecialization =
                new JLabel("Chuyên môn:");

        txtSpecialization =
                new JTextField();

        lblCode =
                new JLabel("Mã sinh viên:");

        lblStatusMessage =
                new JLabel(" ");

        btnRegister =
                new JButton("Đăng ký");

        btnBack =
                new JButton("Quay lại");

        initializeFrame();
        initializeView();
        registerInternalEvents();
        updateRoleForm();
    }

    private void initializeFrame() {
        setTitle("Đăng ký tài khoản");
        setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(720, 690)
        );

        setPreferredSize(
                new Dimension(800, 760)
        );

        setLocationRelativeTo(null);
    }

    private void initializeView() {
        JPanel rootPanel = new JPanel(
                new MigLayout(
                        "fill, insets 24",
                        "[grow, center]",
                        "[grow, center]"
                )
        );

        rootPanel.setBackground(
                new Color(245, 247, 250)
        );

        JPanel cardPanel = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 28 34 30 34",
                        "[160!, right]16[grow, fill]",
                        "[]8[]18[]10[]10[]10[]10[]10[]10[]10[]10[]10[]18[]8[]"
                )
        );

        cardPanel.setBackground(Color.WHITE);

        cardPanel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 20;
                border: 1,1,1,1,#E5E7EB;
                """
        );

        JLabel titleLabel =
                new JLabel("ĐĂNG KÝ TÀI KHOẢN");

        titleLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        24
                )
        );

        titleLabel.setForeground(
                new Color(31, 41, 55)
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Tạo tài khoản sinh viên hoặc giảng viên"
                );

        descriptionLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        14
                )
        );

        descriptionLabel.setForeground(
                new Color(107, 114, 128)
        );

        cardPanel.add(
                titleLabel,
                "span 2, alignx center"
        );

        cardPanel.add(
                descriptionLabel,
                "span 2, alignx center"
        );

        addSectionTitle(
                cardPanel,
                "Thông tin tài khoản"
        );

        addField(
                cardPanel,
                "Đăng ký với vai trò:",
                cboRole
        );

        addField(
                cardPanel,
                "Tên đăng nhập:",
                txtUsername
        );

        addField(
                cardPanel,
                "Mật khẩu:",
                txtPassword
        );

        addField(
                cardPanel,
                "Xác nhận mật khẩu:",
                txtConfirmPassword
        );

        addSectionTitle(
                cardPanel,
                "Thông tin hồ sơ"
        );

        cardPanel.add(lblCode);
        cardPanel.add(
                txtCode,
                "growx"
        );

        addField(
                cardPanel,
                "Họ và tên:",
                txtFullName
        );

        addField(
                cardPanel,
                "Ngày sinh:",
                txtDateOfBirth
        );

        addField(
                cardPanel,
                "Giới tính:",
                cboGender
        );

        addField(
                cardPanel,
                "Email:",
                txtEmail
        );

        addField(
                cardPanel,
                "Số điện thoại:",
                txtPhone
        );

        addField(
                cardPanel,
                "Địa chỉ:",
                txtAddress
        );

        cardPanel.add(lblSpecialization);

        cardPanel.add(
                txtSpecialization,
                "growx"
        );

        JLabel dateHintLabel =
                new JLabel(
                        "Ngày sinh nhập theo định dạng yyyy-MM-dd"
                );

        dateHintLabel.setFont(
                new Font(
                        "SansSerif",
                        Font.ITALIC,
                        12
                )
        );

        dateHintLabel.setForeground(
                new Color(107, 114, 128)
        );

        cardPanel.add(
                dateHintLabel,
                "cell 1 11, gaptop 2"
        );

        lblStatusMessage.setFont(
                new Font(
                        "SansSerif",
                        Font.PLAIN,
                        13
                )
        );

        lblStatusMessage.setForeground(
                new Color(185, 28, 28)
        );

        cardPanel.add(
                lblStatusMessage,
                "span 2, growx"
        );

        JPanel buttonPanel =
                new JPanel(
                        new MigLayout(
                                "insets 0, fillx",
                                "[grow, fill][grow, fill]",
                                "[]"
                        )
                );

        buttonPanel.setOpaque(false);

        configureButtons();

        buttonPanel.add(btnBack);
        buttonPanel.add(btnRegister);

        cardPanel.add(
                buttonPanel,
                "span 2, growx, gaptop 6"
        );

        rootPanel.add(
                new JScrollPane(
                        cardPanel,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                ),
                "grow, width 700:760:820, height 600:680:740"
        );

        JScrollPane scrollPane =
                (JScrollPane) rootPanel.getComponent(0);

        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(
                new Color(245, 247, 250)
        );

        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);
    }

    private void configureButtons() {
        btnRegister.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(
                new Color(37, 99, 235)
        );

        btnRegister.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btnRegister.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                borderWidth: 0;
                focusWidth: 0;
                """
        );

        btnBack.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        14
                )
        );

        btnBack.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btnBack.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                focusWidth: 0;
                """
        );
    }

    private void addSectionTitle(
            JPanel panel,
            String title
    ) {
        JLabel label =
                new JLabel(title);

        label.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        16
                )
        );

        label.setForeground(
                new Color(37, 99, 235)
        );

        panel.add(
                label,
                "span 2, growx, gaptop 8"
        );
    }

    private void addField(
            JPanel panel,
            String labelText,
            JComponent component
    ) {
        panel.add(
                new JLabel(labelText)
        );

        panel.add(
                component,
                "growx"
        );
    }

    private void registerInternalEvents() {
        cboRole.addActionListener(
                event -> updateRoleForm()
        );
    }

    private void updateRoleForm() {
        Role selectedRole =
                getSelectedRole();

        boolean teacherSelected =
                selectedRole == Role.TEACHER;

        if (teacherSelected) {
            lblCode.setText("Mã giảng viên:");
        } else {
            lblCode.setText("Mã sinh viên:");
        }

        lblSpecialization.setVisible(
                teacherSelected
        );

        txtSpecialization.setVisible(
                teacherSelected
        );

        if (teacherSelected) {
            setStatusMessage(
                    "Tài khoản giảng viên cần xác minh email "
                            + "và chờ Admin xét duyệt.",
                    false
            );
        } else {
            clearStatusMessage();
        }

        revalidate();
        repaint();
    }

    public Role getSelectedRole() {
        Object selected =
                cboRole.getSelectedItem();

        return selected instanceof Role role
                ? role
                : Role.STUDENT;
    }

    public String getUsername() {
        return txtUsername
                .getText()
                .trim();
    }

    public char[] getPassword() {
        return txtPassword.getPassword();
    }

    public char[] getConfirmPassword() {
        return txtConfirmPassword.getPassword();
    }

    public String getProfileCode() {
        return txtCode
                .getText()
                .trim();
    }

    public String getFullName() {
        return txtFullName
                .getText()
                .trim();
    }

    public String getDateOfBirth() {
        return txtDateOfBirth
                .getText()
                .trim();
    }

    public String getGender() {
        Object selected =
                cboGender.getSelectedItem();

        return selected == null
                ? null
                : selected.toString();
    }

    public String getEmail() {
        return txtEmail
                .getText()
                .trim();
    }

    public String getPhone() {
        return txtPhone
                .getText()
                .trim();
    }

    public String getAddress() {
        return txtAddress
                .getText()
                .trim();
    }

    public String getSpecialization() {
        return txtSpecialization
                .getText()
                .trim();
    }

    public JButton getBtnRegister() {
        return btnRegister;
    }

    public JButton getBtnBack() {
        return btnBack;
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public void setLoading(boolean loading) {
        cboRole.setEnabled(!loading);
        txtUsername.setEnabled(!loading);
        txtPassword.setEnabled(!loading);
        txtConfirmPassword.setEnabled(!loading);
        txtCode.setEnabled(!loading);
        txtFullName.setEnabled(!loading);
        txtDateOfBirth.setEnabled(!loading);
        cboGender.setEnabled(!loading);
        txtEmail.setEnabled(!loading);
        txtPhone.setEnabled(!loading);
        txtAddress.setEnabled(!loading);
        txtSpecialization.setEnabled(!loading);

        btnRegister.setEnabled(!loading);
        btnBack.setEnabled(!loading);

        btnRegister.setText(
                loading
                        ? "Đang đăng ký..."
                        : "Đăng ký"
        );

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    public void setStatusMessage(
            String message,
            boolean success
    ) {
        lblStatusMessage.setText(
                message == null || message.isBlank()
                        ? " "
                        : message
        );

        lblStatusMessage.setForeground(
                success
                        ? new Color(21, 128, 61)
                        : new Color(185, 28, 28)
        );
    }

    public void clearStatusMessage() {
        lblStatusMessage.setText(" ");
    }

    public void clearPasswordFields() {
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtCode.setText("");
        txtFullName.setText("");
        txtDateOfBirth.setText("");
        cboGender.setSelectedIndex(0);
        txtEmail.setText("");
        txtPhone.setText("");
        txtAddress.setText("");
        txtSpecialization.setText("");

        cboRole.setSelectedItem(
                Role.STUDENT
        );

        clearStatusMessage();
        txtUsername.requestFocusInWindow();
    }
}
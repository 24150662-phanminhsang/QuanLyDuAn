package view.dialog;

import model.AccountStatus;
import model.Role;
import model.User;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;

public class UserFormDialog extends JDialog {

    private static final int DIALOG_WIDTH = 650;
    private static final int CREATE_DIALOG_HEIGHT = 650;
    private static final int EDIT_DIALOG_HEIGHT = 590;

    private static final int FIELD_HEIGHT = 40;

    private final boolean createMode;

    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField fullNameField;
    private final JTextField emailField;
    private final JTextField phoneField;

    private final JComboBox<Role> roleComboBox;
    private final JComboBox<AccountStatus> statusComboBox;

    private boolean confirmed;

    public UserFormDialog(
            Window owner,
            User existingUser
    ) {
        super(owner);

        createMode = existingUser == null;

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        fullNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();

        roleComboBox = new JComboBox<>(Role.values());
        statusComboBox = new JComboBox<>(AccountStatus.values());

        initializeDialog();

        if (existingUser != null) {
            fillExistingUser(existingUser);
        }
    }

    private void initializeDialog() {
        setTitle(
                createMode
                        ? "Thêm tài khoản"
                        : "Cập nhật tài khoản"
        );

        setModal(true);
        setResizable(true);

        setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        JPanel rootPanel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]0[grow, fill]0[]"
                )
        );

        rootPanel.setBackground(Color.WHITE);

        rootPanel.add(
                createHeaderPanel(),
                "growx"
        );

        rootPanel.add(
                createContentPanel(),
                "grow, push"
        );

        rootPanel.add(
                createFooterPanel(),
                "growx"
        );

        setContentPane(rootPanel);

        applyResponsiveDialogSize();

        setLocationRelativeTo(getOwner());
    }

    private void applyResponsiveDialogSize() {
        Dimension screenSize =
                Toolkit.getDefaultToolkit()
                        .getScreenSize();

        int expectedHeight =
                createMode
                        ? CREATE_DIALOG_HEIGHT
                        : EDIT_DIALOG_HEIGHT;

        int maximumWidth =
                Math.max(
                        480,
                        screenSize.width - 100
                );

        int maximumHeight =
                Math.max(
                        420,
                        screenSize.height - 100
                );

        int width =
                Math.min(
                        DIALOG_WIDTH,
                        maximumWidth
                );

        int height =
                Math.min(
                        expectedHeight,
                        maximumHeight
                );

        setMinimumSize(
                new Dimension(
                        Math.min(560, width),
                        Math.min(520, height)
                )
        );

        pack();

        /*
         * Bảo đảm phần footer chứa nút Hủy/Lưu thay đổi
         * luôn hiển thị đầy đủ, kể cả khi font hoặc DPI lớn.
         */
        int packedWidth = Math.max(
                getWidth(),
                width
        );

        int packedHeight = Math.max(
                getHeight(),
                height
        );

        setSize(
                Math.min(packedWidth, maximumWidth),
                Math.min(packedHeight, maximumHeight)
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 22 28 18 28",
                        "[grow, fill]",
                        "[]5[]"
                )
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        UIConstants.BORDER
                )
        );

        JLabel titleLabel = new JLabel(
                createMode
                        ? "Tạo tài khoản mới"
                        : "Chỉnh sửa tài khoản"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                createMode
                        ? "Nhập đầy đủ thông tin để tạo tài khoản mới."
                        : "Cập nhật thông tin và quyền của tài khoản."
        );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 22 34 18 34",
                        "[right, 145!]"
                                + "18"
                                + "[grow, fill]",
                        createMode
                                ? "[]12[]12[]12[]12[]12[]"
                                : "[]12[]12[]12[]12[]12[]"
                )
        );

        contentPanel.setBackground(Color.WHITE);

        configureTextField(usernameField);
        configureTextField(passwordField);
        configureTextField(fullNameField);
        configureTextField(emailField);
        configureTextField(phoneField);

        configureComboBox(roleComboBox);
        configureComboBox(statusComboBox);

        contentPanel.add(
                createFieldLabel("Tên đăng nhập")
        );

        contentPanel.add(
                usernameField,
                "growx, height " + FIELD_HEIGHT + "!"
        );

        if (createMode) {
            contentPanel.add(
                    createFieldLabel("Mật khẩu")
            );

            contentPanel.add(
                    passwordField,
                    "growx, height " + FIELD_HEIGHT + "!"
            );
        }

        contentPanel.add(
                createFieldLabel("Họ và tên")
        );

        contentPanel.add(
                fullNameField,
                "growx, height " + FIELD_HEIGHT + "!"
        );

        contentPanel.add(
                createFieldLabel("Email")
        );

        contentPanel.add(
                emailField,
                "growx, height " + FIELD_HEIGHT + "!"
        );

        contentPanel.add(
                createFieldLabel("Số điện thoại")
        );

        contentPanel.add(
                phoneField,
                "growx, height " + FIELD_HEIGHT + "!"
        );

        contentPanel.add(
                createFieldLabel("Vai trò")
        );

        contentPanel.add(
                roleComboBox,
                "growx, height " + FIELD_HEIGHT + "!"
        );

        if (!createMode) {
            contentPanel.add(
                    createFieldLabel("Trạng thái")
            );

            contentPanel.add(
                    statusComboBox,
                    "growx, height " + FIELD_HEIGHT + "!"
            );
        }

        return contentPanel;
    }

    private void configureTextField(
            JTextField textField
    ) {
        textField.setFont(
                UIConstants.FONT_NORMAL
        );

        textField.setMinimumSize(
                new Dimension(280, FIELD_HEIGHT)
        );

        textField.setPreferredSize(
                new Dimension(360, FIELD_HEIGHT)
        );

        textField.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,11,7,11;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    private void configureComboBox(
            JComboBox<?> comboBox
    ) {
        comboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        comboBox.setMinimumSize(
                new Dimension(280, FIELD_HEIGHT)
        );

        comboBox.setPreferredSize(
                new Dimension(360, FIELD_HEIGHT)
        );

        comboBox.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    private JLabel createFieldLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text + ":");

        label.setFont(
                UIConstants.FONT_MEDIUM
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        label.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        return label;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 14 28 18 28",
                        "[grow][]10[]",
                        "[center]"
                )
        );

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        0,
                        0,
                        UIConstants.BORDER
                )
        );

        JButton cancelButton =
                createCancelButton();

        JButton saveButton =
                createSaveButton();

        panel.add(
                new JPanel(),
                "growx"
        );

        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private JButton createCancelButton() {
        JButton button =
                new JButton("Hủy");

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setPreferredSize(
                new Dimension(120, 40)
        );

        button.setBackground(Color.WHITE);

        button.setForeground(
                UIConstants.TEXT_PRIMARY
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
                borderColor: #CBD5E1;
                focusedBorderColor: #94A3B8;
                """
        );

        button.addActionListener(
                event -> dispose()
        );

        return button;
    }

    private JButton createSaveButton() {
        JButton button = new JButton(
                createMode
                        ? "Tạo tài khoản"
                        : "Lưu thay đổi"
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setPreferredSize(
                new Dimension(150, 40)
        );

        button.setBackground(
                UIConstants.PRIMARY
        );

        button.setForeground(Color.WHITE);
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
                focusedBorderColor: #2563EB;
                """
        );

        button.addActionListener(
                event -> confirmForm()
        );

        return button;
    }

    private void fillExistingUser(
            User user
    ) {
        usernameField.setText(
                safeText(user.getUsername())
        );

        usernameField.setEditable(false);

        usernameField.setBackground(
                new Color(248, 250, 252)
        );

        usernameField.setToolTipText(
                "Không thể thay đổi tên đăng nhập"
        );

        fullNameField.setText(
                safeText(user.getFullName())
        );

        emailField.setText(
                safeText(user.getEmail())
        );

        phoneField.setText(
                safeText(user.getPhone())
        );

        if (user.getRole() != null) {
            roleComboBox.setSelectedItem(
                    user.getRole()
            );
        }

        if (user.getStatus() != null) {
            statusComboBox.setSelectedItem(
                    user.getStatus()
            );
        }
    }

    private void confirmForm() {
        String username =
                usernameField
                        .getText()
                        .trim();

        String fullName =
                fullNameField
                        .getText()
                        .trim();

        String email =
                emailField
                        .getText()
                        .trim();

        String phone =
                phoneField
                        .getText()
                        .trim();

        if (username.isBlank()) {
            showValidationMessage(
                    "Tên đăng nhập không được để trống."
            );

            usernameField.requestFocusInWindow();
            return;
        }

        if (
                createMode
                        && passwordField
                        .getPassword()
                        .length == 0
        ) {
            showValidationMessage(
                    "Mật khẩu không được để trống."
            );

            passwordField.requestFocusInWindow();
            return;
        }

        if (
                createMode
                        && passwordField
                        .getPassword()
                        .length < 6
        ) {
            showValidationMessage(
                    "Mật khẩu phải có ít nhất 6 ký tự."
            );

            passwordField.requestFocusInWindow();
            return;
        }

        if (fullName.isBlank()) {
            showValidationMessage(
                    "Họ và tên không được để trống."
            );

            fullNameField.requestFocusInWindow();
            return;
        }

        if (
                !email.isBlank()
                        && !email.matches(
                        "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$"
                )
        ) {
            showValidationMessage(
                    "Email không đúng định dạng."
            );

            emailField.requestFocusInWindow();
            return;
        }

        if (
                !phone.isBlank()
                        && !phone.matches(
                        "^[0-9]{9,11}$"
                )
        ) {
            showValidationMessage(
                    "Số điện thoại phải gồm từ 9 đến 11 chữ số."
            );

            phoneField.requestFocusInWindow();
            return;
        }

        if (roleComboBox.getSelectedItem() == null) {
            showValidationMessage(
                    "Hãy chọn vai trò cho tài khoản."
            );

            roleComboBox.requestFocusInWindow();
            return;
        }

        if (
                !createMode
                        && statusComboBox
                        .getSelectedItem() == null
        ) {
            showValidationMessage(
                    "Hãy chọn trạng thái tài khoản."
            );

            statusComboBox.requestFocusInWindow();
            return;
        }

        confirmed = true;
        dispose();
    }

    private void showValidationMessage(
            String message
    ) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Dữ liệu chưa hợp lệ",
                JOptionPane.WARNING_MESSAGE
        );
    }

    public static UserFormDialog showCreate(
            Component parent
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        parent
                );

        UserFormDialog dialog =
                new UserFormDialog(
                        owner,
                        null
                );

        dialog.setVisible(true);

        return dialog;
    }

    public static UserFormDialog showEdit(
            Component parent,
            User user
    ) {
        Window owner =
                SwingUtilities.getWindowAncestor(
                        parent
                );

        UserFormDialog dialog =
                new UserFormDialog(
                        owner,
                        user
                );

        dialog.setVisible(true);

        return dialog;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getUsername() {
        return usernameField
                .getText()
                .trim();
    }

    public String getPassword() {
        return new String(
                passwordField.getPassword()
        );
    }

    public String getFullName() {
        return fullNameField
                .getText()
                .trim();
    }

    public String getEmail() {
        return normalize(
                emailField.getText()
        );
    }

    public String getPhone() {
        return normalize(
                phoneField.getText()
        );
    }

    public Role getSelectedRole() {
        return (Role)
                roleComboBox
                        .getSelectedItem();
    }

    public AccountStatus getSelectedStatus() {
        if (createMode) {
            return AccountStatus.ACTIVE;
        }

        return (AccountStatus)
                statusComboBox
                        .getSelectedItem();
    }

    private String normalize(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
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
}
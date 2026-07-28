package view.dialog;

import model.AccountStatus;
import model.Role;
import model.User;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Window;

public class UserFormDialog extends JDialog {

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

        roleComboBox =
                new JComboBox<>(Role.values());

        statusComboBox =
                new JComboBox<>(
                        AccountStatus.values()
                );

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

        setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        setResizable(false);

        JPanel rootPanel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[grow, fill]",
                        "[]10[grow, fill]12[]"
                )
        );

        rootPanel.setBackground(Color.WHITE);

        rootPanel.add(
                createHeaderPanel(),
                "growx"
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        createFormPanel()
                );

        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(14);

        rootPanel.add(
                scrollPane,
                "grow, push"
        );

        rootPanel.add(
                createButtonPanel(),
                "growx"
        );

        setContentPane(rootPanel);

        setPreferredSize(
                new Dimension(
                        480,
                        createMode ? 460 : 430
                )
        );

        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow]",
                        "[][]"
                )
        );

        panel.setOpaque(false);

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
                        ? "Nhập đầy đủ thông tin tài khoản."
                        : "Cập nhật thông tin tài khoản."
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

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 6 2",
                        "110![grow, fill]",
                        "[]8[]8[]8[]8[]8[]"
                )
        );

        panel.setBackground(Color.WHITE);

        configureTextField(usernameField);
        configureTextField(passwordField);
        configureTextField(fullNameField);
        configureTextField(emailField);
        configureTextField(phoneField);

        roleComboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        statusComboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        panel.add(createLabel("Tên đăng nhập"));
        panel.add(usernameField, "height 36!");

        if (createMode) {
            panel.add(createLabel("Mật khẩu"));
            panel.add(passwordField, "height 36!");
        }

        panel.add(createLabel("Họ và tên"));
        panel.add(fullNameField, "height 36!");

        panel.add(createLabel("Email"));
        panel.add(emailField, "height 36!");

        panel.add(createLabel("Số điện thoại"));
        panel.add(phoneField, "height 36!");

        panel.add(createLabel("Vai trò"));
        panel.add(roleComboBox, "height 36!");

        if (!createMode) {
            panel.add(createLabel("Trạng thái"));
            panel.add(statusComboBox, "height 36!");
        }

        return panel;
    }

    private void configureTextField(
            JTextField textField
    ) {
        textField.setFont(
                UIConstants.FONT_NORMAL
        );

        textField.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                margin: 6,9,6,9;
                """
        );
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text + ":");

        label.setFont(UIConstants.FONT_MEDIUM);
        label.setForeground(UIConstants.TEXT_PRIMARY);

        return label;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow][]8[]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JButton cancelButton =
                new JButton("Hủy");

        JButton saveButton = new JButton(
                createMode
                        ? "Tạo tài khoản"
                        : "Lưu thay đổi"
        );

        cancelButton.setPreferredSize(
                new Dimension(95, 36)
        );

        saveButton.setPreferredSize(
                new Dimension(125, 36)
        );

        cancelButton.setFont(
                UIConstants.FONT_MEDIUM
        );

        saveButton.setFont(
                UIConstants.FONT_MEDIUM
        );

        cancelButton.setFocusable(false);
        saveButton.setFocusable(false);

        cancelButton.setBackground(Color.WHITE);

        saveButton.setBackground(
                UIConstants.PRIMARY
        );

        saveButton.setForeground(Color.WHITE);

        cancelButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        saveButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        cancelButton.addActionListener(
                event -> dispose()
        );

        saveButton.addActionListener(
                event -> confirmForm()
        );

        panel.add(new JPanel(), "growx");
        panel.add(cancelButton);
        panel.add(saveButton);

        return panel;
    }

    private void fillExistingUser(User user) {
        usernameField.setText(
                safeText(user.getUsername())
        );

        usernameField.setEditable(false);

        fullNameField.setText(
                safeText(user.getFullName())
        );

        emailField.setText(
                safeText(user.getEmail())
        );

        phoneField.setText(
                safeText(user.getPhone())
        );

        roleComboBox.setSelectedItem(
                user.getRole()
        );

        statusComboBox.setSelectedItem(
                user.getStatus()
        );
    }

    private void confirmForm() {
        if (usernameField.getText().isBlank()) {
            showValidationMessage(
                    "Tên đăng nhập không được để trống."
            );
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
            return;
        }

        if (fullNameField.getText().isBlank()) {
            showValidationMessage(
                    "Họ và tên không được để trống."
            );
            return;
        }

        String email =
                emailField.getText().trim();

        if (
                !email.isBlank()
                        && !email.matches(
                        "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$"
                )
        ) {
            showValidationMessage(
                    "Email không đúng định dạng."
            );
            return;
        }

        String phone =
                phoneField.getText().trim();

        if (
                !phone.isBlank()
                        && !phone.matches(
                        "^[0-9]{9,11}$"
                )
        ) {
            showValidationMessage(
                    "Số điện thoại phải có từ 9 đến 11 chữ số."
            );
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
                roleComboBox.getSelectedItem();
    }

    public AccountStatus getSelectedStatus() {
        return (AccountStatus)
                statusComboBox.getSelectedItem();
    }

    private String normalize(String value) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        return value.trim();
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }
}
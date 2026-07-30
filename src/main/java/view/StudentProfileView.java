package view;

import model.Student;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Date;

public class StudentProfileView extends JPanel {

    private JTextField txtStudentId;
    private JTextField txtStudentCode;
    private JTextField txtFullName;
    private JTextField txtDateOfBirth;
    private JComboBox<String> cboGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cboStatus;

    private JButton btnUpdate;
    private JButton btnReset;

    private Student currentStudent;

    public StudentProfileView() {
        initializeView();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, insets 24, wrap 1",
                        "[grow, fill]",
                        "[]18[grow, fill]"
                )
        );

        setBackground(UIConstants.BACKGROUND);

        add(createHeaderPanel(), "growx");
        add(createProfileCard(), "growx, top");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow]",
                        "[]2[]"
                )
        );

        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Thông tin cá nhân"
        );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Xem và cập nhật thông tin hồ sơ học viên"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleLabel, "wrap");
        panel.add(subtitleLabel);

        return panel;
    }

    private JPanel createProfileCard() {
        JPanel card = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 24",
                        "160![grow, fill]",
                        "[]16[]12[]12[]12[]12[]12[]12[]12[]18[]"
                )
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                4,
                                4,
                                4,
                                4
                        )
                )
        );

        card.putClientProperty(
                "FlatLaf.style",
                "arc: 16"
        );

        JPanel avatarPanel = createAvatarPanel();

        card.add(
                avatarPanel,
                "span 2, growx, wrap"
        );

        txtStudentId = createTextField();
        txtStudentId.setEditable(false);
        txtStudentId.setBackground(
                new Color(248, 250, 252)
        );

        txtStudentCode = createTextField();
        txtStudentCode.setEditable(false);
        txtStudentCode.setBackground(
                new Color(248, 250, 252)
        );

        txtFullName = createTextField();

        txtDateOfBirth = createTextField();
        txtDateOfBirth.putClientProperty(
                "JTextField.placeholderText",
                "yyyy-MM-dd"
        );

        cboGender = new JComboBox<>(
                new String[]{
                        "Nam",
                        "Nữ",
                        "Khác"
                }
        );

        txtPhone = createTextField();
        txtEmail = createTextField();
        txtAddress = createTextField();

        cboStatus = new JComboBox<>(
                new String[]{
                        "ACTIVE",
                        "INACTIVE"
                }
        );

        cboStatus.setEnabled(false);

        card.add(createLabel("ID học viên"));
        card.add(txtStudentId);

        card.add(createLabel("Mã học viên"));
        card.add(txtStudentCode);

        card.add(createLabel("Họ và tên"));
        card.add(txtFullName);

        card.add(createLabel("Ngày sinh"));
        card.add(txtDateOfBirth);

        card.add(createLabel("Giới tính"));
        card.add(cboGender);

        card.add(createLabel("Số điện thoại"));
        card.add(txtPhone);

        card.add(createLabel("Email"));
        card.add(txtEmail);

        card.add(createLabel("Địa chỉ"));
        card.add(txtAddress);

        card.add(createLabel("Trạng thái"));
        card.add(cboStatus);

        JPanel buttonPanel = createButtonPanel();

        card.add(
                buttonPanel,
                "span 2, right"
        );

        return card;
    }

    private JPanel createAvatarPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0 0 12 0",
                        "70![grow]",
                        "[][]"
                )
        );

        panel.setOpaque(false);

        JLabel avatarLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.USER_GRADUATE,
                        48,
                        UIConstants.PRIMARY
                )
        );

        JLabel nameLabel = new JLabel(
                "Hồ sơ học viên"
        );

        nameLabel.setFont(
                UIConstants.FONT_TITLE.deriveFont(
                        Font.BOLD,
                        20f
                )
        );

        nameLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                "Thông tin tài khoản và thông tin liên hệ"
        );

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                avatarLabel,
                "cell 0 0 1 2"
        );

        panel.add(
                nameLabel,
                "cell 1 0"
        );

        panel.add(
                descriptionLabel,
                "cell 1 1"
        );

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[]10[]",
                        "[]"
                )
        );

        panel.setOpaque(false);

        btnUpdate = new JButton(
                "Cập nhật thông tin"
        );

        btnUpdate.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.SAVE,
                        13,
                        Color.WHITE
                )
        );

        btnUpdate.setBackground(
                UIConstants.PRIMARY
        );

        btnUpdate.setForeground(
                Color.WHITE
        );

        btnUpdate.setFont(
                UIConstants.FONT_MEDIUM
        );

        btnUpdate.setFocusable(false);

        btnUpdate.setPreferredSize(
                new Dimension(165, 40)
        );

        btnUpdate.putClientProperty(
                "FlatLaf.style",
                "arc: 10; borderWidth: 0"
        );

        btnReset = new JButton(
                "Khôi phục"
        );

        btnReset.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.UNDO_ALT,
                        13,
                        UIConstants.TEXT_PRIMARY
                )
        );

        btnReset.setBackground(
                Color.WHITE
        );

        btnReset.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        btnReset.setFont(
                UIConstants.FONT_MEDIUM
        );

        btnReset.setFocusable(false);

        btnReset.setPreferredSize(
                new Dimension(120, 40)
        );

        btnReset.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        btnReset.putClientProperty(
                "FlatLaf.style",
                "arc: 10"
        );

        btnUpdate.addActionListener(
                event -> handleUpdate()
        );

        btnReset.addActionListener(
                event -> displayStudent(currentStudent)
        );

        panel.add(btnUpdate);
        panel.add(btnReset);

        return panel;
    }

    public void displayStudent(Student student) {
        currentStudent = student;

        if (student == null) {
            clearForm();
            return;
        }

        txtStudentId.setText(
                String.valueOf(
                        student.getStudentID()
                )
        );

        txtStudentCode.setText(
                safeText(
                        student.getStudentCode()
                )
        );

        txtFullName.setText(
                safeText(
                        student.getFullName()
                )
        );

        txtDateOfBirth.setText(
                student.getDateOfBirth() == null
                        ? ""
                        : student.getDateOfBirth().toString()
        );

        cboGender.setSelectedItem(
                safeText(
                        student.getGender()
                )
        );

        txtPhone.setText(
                safeText(
                        student.getPhone()
                )
        );

        txtEmail.setText(
                safeText(
                        student.getEmail()
                )
        );

        txtAddress.setText(
                safeText(
                        student.getAddress()
                )
        );

        cboStatus.setSelectedItem(
                student.getStatus() == null
                        ? "ACTIVE"
                        : student.getStatus()
        );
    }

    public Student getEditedStudent() {
        if (currentStudent == null) {
            currentStudent = new Student();
        }

        currentStudent.setFullName(
                txtFullName.getText().trim()
        );

        String dateText =
                txtDateOfBirth.getText().trim();

        if (dateText.isEmpty()) {
            currentStudent.setDateOfBirth(null);
        } else {
            currentStudent.setDateOfBirth(
                    Date.valueOf(dateText)
            );
        }

        currentStudent.setGender(
                String.valueOf(
                        cboGender.getSelectedItem()
                )
        );

        currentStudent.setPhone(
                txtPhone.getText().trim()
        );

        currentStudent.setEmail(
                txtEmail.getText().trim()
        );

        currentStudent.setAddress(
                txtAddress.getText().trim()
        );

        return currentStudent;
    }

    public void setUpdateAction(
            Runnable updateAction
    ) {
        for (
                java.awt.event.ActionListener listener
                : btnUpdate.getActionListeners()
        ) {
            btnUpdate.removeActionListener(listener);
        }

        btnUpdate.addActionListener(
                event -> {
                    if (validateForm()) {
                        updateAction.run();
                    }
                }
        );
    }

    private void handleUpdate() {
        if (!validateForm()) {
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Thông tin hợp lệ. Hãy kết nối Controller để cập nhật dữ liệu.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private boolean validateForm() {
        if (
                txtFullName
                        .getText()
                        .trim()
                        .isEmpty()
        ) {
            showError(
                    "Họ và tên không được để trống."
            );

            txtFullName.requestFocus();
            return false;
        }

        String dateText =
                txtDateOfBirth
                        .getText()
                        .trim();

        if (!dateText.isEmpty()) {
            try {
                Date.valueOf(dateText);
            } catch (IllegalArgumentException exception) {
                showError(
                        "Ngày sinh phải đúng định dạng yyyy-MM-dd."
                );

                txtDateOfBirth.requestFocus();
                return false;
            }
        }

        String email =
                txtEmail
                        .getText()
                        .trim();

        if (
                !email.isEmpty()
                        && !email.matches(
                        "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$"
                )
        ) {
            showError(
                    "Email không đúng định dạng."
            );

            txtEmail.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtStudentId.setText("");
        txtStudentCode.setText("");
        txtFullName.setText("");
        txtDateOfBirth.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");

        cboGender.setSelectedIndex(0);
        cboStatus.setSelectedItem("ACTIVE");
    }

    private JTextField createTextField() {
        JTextField textField =
                new JTextField();

        textField.setPreferredSize(
                new Dimension(0, 38)
        );

        return textField;
    }

    private JLabel createLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text);

        label.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return label;
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }

    private void showError(
            String message
    ) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
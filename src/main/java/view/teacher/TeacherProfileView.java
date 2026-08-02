package view.teacher;

import dao.UserDAO;
import model.Teacher;
import model.User;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.TeacherService;
import util.PasswordUtil;
import util.SessionManager;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class TeacherProfileView extends JPanel {

    private final int teacherId;

    private final TeacherService teacherService;
    private final UserDAO userDAO;

    private Teacher currentTeacher;

    private final JLabel avatarLabel;
    private final JLabel displayNameLabel;
    private final JLabel teacherCodeSummaryLabel;

    private final JTextField teacherCodeField;
    private final JTextField usernameField;
    private final JTextField roleField;
    private final JTextField statusField;

    private final JTextField fullNameField;
    private final JTextField dateOfBirthField;
    private final JComboBox<String> genderComboBox;
    private final JTextField emailField;
    private final JTextField phoneField;
    private final JTextField addressField;
    private final JTextField specializationField;

    private final JButton saveButton;
    private final JButton reloadButton;
    private final JButton changePasswordButton;

    private boolean loading;

    public TeacherProfileView(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;

        this.teacherService =
                new TeacherService();

        this.userDAO =
                new UserDAO();

        avatarLabel =
                new JLabel("GV", SwingConstants.CENTER);

        displayNameLabel =
                new JLabel("Giảng viên");

        teacherCodeSummaryLabel =
                new JLabel("--");

        teacherCodeField =
                new JTextField();

        usernameField =
                new JTextField();

        roleField =
                new JTextField();

        statusField =
                new JTextField();

        fullNameField =
                new JTextField();

        dateOfBirthField =
                new JTextField();

        genderComboBox =
                new JComboBox<>(
                        new String[]{
                                "Không xác định",
                                "Nam",
                                "Nữ",
                                "Khác"
                        }
                );

        emailField =
                new JTextField();

        phoneField =
                new JTextField();

        addressField =
                new JTextField();

        specializationField =
                new JTextField();

        saveButton =
                createPrimaryButton(
                        "Cập nhật thông tin",
                        FontAwesomeSolid.SAVE
                );

        reloadButton =
                createSecondaryButton(
                        "Tải lại",
                        FontAwesomeSolid.SYNC_ALT
                );

        changePasswordButton =
                createSecondaryButton(
                        "Đổi mật khẩu",
                        FontAwesomeSolid.KEY
                );

        initializeView();
        configureFields();
        registerEvents();
        loadProfile();
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
                                "fill, wrap 1, insets 18",
                                "[grow, fill]",
                                "[]16[grow, fill]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        JPanel contentPanel =
                new JPanel(
                        new MigLayout(
                                "fill, insets 0",
                                "[280!, fill]16[grow, fill]",
                                "[grow, fill]"
                        )
                );

        contentPanel.setOpaque(false);

        contentPanel.add(
                createSummaryCard(),
                "growy"
        );

        contentPanel.add(
                createProfileFormCard(),
                "grow, push"
        );

        wrapper.add(
                contentPanel,
                "grow, push"
        );

        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]10[]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel("Hồ sơ giảng viên");

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Cập nhật thông tin cá nhân và quản lý mật khẩu."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
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
                reloadButton,
                "cell 1 0 1 2, height 39!"
        );

        panel.add(
                changePasswordButton,
                "cell 2 0 1 2, height 39!"
        );

        return panel;
    }

    /* =====================================================
       THẺ THÔNG TIN TÓM TẮT
       ===================================================== */

    private ContentCard createSummaryCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 24",
                        "[grow, center]",
                        "[]16[]5[]24[]10[]10[]"
                )
        );

        avatarLabel.setPreferredSize(
                new Dimension(92, 92)
        );

        avatarLabel.setMinimumSize(
                new Dimension(92, 92)
        );

        avatarLabel.setMaximumSize(
                new Dimension(92, 92)
        );

        avatarLabel.setFont(
                UIConstants.FONT_TITLE
                        .deriveFont(
                                Font.BOLD,
                                28f
                        )
        );

        avatarLabel.setForeground(Color.WHITE);

        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(
                UIConstants.PRIMARY
        );

        avatarLabel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 999;
                """
        );

        displayNameLabel.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(Font.BOLD)
        );

        displayNameLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        teacherCodeSummaryLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        teacherCodeSummaryLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel accountTitle =
                new JLabel("Thông tin tài khoản");

        accountTitle.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        accountTitle.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        card.add(
                avatarLabel,
                "width 92!, height 92!"
        );

        card.add(displayNameLabel);
        card.add(teacherCodeSummaryLabel);

        card.add(
                accountTitle,
                "growx, align left"
        );

        card.add(
                createReadOnlySummaryRow(
                        "Tên đăng nhập",
                        usernameField
                ),
                "growx"
        );

        card.add(
                createReadOnlySummaryRow(
                        "Vai trò",
                        roleField
                ),
                "growx"
        );

        card.add(
                createReadOnlySummaryRow(
                        "Trạng thái",
                        statusField
                ),
                "growx"
        );

        return card;
    }

    private JPanel createReadOnlySummaryRow(
            String labelText,
            JTextField textField
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow]",
                                "[]4[]"
                        )
                );

        panel.setOpaque(false);

        JLabel label =
                new JLabel(labelText);

        label.setFont(
                UIConstants.FONT_SMALL
        );

        label.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        textField.setEditable(false);
        textField.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        panel.add(label, "wrap");
        panel.add(
                textField,
                "growx, height 36!"
        );

        return panel;
    }

    /* =====================================================
       FORM CẬP NHẬT
       ===================================================== */

    private ContentCard createProfileFormCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 2, insets 22",
                        "[grow, fill]16[grow, fill]",
                        "[]6[]14[]14[]14[]14[]14[]14[]20[]"
                )
        );

        JLabel titleLabel =
                new JLabel("Thông tin cá nhân");

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Các trường mã giảng viên và tài khoản không được phép chỉnh sửa."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                titleLabel,
                "span 2, growx"
        );

        card.add(
                descriptionLabel,
                "span 2, growx"
        );

        card.add(
                createFormField(
                        "Mã giảng viên",
                        teacherCodeField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Họ và tên",
                        fullNameField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Ngày sinh",
                        dateOfBirthField
                ),
                "growx"
        );

        card.add(
                createComboField(
                        "Giới tính",
                        genderComboBox
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Email",
                        emailField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Số điện thoại",
                        phoneField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Địa chỉ",
                        addressField
                ),
                "span 2, growx"
        );

        card.add(
                createFormField(
                        "Chuyên môn",
                        specializationField
                ),
                "span 2, growx"
        );

        JPanel buttonPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]",
                                "[]"
                        )
                );

        buttonPanel.setOpaque(false);

        JLabel hintLabel =
                new JLabel(
                        "Thông tin sẽ được lưu trực tiếp vào SQL Server."
                );

        hintLabel.setFont(
                UIConstants.FONT_SMALL
        );

        hintLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        buttonPanel.add(
                hintLabel,
                "growx"
        );

        buttonPanel.add(
                saveButton,
                "height 40!"
        );

        card.add(
                buttonPanel,
                "span 2, growx"
        );

        return card;
    }

    private JPanel createFormField(
            String labelText,
            JTextField textField
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow, fill]",
                                "[]5[]"
                        )
                );

        panel.setOpaque(false);

        JLabel label =
                new JLabel(labelText);

        label.setFont(
                UIConstants.FONT_MEDIUM
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        panel.add(label);

        panel.add(
                textField,
                "growx, height 39!"
        );

        return panel;
    }

    private JPanel createComboField(
            String labelText,
            JComboBox<String> comboBox
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow, fill]",
                                "[]5[]"
                        )
                );

        panel.setOpaque(false);

        JLabel label =
                new JLabel(labelText);

        label.setFont(
                UIConstants.FONT_MEDIUM
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        panel.add(label);

        panel.add(
                comboBox,
                "growx, height 39!"
        );

        return panel;
    }

    /* =====================================================
       CẤU HÌNH FIELD
       ===================================================== */

    private void configureFields() {
        teacherCodeField.setEditable(false);
        usernameField.setEditable(false);
        roleField.setEditable(false);
        statusField.setEditable(false);

        configureTextField(
                teacherCodeField,
                "Mã giảng viên"
        );

        configureTextField(
                usernameField,
                "Tên đăng nhập"
        );

        configureTextField(
                roleField,
                "Vai trò"
        );

        configureTextField(
                statusField,
                "Trạng thái"
        );

        configureTextField(
                fullNameField,
                "Nhập họ và tên"
        );

        configureTextField(
                dateOfBirthField,
                "yyyy-MM-dd"
        );

        configureTextField(
                emailField,
                "example@gmail.com"
        );

        configureTextField(
                phoneField,
                "Nhập số điện thoại"
        );

        configureTextField(
                addressField,
                "Nhập địa chỉ"
        );

        configureTextField(
                specializationField,
                "Nhập chuyên môn"
        );

        genderComboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        genderComboBox.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

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
                disabledBackground: #F8FAFC;
                """
        );
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        reloadButton.addActionListener(
                event -> loadProfile()
        );

        saveButton.addActionListener(
                event -> updateProfile()
        );

        changePasswordButton.addActionListener(
                event -> showChangePasswordDialog()
        );
    }

    /* =====================================================
       TẢI HỒ SƠ
       ===================================================== */

    public void loadProfile() {
        if (loading) {
            return;
        }

        setLoading(true);

        try {
            Teacher teacher =
                    teacherService.getTeacherById(
                            teacherId
                    );

            if (teacher == null) {
                throw new IllegalStateException(
                        "Không tìm thấy hồ sơ giảng viên."
                );
            }

            currentTeacher = teacher;

            displayTeacher(teacher);
            displayCurrentAccount();

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tải hồ sơ giảng viên.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void displayTeacher(
            Teacher teacher
    ) {
        teacherCodeField.setText(
                safeText(teacher.getTeacherCode())
        );

        fullNameField.setText(
                safeText(teacher.getFullName())
        );

        dateOfBirthField.setText(
                teacher.getDateOfBirth() == null
                        ? ""
                        : teacher.getDateOfBirth()
                        .toLocalDate()
                        .toString()
        );

        selectGender(
                teacher.getGender()
        );

        emailField.setText(
                safeText(teacher.getEmail())
        );

        phoneField.setText(
                safeText(teacher.getPhone())
        );

        addressField.setText(
                safeText(teacher.getAddress())
        );

        specializationField.setText(
                safeText(
                        teacher.getSpecialization()
                )
        );

        statusField.setText(
                formatTeacherStatus(
                        teacher.getStatus()
                )
        );

        String displayName =
                isBlank(teacher.getFullName())
                        ? "Giảng viên"
                        : teacher.getFullName().trim();

        displayNameLabel.setText(displayName);

        teacherCodeSummaryLabel.setText(
                isBlank(teacher.getTeacherCode())
                        ? "--"
                        : teacher.getTeacherCode()
        );

        avatarLabel.setText(
                createInitials(displayName)
        );
    }

    private void displayCurrentAccount() {
        User currentUser =
                SessionManager.getCurrentUser();

        if (currentUser == null) {
            usernameField.setText("");
            roleField.setText("");
            return;
        }

        usernameField.setText(
                safeText(currentUser.getUsername())
        );

        roleField.setText(
                currentUser.getRole() == null
                        ? ""
                        : currentUser.getRole().name()
        );
    }

    /* =====================================================
       CẬP NHẬT HỒ SƠ
       ===================================================== */

    private void updateProfile() {
        if (currentTeacher == null) {
            showWarning(
                    "Chưa tải được hồ sơ giảng viên."
            );
            return;
        }

        try {
            String fullName =
                    fullNameField.getText().trim();

            String email =
                    emailField.getText().trim();

            String phone =
                    phoneField.getText().trim();

            String address =
                    addressField.getText().trim();

            String specialization =
                    specializationField
                            .getText()
                            .trim();

            Date dateOfBirth =
                    parseDateOfBirth(
                            dateOfBirthField.getText()
                    );

            String gender =
                    getSelectedGender();

            currentTeacher.setFullName(fullName);
            currentTeacher.setDateOfBirth(dateOfBirth);
            currentTeacher.setGender(gender);
            currentTeacher.setEmail(email);
            currentTeacher.setPhone(phone);
            currentTeacher.setAddress(address);
            currentTeacher.setSpecialization(
                    specialization
            );

            boolean successful =
                    teacherService.updateProfile(
                            currentTeacher
                    );

            if (!successful) {
                showWarning(
                        "Không thể cập nhật thông tin giảng viên."
                );
                return;
            }

            updateCurrentSessionUser(
                    fullName,
                    email,
                    phone
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật thông tin thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadProfile();

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể cập nhật thông tin.",
                    exception
            );
        }
    }

    /**
     * Đồng bộ dữ liệu hiển thị trong Session hiện tại.
     *
     * Phần này chưa gọi UserDAO.update vì UserDAO.update
     * còn cập nhật cả vai trò và trạng thái.
     */
    private void updateCurrentSessionUser(
            String fullName,
            String email,
            String phone
    ) {
        User currentUser =
                SessionManager.getCurrentUser();

        if (currentUser == null) {
            return;
        }

        currentUser.setFullName(fullName);
        currentUser.setEmail(email);
        currentUser.setPhone(phone);
    }

    /* =====================================================
       ĐỔI MẬT KHẨU
       ===================================================== */

    private void showChangePasswordDialog() {
        JPasswordField currentPasswordField =
                createPasswordField(
                        "Nhập mật khẩu hiện tại"
                );

        JPasswordField newPasswordField =
                createPasswordField(
                        "Nhập mật khẩu mới"
                );

        JPasswordField confirmPasswordField =
                createPasswordField(
                        "Nhập lại mật khẩu mới"
                );

        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 8",
                                "[320!, fill]",
                                "[]5[]12[]5[]12[]5[]"
                        )
                );

        panel.add(
                createDialogLabel(
                        "Mật khẩu hiện tại"
                )
        );

        panel.add(
                currentPasswordField,
                "height 39!"
        );

        panel.add(
                createDialogLabel(
                        "Mật khẩu mới"
                )
        );

        panel.add(
                newPasswordField,
                "height 39!"
        );

        panel.add(
                createDialogLabel(
                        "Xác nhận mật khẩu mới"
                )
        );

        panel.add(
                confirmPasswordField,
                "height 39!"
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Đổi mật khẩu",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result != JOptionPane.OK_OPTION) {
            clearPasswordFields(
                    currentPasswordField,
                    newPasswordField,
                    confirmPasswordField
            );
            return;
        }

        try {
            changePassword(
                    currentPasswordField,
                    newPasswordField,
                    confirmPasswordField
            );

        } finally {
            clearPasswordFields(
                    currentPasswordField,
                    newPasswordField,
                    confirmPasswordField
            );
        }
    }

    private void changePassword(
            JPasswordField currentPasswordField,
            JPasswordField newPasswordField,
            JPasswordField confirmPasswordField
    ) {
        char[] currentPasswordChars =
                currentPasswordField.getPassword();

        char[] newPasswordChars =
                newPasswordField.getPassword();

        char[] confirmPasswordChars =
                confirmPasswordField.getPassword();

        String currentPassword =
                new String(currentPasswordChars);

        String newPassword =
                new String(newPasswordChars);

        String confirmPassword =
                new String(confirmPasswordChars);

        if (currentPassword.isBlank()) {
            showWarning(
                    "Mật khẩu hiện tại không được để trống."
            );
            return;
        }

        validateNewPassword(
                newPassword,
                confirmPassword
        );

        int userId =
                SessionManager.getCurrentUserId();

        try {
            User user =
                    userDAO.findById(userId);

            if (user == null) {
                throw new IllegalStateException(
                        "Không tìm thấy tài khoản người dùng."
                );
            }

            boolean currentPasswordCorrect =
                    PasswordUtil.matches(
                            currentPassword,
                            user.getPasswordHash()
                    );

            if (!currentPasswordCorrect) {
                showWarning(
                        "Mật khẩu hiện tại không chính xác."
                );
                return;
            }

            if (PasswordUtil.matches(
                    newPassword,
                    user.getPasswordHash()
            )) {
                showWarning(
                        "Mật khẩu mới phải khác mật khẩu hiện tại."
                );
                return;
            }

            boolean successful =
                    userDAO.resetPassword(
                            userId,
                            newPassword
                    );

            if (!successful) {
                showWarning(
                        "Không thể cập nhật mật khẩu."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đổi mật khẩu thành công.\n"
                            + "Hãy sử dụng mật khẩu mới "
                            + "trong lần đăng nhập tiếp theo.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException exception) {
            showWarning(
                    exception.getMessage()
            );

        } catch (Exception exception) {
            showError(
                    "Không thể đổi mật khẩu.",
                    exception
            );
        }
    }

    private void validateNewPassword(
            String newPassword,
            String confirmPassword
    ) {
        if (newPassword == null
                || newPassword.isBlank()) {

            throw new IllegalArgumentException(
                    "Mật khẩu mới không được để trống."
            );
        }

        if (newPassword.length() < 6) {
            throw new IllegalArgumentException(
                    "Mật khẩu mới phải có ít nhất 6 ký tự."
            );
        }

        if (!newPassword.equals(
                confirmPassword
        )) {
            throw new IllegalArgumentException(
                    "Xác nhận mật khẩu không khớp."
            );
        }
    }

    /* =====================================================
       STYLE
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

        button.setForeground(Color.WHITE);
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
                margin: 7,14,7,14;
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

        button.setBackground(Color.WHITE);
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

    private JPasswordField createPasswordField(
            String placeholder
    ) {
        JPasswordField field =
                new JPasswordField();

        field.setFont(
                UIConstants.FONT_NORMAL
        );

        field.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        field.putClientProperty(
                "JPasswordField.showRevealButton",
                true
        );

        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );

        return field;
    }

    private JLabel createDialogLabel(
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

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private Date parseDateOfBirth(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            LocalDate localDate =
                    LocalDate.parse(
                            value.trim()
                    );

            if (localDate.isAfter(
                    LocalDate.now()
            )) {
                throw new IllegalArgumentException(
                        "Ngày sinh không được lớn hơn ngày hiện tại."
                );
            }

            return Date.valueOf(localDate);

        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Ngày sinh phải có định dạng yyyy-MM-dd."
            );
        }
    }

    private String getSelectedGender() {
        Object selected =
                genderComboBox.getSelectedItem();

        if (selected == null) {
            return null;
        }

        return switch (
                String.valueOf(selected)
                        .trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "NAM" -> "MALE";
            case "NỮ" -> "FEMALE";
            case "KHÁC" -> "OTHER";
            default -> null;
        };
    }

    private void selectGender(
            String gender
    ) {
        if (gender == null || gender.isBlank()) {
            genderComboBox.setSelectedIndex(0);
            return;
        }

        switch (
                gender.trim()
                        .toUpperCase(Locale.ROOT)
        ) {
            case "MALE" ->
                    genderComboBox.setSelectedItem(
                            "Nam"
                    );

            case "FEMALE" ->
                    genderComboBox.setSelectedItem(
                            "Nữ"
                    );

            case "OTHER" ->
                    genderComboBox.setSelectedItem(
                            "Khác"
                    );

            default ->
                    genderComboBox.setSelectedIndex(0);
        }
    }

    private String formatTeacherStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            return "";
        }

        return switch (
                status.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "ACTIVE" -> "Đang hoạt động";
            case "INACTIVE" -> "Ngừng hoạt động";
            default -> status;
        };
    }

    private String createInitials(
            String fullName
    ) {
        if (fullName == null
                || fullName.isBlank()) {

            return "GV";
        }

        String[] parts =
                fullName.trim()
                        .split("\\s+");

        if (parts.length == 1) {
            return parts[0]
                    .substring(
                            0,
                            Math.min(
                                    2,
                                    parts[0].length()
                            )
                    )
                    .toUpperCase(Locale.ROOT);
        }

        String first =
                parts[0].substring(0, 1);

        String last =
                parts[parts.length - 1]
                        .substring(0, 1);

        return (first + last)
                .toUpperCase(Locale.ROOT);
    }

    private void setLoading(
            boolean loading
    ) {
        this.loading = loading;

        saveButton.setEnabled(!loading);
        reloadButton.setEnabled(!loading);
        changePasswordButton.setEnabled(!loading);

        setEditableEnabled(
                fullNameField,
                !loading
        );

        setEditableEnabled(
                dateOfBirthField,
                !loading
        );

        genderComboBox.setEnabled(!loading);

        setEditableEnabled(
                emailField,
                !loading
        );

        setEditableEnabled(
                phoneField,
                !loading
        );

        setEditableEnabled(
                addressField,
                !loading
        );

        setEditableEnabled(
                specializationField,
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

    private void setEditableEnabled(
            JTextComponent component,
            boolean enabled
    ) {
        component.setEnabled(enabled);
    }

    private void clearPasswordFields(
            JPasswordField... fields
    ) {
        if (fields == null) {
            return;
        }

        for (JPasswordField field : fields) {
            if (field != null) {
                field.setText("");
            }
        }
    }

    private boolean isBlank(
            String value
    ) {
        return value == null
                || value.isBlank();
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
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

        Throwable current = throwable;

        while (current.getCause() != null) {
            current = current.getCause();
        }

        if (current.getMessage() != null
                && !current.getMessage().isBlank()) {

            return current.getMessage();
        }

        return "Không xác định";
    }

    public int getTeacherId() {
        return teacherId;
    }
}
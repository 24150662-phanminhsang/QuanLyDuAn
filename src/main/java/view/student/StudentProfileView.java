package view.student;

import dao.StudentDAO;
import dao.UserDAO;
import model.Student;
import model.User;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.PasswordUtil;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class StudentProfileView extends JPanel {

    private static final Color READ_ONLY_BACKGROUND =
            new Color(248, 250, 252);

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[\\w.!#$%&'*+/=?^`{|}~-]+"
                            + "@[\\w.-]+"
                            + "\\.[A-Za-z]{2,}$"
            );

    private static final Pattern PHONE_PATTERN =
            Pattern.compile(
                    "^[0-9+\\s.-]{8,15}$"
            );

    private final int studentId;

    private final StudentDAO studentDAO;
    private final UserDAO userDAO;

    private Student currentStudent;
    private User currentUser;

    private Runnable profileUpdatedHandler;

    /* =====================================================
       THÔNG TIN TÀI KHOẢN
       ===================================================== */

    private JLabel avatarLabel;

    private JTextField txtStudentCode;
    private JTextField txtUsername;
    private JTextField txtStatus;

    /* =====================================================
       THÔNG TIN CÁ NHÂN
       ===================================================== */

    private JTextField txtFullName;
    private JTextField txtDateOfBirth;
    private JComboBox<String> cboGender;

    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtAddress;

    private JButton btnSaveProfile;
    private JButton btnResetProfile;

    /* =====================================================
       ĐỔI MẬT KHẨU
       ===================================================== */

    private JPasswordField txtCurrentPassword;
    private JPasswordField txtNewPassword;
    private JPasswordField txtConfirmPassword;

    private JCheckBox chkShowPassword;

    private JButton btnChangePassword;
    private JButton btnClearPassword;

    private boolean loading;

    public StudentProfileView(
            int studentId
    ) {
        this(
                studentId,
                null
        );
    }

    public StudentProfileView(
            int studentId,
            Runnable profileUpdatedHandler
    ) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        this.studentId =
                studentId;

        this.profileUpdatedHandler =
                profileUpdatedHandler;

        this.studentDAO =
                new StudentDAO();

        this.userDAO =
                new UserDAO();

        initializeComponents();
        initializeView();
        registerEvents();

        loadData();
    }

    /* =====================================================
       KHỞI TẠO COMPONENT
       ===================================================== */

    private void initializeComponents() {
        avatarLabel =
                new JLabel(
                        "SV",
                        SwingConstants.CENTER
                );

        txtStudentCode =
                createTextField();

        txtUsername =
                createTextField();

        txtStatus =
                createTextField();

        configureReadOnlyField(
                txtStudentCode
        );

        configureReadOnlyField(
                txtUsername
        );

        configureReadOnlyField(
                txtStatus
        );

        txtFullName =
                createTextField();

        txtDateOfBirth =
                createTextField();

        txtDateOfBirth.putClientProperty(
                "JTextField.placeholderText",
                "yyyy-MM-dd"
        );

        cboGender =
                new JComboBox<>(
                        new String[]{
                                "Nam",
                                "Nữ",
                                "Khác"
                        }
                );

        cboGender.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        cboGender.putClientProperty(
                "FlatLaf.style",
                "arc: 9;"
        );

        txtEmail =
                createTextField();

        txtPhone =
                createTextField();

        txtAddress =
                createTextField();

        btnSaveProfile =
                createPrimaryButton(
                        "Lưu thay đổi",
                        FontAwesomeSolid.SAVE
                );

        btnResetProfile =
                createSecondaryButton(
                        "Khôi phục",
                        FontAwesomeSolid.UNDO_ALT
                );

        txtCurrentPassword =
                createPasswordField();

        txtNewPassword =
                createPasswordField();

        txtConfirmPassword =
                createPasswordField();

        chkShowPassword =
                new JCheckBox(
                        "Hiện mật khẩu"
                );

        chkShowPassword.setOpaque(false);

        chkShowPassword.setFont(
                UIConstants.FONT_NORMAL
        );

        chkShowPassword.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        btnChangePassword =
                createPrimaryButton(
                        "Đổi mật khẩu",
                        FontAwesomeSolid.KEY
                );

        btnClearPassword =
                createSecondaryButton(
                        "Nhập lại",
                        FontAwesomeSolid.ERASER
                );
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

        JPanel contentPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 20, gapy 16",
                                "[grow,fill]",
                                ""
                        )
                );

        contentPanel.setOpaque(false);

        contentPanel.add(
                createHeaderPanel(),
                "growx"
        );

        contentPanel.add(
                createProfileCard(),
                "growx"
        );

        contentPanel.add(
                createPasswordCard(),
                "growx"
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        contentPanel
                );

        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(18);

        scrollPane
                .getViewport()
                .setBackground(
                        UIConstants.BACKGROUND
                );

        add(
                scrollPane,
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
                                "fillx, insets 0, gapx 16",
                                "[72!][grow,fill]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        configureAvatar();

        JLabel titleLabel =
                new JLabel(
                        "Hồ sơ cá nhân"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Quản lý thông tin cá nhân "
                                + "và bảo mật tài khoản của bạn."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                avatarLabel,
                "cell 0 0 1 2, "
                        + "width 64!, height 64!, "
                        + "align center"
        );

        panel.add(
                titleLabel,
                "cell 1 0"
        );

        panel.add(
                descriptionLabel,
                "cell 1 1"
        );

        return panel;
    }

    private void configureAvatar() {
        avatarLabel.setOpaque(true);

        avatarLabel.setBackground(
                UIConstants.PRIMARY
        );

        avatarLabel.setForeground(
                Color.WHITE
        );

        avatarLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        avatarLabel.putClientProperty(
                "FlatLaf.style",
                "arc: 999;"
        );
    }

    /* =====================================================
       CARD THÔNG TIN CÁ NHÂN
       ===================================================== */

    private ContentCard createProfileCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20, gapy 14",
                        "[grow,fill]",
                        ""
                )
        );

        card.add(
                createCardHeader(
                        "Thông tin cá nhân",
                        "Các trường mã sinh viên, "
                                + "tên đăng nhập và trạng thái "
                                + "không thể chỉnh sửa."
                ),
                "growx"
        );

        card.add(
                createProfileForm(),
                "growx"
        );

        card.add(
                createProfileButtonPanel(),
                "growx"
        );

        return card;
    }

    private JPanel createProfileForm() {
        JPanel formPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 3, insets 0, gapx 16, gapy 5",
                                "[grow,fill][grow,fill][grow,fill]",
                                ""
                        )
                );

        formPanel.setOpaque(false);

        /*
         * Hàng 1
         */
        formPanel.add(
                createFieldLabel(
                        "Mã sinh viên"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Tên đăng nhập"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Trạng thái"
                )
        );

        formPanel.add(
                txtStudentCode,
                "height 38!"
        );

        formPanel.add(
                txtUsername,
                "height 38!"
        );

        formPanel.add(
                txtStatus,
                "height 38!"
        );

        /*
         * Hàng 2
         */
        formPanel.add(
                createRequiredFieldLabel(
                        "Họ và tên"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Ngày sinh"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Giới tính"
                )
        );

        formPanel.add(
                txtFullName,
                "height 38!"
        );

        formPanel.add(
                txtDateOfBirth,
                "height 38!"
        );

        formPanel.add(
                cboGender,
                "height 38!"
        );

        /*
         * Hàng 3
         */
        formPanel.add(
                createFieldLabel(
                        "Email"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Số điện thoại"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Địa chỉ"
                )
        );

        formPanel.add(
                txtEmail,
                "height 38!"
        );

        formPanel.add(
                txtPhone,
                "height 38!"
        );

        formPanel.add(
                txtAddress,
                "height 38!"
        );

        return formPanel;
    }

    private JPanel createProfileButtonPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 10",
                                "[grow][][]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        JLabel noteLabel =
                new JLabel(
                        "Thông tin được cập nhật trực tiếp "
                                + "vào hồ sơ sinh viên."
                );

        noteLabel.setFont(
                UIConstants.FONT_SMALL
        );

        noteLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                noteLabel,
                "growx"
        );

        panel.add(
                btnResetProfile,
                "height 38!"
        );

        panel.add(
                btnSaveProfile,
                "height 38!"
        );

        return panel;
    }

    /* =====================================================
       CARD ĐỔI MẬT KHẨU
       ===================================================== */

    private ContentCard createPasswordCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20, gapy 14",
                        "[grow,fill]",
                        ""
                )
        );

        card.add(
                createCardHeader(
                        "Đổi mật khẩu",
                        "Mật khẩu mới nên có ít nhất 6 ký tự."
                ),
                "growx"
        );

        card.add(
                createPasswordForm(),
                "growx"
        );

        card.add(
                createPasswordButtonPanel(),
                "growx"
        );

        return card;
    }

    private JPanel createPasswordForm() {
        JPanel formPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 3, insets 0, gapx 16, gapy 5",
                                "[grow,fill][grow,fill][grow,fill]",
                                ""
                        )
                );

        formPanel.setOpaque(false);

        formPanel.add(
                createRequiredFieldLabel(
                        "Mật khẩu hiện tại"
                )
        );

        formPanel.add(
                createRequiredFieldLabel(
                        "Mật khẩu mới"
                )
        );

        formPanel.add(
                createRequiredFieldLabel(
                        "Xác nhận mật khẩu mới"
                )
        );

        formPanel.add(
                txtCurrentPassword,
                "height 38!"
        );

        formPanel.add(
                txtNewPassword,
                "height 38!"
        );

        formPanel.add(
                txtConfirmPassword,
                "height 38!"
        );

        return formPanel;
    }

    private JPanel createPasswordButtonPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 10",
                                "[][grow][][]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                chkShowPassword
        );

        panel.add(
                new JLabel(),
                "growx"
        );

        panel.add(
                btnClearPassword,
                "height 38!"
        );

        panel.add(
                btnChangePassword,
                "height 38!"
        );

        return panel;
    }

    private JPanel createCardHeader(
            String title,
            String description
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0, gapy 3",
                                "[grow,fill]",
                                ""
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        btnSaveProfile.addActionListener(
                event -> saveProfile()
        );

        btnResetProfile.addActionListener(
                event -> loadData()
        );

        btnChangePassword.addActionListener(
                event -> changePassword()
        );

        btnClearPassword.addActionListener(
                event -> clearPasswordFields()
        );

        chkShowPassword.addActionListener(
                event -> togglePasswordVisibility()
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
            Student student =
                    studentDAO.getStudentById(
                            studentId
                    );

            if (student == null) {
                throw new IllegalStateException(
                        "Không tìm thấy hồ sơ sinh viên."
                );
            }

            currentStudent =
                    student;

            Integer userId =
                    currentStudent.getUserId();

            if (userId == null
                    || userId <= 0) {

                throw new IllegalStateException(
                        "Hồ sơ sinh viên chưa liên kết tài khoản."
                );
            }

            currentUser =
                    userDAO.findById(
                            userId
                    );

            if (currentUser == null) {
                throw new IllegalStateException(
                        "Không tìm thấy tài khoản người dùng."
                );
            }

            displayProfile();

        } catch (SQLException exception) {
            showError(
                    "Không thể tải thông tin tài khoản.",
                    exception
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tải hồ sơ sinh viên.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void displayProfile() {
        txtStudentCode.setText(
                safeText(
                        currentStudent.getStudentCode()
                )
        );

        txtUsername.setText(
                safeText(
                        currentUser.getUsername()
                )
        );

        txtStatus.setText(
                formatStatus(
                        currentStudent.getStatus()
                )
        );

        txtFullName.setText(
                safeText(
                        currentStudent.getFullName()
                )
        );

        txtDateOfBirth.setText(
                currentStudent.getDateOfBirth() == null
                        ? ""
                        : currentStudent
                        .getDateOfBirth()
                        .toString()
        );

        cboGender.setSelectedItem(
                formatGender(
                        currentStudent.getGender()
                )
        );

        txtEmail.setText(
                safeText(
                        currentStudent.getEmail()
                )
        );

        txtPhone.setText(
                safeText(
                        currentStudent.getPhone()
                )
        );

        txtAddress.setText(
                safeText(
                        currentStudent.getAddress()
                )
        );

        avatarLabel.setText(
                createInitials(
                        currentStudent.getFullName()
                )
        );
    }

    /* =====================================================
       CẬP NHẬT HỒ SƠ
       ===================================================== */

    private void saveProfile() {
        if (loading) {
            return;
        }

        if (!validateProfileForm()) {
            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn lưu "
                                + "các thay đổi thông tin?",
                        "Xác nhận cập nhật",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        setLoading(true);

        try {
            updateStudentFromForm();

            boolean studentUpdated =
                    studentDAO.updateStudent(
                            currentStudent
                    );

            if (!studentUpdated) {
                throw new IllegalStateException(
                        "Không thể cập nhật hồ sơ sinh viên."
                );
            }

            /*
             * Đồng bộ các trường chung sang Users.
             */
            currentUser.setFullName(
                    currentStudent.getFullName()
            );

            currentUser.setEmail(
                    currentStudent.getEmail()
            );

            currentUser.setPhone(
                    currentStudent.getPhone()
            );

            boolean userUpdated =
                    userDAO.update(
                            currentUser
                    );

            if (!userUpdated) {
                throw new IllegalStateException(
                        "Đã cập nhật hồ sơ sinh viên "
                                + "nhưng không thể đồng bộ tài khoản."
                );
            }


            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật thông tin thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadData();

            if (profileUpdatedHandler != null) {
                profileUpdatedHandler.run();
            }

        } catch (SQLException exception) {
            showError(
                    "Không thể đồng bộ thông tin tài khoản.",
                    exception
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể cập nhật hồ sơ.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void updateStudentFromForm() {
        currentStudent.setFullName(
                txtFullName
                        .getText()
                        .trim()
        );

        String dateText =
                txtDateOfBirth
                        .getText()
                        .trim();

        if (dateText.isBlank()) {
            currentStudent.setDateOfBirth(
                    null
            );
        } else {
            currentStudent.setDateOfBirth(
                    Date.valueOf(
                            dateText
                    )
            );
        }

        currentStudent.setGender(
                convertGenderToDatabase(
                        String.valueOf(
                                cboGender.getSelectedItem()
                        )
                )
        );

        currentStudent.setEmail(
                emptyToNull(
                        txtEmail.getText()
                )
        );

        currentStudent.setPhone(
                emptyToNull(
                        txtPhone.getText()
                )
        );

        currentStudent.setAddress(
                emptyToNull(
                        txtAddress.getText()
                )
        );
    }

    /* =====================================================
       VALIDATION HỒ SƠ
       ===================================================== */

    private boolean validateProfileForm() {
        String fullName =
                txtFullName
                        .getText()
                        .trim();

        if (fullName.isBlank()) {
            showWarning(
                    "Họ và tên không được để trống."
            );

            txtFullName.requestFocusInWindow();

            return false;
        }

        if (fullName.length() < 2) {
            showWarning(
                    "Họ và tên phải có ít nhất 2 ký tự."
            );

            txtFullName.requestFocusInWindow();

            return false;
        }

        String dateText =
                txtDateOfBirth
                        .getText()
                        .trim();

        if (!dateText.isBlank()) {
            try {
                LocalDate dateOfBirth =
                        LocalDate.parse(
                                dateText
                        );

                if (dateOfBirth.isAfter(
                        LocalDate.now()
                )) {
                    showWarning(
                            "Ngày sinh không thể lớn hơn ngày hiện tại."
                    );

                    txtDateOfBirth.requestFocusInWindow();

                    return false;
                }

            } catch (RuntimeException exception) {
                showWarning(
                        "Ngày sinh phải đúng định dạng yyyy-MM-dd."
                );

                txtDateOfBirth.requestFocusInWindow();

                return false;
            }
        }

        String email =
                txtEmail
                        .getText()
                        .trim();

        if (!email.isBlank()
                && !EMAIL_PATTERN
                .matcher(email)
                .matches()) {

            showWarning(
                    "Email không đúng định dạng."
            );

            txtEmail.requestFocusInWindow();

            return false;
        }

        String phone =
                txtPhone
                        .getText()
                        .trim();

        if (!phone.isBlank()
                && !PHONE_PATTERN
                .matcher(phone)
                .matches()) {

            showWarning(
                    "Số điện thoại không đúng định dạng."
            );

            txtPhone.requestFocusInWindow();

            return false;
        }

        return true;
    }

    /* =====================================================
       ĐỔI MẬT KHẨU
       ===================================================== */

    private void changePassword() {
        if (loading) {
            return;
        }

        char[] currentPasswordChars =
                txtCurrentPassword
                        .getPassword();

        char[] newPasswordChars =
                txtNewPassword
                        .getPassword();

        char[] confirmPasswordChars =
                txtConfirmPassword
                        .getPassword();

        String currentPassword =
                new String(
                        currentPasswordChars
                );

        String newPassword =
                new String(
                        newPasswordChars
                );

        String confirmPassword =
                new String(
                        confirmPasswordChars
                );

        try {
            if (!validatePasswordForm(
                    currentPassword,
                    newPassword,
                    confirmPassword
            )) {
                return;
            }

            if (currentUser == null) {
                throw new IllegalStateException(
                        "Chưa tải được thông tin tài khoản."
                );
            }

            boolean currentPasswordCorrect =
                    PasswordUtil.matches(
                            currentPassword,
                            currentUser.getPasswordHash()
                    );

            if (!currentPasswordCorrect) {
                showWarning(
                        "Mật khẩu hiện tại không chính xác."
                );

                txtCurrentPassword
                        .requestFocusInWindow();

                return;
            }

            if (PasswordUtil.matches(
                    newPassword,
                    currentUser.getPasswordHash()
            )) {
                showWarning(
                        "Mật khẩu mới phải khác mật khẩu hiện tại."
                );

                txtNewPassword.requestFocusInWindow();

                return;
            }

            int answer =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Bạn có chắc muốn đổi mật khẩu?",
                            "Xác nhận đổi mật khẩu",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            setLoading(true);

            boolean successful =
                    userDAO.resetPassword(
                            currentUser.getUserId(),
                            newPassword
                    );

            if (!successful) {
                throw new IllegalStateException(
                        "Không thể cập nhật mật khẩu."
                );
            }

            /*
             * Làm mới passwordHash đang giữ trong View.
             */
            currentUser =
                    userDAO.findById(
                            currentUser.getUserId()
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Đổi mật khẩu thành công.\n"
                            + "Hãy sử dụng mật khẩu mới "
                            + "trong lần đăng nhập tiếp theo.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearPasswordFields();

        } catch (SQLException exception) {
            showError(
                    "Không thể đổi mật khẩu.",
                    exception
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể đổi mật khẩu.",
                    exception
            );

        } finally {
            Arrays.fill(
                    currentPasswordChars,
                    '\0'
            );

            Arrays.fill(
                    newPasswordChars,
                    '\0'
            );

            Arrays.fill(
                    confirmPasswordChars,
                    '\0'
            );

            setLoading(false);
        }
    }

    private boolean validatePasswordForm(
            String currentPassword,
            String newPassword,
            String confirmPassword
    ) {
        if (currentPassword == null
                || currentPassword.isBlank()) {

            showWarning(
                    "Vui lòng nhập mật khẩu hiện tại."
            );

            txtCurrentPassword
                    .requestFocusInWindow();

            return false;
        }

        if (newPassword == null
                || newPassword.isBlank()) {

            showWarning(
                    "Vui lòng nhập mật khẩu mới."
            );

            txtNewPassword
                    .requestFocusInWindow();

            return false;
        }

        if (newPassword.length() < 6) {
            showWarning(
                    "Mật khẩu mới phải có ít nhất 6 ký tự."
            );

            txtNewPassword
                    .requestFocusInWindow();

            return false;
        }

        if (newPassword.length() > 100) {
            showWarning(
                    "Mật khẩu mới quá dài."
            );

            txtNewPassword
                    .requestFocusInWindow();

            return false;
        }

        if (!newPassword.equals(
                confirmPassword
        )) {
            showWarning(
                    "Xác nhận mật khẩu mới không khớp."
            );

            txtConfirmPassword
                    .requestFocusInWindow();

            return false;
        }

        return true;
    }

    private void clearPasswordFields() {
        txtCurrentPassword.setText("");
        txtNewPassword.setText("");
        txtConfirmPassword.setText("");

        chkShowPassword.setSelected(false);

        togglePasswordVisibility();

        txtCurrentPassword
                .requestFocusInWindow();
    }

    private void togglePasswordVisibility() {
        char echoCharacter =
                chkShowPassword.isSelected()
                        ? (char) 0
                        : '•';

        txtCurrentPassword.setEchoChar(
                echoCharacter
        );

        txtNewPassword.setEchoChar(
                echoCharacter
        );

        txtConfirmPassword.setEchoChar(
                echoCharacter
        );
    }

    /* =====================================================
       COMPONENT HỖ TRỢ
       ===================================================== */

    private JTextField createTextField() {
        JTextField field =
                new JTextField();

        field.setFont(
                UIConstants.FONT_NORMAL
        );

        field.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                margin: 7,10,7,10;
                """
        );

        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field =
                new JPasswordField();

        field.setFont(
                UIConstants.FONT_NORMAL
        );

        field.setPreferredSize(
                new Dimension(
                        0,
                        38
                )
        );

        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                margin: 7,10,7,10;
                """
        );

        return field;
    }

    private void configureReadOnlyField(
            JTextField field
    ) {
        field.setEditable(false);

        field.setFocusable(false);

        field.setBackground(
                READ_ONLY_BACKGROUND
        );

        field.setForeground(
                UIConstants.TEXT_SECONDARY
        );
    }

    private JLabel createFieldLabel(
            String text
    ) {
        JLabel label =
                new JLabel(text);

        label.setFont(
                UIConstants.FONT_SMALL
                        .deriveFont(Font.BOLD)
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return label;
    }

    private JLabel createRequiredFieldLabel(
            String text
    ) {
        JLabel label =
                new JLabel(
                        "<html>"
                                + text
                                + " <font color='#EF4444'>*</font>"
                                + "</html>"
                );

        label.setFont(
                UIConstants.FONT_SMALL
                        .deriveFont(Font.BOLD)
        );

        label.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        return label;
    }

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

    /* =====================================================
       LOADING
       ===================================================== */

    private void setLoading(
            boolean loading
    ) {
        this.loading =
                loading;

        txtFullName.setEnabled(
                !loading
        );

        txtDateOfBirth.setEnabled(
                !loading
        );

        cboGender.setEnabled(
                !loading
        );

        txtEmail.setEnabled(
                !loading
        );

        txtPhone.setEnabled(
                !loading
        );

        txtAddress.setEnabled(
                !loading
        );

        btnSaveProfile.setEnabled(
                !loading
        );

        btnResetProfile.setEnabled(
                !loading
        );

        txtCurrentPassword.setEnabled(
                !loading
        );

        txtNewPassword.setEnabled(
                !loading
        );

        txtConfirmPassword.setEnabled(
                !loading
        );

        chkShowPassword.setEnabled(
                !loading
        );

        btnChangePassword.setEnabled(
                !loading
        );

        btnClearPassword.setEnabled(
                !loading
        );

        btnSaveProfile.setText(
                loading
                        ? "Đang xử lý..."
                        : "Lưu thay đổi"
        );

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    /* =====================================================
       FORMAT
       ===================================================== */

    private String formatGender(
            String gender
    ) {
        if (gender == null
                || gender.isBlank()) {

            return "Khác";
        }

        return switch (
                gender.trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
                ) {
            case "MALE",
                 "NAM" -> "Nam";

            case "FEMALE",
                 "NỮ",
                 "NU" -> "Nữ";

            default -> "Khác";
        };
    }

    private String convertGenderToDatabase(
            String displayGender
    ) {
        return switch (
                displayGender
                ) {
            case "Nam" -> "MALE";
            case "Nữ" -> "FEMALE";
            default -> "OTHER";
        };
    }

    private String formatStatus(
            String status
    ) {
        if (status == null
                || status.isBlank()) {

            return "--";
        }

        return switch (
                status.trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
                ) {
            case "ACTIVE" ->
                    "Đang hoạt động";

            case "INACTIVE" ->
                    "Ngừng hoạt động";

            default ->
                    status.trim();
        };
    }

    private String createInitials(
            String fullName
    ) {
        if (fullName == null
                || fullName.isBlank()) {

            return "SV";
        }

        String[] words =
                fullName.trim()
                        .split("\\s+");

        if (words.length == 1) {
            String word =
                    words[0];

            return word.substring(
                            0,
                            Math.min(
                                    2,
                                    word.length()
                            )
                    )
                    .toUpperCase(
                            Locale.ROOT
                    );
        }

        String first =
                words[0].substring(
                        0,
                        1
                );

        String last =
                words[
                        words.length - 1
                        ].substring(
                        0,
                        1
                );

        return (
                first
                        + last
        ).toUpperCase(
                Locale.ROOT
        );
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
    }

    private String emptyToNull(
            String value
    ) {
        if (value == null
                || value.isBlank()) {

            return null;
        }

        return value.trim();
    }

    /* =====================================================
       THÔNG BÁO
       ===================================================== */

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
       GETTER / CALLBACK
       ===================================================== */

    public int getStudentId() {
        return studentId;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public void setProfileUpdatedHandler(
            Runnable profileUpdatedHandler
    ) {
        this.profileUpdatedHandler =
                profileUpdatedHandler;
    }
}
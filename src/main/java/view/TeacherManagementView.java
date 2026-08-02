package view;

import controller.TeacherController;
import model.Teacher;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;

public class TeacherManagementView extends JPanel {

    private static final String LIST_CARD =
            "LIST";

    private static final String FORM_CARD =
            "FORM";

    private final TeacherController teacherController;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private JTextField searchField;
    private JComboBox<String> statusFilterComboBox;

    private JTable teacherTable;
    private DefaultTableModel tableModel;

    private JButton addButton;
    private JButton editButton;
    private JButton statusButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton classesButton;

    private JLabel formTitleLabel;

    private JTextField teacherIdField;
    private JTextField teacherCodeField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField fullNameField;
    private JTextField birthDateField;
    private JComboBox<String> genderComboBox;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField specializationField;
    private JComboBox<String> statusComboBox;

    private JButton cancelFormButton;
    private JButton saveFormButton;

    private Teacher editingTeacher;

    public TeacherManagementView() {
        teacherController =
                new TeacherController();

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(
                        cardLayout
                );

        initializeView();
        registerEvents();
        loadTeachers();
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

        contentPanel.setOpaque(false);

        contentPanel.add(
                createListCard(),
                LIST_CARD
        );

        contentPanel.add(
                createFormCard(),
                FORM_CARD
        );

        add(
                contentPanel,
                BorderLayout.CENTER
        );
    }

    /* =====================================================
       DANH SÁCH GIẢNG VIÊN
       ===================================================== */

    private JPanel createListCard() {
        JPanel wrapper =
                new JPanel(
                        new BorderLayout(
                                0,
                                14
                        )
                );

        wrapper.setOpaque(false);

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        16,
                        16,
                        16
                )
        );

        wrapper.add(
                createToolbar(),
                BorderLayout.NORTH
        );

        wrapper.add(
                createTableCard(),
                BorderLayout.CENTER
        );

        return wrapper;
    }

    private JPanel createToolbar() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 16",
                        "[grow,fill]",
                        "[][]"
                )
        );

        JPanel firstRow =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 10",
                                "[grow,fill]180![][]",
                                "[]"
                        )
                );

        firstRow.setOpaque(false);

        searchField =
                new JTextField();

        configureTextField(
                searchField,
                "Tìm mã, họ tên, email, số điện thoại hoặc chuyên môn"
        );

        statusFilterComboBox =
                new JComboBox<>(
                        new String[]{
                                "TẤT CẢ",
                                "ACTIVE",
                                "INACTIVE"
                        }
                );

        configureComboBox(
                statusFilterComboBox
        );

        refreshButton =
                createButton(
                        "Làm mới",
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        addButton =
                createButton(
                        "Thêm giảng viên",
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        firstRow.add(
                searchField,
                "growx, height 38!"
        );

        firstRow.add(
                statusFilterComboBox,
                "height 38!"
        );

        firstRow.add(
                refreshButton,
                "height 38!"
        );

        firstRow.add(
                addButton,
                "height 38!"
        );

        JPanel secondRow =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 8",
                                "[grow][][][][]",
                                "[]"
                        )
                );

        secondRow.setOpaque(false);

        editButton =
                createButton(
                        "Cập nhật",
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        statusButton =
                createButton(
                        "Ngừng hoạt động",
                        Color.WHITE,
                        UIConstants.WARNING
                );

        classesButton =
                createButton(
                        "Số lớp phụ trách",
                        Color.WHITE,
                        UIConstants.PURPLE
                );

        deleteButton =
                createButton(
                        "Xóa",
                        Color.WHITE,
                        UIConstants.DANGER
                );

        secondRow.add(
                new JLabel(),
                "growx"
        );

        secondRow.add(editButton);
        secondRow.add(statusButton);
        secondRow.add(classesButton);
        secondRow.add(deleteButton);

        card.add(
                firstRow,
                "growx"
        );

        card.add(
                secondRow,
                "growx"
        );

        return card;
    }

    private JPanel createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[grow,fill]",
                        "[]8[grow,fill]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Danh sách giảng viên"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Chọn một dòng để cập nhật, thay đổi trạng thái hoặc xem số lớp phụ trách"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JPanel titlePanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow]",
                                "[][]"
                        )
                );

        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(descriptionLabel);

        card.add(
                titlePanel,
                "growx"
        );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Mã GV",
                                "Họ và tên",
                                "Email",
                                "Số điện thoại",
                                "Chuyên môn",
                                "Trạng thái",
                                "Ngày tạo"
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

        teacherTable =
                new JTable(
                        tableModel
                );

        teacherTable.setRowHeight(40);
        teacherTable.setFillsViewportHeight(true);

        teacherTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        teacherTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        teacherTable.setShowVerticalLines(false);
        teacherTable.setGridColor(
                UIConstants.BORDER
        );

        teacherTable
                .getTableHeader()
                .setReorderingAllowed(false);

        teacherTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                40
                        )
                );

        configureTableColumns();

        JScrollPane scrollPane =
                new JScrollPane(
                        teacherTable
                );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.getViewport().setBackground(
                Color.WHITE
        );

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane
                .getHorizontalScrollBar()
                .setUnitIncrement(20);

        card.add(
                scrollPane,
                "grow, push"
        );

        return card;
    }

    private void configureTableColumns() {
        int[] widths = {
                60,
                100,
                190,
                190,
                120,
                180,
                120,
                155
        };

        for (
                int index = 0;
                index < widths.length;
                index++
        ) {
            teacherTable
                    .getColumnModel()
                    .getColumn(index)
                    .setPreferredWidth(
                            widths[index]
                    );
        }
    }

    /* =====================================================
       FORM THÊM / CẬP NHẬT
       ===================================================== */

    private JPanel createFormCard() {
        JPanel wrapper =
                new JPanel(
                        new BorderLayout()
                );

        wrapper.setOpaque(false);

        wrapper.setBorder(
                BorderFactory.createEmptyBorder(
                        16,
                        16,
                        16,
                        16
                )
        );

        ContentCard formCard =
                new ContentCard();

        formCard.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20 24",
                        "[grow,fill]",
                        "[]16[]"
                )
        );

        JPanel headerPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 10",
                                "[grow,fill][][]",
                                "[][]"
                        )
                );

        headerPanel.setOpaque(false);

        formTitleLabel =
                new JLabel(
                        "Thêm giảng viên"
                );

        formTitleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        formTitleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Nhập đầy đủ thông tin hồ sơ giảng viên"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        cancelFormButton =
                createButton(
                        "Hủy",
                        Color.WHITE,
                        UIConstants.TEXT_SECONDARY
                );

        saveFormButton =
                createButton(
                        "Lưu giảng viên",
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        headerPanel.add(
                formTitleLabel,
                "cell 0 0"
        );

        headerPanel.add(
                descriptionLabel,
                "cell 0 1"
        );

        headerPanel.add(
                cancelFormButton,
                "cell 1 0 1 2, aligny center, width 105!, height 40!"
        );

        headerPanel.add(
                saveFormButton,
                "cell 2 0 1 2, aligny center, width 155!, height 40!"
        );

        formCard.add(
                headerPanel,
                "growx"
        );

        formCard.add(
                createTeacherFormPanel(),
                "growx"
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        formCard
                );

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        wrapper.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return wrapper;
    }

    private JPanel createTeacherFormPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 16",
                                "[right,160!]12[grow,fill]",
                                "[]12[]12[]12[]12[]12[]12[]12[]12[]12[]12[]12[]12[]"
                        )
                );

        panel.setBackground(
                Color.WHITE
        );

        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.BORDER
                        ),
                        "Thông tin giảng viên"
                )
        );

        teacherIdField =
                new JTextField();

        teacherCodeField =
                new JTextField();

        usernameField =
                new JTextField();

        passwordField =
                new JPasswordField();

        confirmPasswordField =
                new JPasswordField();

        fullNameField =
                new JTextField();

        birthDateField =
                new JTextField();

        genderComboBox =
                new JComboBox<>(
                        new String[]{
                                "",
                                "MALE",
                                "FEMALE",
                                "OTHER"
                        }
                );

        emailField =
                new JTextField();

        phoneField =
                new JTextField();

        addressArea =
                new JTextArea(
                        3,
                        20
                );

        specializationField =
                new JTextField();

        statusComboBox =
                new JComboBox<>(
                        new String[]{
                                "ACTIVE",
                                "INACTIVE"
                        }
                );

        teacherIdField.setEditable(false);

        configureTextField(
                teacherIdField,
                "Tự động sinh"
        );

        configureTextField(
                teacherCodeField,
                "Ví dụ: GV001"
        );

        configureTextField(
                usernameField,
                "Tên đăng nhập"
        );

        configurePasswordField(
                passwordField,
                "Mật khẩu tối thiểu 6 ký tự"
        );

        configurePasswordField(
                confirmPasswordField,
                "Nhập lại mật khẩu"
        );

        configureTextField(
                fullNameField,
                "Họ và tên giảng viên"
        );

        configureTextField(
                birthDateField,
                "yyyy-MM-dd"
        );

        configureTextField(
                emailField,
                "email@example.com"
        );

        configureTextField(
                phoneField,
                "9 đến 11 chữ số"
        );

        configureTextField(
                specializationField,
                "Ví dụ: Công nghệ thông tin"
        );

        configureComboBox(
                genderComboBox
        );

        configureComboBox(
                statusComboBox
        );

        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setFont(
                UIConstants.FONT_NORMAL
        );

        panel.add(
                createFormLabel(
                        "ID giảng viên"
                )
        );

        panel.add(
                teacherIdField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Mã giảng viên *"
                )
        );

        panel.add(
                teacherCodeField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Tên đăng nhập *"
                )
        );

        panel.add(
                usernameField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Mật khẩu *"
                )
        );

        panel.add(
                passwordField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Xác nhận mật khẩu *"
                )
        );

        panel.add(
                confirmPasswordField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Họ và tên *"
                )
        );

        panel.add(
                fullNameField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Ngày sinh"
                )
        );

        panel.add(
                birthDateField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Giới tính"
                )
        );

        panel.add(
                genderComboBox,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Email"
                )
        );

        panel.add(
                emailField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Số điện thoại"
                )
        );

        panel.add(
                phoneField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Chuyên môn *"
                )
        );

        panel.add(
                specializationField,
                "height 38!"
        );

        panel.add(
                createFormLabel(
                        "Địa chỉ"
                )
        );

        panel.add(
                new JScrollPane(
                        addressArea
                ),
                "height 90!"
        );

        panel.add(
                createFormLabel(
                        "Trạng thái"
                )
        );

        panel.add(
                statusComboBox,
                "height 38!"
        );

        return panel;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        addButton.addActionListener(
                event -> showCreateForm()
        );

        editButton.addActionListener(
                event -> showEditForm()
        );

        statusButton.addActionListener(
                event -> toggleSelectedTeacherStatus()
        );

        classesButton.addActionListener(
                event -> showAssignedClassCount()
        );

        deleteButton.addActionListener(
                event -> deleteSelectedTeacher()
        );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    statusFilterComboBox.setSelectedIndex(0);
                    loadTeachers();
                }
        );

        cancelFormButton.addActionListener(
                event -> showListCard()
        );

        saveFormButton.addActionListener(
                event -> saveTeacher()
        );

        teacherTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateButtonState();
                            }
                        }
                );

        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent event
                            ) {
                                filterTeachers();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event
                            ) {
                                filterTeachers();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event
                            ) {
                                filterTeachers();
                            }
                        }
                );

        statusFilterComboBox.addActionListener(
                event -> filterTeachers()
        );
    }

    /* =====================================================
       TẢI VÀ LỌC DỮ LIỆU
       ===================================================== */

    public void loadTeachers() {
        try {
            List<Teacher> teachers =
                    teacherController.getAllTeachers();

            displayTeachers(
                    teachers
            );

        } catch (RuntimeException exception) {
            tableModel.setRowCount(0);

            showError(
                    "Không thể tải danh sách giảng viên.",
                    exception
            );
        }
    }

    private void filterTeachers() {
        String keyword =
                searchField.getText() == null
                        ? ""
                        : searchField.getText().trim();

        String selectedStatus =
                String.valueOf(
                        statusFilterComboBox.getSelectedItem()
                );

        try {
            List<Teacher> teachers;

            if (
                    "TẤT CẢ".equals(selectedStatus)
                            || selectedStatus.isBlank()
            ) {
                teachers =
                        keyword.isBlank()
                                ? teacherController.getAllTeachers()
                                : teacherController.searchTeachers(
                                keyword
                        );

            } else {
                List<Teacher> byStatus =
                        teacherController.getTeachersByStatus(
                                selectedStatus
                        );

                if (keyword.isBlank()) {
                    teachers =
                            byStatus;

                } else {
                    String lowerKeyword =
                            keyword.toLowerCase();

                    teachers =
                            byStatus.stream()
                                    .filter(
                                            teacher ->
                                                    containsIgnoreCase(
                                                            teacher.getTeacherCode(),
                                                            lowerKeyword
                                                    )
                                                            || containsIgnoreCase(
                                                            teacher.getFullName(),
                                                            lowerKeyword
                                                    )
                                                            || containsIgnoreCase(
                                                            teacher.getEmail(),
                                                            lowerKeyword
                                                    )
                                                            || containsIgnoreCase(
                                                            teacher.getPhone(),
                                                            lowerKeyword
                                                    )
                                                            || containsIgnoreCase(
                                                            teacher.getSpecialization(),
                                                            lowerKeyword
                                                    )
                                    )
                                    .toList();
                }
            }

            displayTeachers(
                    teachers
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể lọc danh sách giảng viên.",
                    exception
            );
        }
    }

    private void displayTeachers(
            List<Teacher> teachers
    ) {
        tableModel.setRowCount(0);

        if (teachers == null) {
            teachers =
                    Collections.emptyList();
        }

        for (Teacher teacher : teachers) {
            if (teacher == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            teacher.getTeacherId(),
                            teacher.getTeacherCode(),
                            teacher.getFullName(),
                            teacher.getEmail(),
                            teacher.getPhone(),
                            teacher.getSpecialization(),
                            formatStatus(
                                    teacher.getStatus()
                            ),
                            teacher.getCreatedAt()
                    }
            );
        }

        teacherTable.clearSelection();
        updateButtonState();
    }

    /* =====================================================
       THÊM VÀ CẬP NHẬT
       ===================================================== */

    private void showCreateForm() {
        editingTeacher =
                null;

        clearForm();

        teacherCodeField.setEditable(
                true
        );

        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        confirmPasswordField.setEnabled(true);

        formTitleLabel.setText(
                "Thêm giảng viên"
        );

        saveFormButton.setText(
                "Lưu giảng viên"
        );

        statusComboBox.setSelectedItem(
                "ACTIVE"
        );

        cardLayout.show(
                contentPanel,
                FORM_CARD
        );
    }

    private void showEditForm() {
        int teacherId =
                getSelectedTeacherId();

        if (teacherId <= 0) {
            showWarning(
                    "Vui lòng chọn giảng viên cần cập nhật."
            );

            return;
        }

        try {
            editingTeacher =
                    teacherController.getTeacherById(
                            teacherId
                    );

            if (editingTeacher == null) {
                showWarning(
                        "Không tìm thấy giảng viên."
                );

                return;
            }

            fillForm(
                    editingTeacher
            );

            teacherCodeField.setEditable(
                    true
            );

            formTitleLabel.setText(
                    "Cập nhật giảng viên"
            );

            saveFormButton.setText(
                    "Lưu thay đổi"
            );

            cardLayout.show(
                    contentPanel,
                    FORM_CARD
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tải thông tin giảng viên.",
                    exception
            );
        }
    }

    private void saveTeacher() {
        try {
            Teacher teacher =
                    readTeacherFromForm();

            boolean successful;

            if (editingTeacher == null) {
                String username =
                        requiredText(
                                usernameField,
                                "Tên đăng nhập"
                        );

                String password =
                        new String(
                                passwordField.getPassword()
                        );

                String confirmPassword =
                        new String(
                                confirmPasswordField.getPassword()
                        );

                successful =
                        teacherController.createTeacherAccount(
                                teacher,
                                username,
                                password,
                                confirmPassword
                        );

            } else {
                teacher.setTeacherId(
                        editingTeacher.getTeacherId()
                );

                teacher.setUserId(
                        editingTeacher.getUserId()
                );

                successful =
                        teacherController.updateTeacherAndUser(
                                teacher
                        );
            }

            if (!successful) {
                showWarning(
                        editingTeacher == null
                                ? "Thêm giảng viên thất bại."
                                : "Cập nhật giảng viên thất bại."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    editingTeacher == null
                            ? "Thêm giảng viên thành công."
                            : "Cập nhật giảng viên thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            showListCard();
            loadTeachers();

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể lưu thông tin giảng viên.",
                    exception
            );
        }
    }

    private Teacher readTeacherFromForm() {
        String teacherCode =
                requiredText(
                        teacherCodeField,
                        "Mã giảng viên"
                );

        String fullName =
                requiredText(
                        fullNameField,
                        "Họ và tên"
                );

        String specialization =
                requiredText(
                        specializationField,
                        "Chuyên môn"
                );

        Teacher teacher =
                new Teacher();

        teacher.setTeacherCode(
                teacherCode
        );

        teacher.setFullName(
                fullName
        );

        teacher.setDateOfBirth(
                parseOptionalDate(
                        birthDateField.getText()
                )
        );

        teacher.setGender(
                normalizeOptionalComboValue(
                        genderComboBox
                )
        );

        teacher.setEmail(
                optionalText(
                        emailField
                )
        );

        teacher.setPhone(
                optionalText(
                        phoneField
                )
        );

        teacher.setAddress(
                optionalText(
                        addressArea
                )
        );

        teacher.setSpecialization(
                specialization
        );

        teacher.setStatus(
                String.valueOf(
                        statusComboBox.getSelectedItem()
                )
        );

        return teacher;
    }

    /* =====================================================
       TRẠNG THÁI, XÓA, THỐNG KÊ
       ===================================================== */

    private void toggleSelectedTeacherStatus() {
        int teacherId =
                getSelectedTeacherId();

        if (teacherId <= 0) {
            showWarning(
                    "Vui lòng chọn giảng viên."
            );

            return;
        }

        try {
            Teacher teacher =
                    teacherController.getTeacherById(
                            teacherId
                    );

            if (teacher == null) {
                showWarning(
                        "Không tìm thấy giảng viên."
                );

                return;
            }

            boolean activating =
                    "INACTIVE".equalsIgnoreCase(
                            teacher.getStatus()
                    );

            int answer =
                    JOptionPane.showConfirmDialog(
                            this,
                            activating
                                    ? "Bạn có muốn kích hoạt lại giảng viên này?"
                                    : "Bạn có muốn ngừng hoạt động giảng viên này?",
                            "Xác nhận",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                    );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean successful =
                    activating
                            ? teacherController.activateTeacher(
                            teacherId
                    )
                            : teacherController.deactivateTeacher(
                            teacherId
                    );

            if (!successful) {
                showWarning(
                        "Không thể cập nhật trạng thái giảng viên."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    activating
                            ? "Đã kích hoạt lại giảng viên."
                            : "Đã ngừng hoạt động giảng viên.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadTeachers();

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể cập nhật trạng thái giảng viên.",
                    exception
            );
        }
    }

    private void showAssignedClassCount() {
        int teacherId =
                getSelectedTeacherId();

        if (teacherId <= 0) {
            showWarning(
                    "Vui lòng chọn giảng viên."
            );

            return;
        }

        try {
            Teacher teacher =
                    teacherController.getTeacherById(
                            teacherId
                    );

            int totalClasses =
                    teacherController.countAssignedClasses(
                            teacherId
                    );

            int activeClasses =
                    teacherController.countActiveClasses(
                            teacherId
                    );

            JOptionPane.showMessageDialog(
                    this,
                    "Giảng viên: "
                            + (
                            teacher == null
                                    ? teacherId
                                    : teacher.getFullName()
                    )
                            + "\nTổng số lớp phụ trách: "
                            + totalClasses
                            + "\nLớp đang hoạt động: "
                            + activeClasses,
                    "Thông tin lớp phụ trách",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tải số lớp phụ trách.",
                    exception
            );
        }
    }

    private void deleteSelectedTeacher() {
        int teacherId =
                getSelectedTeacherId();

        if (teacherId <= 0) {
            showWarning(
                    "Vui lòng chọn giảng viên cần xóa."
            );

            return;
        }

        try {
            if (
                    !teacherController.canDeleteTeacher(
                            teacherId
                    )
            ) {
                showWarning(
                        "Giảng viên đang có lớp được phân công. "
                                + "Hãy chuyển sang trạng thái INACTIVE thay vì xóa."
                );

                return;
            }

            int answer =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Bạn có chắc muốn xóa giảng viên này?",
                            "Xác nhận xóa",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE
                    );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean successful =
                    teacherController.deleteTeacher(
                            teacherId
                    );

            if (!successful) {
                showWarning(
                        "Không thể xóa giảng viên."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xóa giảng viên thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadTeachers();

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể xóa giảng viên.",
                    exception
            );
        }
    }

    /* =====================================================
       HỖ TRỢ FORM VÀ BẢNG
       ===================================================== */

    private int getSelectedTeacherId() {
        int selectedRow =
                teacherTable.getSelectedRow();

        if (selectedRow < 0) {
            return -1;
        }

        int modelRow =
                teacherTable.convertRowIndexToModel(
                        selectedRow
                );

        Object value =
                tableModel.getValueAt(
                        modelRow,
                        0
                );

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(
                    String.valueOf(value)
            );

        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private void updateButtonState() {
        int teacherId =
                getSelectedTeacherId();

        boolean hasSelection =
                teacherId > 0;

        editButton.setEnabled(
                hasSelection
        );

        statusButton.setEnabled(
                hasSelection
        );

        classesButton.setEnabled(
                hasSelection
        );

        deleteButton.setEnabled(
                hasSelection
        );

        if (!hasSelection) {
            statusButton.setText(
                    "Ngừng hoạt động"
            );

            return;
        }

        try {
            Teacher teacher =
                    teacherController.getTeacherById(
                            teacherId
                    );

            boolean inactive =
                    teacher != null
                            && "INACTIVE".equalsIgnoreCase(
                            teacher.getStatus()
                    );

            statusButton.setText(
                    inactive
                            ? "Kích hoạt"
                            : "Ngừng hoạt động"
            );

        } catch (RuntimeException exception) {
            statusButton.setText(
                    "Ngừng hoạt động"
            );
        }
    }

    private void fillForm(
            Teacher teacher
    ) {
        teacherIdField.setText(
                String.valueOf(
                        teacher.getTeacherId()
                )
        );

        teacherCodeField.setText(
                safeText(
                        teacher.getTeacherCode()
                )
        );

        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");

        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        confirmPasswordField.setEnabled(false);

        fullNameField.setText(
                safeText(
                        teacher.getFullName()
                )
        );

        birthDateField.setText(
                teacher.getDateOfBirth() == null
                        ? ""
                        : teacher.getDateOfBirth()
                        .toString()
        );

        genderComboBox.setSelectedItem(
                teacher.getGender() == null
                        ? ""
                        : teacher.getGender()
        );

        emailField.setText(
                safeText(
                        teacher.getEmail()
                )
        );

        phoneField.setText(
                safeText(
                        teacher.getPhone()
                )
        );

        addressArea.setText(
                safeText(
                        teacher.getAddress()
                )
        );

        specializationField.setText(
                safeText(
                        teacher.getSpecialization()
                )
        );

        statusComboBox.setSelectedItem(
                teacher.getStatus() == null
                        ? "ACTIVE"
                        : teacher.getStatus()
        );
    }

    private void clearForm() {
        teacherIdField.setText("");
        teacherCodeField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");

        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        confirmPasswordField.setEnabled(true);
        fullNameField.setText("");
        birthDateField.setText("");
        genderComboBox.setSelectedIndex(0);
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        specializationField.setText("");
        statusComboBox.setSelectedItem("ACTIVE");
    }

    private void showListCard() {
        editingTeacher =
                null;

        clearForm();

        cardLayout.show(
                contentPanel,
                LIST_CARD
        );
    }

    /* =====================================================
       VALIDATION VÀ COMPONENT
       ===================================================== */

    private String requiredText(
            JTextField field,
            String fieldName
    ) {
        String value =
                field.getText() == null
                        ? ""
                        : field.getText().trim();

        if (value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        return value;
    }

    private String optionalText(
            JTextField field
    ) {
        if (field.getText() == null) {
            return null;
        }

        String value =
                field.getText().trim();

        return value.isBlank()
                ? null
                : value;
    }

    private String optionalText(
            JTextArea area
    ) {
        if (area.getText() == null) {
            return null;
        }

        String value =
                area.getText().trim();

        return value.isBlank()
                ? null
                : value;
    }

    private Integer parseOptionalPositiveInteger(
            String value,
            String fieldName
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
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

    private Date parseOptionalDate(
            String value
    ) {
        if (
                value == null
                        || value.isBlank()
        ) {
            return null;
        }

        try {
            return Date.valueOf(
                    LocalDate.parse(
                            value.trim()
                    )
            );

        } catch (
                DateTimeParseException
                | IllegalArgumentException exception
        ) {
            throw new IllegalArgumentException(
                    "Ngày sinh phải có định dạng yyyy-MM-dd."
            );
        }
    }

    private String normalizeOptionalComboValue(
            JComboBox<String> comboBox
    ) {
        Object value =
                comboBox.getSelectedItem();

        if (value == null) {
            return null;
        }

        String text =
                String.valueOf(value)
                        .trim();

        return text.isBlank()
                ? null
                : text;
    }

    private boolean containsIgnoreCase(
            String value,
            String lowerKeyword
    ) {
        return value != null
                && value.toLowerCase()
                .contains(
                        lowerKeyword
                );
    }

    private String formatStatus(
            String status
    ) {
        if ("INACTIVE".equalsIgnoreCase(status)) {
            return "Ngừng hoạt động";
        }

        return "Đang hoạt động";
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value;
    }

    private JLabel createFormLabel(
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

    private void configureTextField(
            JTextField field,
            String placeholder
    ) {
        field.setFont(
                UIConstants.FONT_NORMAL
        );

        field.putClientProperty(
                "JTextField.placeholderText",
                placeholder
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
    }

    private void configurePasswordField(
            JPasswordField field,
            String placeholder
    ) {
        field.setFont(
                UIConstants.FONT_NORMAL
        );

        field.putClientProperty(
                "JTextField.placeholderText",
                placeholder
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
    }

    private void configureComboBox(
            JComboBox<String> comboBox
    ) {
        comboBox.setFont(
                UIConstants.FONT_NORMAL
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

    private JButton createButton(
            String text,
            Color background,
            Color foreground
    ) {
        JButton button =
                new JButton(text);

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setBackground(
                background
        );

        button.setForeground(
                foreground
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
                focusWidth: 0;
                margin: 7,12,7,12;
                """
        );

        return button;
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
        Throwable root =
                throwable;

        while (
                root != null
                        && root.getCause() != null
        ) {
            root =
                    root.getCause();
        }

        String detail =
                root == null
                        || root.getMessage() == null
                        || root.getMessage().isBlank()
                        ? "Không xác định"
                        : root.getMessage();

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nChi tiết: "
                        + detail,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

package view;

import controller.StudentController;
import model.Student;
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
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class StudentManagementView extends JPanel {

    private static final String ACTIVE_STATUS =
            "ACTIVE";

    private static final String INACTIVE_STATUS =
            "INACTIVE";

    private final StudentController controller;

    private JTextField txtStudentId;
    private JTextField txtStudentCode;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;

    private JTextField txtFullName;
    private JTextField txtDateOfBirth;
    private JComboBox<String> cboGender;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;
    private JComboBox<String> cboStatus;
    private JTextField txtSearch;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnClear;

    private JLabel resultLabel;
    private JLabel totalStudentLabel;
    private JLabel accountHintLabel;

    private boolean loading;

    public StudentManagementView() {
        controller =
                new StudentController();

        initializeView();
        registerEvents();
        loadStudents();
    }

    /* =====================================================
       KHỞI TẠO VIEW
       ===================================================== */

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, insets 16, wrap 1",
                        "[grow, fill]",
                        "[]14[]14[grow, fill]"
                )
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        add(
                createPageHeader(),
                "growx"
        );

        add(
                createStudentForm(),
                "growx"
        );

        add(
                createStudentTablePanel(),
                "grow, push"
        );
    }

    /* =====================================================
       HEADER
       ===================================================== */

    private JPanel createPageHeader() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]12[300!, fill]8[]",
                                "[center]"
                        )
                );

        panel.setOpaque(false);

        JPanel titlePanel =
                new JPanel(
                        new MigLayout(
                                "insets 0, wrap 1",
                                "[grow]",
                                "[]2[]"
                        )
                );

        titlePanel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Quản lý học viên"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel =
                new JLabel(
                        "Quản lý tài khoản đăng nhập và hồ sơ học viên"
                );

        subtitleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        txtSearch =
                new JTextField();

        txtSearch.setPreferredSize(
                new Dimension(
                        300,
                        38
                )
        );

        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Tìm mã, tên, email, số điện thoại..."
        );

        txtSearch.putClientProperty(
                "JTextField.leadingIcon",
                FontIcon.of(
                        FontAwesomeSolid.SEARCH,
                        14,
                        UIConstants.TEXT_SECONDARY
                )
        );

        txtSearch.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                """
        );

        btnRefresh =
                createButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        btnRefresh.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #2563EB;
                borderWidth: 1;
                focusWidth: 0;
                margin: 7,10,7,10;
                """
        );

        panel.add(
                titlePanel,
                "growx"
        );

        panel.add(
                txtSearch,
                "height 38!"
        );

        panel.add(
                btnRefresh,
                "height 38!"
        );

        return panel;
    }

    /* =====================================================
       FORM
       ===================================================== */

    private JPanel createStudentForm() {
        ContentCard container =
                new ContentCard();

        container.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]12[]10[]14[]"
                )
        );

        container.add(
                createFormHeader(),
                "growx"
        );

        container.add(
                createAccountHintPanel(),
                "growx"
        );

        container.add(
                createFormFields(),
                "growx"
        );

        container.add(
                createFormButtonPanel(),
                "growx"
        );

        return container;
    }

    private JPanel createFormHeader() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]",
                                "[center]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Thông tin học viên"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel hintLabel =
                new JLabel(
                        "Tạo tài khoản khi thêm mới; chọn một dòng để cập nhật"
                );

        hintLabel.setFont(
                UIConstants.FONT_SMALL
        );

        hintLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleLabel);
        panel.add(hintLabel);

        return panel;
    }

    private JPanel createAccountHintPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 8 12",
                                "[grow]",
                                "[]"
                        )
                );

        panel.setBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 10;"
        );

        accountHintLabel =
                new JLabel(
                        "Khi thêm mới: bắt buộc nhập tên đăng nhập, mật khẩu và xác nhận mật khẩu."
                );

        accountHintLabel.setFont(
                UIConstants.FONT_SMALL
        );

        accountHintLabel.setForeground(
                UIConstants.PRIMARY
        );

        panel.add(
                accountHintLabel,
                "growx"
        );

        return panel;
    }

    private JPanel createFormFields() {
        JPanel formPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 4, insets 0",
                                "[grow, fill][grow, fill][grow, fill][grow, fill]",
                                "[]5[]11[]5[]11[]5[]11[]5[]"
                        )
                );

        formPanel.setOpaque(false);

        txtStudentId =
                createTextField();

        txtStudentId.setEditable(false);

        txtStudentId.setBackground(
                new Color(
                        248,
                        250,
                        252
                )
        );

        txtStudentCode =
                createTextField();

        txtUsername =
                createTextField();

        txtPassword =
                createPasswordField(
                        "Mật khẩu tối thiểu 6 ký tự"
                );

        txtConfirmPassword =
                createPasswordField(
                        "Nhập lại mật khẩu"
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
                createComboBox(
                        new String[]{
                                "Nam",
                                "Nữ",
                                "Khác"
                        }
                );

        txtPhone =
                createTextField();

        txtEmail =
                createTextField();

        txtAddress =
                createTextField();

        cboStatus =
                createComboBox(
                        new String[]{
                                ACTIVE_STATUS,
                                INACTIVE_STATUS
                        }
                );

        formPanel.add(
                createFieldLabel(
                        "ID học viên"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Mã học viên *"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Tên đăng nhập *"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Mật khẩu *"
                )
        );

        formPanel.add(txtStudentId);
        formPanel.add(txtStudentCode);
        formPanel.add(txtUsername);
        formPanel.add(txtPassword);

        formPanel.add(
                createFieldLabel(
                        "Xác nhận mật khẩu *"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Họ và tên *"
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

        formPanel.add(txtConfirmPassword);
        formPanel.add(txtFullName);
        formPanel.add(txtDateOfBirth);
        formPanel.add(cboGender);

        formPanel.add(
                createFieldLabel(
                        "Số điện thoại"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Email"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Địa chỉ"
                )
        );

        formPanel.add(
                createFieldLabel(
                        "Trạng thái"
                )
        );

        formPanel.add(txtPhone);
        formPanel.add(txtEmail);
        formPanel.add(txtAddress);
        formPanel.add(cboStatus);

        return formPanel;
    }

    private JPanel createFormButtonPanel() {
        JPanel buttonPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow]8[]8[]8[]8[]",
                                "[]"
                        )
                );

        buttonPanel.setOpaque(false);

        btnAdd =
                createButton(
                        "Thêm mới",
                        FontAwesomeSolid.PLUS,
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        btnUpdate =
                createButton(
                        "Cập nhật",
                        FontAwesomeSolid.EDIT,
                        UIConstants.WARNING,
                        Color.WHITE
                );

        btnDelete =
                createButton(
                        "Xóa",
                        FontAwesomeSolid.TRASH_ALT,
                        UIConstants.DANGER,
                        Color.WHITE
                );

        btnClear =
                createButton(
                        "Nhập lại",
                        FontAwesomeSolid.ERASER,
                        Color.WHITE,
                        UIConstants.TEXT_PRIMARY
                );

        btnClear.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #E2E8F0;
                borderWidth: 1;
                focusWidth: 0;
                margin: 7,11,7,11;
                """
        );

        buttonPanel.add(
                new JLabel(),
                "growx"
        );

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        return buttonPanel;
    }

    /* =====================================================
       BẢNG HỌC VIÊN
       ===================================================== */

    private JPanel createStudentTablePanel() {
        ContentCard container =
                new ContentCard();

        container.setLayout(
                new BorderLayout(
                        0,
                        12
                )
        );

        container.setBorder(
                BorderFactory.createEmptyBorder(
                        18,
                        20,
                        18,
                        20
                )
        );

        container.add(
                createTableHeader(),
                BorderLayout.NORTH
        );

        initializeStudentTable();

        JScrollPane scrollPane =
                new JScrollPane(
                        studentTable
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
                .setUnitIncrement(16);

        scrollPane.setMinimumSize(
                new Dimension(
                        500,
                        260
                )
        );

        container.add(
                scrollPane,
                BorderLayout.CENTER
        );

        container.add(
                createTableFooter(),
                BorderLayout.SOUTH
        );

        return container;
    }

    private JPanel createTableHeader() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]",
                                "[center]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Danh sách học viên"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        totalStudentLabel =
                new JLabel(
                        "0 học viên"
                );

        totalStudentLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        totalStudentLabel.setForeground(
                UIConstants.PRIMARY
        );

        panel.add(titleLabel);
        panel.add(totalStudentLabel);

        return panel;
    }

    private void initializeStudentTable() {
        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Mã học viên",
                                "Họ và tên",
                                "Ngày sinh",
                                "Giới tính",
                                "Email",
                                "Số điện thoại",
                                "Địa chỉ",
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

        studentTable =
                new JTable(
                        tableModel
                );

        studentTable.setRowHeight(42);
        studentTable.setFillsViewportHeight(true);

        studentTable.setFont(
                UIConstants.FONT_SMALL
        );

        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        studentTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        studentTable.setShowVerticalLines(false);
        studentTable.setShowHorizontalLines(true);

        studentTable.setGridColor(
                UIConstants.BORDER
        );

        studentTable.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        studentTable.setSelectionBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        studentTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        studentTable
                .getTableHeader()
                .setReorderingAllowed(false);

        studentTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM.deriveFont(
                                Font.BOLD
                        )
                );

        studentTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                42
                        )
                );

        studentTable
                .getTableHeader()
                .setBackground(
                        new Color(
                                248,
                                250,
                                252
                        )
                );

        studentTable
                .getTableHeader()
                .setForeground(
                        UIConstants.TEXT_PRIMARY
                );

        configureColumnWidths();
        configureTableRenderers();

        tableSorter =
                new TableRowSorter<>(
                        tableModel
                );

        studentTable.setRowSorter(
                tableSorter
        );
    }

    private void configureColumnWidths() {
        setColumnWidth(0, 55);
        setColumnWidth(1, 115);
        setColumnWidth(2, 180);
        setColumnWidth(3, 105);
        setColumnWidth(4, 85);
        setColumnWidth(5, 200);
        setColumnWidth(6, 125);
        setColumnWidth(7, 210);
        setColumnWidth(8, 110);
    }

    private void setColumnWidth(
            int column,
            int width
    ) {
        studentTable
                .getColumnModel()
                .getColumn(column)
                .setPreferredWidth(width);
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        int[] centeredColumns = {
                0,
                3,
                4,
                8
        };

        for (int column : centeredColumns) {
            studentTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        studentTable
                .getColumnModel()
                .getColumn(8)
                .setCellRenderer(
                        new StatusCellRenderer()
                );
    }

    private JPanel createTableFooter() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        resultLabel =
                new JLabel(
                        "Chưa có học viên"
                );

        resultLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        resultLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                resultLabel,
                "growx"
        );

        return panel;
    }

    /* =====================================================
       EVENTS
       ===================================================== */

    private void registerEvents() {
        btnRefresh.addActionListener(
                event -> {
                    txtSearch.setText("");
                    clearForm();
                    loadStudents();
                }
        );

        btnAdd.addActionListener(
                event -> addStudent()
        );

        btnUpdate.addActionListener(
                event -> updateStudent()
        );

        btnDelete.addActionListener(
                event -> deleteStudent()
        );

        btnClear.addActionListener(
                event -> clearForm()
        );

        txtSearch
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent event
                            ) {
                                filterStudents();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event
                            ) {
                                filterStudents();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event
                            ) {
                                filterStudents();
                            }
                        }
                );

        studentTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                fillFormFromSelectedRow();
                                updateButtonState();
                            }
                        }
                );

        studentTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (
                                event.getClickCount() == 2
                                        && studentTable
                                        .getSelectedRow() >= 0
                        ) {
                            txtFullName.requestFocus();
                        }
                    }
                }
        );

        updateButtonState();
    }

    /* =====================================================
       LOAD / FILTER
       ===================================================== */

    public void loadStudents() {
        if (loading) {
            return;
        }

        loading = true;
        tableModel.setRowCount(0);

        try {
            List<Student> students =
                    controller.getAllStudents();

            if (students == null) {
                students =
                        Collections.emptyList();
            }

            for (Student student : students) {
                if (student == null) {
                    continue;
                }

                tableModel.addRow(
                        new Object[]{
                                student.getStudentID(),
                                safeText(
                                        student.getStudentCode()
                                ),
                                safeText(
                                        student.getFullName()
                                ),
                                student.getDateOfBirth(),
                                safeText(
                                        student.getGender()
                                ),
                                safeText(
                                        student.getEmail()
                                ),
                                safeText(
                                        student.getPhone()
                                ),
                                safeText(
                                        student.getAddress()
                                ),
                                safeText(
                                        student.getStatus()
                                )
                        }
                );
            }

            tableSorter.setRowFilter(null);

            updateStudentSummary();

        } catch (RuntimeException exception) {
            tableModel.setRowCount(0);
            updateStudentSummary();

            showError(
                    "Không thể tải danh sách học viên.",
                    exception
            );

        } finally {
            loading = false;
        }
    }

    private void filterStudents() {
        if (tableSorter == null) {
            return;
        }

        String keyword =
                txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            tableSorter.setRowFilter(null);

        } else {
            tableSorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)"
                                    + Pattern.quote(
                                    keyword
                            )
                    )
            );
        }

        updateStudentSummary();
        clearFormSelectionOnly();
    }

    private void updateStudentSummary() {
        int totalRows =
                tableModel.getRowCount();

        int visibleRows =
                studentTable == null
                        ? totalRows
                        : studentTable.getRowCount();

        totalStudentLabel.setText(
                totalRows
                        + " học viên"
        );

        if (totalRows == 0) {
            resultLabel.setText(
                    "Chưa có học viên trong hệ thống"
            );

        } else if (
                txtSearch != null
                        && !txtSearch.getText()
                        .isBlank()
        ) {
            resultLabel.setText(
                    "Tìm thấy "
                            + visibleRows
                            + " trong "
                            + totalRows
                            + " học viên"
            );

        } else {
            resultLabel.setText(
                    "Đang hiển thị "
                            + totalRows
                            + " học viên"
            );
        }
    }

    /* =====================================================
       THÊM HỌC VIÊN + TÀI KHOẢN
       ===================================================== */

    private void addStudent() {
        if (!validateForm(false)) {
            return;
        }

        try {
            Student student =
                    readStudentFromForm();

            String username =
                    txtUsername
                            .getText()
                            .trim();

            String password =
                    new String(
                            txtPassword.getPassword()
                    );

            String confirmPassword =
                    new String(
                            txtConfirmPassword.getPassword()
                    );

            boolean successful =
                    controller.createStudentAccount(
                            student,
                            username,
                            password,
                            confirmPassword
                    );

            if (!successful) {
                showError(
                        "Không thể thêm học viên."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã tạo tài khoản và hồ sơ học viên thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadStudents();
            clearForm();

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể thêm học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       CẬP NHẬT
       ===================================================== */

    private void updateStudent() {
        if (!validateForm(true)) {
            return;
        }

        try {
            Student student =
                    readStudentFromForm();

            student.setStudentID(
                    Integer.parseInt(
                            txtStudentId
                                    .getText()
                                    .trim()
                    )
            );

            Student currentStudent =
                    controller.getStudentById(
                            student.getStudentID()
                    );

            if (currentStudent == null) {
                showWarning(
                        "Không tìm thấy học viên cần cập nhật."
                );

                return;
            }

            student.setUserId(
                    currentStudent.getUserId()
            );

            boolean successful =
                    controller.updateStudentAndUser(
                            student
                    );

            if (!successful) {
                showError(
                        "Không thể cập nhật học viên."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật học viên và tài khoản thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadStudents();
            clearForm();

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể cập nhật học viên.",
                    exception
            );
        }
    }

    /* =====================================================
       XÓA
       ===================================================== */

    private void deleteStudent() {
        String idText =
                txtStudentId
                        .getText()
                        .trim();

        if (idText.isEmpty()) {
            showWarning(
                    "Vui lòng chọn học viên cần xóa."
            );

            return;
        }

        String studentName =
                txtFullName
                        .getText()
                        .trim();

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa học viên"
                                + (
                                studentName.isEmpty()
                                        ? " này?"
                                        : " \""
                                        + studentName
                                        + "\"?"
                        ),
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int studentId =
                    Integer.parseInt(
                            idText
                    );

            boolean successful =
                    controller.deleteStudent(
                            studentId
                    );

            if (!successful) {
                showError(
                        "Không thể xóa học viên."
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xóa học viên thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadStudents();
            clearForm();

        } catch (RuntimeException exception) {
            showError(
                    "Không thể xóa học viên. "
                            + "Học viên có thể đang liên kết với dữ liệu khác.",
                    exception
            );
        }
    }

    /* =====================================================
       ĐỌC FORM
       ===================================================== */

    private Student readStudentFromForm() {
        Student student =
                new Student();

        student.setStudentCode(
                txtStudentCode
                        .getText()
                        .trim()
        );

        student.setFullName(
                txtFullName
                        .getText()
                        .trim()
        );

        String dateText =
                txtDateOfBirth
                        .getText()
                        .trim();

        if (!dateText.isEmpty()) {
            student.setDateOfBirth(
                    Date.valueOf(
                            dateText
                    )
            );
        }

        student.setGender(
                String.valueOf(
                        cboGender
                                .getSelectedItem()
                )
        );

        student.setPhone(
                txtPhone
                        .getText()
                        .trim()
        );

        student.setEmail(
                txtEmail
                        .getText()
                        .trim()
        );

        student.setAddress(
                txtAddress
                        .getText()
                        .trim()
        );

        student.setStatus(
                String.valueOf(
                        cboStatus
                                .getSelectedItem()
                )
        );

        return student;
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private boolean validateForm(
            boolean requireId
    ) {
        if (
                requireId
                        && txtStudentId
                        .getText()
                        .trim()
                        .isEmpty()
        ) {
            showWarning(
                    "Vui lòng chọn học viên cần cập nhật."
            );

            return false;
        }

        if (
                txtStudentCode
                        .getText()
                        .trim()
                        .isEmpty()
        ) {
            showWarning(
                    "Mã học viên không được để trống."
            );

            txtStudentCode.requestFocus();
            return false;
        }

        if (!requireId) {
            String username =
                    txtUsername
                            .getText()
                            .trim();

            String password =
                    new String(
                            txtPassword.getPassword()
                    );

            String confirmPassword =
                    new String(
                            txtConfirmPassword.getPassword()
                    );

            if (username.isEmpty()) {
                showWarning(
                        "Tên đăng nhập không được để trống."
                );

                txtUsername.requestFocus();
                return false;
            }

            if (password.length() < 6) {
                showWarning(
                        "Mật khẩu phải có ít nhất 6 ký tự."
                );

                txtPassword.requestFocus();
                return false;
            }

            if (!password.equals(confirmPassword)) {
                showWarning(
                        "Mật khẩu xác nhận không khớp."
                );

                txtConfirmPassword.requestFocus();
                return false;
            }
        }

        if (
                txtFullName
                        .getText()
                        .trim()
                        .isEmpty()
        ) {
            showWarning(
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
                Date.valueOf(
                        dateText
                );

            } catch (IllegalArgumentException exception) {
                showWarning(
                        "Ngày sinh phải đúng định dạng yyyy-MM-dd."
                );

                txtDateOfBirth.requestFocus();
                return false;
            }
        }

        String phone =
                txtPhone
                        .getText()
                        .trim();

        if (
                !phone.isEmpty()
                        && !phone.matches(
                        "^[0-9]{9,11}$"
                )
        ) {
            showWarning(
                    "Số điện thoại phải gồm từ 9 đến 11 chữ số."
            );

            txtPhone.requestFocus();
            return false;
        }

        String email =
                txtEmail
                        .getText()
                        .trim();

        if (
                !email.isEmpty()
                        && !email.matches(
                        "^[\\w.!#$%&'*+/=?^`{|}~-]+"
                                + "@[\\w.-]+"
                                + "\\.[A-Za-z]{2,}$"
                )
        ) {
            showWarning(
                    "Email không đúng định dạng."
            );

            txtEmail.requestFocus();
            return false;
        }

        return true;
    }

    /* =====================================================
       CHỌN DÒNG
       ===================================================== */

    private void fillFormFromSelectedRow() {
        int selectedRow =
                studentTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                studentTable.convertRowIndexToModel(
                        selectedRow
                );

        txtStudentId.setText(
                valueAt(
                        modelRow,
                        0
                )
        );

        txtStudentCode.setText(
                valueAt(
                        modelRow,
                        1
                )
        );

        txtFullName.setText(
                valueAt(
                        modelRow,
                        2
                )
        );

        txtDateOfBirth.setText(
                valueAt(
                        modelRow,
                        3
                )
        );

        cboGender.setSelectedItem(
                valueAt(
                        modelRow,
                        4
                )
        );

        txtEmail.setText(
                valueAt(
                        modelRow,
                        5
                )
        );

        txtPhone.setText(
                valueAt(
                        modelRow,
                        6
                )
        );

        txtAddress.setText(
                valueAt(
                        modelRow,
                        7
                )
        );

        cboStatus.setSelectedItem(
                valueAt(
                        modelRow,
                        8
                )
        );

        setAccountFieldsForUpdateMode();
    }

    private void setAccountFieldsForUpdateMode() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtConfirmPassword.setEnabled(false);

        accountHintLabel.setText(
                "Chế độ cập nhật: thông tin tài khoản cơ bản sẽ được đồng bộ; "
                        + "username và mật khẩu không thay đổi tại đây."
        );
    }

    private void setAccountFieldsForCreateMode() {
        txtUsername.setEnabled(true);
        txtPassword.setEnabled(true);
        txtConfirmPassword.setEnabled(true);

        accountHintLabel.setText(
                "Khi thêm mới: bắt buộc nhập tên đăng nhập, mật khẩu và xác nhận mật khẩu."
        );
    }

    private String valueAt(
            int row,
            int column
    ) {
        Object value =
                tableModel.getValueAt(
                        row,
                        column
                );

        return value == null
                ? ""
                : value.toString();
    }

    /* =====================================================
       CLEAR FORM
       ===================================================== */

    private void clearForm() {
        txtStudentId.setText("");
        txtStudentCode.setText("");

        txtUsername.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");

        txtFullName.setText("");
        txtDateOfBirth.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAddress.setText("");

        cboGender.setSelectedIndex(0);

        cboStatus.setSelectedItem(
                ACTIVE_STATUS
        );

        studentTable.clearSelection();

        setAccountFieldsForCreateMode();
        updateButtonState();

        txtStudentCode.requestFocus();
    }

    private void clearFormSelectionOnly() {
        studentTable.clearSelection();
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasSelection =
                studentTable != null
                        && studentTable
                        .getSelectedRow() >= 0
                        && !txtStudentId
                        .getText()
                        .isBlank();

        btnUpdate.setEnabled(
                hasSelection
        );

        btnDelete.setEnabled(
                hasSelection
        );

        btnAdd.setEnabled(
                !hasSelection
        );

        if (!hasSelection) {
            setAccountFieldsForCreateMode();
        }
    }

    /* =====================================================
       COMPONENT HELPERS
       ===================================================== */

    private JTextField createTextField() {
        JTextField textField =
                new JTextField();

        textField.setPreferredSize(
                new Dimension(
                        0,
                        36
                )
        );

        textField.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                margin: 6,9,6,9;
                """
        );

        return textField;
    }

    private JPasswordField createPasswordField(
            String placeholder
    ) {
        JPasswordField field =
                new JPasswordField();

        field.setPreferredSize(
                new Dimension(
                        0,
                        36
                )
        );

        field.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                margin: 6,9,6,9;
                """
        );

        return field;
    }

    private JComboBox<String> createComboBox(
            String[] values
    ) {
        JComboBox<String> comboBox =
                new JComboBox<>(
                        values
                );

        comboBox.setPreferredSize(
                new Dimension(
                        0,
                        36
                )
        );

        comboBox.putClientProperty(
                "FlatLaf.style",
                "arc: 9"
        );

        return comboBox;
    }

    private JLabel createFieldLabel(
            String text
    ) {
        JLabel label =
                new JLabel(
                        text
                );

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

    private JButton createButton(
            String text,
            FontAwesomeSolid icon,
            Color background,
            Color foreground
    ) {
        JButton button =
                new JButton(
                        text
                );

        button.setIcon(
                FontIcon.of(
                        icon,
                        13,
                        foreground
                )
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
        );

        button.setBackground(background);
        button.setForeground(foreground);
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
                margin: 7,11,7,11;
                """
        );

        return button;
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
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
            String message
    ) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
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

        showError(
                message
                        + "\nChi tiết: "
                        + detail
        );
    }

    /* =====================================================
       STATUS RENDERER
       ===================================================== */

    private static class StatusCellRenderer
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
            String status =
                    value == null
                            ? ""
                            : value.toString()
                            .trim()
                            .toUpperCase(
                                    Locale.ROOT
                            );

            String displayText =
                    switch (status) {
                        case ACTIVE_STATUS ->
                                "● Hoạt động";

                        case INACTIVE_STATUS ->
                                "● Ngừng hoạt động";

                        default ->
                                status;
                    };

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            displayText,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setFont(
                    UIConstants.FONT_SMALL.deriveFont(
                            Font.BOLD
                    )
            );

            if (!isSelected) {
                setBackground(
                        Color.WHITE
                );

                setForeground(
                        ACTIVE_STATUS.equals(
                                status
                        )
                                ? UIConstants.SUCCESS
                                : INACTIVE_STATUS.equals(
                                status
                        )
                                ? UIConstants.DANGER
                                : UIConstants.TEXT_SECONDARY
                );
            }

            return component;
        }
    }
}
package view;

import controller.UserController;
import model.AccountStatus;
import model.Role;
import model.Student;
import model.Teacher;
import model.User;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.SessionManager;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class UserManagementView extends JPanel {

    private static final String CARD_LIST =
            "USER_LIST";

    private static final String CARD_FORM =
            "USER_FORM";

    private static final int ROWS_PER_PAGE =
            8;

    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy"
            );

    private static final DateTimeFormatter INPUT_DATE_FORMAT =
            DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd"
            );

    private final UserController userController;

    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final DefaultTableModel tableModel;
    private final JTable userTable;

    private final JTextField searchField;
    private final JLabel resultLabel;
    private final JLabel pageLabel;
    private final JLabel totalUserLabel;

    private JButton previousButton;
    private JButton nextButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton lockButton;
    private JButton resetPasswordButton;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton exportButton;

    private List<User> allUsers =
            Collections.emptyList();

    private int currentPage =
            1;

    private boolean loading;

    /* =====================================================
       FORM
       ===================================================== */

    private JLabel formTitleLabel;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;

    private JComboBox<Role> roleComboBox;
    private JComboBox<AccountStatus> statusComboBox;

    private JPanel roleSpecificPanel;
    private CardLayout roleSpecificLayout;

    private JTextField studentCodeField;
    private JTextField studentBirthDateField;
    private JComboBox<String> studentGenderComboBox;
    private JTextArea studentAddressArea;

    private JTextField teacherCodeField;
    private JTextField teacherBirthDateField;
    private JComboBox<String> teacherGenderComboBox;
    private JTextField teacherSpecializationField;
    private JTextArea teacherAddressArea;

    private JButton saveFormButton;
    private JButton cancelFormButton;

    private User editingUser;

    public UserManagementView() {
        this.userController =
                new UserController();

        this.cardLayout =
                new CardLayout();

        this.cardPanel =
                new JPanel(
                        cardLayout
                );

        this.searchField =
                new JTextField();

        this.resultLabel =
                new JLabel();

        this.pageLabel =
                new JLabel();

        this.totalUserLabel =
                new JLabel(
                        "0 tài khoản"
                );

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID",
                                "Tên đăng nhập",
                                "Họ và tên",
                                "Email",
                                "Số điện thoại",
                                "Vai trò",
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

        this.userTable =
                new JTable(
                        tableModel
                );

        initializeView();
        registerEvents();
        loadUsers();
    }

    /* =====================================================
       KHỞI TẠO VIEW
       ===================================================== */

    private void initializeView() {
        setLayout(
                new BorderLayout()
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        cardPanel.setOpaque(false);

        cardPanel.add(
                createListCard(),
                CARD_LIST
        );

        cardPanel.add(
                createFormCard(),
                CARD_FORM
        );

        add(
                cardPanel,
                BorderLayout.CENTER
        );

        showListCard();
    }

    /* =====================================================
       DANH SÁCH
       ===================================================== */

    private JPanel createListCard() {
        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, insets 16",
                                "[grow,fill]",
                                "[grow,fill]"
                        )
                );

        wrapper.setOpaque(false);

        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow,fill]",
                        "[]14[]12[grow,fill]12[]"
                )
        );

        card.add(
                createTitlePanel(),
                "growx"
        );

        card.add(
                createToolbar(),
                "growx"
        );

        card.add(
                createTableScrollPane(),
                "grow,push"
        );

        card.add(
                createPaginationPanel(),
                "growx"
        );

        wrapper.add(
                card,
                "grow,push"
        );

        return wrapper;
    }

    private JPanel createTitlePanel() {
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
                        "Quản lý tài khoản"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Thêm, cập nhật, khóa và duyệt tài khoản trong hệ thống"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalUserLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(
                                Font.BOLD
                        )
        );

        totalUserLabel.setForeground(
                UIConstants.PRIMARY
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
                totalUserLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    private JPanel createToolbar() {
        JPanel toolbar =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0, gapy 8",
                                "[grow,fill]",
                                "[][]"
                        )
                );

        toolbar.setOpaque(false);
        configureSearchField();

        JButton refreshButton =
                createToolbarButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        JButton addButton =
                createToolbarButton(
                        "Thêm tài khoản",
                        FontAwesomeSolid.PLUS,
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        editButton =
                createToolbarButton(
                        "Cập nhật",
                        FontAwesomeSolid.EDIT,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        lockButton =
                createToolbarButton(
                        "Khóa/Mở",
                        FontAwesomeSolid.LOCK,
                        Color.WHITE,
                        UIConstants.WARNING
                );

        resetPasswordButton =
                createToolbarButton(
                        "Reset MK",
                        FontAwesomeSolid.KEY,
                        Color.WHITE,
                        UIConstants.PURPLE
                );

        approveButton =
                createToolbarButton(
                        "Duyệt",
                        FontAwesomeSolid.CHECK,
                        UIConstants.SUCCESS,
                        Color.WHITE
                );

        rejectButton =
                createToolbarButton(
                        "Từ chối",
                        FontAwesomeSolid.TIMES,
                        Color.WHITE,
                        UIConstants.DANGER
                );

        deleteButton =
                createToolbarButton(
                        "Xóa",
                        FontAwesomeSolid.TRASH_ALT,
                        Color.WHITE,
                        UIConstants.DANGER
                );

        exportButton =
                createToolbarButton(
                        "Xuất CSV",
                        FontAwesomeSolid.DOWNLOAD,
                        UIConstants.SUCCESS,
                        Color.WHITE
                );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    currentPage = 1;
                    loadUsers();
                }
        );

        addButton.addActionListener(
                event -> showCreateForm()
        );

        editButton.addActionListener(
                event -> showEditForm()
        );

        lockButton.addActionListener(
                event -> toggleSelectedUserLock()
        );

        resetPasswordButton.addActionListener(
                event -> resetSelectedUserPassword()
        );

        approveButton.addActionListener(
                event -> approveSelectedTeacher()
        );

        rejectButton.addActionListener(
                event -> rejectSelectedTeacher()
        );

        deleteButton.addActionListener(
                event -> deleteSelectedUser()
        );

        exportButton.addActionListener(
                event -> exportCsv()
        );

        JPanel searchRow =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 10",
                                "[grow,fill][][]",
                                "[]"
                        )
                );

        searchRow.setOpaque(false);

        searchRow.add(
                searchField,
                "growx, height 38!"
        );

        searchRow.add(
                refreshButton,
                "height 38!"
        );

        searchRow.add(
                addButton,
                "height 38!"
        );

        JPanel actionRow =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0, gapx 7",
                                "[grow][][][][][][][]",
                                "[]"
                        )
                );

        actionRow.setOpaque(false);

        actionRow.add(
                new JLabel(),
                "growx"
        );

        actionRow.add(editButton);
        actionRow.add(lockButton);
        actionRow.add(resetPasswordButton);
        actionRow.add(approveButton);
        actionRow.add(rejectButton);
        actionRow.add(deleteButton);
        actionRow.add(exportButton);

        toolbar.add(
                searchRow,
                "growx"
        );

        toolbar.add(
                actionRow,
                "growx"
        );

        return toolbar;
    }

    private void configureSearchField() {
        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo tên đăng nhập, họ tên, email, vai trò..."
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
                """
        );

        searchField.setPreferredSize(
                new Dimension(
                        350,
                        38
                )
        );
    }

    private JScrollPane createTableScrollPane() {
        userTable.setRowHeight(42);
        userTable.setFillsViewportHeight(true);

        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        /*
         * Giữ nguyên độ rộng từng cột và dùng thanh cuộn ngang,
         * tránh cột Trạng thái/Ngày tạo bị cắt.
         */
        userTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        userTable.setShowHorizontalLines(true);
        userTable.setShowVerticalLines(false);

        userTable.setGridColor(
                UIConstants.BORDER
        );

        userTable.setIntercellSpacing(
                new Dimension(
                        0,
                        1
                )
        );

        userTable.setSelectionBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        userTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        userTable
                .getTableHeader()
                .setReorderingAllowed(false);

        userTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                40
                        )
                );

        userTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(
                                        Font.BOLD
                                )
                );

        configureColumnWidths();
        configureRenderers();

        JScrollPane scrollPane =
                new JScrollPane(
                        userTable
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
                .setUnitIncrement(20);

        return scrollPane;
    }

    private void configureColumnWidths() {
        setColumnWidth(0, 50);
        setColumnWidth(1, 120);
        setColumnWidth(2, 165);
        setColumnWidth(3, 190);
        setColumnWidth(4, 125);
        setColumnWidth(5, 95);
        setColumnWidth(6, 145);
        setColumnWidth(7, 100);
    }

    private void setColumnWidth(
            int columnIndex,
            int preferredWidth
    ) {
        userTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(
                        preferredWidth
                );
    }

    private void configureRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        int[] centeredColumns = {
                0, 5, 6, 7
        };

        for (int column : centeredColumns) {
            userTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        userTable
                .getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new RoleCellRenderer()
                );

        userTable
                .getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new StatusCellRenderer()
                );
    }

    private JPanel createPaginationPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][][][]",
                                "[center]"
                        )
                );

        panel.setOpaque(false);

        resultLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        resultLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        previousButton =
                createPaginationButton(
                        "‹"
                );

        nextButton =
                createPaginationButton(
                        "›"
                );

        previousButton.addActionListener(
                event -> {
                    if (currentPage > 1) {
                        currentPage--;
                        displayCurrentPage();
                    }
                }
        );

        nextButton.addActionListener(
                event -> {
                    if (
                            currentPage
                                    < getTotalPages()
                    ) {
                        currentPage++;
                        displayCurrentPage();
                    }
                }
        );

        pageLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(
                                Font.BOLD
                        )
        );

        panel.add(
                resultLabel,
                "growx"
        );

        panel.add(previousButton);
        panel.add(
                pageLabel,
                "width 58!"
        );
        panel.add(nextButton);

        return panel;
    }

    /* =====================================================
       FORM TÀI KHOẢN
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

        ContentCard formContent =
                new ContentCard();

        formContent.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20 24",
                        "[grow,fill]",
                        "[]16[]16[]"
                )
        );

        cancelFormButton =
                createToolbarButton(
                        "Hủy",
                        FontAwesomeSolid.TIMES,
                        Color.WHITE,
                        UIConstants.TEXT_SECONDARY
                );

        saveFormButton =
                createToolbarButton(
                        "Lưu tài khoản",
                        FontAwesomeSolid.SAVE,
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        formTitleLabel =
                new JLabel(
                        "Thêm tài khoản"
                );

        formTitleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        formTitleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Thông tin hồ sơ thay đổi theo vai trò được chọn"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
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

        formContent.add(
                headerPanel,
                "growx"
        );

        formContent.add(
                createCommonFormPanel(),
                "growx"
        );

        formContent.add(
                createRoleSpecificPanel(),
                "growx"
        );

        JScrollPane scrollPane =
                new JScrollPane(
                        formContent
                );

        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);

        scrollPane
                .getViewport()
                .setOpaque(false);

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        wrapper.add(
                scrollPane,
                BorderLayout.CENTER
        );

        return wrapper;
    }

    private JPanel createCommonFormPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 16",
                                "[right,150!]12[grow,fill]",
                                "[]12[]12[]12[]12[]12[]12[]"
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
                        "Thông tin tài khoản"
                )
        );

        usernameField =
                createTextField(
                        "Tên đăng nhập"
                );

        passwordField =
                new JPasswordField();

        passwordField.putClientProperty(
                "JTextField.placeholderText",
                "Mật khẩu ít nhất 6 ký tự"
        );

        fullNameField =
                createTextField(
                        "Họ và tên"
                );

        emailField =
                createTextField(
                        "Email"
                );

        phoneField =
                createTextField(
                        "Số điện thoại"
                );

        roleComboBox =
                new JComboBox<>(
                        Role.values()
                );

        statusComboBox =
                new JComboBox<>(
                        AccountStatus.values()
                );

        styleComboBox(roleComboBox);
        styleComboBox(statusComboBox);

        panel.add(createFormLabel("Tên đăng nhập *"));
        panel.add(usernameField, "growx, height 38!");

        panel.add(createFormLabel("Mật khẩu *"));
        panel.add(passwordField, "growx, height 38!");

        panel.add(createFormLabel("Họ và tên *"));
        panel.add(fullNameField, "growx, height 38!");

        panel.add(createFormLabel("Email"));
        panel.add(emailField, "growx, height 38!");

        panel.add(createFormLabel("Số điện thoại"));
        panel.add(phoneField, "growx, height 38!");

        panel.add(createFormLabel("Vai trò *"));
        panel.add(roleComboBox, "growx, height 38!");

        panel.add(createFormLabel("Trạng thái"));
        panel.add(statusComboBox, "growx, height 38!");

        return panel;
    }

    private JPanel createRoleSpecificPanel() {
        roleSpecificLayout =
                new CardLayout();

        roleSpecificPanel =
                new JPanel(
                        roleSpecificLayout
                );

        roleSpecificPanel.setOpaque(false);

        roleSpecificPanel.add(
                createAdminProfilePanel(),
                Role.ADMIN.name()
        );

        roleSpecificPanel.add(
                createStudentProfilePanel(),
                Role.STUDENT.name()
        );

        roleSpecificPanel.add(
                createTeacherProfilePanel(),
                Role.TEACHER.name()
        );

        return roleSpecificPanel;
    }

    private JPanel createAdminProfilePanel() {
        JPanel panel =
                createProfileContainer(
                        "Thông tin quản trị viên"
                );

        JLabel label =
                new JLabel(
                        "Tài khoản Admin hiện chỉ lưu trong bảng Users."
                );

        label.setFont(
                UIConstants.FONT_NORMAL
        );

        label.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                label,
                "span 4, growx"
        );

        return panel;
    }

    private JPanel createStudentProfilePanel() {
        JPanel panel =
                createProfileContainer(
                        "Hồ sơ sinh viên"
                );

        studentCodeField =
                createTextField(
                        "Mã sinh viên"
                );

        studentBirthDateField =
                createTextField(
                        "yyyy-MM-dd"
                );

        studentGenderComboBox =
                new JComboBox<>(
                        new String[]{
                                "",
                                "NAM",
                                "NỮ",
                                "KHÁC"
                        }
                );

        studentAddressArea =
                createTextArea();

        styleComboBox(
                studentGenderComboBox
        );

        panel.add(createFormLabel("Mã sinh viên *"));
        panel.add(studentCodeField, "growx, height 38!");

        panel.add(createFormLabel("Ngày sinh"));
        panel.add(studentBirthDateField, "growx, height 38!");

        panel.add(createFormLabel("Giới tính"));
        panel.add(studentGenderComboBox, "growx, height 38!");

        panel.add(createFormLabel("Địa chỉ"));
        panel.add(
                new JScrollPane(
                        studentAddressArea
                ),
                "growx, height 85!"
        );

        return panel;
    }

    private JPanel createTeacherProfilePanel() {
        JPanel panel =
                createProfileContainer(
                        "Hồ sơ giảng viên"
                );

        teacherCodeField =
                createTextField(
                        "Mã giảng viên"
                );

        teacherBirthDateField =
                createTextField(
                        "yyyy-MM-dd"
                );

        teacherGenderComboBox =
                new JComboBox<>(
                        new String[]{
                                "",
                                "NAM",
                                "NỮ",
                                "KHÁC"
                        }
                );

        teacherSpecializationField =
                createTextField(
                        "Chuyên môn"
                );

        teacherAddressArea =
                createTextArea();

        styleComboBox(
                teacherGenderComboBox
        );

        panel.add(createFormLabel("Mã giảng viên *"));
        panel.add(teacherCodeField, "growx, height 38!");

        panel.add(createFormLabel("Ngày sinh"));
        panel.add(teacherBirthDateField, "growx, height 38!");

        panel.add(createFormLabel("Giới tính"));
        panel.add(teacherGenderComboBox, "growx, height 38!");

        panel.add(createFormLabel("Chuyên môn"));
        panel.add(teacherSpecializationField, "growx, height 38!");

        panel.add(createFormLabel("Địa chỉ"));
        panel.add(
                new JScrollPane(
                        teacherAddressArea
                ),
                "growx, height 85!"
        );

        return panel;
    }

    private JPanel createProfileContainer(
            String title
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 16",
                                "[right,150!]12[grow,fill]",
                                "[]12[]12[]12[]12[]"
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
                        title
                )
        );

        return panel;
    }

    private JPanel createFixedFormActionPanel() {
        ContentCard actionCard =
                new ContentCard();

        actionCard.setLayout(
                new MigLayout(
                        "fillx, insets 12 18",
                        "[grow][][]",
                        "[]"
                )
        );

        JLabel noteLabel =
                new JLabel(
                        "Các trường có dấu * là bắt buộc"
                );

        noteLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        noteLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        cancelFormButton =
                createToolbarButton(
                        "Hủy",
                        FontAwesomeSolid.TIMES,
                        Color.WHITE,
                        UIConstants.TEXT_SECONDARY
                );

        saveFormButton =
                createToolbarButton(
                        "Lưu tài khoản",
                        FontAwesomeSolid.SAVE,
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        cancelFormButton.setPreferredSize(
                new Dimension(
                        110,
                        40
                )
        );

        saveFormButton.setPreferredSize(
                new Dimension(
                        150,
                        40
                )
        );

        actionCard.add(
                noteLabel,
                "growx"
        );

        actionCard.add(
                cancelFormButton,
                "height 40!"
        );

        actionCard.add(
                saveFormButton,
                "height 40!"
        );

        return actionCard;
    }


    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        searchField
                .getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }
                        }
                );

        userTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateActionButtonState();
                            }
                        }
                );

        userTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (
                                event.getClickCount() == 2
                                        && userTable
                                        .getSelectedRow()
                                        >= 0
                        ) {
                            showEditForm();
                        }
                    }
                }
        );

        roleComboBox.addActionListener(
                event -> updateRoleSpecificForm()
        );

        cancelFormButton.addActionListener(
                event -> showListCard()
        );

        saveFormButton.addActionListener(
                event -> saveUserForm()
        );

        updateActionButtonState();
    }

    /* =====================================================
       HIỂN THỊ CARD
       ===================================================== */

    private void showListCard() {
        editingUser = null;
        clearForm();

        cardLayout.show(
                cardPanel,
                CARD_LIST
        );
    }

    private void showCreateForm() {
        editingUser = null;
        clearForm();

        formTitleLabel.setText(
                "Thêm tài khoản"
        );

        saveFormButton.setText(
                "Lưu tài khoản"
        );

        usernameField.setEditable(true);
        passwordField.setEnabled(true);

        roleComboBox.setEnabled(true);

        statusComboBox.setSelectedItem(
                AccountStatus.ACTIVE
        );

        updateRoleSpecificForm();

        cardLayout.show(
                cardPanel,
                CARD_FORM
        );
    }

    private void showEditForm() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            showWarning(
                    "Hãy chọn tài khoản cần cập nhật."
            );
            return;
        }

        editingUser = selectedUser;

        formTitleLabel.setText(
                "Cập nhật tài khoản"
        );

        saveFormButton.setText(
                "Lưu thay đổi"
        );

        usernameField.setText(
                safeText(
                        selectedUser.getUsername()
                )
        );

        usernameField.setEditable(false);

        passwordField.setText("");
        passwordField.setEnabled(false);

        fullNameField.setText(
                safeText(
                        selectedUser.getFullName()
                )
        );

        emailField.setText(
                safeText(
                        selectedUser.getEmail()
                )
        );

        phoneField.setText(
                safeText(
                        selectedUser.getPhone()
                )
        );

        roleComboBox.setSelectedItem(
                selectedUser.getRole()
        );

        /*
         * Không cho đổi vai trò trong màn cập nhật vì
         * sẽ làm sai liên kết Student/Teacher hiện có.
         */
        roleComboBox.setEnabled(false);

        statusComboBox.setSelectedItem(
                selectedUser.getStatus()
        );

        updateRoleSpecificForm();

        cardLayout.show(
                cardPanel,
                CARD_FORM
        );
    }

    private void updateRoleSpecificForm() {
        Role role =
                getSelectedRole();

        roleSpecificLayout.show(
                roleSpecificPanel,
                role.name()
        );

        boolean creating =
                editingUser == null;

        roleSpecificPanel.setVisible(
                creating
                        || role != Role.ADMIN
        );

        revalidate();
        repaint();
    }

    /* =====================================================
       LƯU FORM
       ===================================================== */

    private void saveUserForm() {
        try {
            if (editingUser == null) {
                createUserFromForm();
            } else {
                updateUserFromForm();
            }

        } catch (
                IllegalArgumentException
                | IllegalStateException exception
        ) {
            showWarning(
                    exception.getMessage()
            );

        } catch (Exception exception) {
            showError(
                    "Không thể lưu tài khoản.",
                    exception
            );
        }
    }

    private void createUserFromForm()
            throws SQLException {

        String username =
                usernameField
                        .getText()
                        .trim();

        String password =
                new String(
                        passwordField
                                .getPassword()
                );

        String fullName =
                fullNameField
                        .getText()
                        .trim();

        String email =
                normalize(
                        emailField.getText()
                );

        String phone =
                normalize(
                        phoneField.getText()
                );

        Role role =
                getSelectedRole();

        Student student =
                null;

        Teacher teacher =
                null;

        if (role == Role.STUDENT) {
            student =
                    buildStudentFromForm();
        }

        if (role == Role.TEACHER) {
            teacher =
                    buildTeacherFromForm();
        }

        boolean successful =
                userController
                        .createRoleAccount(
                                username,
                                password,
                                fullName,
                                email,
                                phone,
                                role,
                                student,
                                teacher
                        );

        if (!successful) {
            showWarning(
                    "Không thể tạo tài khoản."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Thêm tài khoản thành công.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        currentPage = 1;
        loadUsers();
        showListCard();
    }

    private void updateUserFromForm()
            throws SQLException {

        editingUser.setFullName(
                fullNameField
                        .getText()
                        .trim()
        );

        editingUser.setEmail(
                normalize(
                        emailField.getText()
                )
        );

        editingUser.setPhone(
                normalize(
                        phoneField.getText()
                )
        );

        editingUser.setStatus(
                (AccountStatus)
                        statusComboBox
                                .getSelectedItem()
        );

        boolean successful =
                userController.updateUser(
                        editingUser
                );

        if (!successful) {
            showWarning(
                    "Không thể cập nhật tài khoản."
            );
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Cập nhật tài khoản thành công.",
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE
        );

        loadUsers();
        showListCard();
    }

    private Student buildStudentFromForm() {
        Student student =
                new Student();

        student.setStudentCode(
                studentCodeField
                        .getText()
        );

        student.setDateOfBirth(
                parseSqlDate(
                        studentBirthDateField
                                .getText(),
                        "Ngày sinh sinh viên"
                )
        );

        student.setGender(
                selectedGender(
                        studentGenderComboBox
                )
        );

        student.setAddress(
                normalize(
                        studentAddressArea
                                .getText()
                )
        );

        student.setStatus(
                "ACTIVE"
        );

        return student;
    }

    private Teacher buildTeacherFromForm() {
        Teacher teacher =
                new Teacher();

        teacher.setTeacherCode(
                teacherCodeField
                        .getText()
        );

        teacher.setDateOfBirth(
                parseSqlDate(
                        teacherBirthDateField
                                .getText(),
                        "Ngày sinh giảng viên"
                )
        );

        teacher.setGender(
                selectedGender(
                        teacherGenderComboBox
                )
        );

        teacher.setSpecialization(
                normalize(
                        teacherSpecializationField
                                .getText()
                )
        );

        teacher.setAddress(
                normalize(
                        teacherAddressArea
                                .getText()
                )
        );

        teacher.setStatus(
                "ACTIVE"
        );

        return teacher;
    }

    /* =====================================================
       TẢI DỮ LIỆU
       ===================================================== */

    public void loadUsers() {
        if (loading) {
            return;
        }

        loading = true;

        try {
            List<User> users =
                    userController.getAllUsers();

            allUsers =
                    users == null
                            ? Collections.emptyList()
                            : users;

            currentPage =
                    Math.max(
                            1,
                            Math.min(
                                    currentPage,
                                    getTotalPages()
                            )
                    );

            displayCurrentPage();

        } catch (SQLException exception) {
            allUsers =
                    Collections.emptyList();

            tableModel.setRowCount(0);

            updateSummaryState();

            showError(
                    "Không thể tải danh sách tài khoản.",
                    exception
            );

        } finally {
            loading = false;
        }
    }

    private void applySearch() {
        currentPage = 1;
        displayCurrentPage();
    }

    private void displayCurrentPage() {
        List<User> filteredUsers =
                getFilteredUsers();

        int totalRows =
                filteredUsers.size();

        int totalPages =
                Math.max(
                        1,
                        (int) Math.ceil(
                                totalRows
                                        / (double) ROWS_PER_PAGE
                        )
                );

        currentPage =
                Math.max(
                        1,
                        Math.min(
                                currentPage,
                                totalPages
                        )
                );

        int startIndex =
                (currentPage - 1)
                        * ROWS_PER_PAGE;

        int endIndex =
                Math.min(
                        startIndex
                                + ROWS_PER_PAGE,
                        totalRows
                );

        tableModel.setRowCount(0);

        if (startIndex < endIndex) {
            for (
                    User user :
                    filteredUsers.subList(
                            startIndex,
                            endIndex
                    )
            ) {
                if (user == null) {
                    continue;
                }

                tableModel.addRow(
                        new Object[]{
                                user.getUserId(),
                                safeText(
                                        user.getUsername()
                                ),
                                safeText(
                                        user.getFullName()
                                ),
                                safeText(
                                        user.getEmail()
                                ),
                                safeText(
                                        user.getPhone()
                                ),
                                user.getRole(),
                                user.getStatus(),
                                user.getCreatedAt() == null
                                        ? ""
                                        : user
                                        .getCreatedAt()
                                        .format(
                                                DISPLAY_DATE_FORMAT
                                        )
                        }
                );
            }
        }

        if (totalRows == 0) {
            resultLabel.setText(
                    searchField
                            .getText()
                            .isBlank()
                            ? "Chưa có tài khoản trong hệ thống"
                            : "Không tìm thấy tài khoản phù hợp"
            );
        } else {
            resultLabel.setText(
                    "Hiển thị "
                            + (startIndex + 1)
                            + "–"
                            + endIndex
                            + " trong "
                            + totalRows
                            + " tài khoản"
            );
        }

        pageLabel.setText(
                currentPage
                        + " / "
                        + totalPages
        );

        previousButton.setEnabled(
                currentPage > 1
        );

        nextButton.setEnabled(
                currentPage < totalPages
        );

        userTable.clearSelection();

        updateSummaryState();
        updateActionButtonState();

        userTable.revalidate();
        userTable.repaint();
    }

    private void updateSummaryState() {
        int totalUsers =
                allUsers == null
                        ? 0
                        : allUsers.size();

        totalUserLabel.setText(
                totalUsers
                        + " tài khoản"
        );

        exportButton.setEnabled(
                totalUsers > 0
        );
    }

    /* =====================================================
       ACTION BUTTON
       ===================================================== */

    private void updateActionButtonState() {
        User selectedUser =
                getSelectedUser();

        boolean hasSelection =
                selectedUser != null;

        editButton.setEnabled(
                hasSelection
        );

        deleteButton.setEnabled(
                hasSelection
                        && !isMainAdmin(
                        selectedUser
                )
        );

        resetPasswordButton.setEnabled(
                hasSelection
        );

        lockButton.setEnabled(
                hasSelection
                        && !isMainAdmin(
                        selectedUser
                )
        );

        boolean pendingTeacher =
                hasSelection
                        && selectedUser.getRole()
                        == Role.TEACHER
                        && selectedUser.getStatus()
                        == AccountStatus.PENDING_APPROVAL;

        approveButton.setEnabled(
                pendingTeacher
        );

        rejectButton.setEnabled(
                pendingTeacher
        );

        if (hasSelection
                && selectedUser.getStatus()
                == AccountStatus.LOCKED) {

            lockButton.setText(
                    "Mở khóa"
            );

            lockButton.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.UNLOCK,
                            13,
                            UIConstants.WARNING
                    )
            );

        } else {
            lockButton.setText(
                    "Khóa"
            );

            lockButton.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.LOCK,
                            13,
                            UIConstants.WARNING
                    )
            );
        }
    }

    private void toggleSelectedUserLock() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            showWarning(
                    "Hãy chọn tài khoản."
            );
            return;
        }

        if (isMainAdmin(selectedUser)) {
            showWarning(
                    "Không thể khóa tài khoản quản trị chính."
            );
            return;
        }

        try {
            boolean successful;

            if (
                    selectedUser.getStatus()
                            == AccountStatus.LOCKED
            ) {
                successful =
                        userController.unlockUser(
                                selectedUser
                                        .getUserId()
                        );
            } else {
                successful =
                        userController.lockUser(
                                selectedUser
                                        .getUserId()
                        );
            }

            if (!successful) {
                showWarning(
                        "Không thể cập nhật trạng thái tài khoản."
                );
                return;
            }

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể khóa hoặc mở khóa tài khoản.",
                    exception
            );
        }
    }

    private void resetSelectedUserPassword() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            showWarning(
                    "Hãy chọn tài khoản cần reset mật khẩu."
            );
            return;
        }

        JPasswordField passwordInput =
                new JPasswordField();

        JPasswordField confirmInput =
                new JPasswordField();

        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 8",
                                "[right]12[grow,fill]",
                                "[]10[]"
                        )
                );

        panel.add(
                new JLabel(
                        "Mật khẩu mới:"
                )
        );

        panel.add(
                passwordInput
        );

        panel.add(
                new JLabel(
                        "Nhập lại:"
                )
        );

        panel.add(
                confirmInput
        );

        int result =
                JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Reset mật khẩu - "
                                + selectedUser
                                .getUsername(),
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if (result
                != JOptionPane.OK_OPTION) {
            return;
        }

        String newPassword =
                new String(
                        passwordInput
                                .getPassword()
                );

        String confirmPassword =
                new String(
                        confirmInput
                                .getPassword()
                );

        if (!newPassword.equals(
                confirmPassword
        )) {
            showWarning(
                    "Mật khẩu nhập lại không khớp."
            );
            return;
        }

        try {
            boolean successful =
                    userController.resetPassword(
                            selectedUser
                                    .getUserId(),
                            newPassword
                    );

            if (!successful) {
                showWarning(
                        "Không thể reset mật khẩu."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Reset mật khẩu thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception exception) {
            showError(
                    "Không thể reset mật khẩu.",
                    exception
            );
        }
    }

    private void approveSelectedTeacher() {
        User selectedUser =
                getSelectedUser();

        if (!isPendingTeacher(
                selectedUser
        )) {
            showWarning(
                    "Hãy chọn giảng viên đang chờ duyệt."
            );
            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Duyệt tài khoản giảng viên \""
                                + selectedUser
                                .getUsername()
                                + "\"?",
                        "Xác nhận duyệt",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer
                != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int adminUserId =
                    SessionManager
                            .getCurrentUserId();

            boolean successful =
                    userController.approveTeacher(
                            selectedUser
                                    .getUserId(),
                            adminUserId
                    );

            if (!successful) {
                showWarning(
                        "Không thể duyệt tài khoản giảng viên."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã duyệt tài khoản giảng viên.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            /*
             * Có thể gọi EmailService tại đây sau khi
             * module gửi email được hoàn thiện.
             */

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể duyệt tài khoản giảng viên.",
                    exception
            );
        }
    }

    private void rejectSelectedTeacher() {
        User selectedUser =
                getSelectedUser();

        if (!isPendingTeacher(
                selectedUser
        )) {
            showWarning(
                    "Hãy chọn giảng viên đang chờ duyệt."
            );
            return;
        }

        JTextArea reasonArea =
                createTextArea();

        reasonArea.setRows(4);

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        new JScrollPane(
                                reasonArea
                        ),
                        "Nhập lý do từ chối",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (answer
                != JOptionPane.OK_OPTION) {
            return;
        }

        String reason =
                reasonArea
                        .getText()
                        .trim();

        if (reason.isBlank()) {
            showWarning(
                    "Vui lòng nhập lý do từ chối."
            );
            return;
        }

        try {
            int adminUserId =
                    SessionManager
                            .getCurrentUserId();

            boolean successful =
                    userController.rejectTeacher(
                            selectedUser
                                    .getUserId(),
                            adminUserId,
                            reason
                    );

            if (!successful) {
                showWarning(
                        "Không thể từ chối tài khoản giảng viên."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã từ chối tài khoản giảng viên.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể từ chối tài khoản giảng viên.",
                    exception
            );
        }
    }

    private void deleteSelectedUser() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            showWarning(
                    "Hãy chọn tài khoản cần xóa."
            );
            return;
        }

        if (isMainAdmin(selectedUser)) {
            showWarning(
                    "Không thể xóa tài khoản quản trị chính."
            );
            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa tài khoản \""
                                + selectedUser
                                .getUsername()
                                + "\"?\n"
                                + "Hồ sơ Student/Teacher vẫn được giữ "
                                + "nhưng sẽ bị gỡ liên kết tài khoản.",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (answer
                != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean successful =
                    userController.deleteUser(
                            selectedUser
                                    .getUserId()
                    );

            if (!successful) {
                showWarning(
                        "Không thể xóa tài khoản."
                );
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xóa tài khoản thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể xóa tài khoản.",
                    exception
            );
        }
    }

    /* =====================================================
       CHỌN USER
       ===================================================== */

    private User getSelectedUser() {
        int selectedRow =
                userTable.getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow =
                userTable
                        .convertRowIndexToModel(
                                selectedRow
                        );

        Object idValue =
                tableModel.getValueAt(
                        modelRow,
                        0
                );

        int userId;

        if (
                idValue
                        instanceof Number number
        ) {
            userId =
                    number.intValue();

        } else {
            try {
                userId =
                        Integer.parseInt(
                                String.valueOf(
                                        idValue
                                )
                        );

            } catch (
                    NumberFormatException exception
            ) {
                return null;
            }
        }

        return allUsers.stream()
                .filter(
                        user ->
                                user != null
                                        && user
                                        .getUserId()
                                        == userId
                )
                .findFirst()
                .orElse(null);
    }

    private boolean isPendingTeacher(
            User user
    ) {
        return user != null
                && user.getRole()
                == Role.TEACHER
                && user.getStatus()
                == AccountStatus.PENDING_APPROVAL;
    }

    private boolean isMainAdmin(
            User user
    ) {
        return user != null
                && "admin".equalsIgnoreCase(
                user.getUsername()
        );
    }

    /* =====================================================
       TÌM KIẾM VÀ PHÂN TRANG
       ===================================================== */

    private int getTotalPages() {
        return Math.max(
                1,
                (int) Math.ceil(
                        getFilteredUsers()
                                .size()
                                / (double) ROWS_PER_PAGE
                )
        );
    }

    private List<User> getFilteredUsers() {
        if (
                allUsers == null
                        || allUsers.isEmpty()
        ) {
            return Collections.emptyList();
        }

        String keyword =
                searchField
                        .getText()
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (keyword.isBlank()) {
            return allUsers;
        }

        return allUsers.stream()
                .filter(
                        user ->
                                user != null
                )
                .filter(
                        user ->
                                contains(
                                        user.getUsername(),
                                        keyword
                                )
                                        || contains(
                                        user.getFullName(),
                                        keyword
                                )
                                        || contains(
                                        user.getEmail(),
                                        keyword
                                )
                                        || contains(
                                        user.getPhone(),
                                        keyword
                                )
                                        || contains(
                                        user.getRole()
                                                == null
                                                ? null
                                                : user
                                                .getRole()
                                                .name(),
                                        keyword
                                )
                                        || contains(
                                        user.getStatus()
                                                == null
                                                ? null
                                                : user
                                                .getStatus()
                                                .name(),
                                        keyword
                                )
                )
                .toList();
    }

    private boolean contains(
            String value,
            String keyword
    ) {
        return value != null
                && value
                .toLowerCase(
                        Locale.ROOT
                )
                .contains(
                        keyword
                );
    }

    /* =====================================================
       XUẤT CSV
       ===================================================== */

    private void exportCsv() {
        List<User> usersToExport =
                getFilteredUsers();

        if (usersToExport.isEmpty()) {
            showWarning(
                    "Không có dữ liệu để xuất."
            );
            return;
        }

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Xuất danh sách tài khoản"
        );

        fileChooser.setSelectedFile(
                new File(
                        "danh-sach-tai-khoan.csv"
                )
        );

        fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Tệp CSV (*.csv)",
                        "csv"
                )
        );

        if (
                fileChooser.showSaveDialog(
                        this
                )
                        != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File file =
                ensureCsvExtension(
                        fileChooser
                                .getSelectedFile()
                );

        try (
                BufferedWriter writer =
                        Files.newBufferedWriter(
                                file.toPath(),
                                StandardCharsets.UTF_8
                        )
        ) {
            writer.write('\uFEFF');

            writer.write(
                    "ID,Tên đăng nhập,Họ và tên,"
                            + "Email,Số điện thoại,Vai trò,"
                            + "Trạng thái,Ngày tạo"
            );

            writer.newLine();

            for (User user : usersToExport) {
                if (user == null) {
                    continue;
                }

                writer.write(
                        user.getUserId()
                                + ","
                                + csv(
                                user.getUsername()
                        )
                                + ","
                                + csv(
                                user.getFullName()
                        )
                                + ","
                                + csv(
                                user.getEmail()
                        )
                                + ","
                                + csv(
                                user.getPhone()
                        )
                                + ","
                                + csv(
                                user.getRole()
                                        == null
                                        ? ""
                                        : user
                                        .getRole()
                                        .name()
                        )
                                + ","
                                + csv(
                                user.getStatus()
                                        == null
                                        ? ""
                                        : user
                                        .getStatus()
                                        .name()
                        )
                                + ","
                                + csv(
                                user.getCreatedAt()
                                        == null
                                        ? ""
                                        : user
                                        .getCreatedAt()
                                        .format(
                                                DISPLAY_DATE_FORMAT
                                        )
                        )
                );

                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xuất dữ liệu thành công:\n"
                            + file
                            .getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException exception) {
            showError(
                    "Không thể xuất dữ liệu.",
                    exception
            );
        }
    }

    /* =====================================================
       COMPONENT HỖ TRỢ
       ===================================================== */

    private JButton createToolbarButton(
            String text,
            FontAwesomeSolid icon,
            Color background,
            Color foreground
    ) {
        JButton button =
                new JButton(text);

        button.setIcon(
                FontIcon.of(
                        icon,
                        13,
                        foreground
                )
        );

        button.setBackground(
                background
        );

        button.setForeground(
                foreground
        );

        button.setFont(
                UIConstants.FONT_MEDIUM
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
                margin: 7,10,7,10;
                """
        );

        return button;
    }

    private JButton createPaginationButton(
            String text
    ) {
        JButton button =
                new JButton(text);

        button.setFocusable(false);

        button.setPreferredSize(
                new Dimension(
                        36,
                        32
                )
        );

        return button;
    }

    private JTextField createTextField(
            String placeholder
    ) {
        JTextField field =
                new JTextField();

        field.putClientProperty(
                "JTextField.placeholderText",
                placeholder
        );

        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                """
        );

        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area =
                new JTextArea();

        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setFont(
                UIConstants.FONT_NORMAL
        );

        return area;
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

    private void styleComboBox(
            JComboBox<?> comboBox
    ) {
        comboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        comboBox.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                """
        );
    }

    /* =====================================================
       HÀM HỖ TRỢ FORM
       ===================================================== */

    private void clearForm() {
        if (usernameField == null) {
            return;
        }

        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");

        roleComboBox.setSelectedItem(
                Role.STUDENT
        );

        statusComboBox.setSelectedItem(
                AccountStatus.ACTIVE
        );

        studentCodeField.setText("");
        studentBirthDateField.setText("");
        studentGenderComboBox.setSelectedIndex(0);
        studentAddressArea.setText("");

        teacherCodeField.setText("");
        teacherBirthDateField.setText("");
        teacherGenderComboBox.setSelectedIndex(0);
        teacherSpecializationField.setText("");
        teacherAddressArea.setText("");

        usernameField.setEditable(true);
        passwordField.setEnabled(true);
        roleComboBox.setEnabled(true);
    }

    private Role getSelectedRole() {
        Object value =
                roleComboBox
                        .getSelectedItem();

        return value instanceof Role role
                ? role
                : Role.STUDENT;
    }

    private String selectedGender(
            JComboBox<String> comboBox
    ) {
        Object value =
                comboBox.getSelectedItem();

        if (value == null) {
            return null;
        }

        String text =
                value.toString().trim();

        return text.isBlank()
                ? null
                : text;
    }

    private Date parseSqlDate(
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
            LocalDate localDate =
                    LocalDate.parse(
                            value.trim(),
                            INPUT_DATE_FORMAT
                    );

            return Date.valueOf(
                    localDate
            );

        } catch (
                DateTimeParseException exception
        ) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải có định dạng yyyy-MM-dd."
            );
        }
    }

    private String normalize(
            String value
    ) {
        return value == null
                || value.isBlank()
                ? null
                : value.trim();
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
    }

    private File ensureCsvExtension(
            File file
    ) {
        if (
                file.getName()
                        .toLowerCase(
                                Locale.ROOT
                        )
                        .endsWith(
                                ".csv"
                        )
        ) {
            return file;
        }

        return new File(
                file.getAbsolutePath()
                        + ".csv"
        );
    }

    private String csv(
            String value
    ) {
        if (value == null) {
            return "\"\"";
        }

        return "\""
                + value.replace(
                "\"",
                "\"\""
        )
                + "\"";
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
            Exception exception
    ) {
        String detail =
                exception == null
                        || exception.getMessage()
                        == null
                        || exception.getMessage()
                        .isBlank()
                        ? "Không xác định"
                        : exception.getMessage();

        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nChi tiết: "
                        + detail,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /* =====================================================
       RENDERER
       ===================================================== */

    private static class RoleCellRenderer
            extends DefaultTableCellRenderer {

        public RoleCellRenderer() {
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
                            getRoleText(value),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                setForeground(
                        getRoleColor(value)
                );

                setBackground(
                        Color.WHITE
                );
            }

            setFont(
                    UIConstants.FONT_SMALL
                            .deriveFont(
                                    Font.BOLD
                            )
            );

            return component;
        }

        private static String getRoleText(
                Object value
        ) {
            if (!(value instanceof Role role)) {
                return "";
            }

            return switch (role) {
                case ADMIN ->
                        "Quản trị";

                case TEACHER ->
                        "Giảng viên";

                case STUDENT ->
                        "Học viên";
            };
        }

        private static Color getRoleColor(
                Object value
        ) {
            if (!(value instanceof Role role)) {
                return UIConstants.TEXT_SECONDARY;
            }

            return switch (role) {
                case ADMIN ->
                        UIConstants.DANGER;

                case TEACHER ->
                        UIConstants.PURPLE;

                case STUDENT ->
                        UIConstants.PRIMARY;
            };
        }
    }

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
            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            getStatusText(value),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                setForeground(
                        getStatusColor(value)
                );

                setBackground(
                        Color.WHITE
                );
            }

            setFont(
                    UIConstants.FONT_SMALL
                            .deriveFont(
                                    Font.BOLD
                            )
            );

            return component;
        }

        private static String getStatusText(
                Object value
        ) {
            if (!(value
                    instanceof AccountStatus status)) {
                return "";
            }

            return switch (status) {
                case ACTIVE ->
                        "Hoạt động";

                case PENDING_EMAIL ->
                        "Chờ xác minh";

                case PENDING_APPROVAL ->
                        "Chờ duyệt";

                case LOCKED ->
                        "Đã khóa";

                case INACTIVE ->
                        "Không hoạt động";
            };
        }

        private static Color getStatusColor(
                Object value
        ) {
            if (!(value
                    instanceof AccountStatus status)) {
                return UIConstants.TEXT_SECONDARY;
            }

            return switch (status) {
                case ACTIVE ->
                        UIConstants.SUCCESS;

                case PENDING_EMAIL ->
                        new Color(
                                202,
                                138,
                                4
                        );

                case PENDING_APPROVAL ->
                        new Color(
                                234,
                                88,
                                12
                        );

                case LOCKED ->
                        UIConstants.DANGER;

                case INACTIVE ->
                        UIConstants.WARNING;
            };
        }
    }
}
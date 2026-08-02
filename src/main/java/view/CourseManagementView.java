package view;

import controller.CourseController;
import model.Course;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CourseManagementView extends JPanel {

    private static final String LIST_CARD = "COURSE_LIST";
    private static final String FORM_CARD = "COURSE_FORM";

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_CODE = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_DESCRIPTION = 3;
    private static final int COLUMN_CREDITS = 4;
    private static final int COLUMN_FEE = 5;
    private static final int COLUMN_STATUS = 6;

    private final CourseController courseController;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private final DefaultTableModel tableModel;
    private final JTable tableCourse;
    private final JTextField txtSearch;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnToggleStatus;
    private JButton btnArchive;

    private JLabel lblCourseCount;
    private JLabel lblResultSummary;
    private JLabel lblFormTitle;

    private JTextField txtCode;
    private JTextField txtName;
    private JTextArea txtDescription;
    private JSpinner spCredits;
    private JTextField txtTuition;
    private JComboBox<String> cboStatus;
    private JButton btnCancel;
    private JButton btnSave;

    private List<Course> allCourses = Collections.emptyList();
    private Course editingCourse;
    private boolean managementMode = true;
    private boolean loading;

    public CourseManagementView() {
        this.courseController = new CourseController();
        this.cardLayout = new CardLayout();
        this.cardPanel = new JPanel(cardLayout);
        this.txtSearch = new JTextField();

        this.tableModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Mã khóa học",
                        "Tên khóa học",
                        "Mô tả",
                        "Tín chỉ",
                        "Học phí",
                        "Trạng thái"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case COLUMN_ID, COLUMN_CREDITS -> Integer.class;
                    case COLUMN_FEE -> BigDecimal.class;
                    default -> String.class;
                };
            }
        };

        this.tableCourse = new JTable(tableModel);

        initializeView();
        registerEvents();
        setManagementMode(true);
        loadCourses();
    }

    private void initializeView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        cardPanel.setOpaque(false);
        cardPanel.add(createListCard(), LIST_CARD);
        cardPanel.add(createFormCard(), FORM_CARD);

        add(cardPanel, BorderLayout.CENTER);
        showListCard();
    }

    private JPanel createListCard() {
        JPanel wrapper = new JPanel(
                new MigLayout(
                        "fill, insets 16",
                        "[grow,fill]",
                        "[grow,fill]"
                )
        );
        wrapper.setOpaque(false);

        ContentCard card = new ContentCard();
        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow,fill]",
                        "[]14[]12[grow,fill]10[]"
                )
        );

        card.add(createListHeader(), "growx");
        card.add(createToolbar(), "growx");
        card.add(createTableScrollPane(), "grow,push");
        card.add(createTableFooter(), "growx");

        wrapper.add(card, "grow,push");
        return wrapper;
    }

    private JPanel createListHeader() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow][]",
                        "[][]"
                )
        );
        panel.setOpaque(false);

        JLabel title = new JLabel("Quản lý khóa học");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Quản lý thông tin, học phí và trạng thái của các khóa học"
        );
        subtitle.setFont(UIConstants.FONT_NORMAL);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        lblCourseCount = new JLabel("0 khóa học");
        lblCourseCount.setFont(UIConstants.FONT_MEDIUM.deriveFont(Font.BOLD));
        lblCourseCount.setForeground(UIConstants.PRIMARY);

        panel.add(title, "cell 0 0");
        panel.add(subtitle, "cell 0 1");
        panel.add(lblCourseCount, "cell 1 0 1 2, align right");

        return panel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0, gapy 8",
                        "[grow,fill]",
                        "[][]"
                )
        );
        toolbar.setOpaque(false);

        configureSearchField();

        btnRefresh = createButton(
                "Làm mới",
                FontAwesomeSolid.SYNC_ALT,
                Color.WHITE,
                UIConstants.PRIMARY
        );

        btnAdd = createButton(
                "Thêm khóa học",
                FontAwesomeSolid.PLUS,
                UIConstants.PRIMARY,
                Color.WHITE
        );

        btnUpdate = createButton(
                "Cập nhật",
                FontAwesomeSolid.EDIT,
                Color.WHITE,
                UIConstants.PRIMARY
        );

        btnToggleStatus = createButton(
                "Tạm ngưng",
                FontAwesomeSolid.PAUSE,
                Color.WHITE,
                UIConstants.WARNING
        );

        btnArchive = createButton(
                "Lưu trữ",
                FontAwesomeSolid.ARCHIVE,
                Color.WHITE,
                UIConstants.PURPLE
        );

        /*
         * Giữ lại nút này để tương thích DashboardController cũ.
         * Giao diện mới không hiển thị nút xóa trực tiếp.
         */
        btnDelete = new JButton("Xóa");
        btnDelete.setVisible(false);

        JPanel searchRow = new JPanel(
                new MigLayout(
                        "fillx, insets 0, gapx 8",
                        "[grow,fill][][]",
                        "[]"
                )
        );
        searchRow.setOpaque(false);
        searchRow.add(txtSearch, "growx,height 38!");
        searchRow.add(btnRefresh, "height 38!");
        searchRow.add(btnAdd, "height 38!");

        JPanel actionRow = new JPanel(
                new MigLayout(
                        "fillx, insets 0, gapx 8",
                        "[grow][][][]",
                        "[]"
                )
        );
        actionRow.setOpaque(false);
        actionRow.add(new JLabel(), "growx");
        actionRow.add(btnUpdate);
        actionRow.add(btnToggleStatus);
        actionRow.add(btnArchive);

        toolbar.add(searchRow, "growx");
        toolbar.add(actionRow, "growx");
        return toolbar;
    }

    private void configureSearchField() {
        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo mã, tên, mô tả hoặc trạng thái..."
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
    }

    private JScrollPane createTableScrollPane() {
        configureTable();

        JScrollPane scrollPane = new JScrollPane(tableCourse);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER)
        );
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        return scrollPane;
    }

    private void configureTable() {
        tableCourse.setRowHeight(42);
        tableCourse.setFillsViewportHeight(true);
        tableCourse.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableCourse.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableCourse.setShowVerticalLines(false);
        tableCourse.setShowHorizontalLines(true);
        tableCourse.setGridColor(UIConstants.BORDER);
        tableCourse.setIntercellSpacing(new Dimension(0, 1));
        tableCourse.setSelectionBackground(new Color(239, 246, 255));
        tableCourse.setSelectionForeground(UIConstants.TEXT_PRIMARY);

        tableCourse.getTableHeader().setReorderingAllowed(false);
        tableCourse.getTableHeader().setPreferredSize(new Dimension(0, 42));
        tableCourse.getTableHeader().setFont(
                UIConstants.FONT_MEDIUM.deriveFont(Font.BOLD)
        );

        setColumnWidth(COLUMN_ID, 55);
        setColumnWidth(COLUMN_CODE, 130);
        setColumnWidth(COLUMN_NAME, 220);
        setColumnWidth(COLUMN_DESCRIPTION, 310);
        setColumnWidth(COLUMN_CREDITS, 80);
        setColumnWidth(COLUMN_FEE, 145);
        setColumnWidth(COLUMN_STATUS, 135);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tableCourse.getColumnModel().getColumn(COLUMN_ID).setCellRenderer(center);
        tableCourse.getColumnModel().getColumn(COLUMN_CREDITS).setCellRenderer(center);
        tableCourse.getColumnModel().getColumn(COLUMN_FEE)
                .setCellRenderer(new CurrencyCellRenderer());
        tableCourse.getColumnModel().getColumn(COLUMN_STATUS)
                .setCellRenderer(new StatusCellRenderer());
    }

    private void setColumnWidth(int column, int width) {
        tableCourse.getColumnModel().getColumn(column).setPreferredWidth(width);
        tableCourse.getColumnModel().getColumn(column).setMinWidth(width);
    }

    private JPanel createTableFooter() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow]",
                        "[]"
                )
        );
        panel.setOpaque(false);

        lblResultSummary = new JLabel("Chưa có dữ liệu khóa học");
        lblResultSummary.setFont(UIConstants.FONT_NORMAL);
        lblResultSummary.setForeground(UIConstants.TEXT_SECONDARY);

        panel.add(lblResultSummary, "growx");
        return panel;
    }

    private JPanel createFormCard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );

        ContentCard formCard = new ContentCard();
        formCard.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20 24",
                        "[grow,fill]",
                        "[]16[]"
                )
        );

        formCard.add(createFormHeader(), "growx");
        formCard.add(createCourseForm(), "growx");

        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        wrapper.add(scrollPane, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel createFormHeader() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0, gapx 10",
                        "[grow,fill][][]",
                        "[][]"
                )
        );
        panel.setOpaque(false);

        lblFormTitle = new JLabel("Thêm khóa học");
        lblFormTitle.setFont(UIConstants.FONT_TITLE);
        lblFormTitle.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel subtitle = new JLabel(
                "Nhập đầy đủ thông tin để lưu khóa học"
        );
        subtitle.setFont(UIConstants.FONT_NORMAL);
        subtitle.setForeground(UIConstants.TEXT_SECONDARY);

        btnCancel = createButton(
                "Hủy",
                FontAwesomeSolid.TIMES,
                Color.WHITE,
                UIConstants.TEXT_SECONDARY
        );

        btnSave = createButton(
                "Lưu khóa học",
                FontAwesomeSolid.SAVE,
                UIConstants.PRIMARY,
                Color.WHITE
        );

        panel.add(lblFormTitle, "cell 0 0");
        panel.add(subtitle, "cell 0 1");
        panel.add(btnCancel, "cell 1 0 1 2,height 40!,width 105!");
        panel.add(btnSave, "cell 2 0 1 2,height 40!,width 155!");

        return panel;
    }

    private JPanel createCourseForm() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 18",
                        "[right,150!]12[grow,fill]",
                        "[]12[]12[]12[]12[]12[]"
                )
        );
        panel.setBackground(Color.WHITE);
        panel.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(UIConstants.BORDER),
                        "Thông tin khóa học"
                )
        );

        txtCode = createTextField("Ví dụ: JAVA01");
        txtName = createTextField("Tên khóa học");
        txtTuition = createTextField("Ví dụ: 3000000");

        txtDescription = new JTextArea(5, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(UIConstants.FONT_NORMAL);

        JScrollPane descriptionScroll = new JScrollPane(txtDescription);
        descriptionScroll.setBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER)
        );

        spCredits = new JSpinner(
                new SpinnerNumberModel(1, 1, 50, 1)
        );

        cboStatus = new JComboBox<>(
                new String[]{"ACTIVE", "INACTIVE", "ARCHIVED"}
        );
        cboStatus.setFont(UIConstants.FONT_NORMAL);

        panel.add(createFormLabel("Mã khóa học *"));
        panel.add(txtCode, "growx,height 38!");

        panel.add(createFormLabel("Tên khóa học *"));
        panel.add(txtName, "growx,height 38!");

        panel.add(createFormLabel("Mô tả"));
        panel.add(descriptionScroll, "growx,height 115!");

        panel.add(createFormLabel("Số tín chỉ *"));
        panel.add(spCredits, "growx,height 38!");

        panel.add(createFormLabel("Học phí *"));
        panel.add(txtTuition, "growx,height 38!");

        panel.add(createFormLabel("Trạng thái"));
        panel.add(cboStatus, "growx,height 38!");

        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(UIConstants.FONT_NORMAL);
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                margin: 7,10,7,10;
                """
        );
        return field;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_MEDIUM);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    private JButton createButton(
            String text,
            FontAwesomeSolid icon,
            Color background,
            Color foreground
    ) {
        JButton button = new JButton(text);
        button.setIcon(FontIcon.of(icon, 13, foreground));
        button.setFont(UIConstants.FONT_MEDIUM);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusable(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 1;
                focusWidth: 0;
                margin: 7,11,7,11;
                """
        );
        return button;
    }

    private void registerEvents() {
        btnAdd.addActionListener(event -> showCreateForm());
        btnUpdate.addActionListener(event -> showEditForm());
        btnToggleStatus.addActionListener(event -> toggleSelectedCourseStatus());
        btnArchive.addActionListener(event -> archiveSelectedCourse());
        btnRefresh.addActionListener(event -> refreshList());
        btnCancel.addActionListener(event -> showListCard());
        btnSave.addActionListener(event -> saveCourse());

        tableCourse.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) {
                updateActionButtonState();
            }
        });

        tableCourse.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent event) {
                if (
                        event.getClickCount() == 2
                                && managementMode
                                && tableCourse.getSelectedRow() >= 0
                ) {
                    showEditForm();
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                applySearch();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                applySearch();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                applySearch();
            }
        });

        updateActionButtonState();
    }

    public final void loadCourses() {
        if (loading) {
            return;
        }

        loading = true;

        try {
            List<Course> courses = courseController.getAllCourses();
            allCourses = courses == null
                    ? Collections.emptyList()
                    : courses;
            displayCourses(getFilteredCourses());
        } catch (RuntimeException exception) {
            allCourses = Collections.emptyList();
            tableModel.setRowCount(0);
            updateSummary();
            showError("Không thể tải danh sách khóa học.", exception);
        } finally {
            loading = false;
        }
    }

    private void refreshList() {
        txtSearch.setText("");
        loadCourses();
    }

    private void applySearch() {
        displayCourses(getFilteredCourses());
    }

    private List<Course> getFilteredCourses() {
        if (allCourses == null || allCourses.isEmpty()) {
            return Collections.emptyList();
        }

        String keyword = txtSearch.getText() == null
                ? ""
                : txtSearch.getText().trim().toLowerCase(Locale.ROOT);

        if (keyword.isBlank()) {
            return allCourses;
        }

        return allCourses.stream()
                .filter(course -> course != null)
                .filter(course ->
                        contains(course.getCourseCode(), keyword)
                                || contains(course.getCourseName(), keyword)
                                || contains(course.getDescription(), keyword)
                                || contains(course.getStatus(), keyword)
                                || contains(formatStatus(course.getStatus()), keyword)
                )
                .toList();
    }

    private boolean contains(String value, String keyword) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void displayCourses(List<Course> courses) {
        tableModel.setRowCount(0);

        if (courses != null) {
            for (Course course : courses) {
                if (course == null) {
                    continue;
                }

                tableModel.addRow(
                        new Object[]{
                                course.getCourseId(),
                                course.getCourseCode(),
                                course.getCourseName(),
                                safeText(course.getDescription()),
                                course.getCredits(),
                                course.getTuitionFee(),
                                normalizeStatus(course.getStatus())
                        }
                );
            }
        }

        tableCourse.clearSelection();
        updateSummary();
        updateActionButtonState();
        tableCourse.revalidate();
        tableCourse.repaint();
    }

    private void updateSummary() {
        int total = allCourses == null ? 0 : allCourses.size();
        int visible = tableModel.getRowCount();

        lblCourseCount.setText(total + " khóa học");

        if (total == 0) {
            lblResultSummary.setText("Chưa có khóa học trong hệ thống");
        } else if (txtSearch.getText() != null && !txtSearch.getText().isBlank()) {
            lblResultSummary.setText(
                    "Tìm thấy " + visible + " trong " + total + " khóa học"
            );
        } else {
            lblResultSummary.setText("Đang hiển thị " + total + " khóa học");
        }
    }

    private void showCreateForm() {
        editingCourse = null;
        clearForm();

        lblFormTitle.setText("Thêm khóa học");
        btnSave.setText("Lưu khóa học");
        txtCode.setEditable(true);
        cboStatus.setSelectedItem("ACTIVE");

        cardLayout.show(cardPanel, FORM_CARD);
    }

    private void showEditForm() {
        int courseId = getSelectedCourseId();

        if (courseId <= 0) {
            showWarning("Vui lòng chọn khóa học cần cập nhật.");
            return;
        }

        try {
            Course course = courseController.getCourseById(courseId);

            if (course == null) {
                showWarning("Không tìm thấy khóa học cần cập nhật.");
                return;
            }

            editingCourse = course;

            txtCode.setText(safeText(course.getCourseCode()));
            txtName.setText(safeText(course.getCourseName()));
            txtDescription.setText(safeText(course.getDescription()));
            spCredits.setValue(Math.max(1, course.getCredits()));
            txtTuition.setText(
                    course.getTuitionFee() == null
                            ? "0"
                            : course.getTuitionFee().toPlainString()
            );
            cboStatus.setSelectedItem(normalizeStatus(course.getStatus()));

            lblFormTitle.setText("Cập nhật khóa học");
            btnSave.setText("Lưu thay đổi");

            /* Mã khóa học được giữ cố định khi cập nhật. */
            txtCode.setEditable(false);

            cardLayout.show(cardPanel, FORM_CARD);
        } catch (RuntimeException exception) {
            showError("Không thể tải thông tin khóa học.", exception);
        }
    }

    private void showListCard() {
        editingCourse = null;
        clearForm();
        cardLayout.show(cardPanel, LIST_CARD);
    }

    private void clearForm() {
        if (txtCode == null) {
            return;
        }

        txtCode.setText("");
        txtName.setText("");
        txtDescription.setText("");
        spCredits.setValue(1);
        txtTuition.setText("");
        cboStatus.setSelectedItem("ACTIVE");
        txtCode.setEditable(true);
    }

    private void saveCourse() {
        try {
            Course course = buildCourseFromForm();
            boolean creating = editingCourse == null;
            boolean successful;

            if (creating) {
                successful = courseController.addCourse(course);
            } else {
                course.setCourseId(editingCourse.getCourseId());
                successful = courseController.updateCourse(course);
            }

            if (!successful) {
                showWarning("Không thể lưu khóa học.");
                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    creating
                            ? "Thêm khóa học thành công."
                            : "Cập nhật khóa học thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadCourses();
            showListCard();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError("Không thể lưu khóa học.", exception);
        }
    }

    private Course buildCourseFromForm() {
        String code = txtCode.getText() == null
                ? ""
                : txtCode.getText().trim();
        String name = txtName.getText() == null
                ? ""
                : txtName.getText().trim();
        String description = txtDescription.getText() == null
                ? null
                : txtDescription.getText().trim();

        if (code.isBlank()) {
            throw new IllegalArgumentException(
                    "Mã khóa học không được để trống."
            );
        }

        if (name.isBlank()) {
            throw new IllegalArgumentException(
                    "Tên khóa học không được để trống."
            );
        }

        int credits = ((Number) spCredits.getValue()).intValue();

        if (credits <= 0) {
            throw new IllegalArgumentException(
                    "Số tín chỉ phải lớn hơn 0."
            );
        }

        Course course = new Course();
        course.setCourseCode(code);
        course.setCourseName(name);
        course.setDescription(description);
        course.setCredits(credits);
        course.setTuitionFee(parseTuitionFee(txtTuition.getText()));
        course.setStatus(String.valueOf(cboStatus.getSelectedItem()));

        return course;
    }

    private BigDecimal parseTuitionFee(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }

        String normalized = value.trim()
                .replace("đ", "")
                .replace("₫", "")
                .replace(".", "")
                .replace(",", "")
                .replace(" ", "");

        try {
            BigDecimal amount = new BigDecimal(normalized);

            if (amount.signum() < 0) {
                throw new IllegalArgumentException(
                        "Học phí không được âm."
                );
            }

            return amount;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Học phí không hợp lệ."
            );
        }
    }

    private void toggleSelectedCourseStatus() {
        int courseId = getSelectedCourseId();

        if (courseId <= 0) {
            showWarning("Vui lòng chọn khóa học.");
            return;
        }

        try {
            Course course = courseController.getCourseById(courseId);

            if (course == null) {
                showWarning("Không tìm thấy khóa học.");
                return;
            }

            String status = normalizeStatus(course.getStatus());

            if ("ARCHIVED".equals(status)) {
                showWarning("Khóa học đã được lưu trữ.");
                return;
            }

            boolean activate = "INACTIVE".equals(status);
            String action = activate ? "kích hoạt lại" : "tạm ngưng";

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn " + action + " khóa học \""
                            + course.getCourseName() + "\"?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean successful = activate
                    ? courseController.activateCourse(courseId)
                    : courseController.deactivateCourse(courseId);

            if (!successful) {
                showWarning("Không thể cập nhật trạng thái khóa học.");
                return;
            }

            loadCourses();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError("Không thể cập nhật trạng thái khóa học.", exception);
        }
    }

    private void archiveSelectedCourse() {
        int courseId = getSelectedCourseId();

        if (courseId <= 0) {
            showWarning("Vui lòng chọn khóa học cần lưu trữ.");
            return;
        }

        try {
            Course course = courseController.getCourseById(courseId);

            if (course == null) {
                showWarning("Không tìm thấy khóa học.");
                return;
            }

            if ("ARCHIVED".equals(normalizeStatus(course.getStatus()))) {
                showWarning("Khóa học đã được lưu trữ.");
                return;
            }

            int classCount = courseController.countClasses(courseId);

            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Bạn có chắc muốn lưu trữ khóa học \""
                            + course.getCourseName()
                            + "\"?\n\nSố lớp liên quan: "
                            + classCount
                            + "\nKhóa học sẽ không còn được dùng để tạo lớp mới.",
                    "Xác nhận lưu trữ",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (answer != JOptionPane.YES_OPTION) {
                return;
            }

            boolean successful = courseController.archiveCourse(courseId);

            if (!successful) {
                showWarning("Không thể lưu trữ khóa học.");
                return;
            }

            loadCourses();
        } catch (IllegalArgumentException | IllegalStateException exception) {
            showWarning(exception.getMessage());
        } catch (RuntimeException exception) {
            showError("Không thể lưu trữ khóa học.", exception);
        }
    }

    private void updateActionButtonState() {
        Course selected = getSelectedCourse();
        boolean hasSelection = selected != null;

        btnUpdate.setEnabled(managementMode && hasSelection);
        btnToggleStatus.setEnabled(managementMode && hasSelection);
        btnArchive.setEnabled(managementMode && hasSelection);

        if (!hasSelection) {
            btnToggleStatus.setText("Tạm ngưng");
            btnToggleStatus.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.PAUSE,
                            13,
                            UIConstants.WARNING
                    )
            );
            return;
        }

        String status = normalizeStatus(selected.getStatus());

        if ("INACTIVE".equals(status)) {
            btnToggleStatus.setText("Kích hoạt");
            btnToggleStatus.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.PLAY,
                            13,
                            UIConstants.WARNING
                    )
            );
        } else {
            btnToggleStatus.setText("Tạm ngưng");
            btnToggleStatus.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.PAUSE,
                            13,
                            UIConstants.WARNING
                    )
            );
        }

        boolean archived = "ARCHIVED".equals(status);
        btnToggleStatus.setEnabled(managementMode && !archived);
        btnArchive.setEnabled(managementMode && !archived);
    }

    private Course getSelectedCourse() {
        int courseId = getSelectedCourseId();

        if (courseId <= 0 || allCourses == null) {
            return null;
        }

        return allCourses.stream()
                .filter(course -> course != null && course.getCourseId() == courseId)
                .findFirst()
                .orElse(null);
    }

    public int getSelectedCourseId() {
        int selectedViewRow = tableCourse.getSelectedRow();

        if (selectedViewRow < 0) {
            return -1;
        }

        int modelRow = tableCourse.convertRowIndexToModel(selectedViewRow);
        Object value = tableModel.getValueAt(modelRow, COLUMN_ID);

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "ACTIVE";
        }

        return switch (status.trim().toUpperCase(Locale.ROOT)) {
            case "OPEN", "ACTIVE" -> "ACTIVE";
            case "CLOSED", "INACTIVE" -> "INACTIVE";
            case "ARCHIVED" -> "ARCHIVED";
            default -> status.trim().toUpperCase(Locale.ROOT);
        };
    }

    private String formatStatus(String status) {
        return switch (normalizeStatus(status)) {
            case "ACTIVE" -> "Đang hoạt động";
            case "INACTIVE" -> "Tạm ngưng";
            case "ARCHIVED" -> "Đã lưu trữ";
            default -> safeText(status);
        };
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Thông báo",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message, Throwable throwable) {
        Throwable root = throwable;

        while (root != null && root.getCause() != null) {
            root = root.getCause();
        }

        String detail = root == null
                || root.getMessage() == null
                || root.getMessage().isBlank()
                ? "Không xác định"
                : root.getMessage();

        JOptionPane.showMessageDialog(
                this,
                message + "\nChi tiết: " + detail,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /* =====================================================
       TƯƠNG THÍCH VỚI CODE CŨ
       ===================================================== */

    public JTable getTableCourse() {
        return tableCourse;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnAdd() {
        return btnAdd;
    }

    public JButton getBtnUpdate() {
        return btnUpdate;
    }

    public JButton getBtnDelete() {
        return btnDelete;
    }

    public JButton getBtnRefresh() {
        return btnRefresh;
    }

    public JTextField getTxtSearch() {
        return txtSearch;
    }

    public void clearSelection() {
        tableCourse.clearSelection();
        updateActionButtonState();
    }

    public void setManagementMode(boolean managementMode) {
        this.managementMode = managementMode;

        btnAdd.setVisible(managementMode);
        btnUpdate.setVisible(managementMode);
        btnToggleStatus.setVisible(managementMode);
        btnArchive.setVisible(managementMode);

        btnRefresh.setText(managementMode ? "Làm mới" : "Tải lại");

        updateActionButtonState();
        revalidate();
        repaint();
    }

    public boolean isManagementMode() {
        return managementMode;
    }

    private static class CurrencyCellRenderer
            extends DefaultTableCellRenderer {

        private final DecimalFormat formatter = new DecimalFormat("#,##0 đ");

        public CurrencyCellRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
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
            Object displayValue = value;

            if (value instanceof Number number) {
                displayValue = formatter.format(number.doubleValue());
            }

            Component component = super.getTableCellRendererComponent(
                    table,
                    displayValue,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
            return component;
        }
    }

    private static class StatusCellRenderer
            extends DefaultTableCellRenderer {

        public StatusCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
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
            String status = value == null
                    ? ""
                    : value.toString().trim().toUpperCase(Locale.ROOT);

            String displayText = switch (status) {
                case "ACTIVE" -> "● Đang hoạt động";
                case "INACTIVE" -> "● Tạm ngưng";
                case "ARCHIVED" -> "● Đã lưu trữ";
                default -> status;
            };

            Component component = super.getTableCellRendererComponent(
                    table,
                    displayText,
                    isSelected,
                    hasFocus,
                    row,
                    column
            );

            setFont(UIConstants.FONT_SMALL.deriveFont(Font.BOLD));

            if (!isSelected) {
                setBackground(Color.WHITE);
                setForeground(
                        switch (status) {
                            case "ACTIVE" -> UIConstants.SUCCESS;
                            case "INACTIVE" -> UIConstants.WARNING;
                            case "ARCHIVED" -> UIConstants.PURPLE;
                            default -> UIConstants.TEXT_SECONDARY;
                        }
                );
            }

            return component;
        }
    }
}
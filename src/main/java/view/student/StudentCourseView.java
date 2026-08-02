package view.student;

import controller.StudentCourseController;
import controller.StudentRegistrationController;
import model.dto.AvailableClassDTO;
import model.dto.RegistrationResult;
import model.dto.StudentCourseDTO;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudentCourseView extends JPanel {

    private final int studentId;

    private final StudentCourseController studentCourseController;
    private final StudentRegistrationController registrationController;

    private final JTextField searchField;

    private final JButton registrationModeButton;
    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton actionButton;

    private final JLabel titleLabel;
    private final JLabel descriptionLabel;

    private final JLabel totalTitleLabel;
    private final JLabel totalCourseLabel;

    private final JLabel secondSummaryTitleLabel;
    private final JLabel secondSummaryValueLabel;

    private final JLabel selectedTitleLabel;
    private final JLabel selectedCourseLabel;

    private final DefaultTableModel tableModel;
    private final JTable courseTable;

    private List<StudentCourseDTO> currentCourses =
            Collections.emptyList();

    private List<AvailableClassDTO> availableClasses =
            Collections.emptyList();

    private StudentCourseDTO selectedCourse;
    private AvailableClassDTO selectedAvailableClass;

    private CourseActionHandler courseActionHandler;

    private boolean registrationMode;
    private boolean loading;

    public StudentCourseView(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        this.studentId = studentId;

        this.studentCourseController =
                new StudentCourseController();

        this.registrationController =
                new StudentRegistrationController();

        this.searchField =
                new JTextField();

        this.registrationModeButton =
                createPrimaryButton(
                        "Đăng ký khóa học",
                        FontAwesomeSolid.PLUS_CIRCLE
                );

        this.searchButton =
                createPrimaryButton(
                        "Tìm kiếm",
                        FontAwesomeSolid.SEARCH
                );

        this.refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        this.actionButton =
                createPrimaryButton(
                        "Xem chi tiết",
                        FontAwesomeSolid.EYE
                );

        this.titleLabel =
                new JLabel("Khóa học của tôi");

        this.descriptionLabel =
                new JLabel(
                        "Theo dõi lớp học, giảng viên, "
                                + "lịch học và tiến độ hiện tại."
                );

        this.totalTitleLabel =
                new JLabel("Tổng khóa học");

        this.totalCourseLabel =
                new JLabel("0 khóa học");

        this.secondSummaryTitleLabel =
                new JLabel("Tiến độ trung bình");

        this.secondSummaryValueLabel =
                new JLabel("0%");

        this.selectedTitleLabel =
                new JLabel("Khóa học đang chọn");

        this.selectedCourseLabel =
                new JLabel("Chưa chọn khóa học");

        this.tableModel =
                new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        this.courseTable =
                new JTable(tableModel);

        initializeView();
        configureSearchField();
        configureTableCommon();
        registerEvents();

        showMyCoursesMode();
    }

    /* =====================================================
       KHỞI TẠO VIEW
       ===================================================== */

    private void initializeView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 1, insets 18",
                                "[grow,fill]",
                                "[]14[]14[grow,fill]12[]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        wrapper.add(
                createFilterPanel(),
                "growx"
        );

        wrapper.add(
                createTableCard(),
                "grow,push"
        );

        wrapper.add(
                createSelectedCourseCard(),
                "growx"
        );

        add(wrapper, BorderLayout.CENTER);
    }

    /* =====================================================
       HEADER
       ===================================================== */

    private JPanel createHeaderPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow][]14[]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalTitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        totalTitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalCourseLabel.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(Font.BOLD)
        );

        totalCourseLabel.setForeground(
                UIConstants.PRIMARY
        );

        secondSummaryTitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        secondSummaryTitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        secondSummaryValueLabel.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(Font.BOLD)
        );

        secondSummaryValueLabel.setForeground(
                UIConstants.SUCCESS
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
                totalTitleLabel,
                "cell 1 0, align right"
        );

        panel.add(
                totalCourseLabel,
                "cell 1 1, align right"
        );

        panel.add(
                secondSummaryTitleLabel,
                "cell 2 0, align right"
        );

        panel.add(
                secondSummaryValueLabel,
                "cell 2 1, align right"
        );

        return panel;
    }

    /* =====================================================
       BỘ LỌC
       ===================================================== */

    private ContentCard createFilterPanel() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 16, gapx 10",
                        "[grow,fill][][][]",
                        "[]"
                )
        );

        card.add(
                searchField,
                "height 40!"
        );

        card.add(
                registrationModeButton,
                "height 40!"
        );

        card.add(
                searchButton,
                "height 40!"
        );

        card.add(
                refreshButton,
                "height 40!"
        );

        return card;
    }

    private void configureSearchField() {
        searchField.setFont(
                UIConstants.FONT_NORMAL
        );

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo mã, tên khóa học, lớp, "
                        + "giảng viên, lịch hoặc phòng"
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
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );
    }

    /* =====================================================
       BẢNG
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, insets 16",
                        "[grow,fill]",
                        "[grow,fill]"
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(courseTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane
                .getViewport()
                .setBackground(Color.WHITE);

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane
                .getHorizontalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        card.add(
                scrollPane,
                "grow,push"
        );

        return card;
    }

    private void configureTableCommon() {
        courseTable.setRowHeight(42);
        courseTable.setFillsViewportHeight(true);

        courseTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        courseTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        courseTable.setShowVerticalLines(false);
        courseTable.setShowHorizontalLines(true);

        courseTable.setGridColor(
                UIConstants.BORDER
        );

        courseTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        courseTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        courseTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        courseTable
                .getTableHeader()
                .setReorderingAllowed(false);

        courseTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 42)
                );

        courseTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(Font.BOLD)
                );
    }

    private void configureMyCoursesColumns() {
        tableModel.setColumnIdentifiers(
                new Object[]{
                        "STT",
                        "Mã khóa học",
                        "Tên khóa học",
                        "Mã lớp",
                        "Giảng viên",
                        "Lịch học",
                        "Phòng",
                        "Tiến độ",
                        "Học phí",
                        "Trạng thái"
                }
        );

        applyCenterRenderer();

        setColumnWidth(0, 50);
        setColumnWidth(1, 110);
        setColumnWidth(2, 210);
        setColumnWidth(3, 110);
        setColumnWidth(4, 170);
        setColumnWidth(5, 210);
        setColumnWidth(6, 90);
        setColumnWidth(7, 90);
        setColumnWidth(8, 130);
        setColumnWidth(9, 130);
    }

    private void configureAvailableColumns() {
        tableModel.setColumnIdentifiers(
                new Object[]{
                        "STT",
                        "Mã khóa học",
                        "Tên khóa học",
                        "Mã lớp",
                        "Giảng viên",
                        "Lịch học",
                        "Phòng",
                        "Còn chỗ",
                        "Học phí",
                        "Trạng thái"
                }
        );

        applyCenterRenderer();

        setColumnWidth(0, 50);
        setColumnWidth(1, 110);
        setColumnWidth(2, 210);
        setColumnWidth(3, 110);
        setColumnWidth(4, 170);
        setColumnWidth(5, 210);
        setColumnWidth(6, 90);
        setColumnWidth(7, 100);
        setColumnWidth(8, 130);
        setColumnWidth(9, 140);
    }

    private void applyCenterRenderer() {
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();

        renderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        for (
                int column = 0;
                column < tableModel.getColumnCount();
                column++
        ) {
            courseTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(renderer);
        }
    }

    private void setColumnWidth(
            int columnIndex,
            int width
    ) {
        courseTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(width);

        courseTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setMinWidth(width);
    }

    /* =====================================================
       THẺ LỰA CHỌN
       ===================================================== */

    private ContentCard createSelectedCourseCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 18",
                        "[grow][]",
                        "[][]"
                )
        );

        selectedTitleLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        selectedTitleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        selectedCourseLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        selectedCourseLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                selectedTitleLabel,
                "cell 0 0"
        );

        card.add(
                selectedCourseLabel,
                "cell 0 1"
        );

        card.add(
                actionButton,
                "cell 1 0 1 2, height 38!"
        );

        return card;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        registrationModeButton.addActionListener(
                event -> toggleRegistrationMode()
        );

        searchButton.addActionListener(
                event -> searchData()
        );

        refreshButton.addActionListener(
                event -> refreshCurrentMode()
        );

        searchField.addActionListener(
                event -> searchData()
        );

        courseTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateSelection();
                            }
                        }
                );

        courseTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (event.getClickCount() == 2) {
                            performCurrentAction();
                        }
                    }
                }
        );

        actionButton.addActionListener(
                event -> performCurrentAction()
        );
    }

    private void toggleRegistrationMode() {
        if (registrationMode) {
            showMyCoursesMode();
        } else {
            showRegistrationMode();
        }
    }

    private void refreshCurrentMode() {
        searchField.setText("");

        if (registrationMode) {
            loadAvailableClasses();
        } else {
            loadData();
        }
    }

    /* =====================================================
       CHẾ ĐỘ KHÓA HỌC ĐÃ ĐĂNG KÝ
       ===================================================== */

    private void showMyCoursesMode() {
        registrationMode = false;

        titleLabel.setText(
                "Khóa học của tôi"
        );

        descriptionLabel.setText(
                "Theo dõi lớp học, giảng viên, "
                        + "lịch học và tiến độ hiện tại."
        );

        registrationModeButton.setText(
                "Đăng ký khóa học"
        );

        registrationModeButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.PLUS_CIRCLE,
                        14,
                        Color.WHITE
                )
        );

        totalTitleLabel.setText(
                "Tổng khóa học"
        );

        secondSummaryTitleLabel.setText(
                "Tiến độ trung bình"
        );

        secondSummaryTitleLabel.setVisible(true);
        secondSummaryValueLabel.setVisible(true);

        selectedTitleLabel.setText(
                "Khóa học đang chọn"
        );

        actionButton.setText(
                "Xem chi tiết"
        );

        actionButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.EYE,
                        14,
                        Color.WHITE
                )
        );

        configureMyCoursesColumns();
        loadData();
    }

    /* =====================================================
       CHẾ ĐỘ ĐĂNG KÝ
       ===================================================== */

    private void showRegistrationMode() {
        registrationMode = true;

        titleLabel.setText(
                "Đăng ký khóa học"
        );

        descriptionLabel.setText(
                "Danh sách các lớp chưa đăng ký, "
                        + "còn chỗ và có thể tham gia."
        );

        registrationModeButton.setText(
                "Quay lại"
        );

        registrationModeButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.ARROW_LEFT,
                        14,
                        Color.WHITE
                )
        );

        totalTitleLabel.setText(
                "Có thể đăng ký"
        );

        secondSummaryTitleLabel.setVisible(false);
        secondSummaryValueLabel.setVisible(false);

        selectedTitleLabel.setText(
                "Lớp đang chọn"
        );

        actionButton.setText(
                "Đăng ký"
        );

        actionButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.PLUS_CIRCLE,
                        14,
                        Color.WHITE
                )
        );

        configureAvailableColumns();
        loadAvailableClasses();
    }

    /* =====================================================
       TẢI KHÓA HỌC ĐÃ ĐĂNG KÝ
       ===================================================== */

    public void loadData() {
        if (loading) {
            return;
        }

        setLoading(true);

        try {
            List<StudentCourseDTO> courses =
                    studentCourseController
                            .getCourses(studentId);

            currentCourses =
                    courses == null
                            ? Collections.emptyList()
                            : courses;

            displayMyCourses(currentCourses);
            updateMyCoursesSummary(currentCourses);

        } catch (RuntimeException exception) {
            currentCourses =
                    Collections.emptyList();

            displayMyCourses(currentCourses);
            updateMyCoursesSummary(currentCourses);

            showError(
                    "Không thể tải khóa học của sinh viên.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       TẢI KHÓA HỌC CÓ THỂ ĐĂNG KÝ
       ===================================================== */

    private void loadAvailableClasses() {
        if (loading) {
            return;
        }

        setLoading(true);

        try {
            List<AvailableClassDTO> classes =
                    registrationController
                            .getAvailableClasses(
                                    studentId
                            );

            availableClasses =
                    classes == null
                            ? Collections.emptyList()
                            : classes;

            displayAvailableClasses(
                    availableClasses
            );

            totalCourseLabel.setText(
                    availableClasses.size()
                            + " lớp"
            );

        } catch (RuntimeException exception) {
            availableClasses =
                    Collections.emptyList();

            displayAvailableClasses(
                    availableClasses
            );

            totalCourseLabel.setText(
                    "0 lớp"
            );

            showError(
                    "Không thể tải danh sách lớp có thể đăng ký.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

    private void searchData() {
        if (loading) {
            return;
        }

        String keyword =
                searchField.getText() == null
                        ? ""
                        : searchField
                        .getText()
                        .trim();

        setLoading(true);

        try {
            if (registrationMode) {
                List<AvailableClassDTO> classes =
                        registrationController
                                .searchAvailableClasses(
                                        studentId,
                                        keyword
                                );

                availableClasses =
                        classes == null
                                ? Collections.emptyList()
                                : classes;

                displayAvailableClasses(
                        availableClasses
                );

                totalCourseLabel.setText(
                        availableClasses.size()
                                + " lớp"
                );

            } else {
                List<StudentCourseDTO> courses =
                        studentCourseController
                                .searchCourses(
                                        studentId,
                                        keyword
                                );

                currentCourses =
                        courses == null
                                ? Collections.emptyList()
                                : courses;

                displayMyCourses(currentCourses);
                updateMyCoursesSummary(currentCourses);
            }

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tìm kiếm khóa học.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       HIỂN THỊ KHÓA HỌC ĐÃ ĐĂNG KÝ
       ===================================================== */

    private void displayMyCourses(
            List<StudentCourseDTO> courses
    ) {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (StudentCourseDTO course : courses) {
            if (course == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            safeText(
                                    course.getCourseCode()
                            ),
                            safeText(
                                    course.getCourseName()
                            ),
                            safeText(
                                    course.getClassCode()
                            ),
                            safeText(
                                    course.getTeacherName()
                            ),
                            safeText(
                                    course.getScheduleText()
                            ),
                            safeText(
                                    course.getRoom()
                            ),
                            course.getProgressPercent()
                                    + "%",
                            formatMoney(
                                    course.getTuitionFee()
                            ),
                            formatEnrollmentStatus(
                                    course.getEnrollmentStatus()
                            )
                    }
            );
        }

        clearSelection();

        courseTable.revalidate();
        courseTable.repaint();
    }

    /* =====================================================
       HIỂN THỊ KHÓA HỌC CHƯA ĐĂNG KÝ
       ===================================================== */

    private void displayAvailableClasses(
            List<AvailableClassDTO> classes
    ) {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (AvailableClassDTO classDTO : classes) {
            if (classDTO == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            safeText(
                                    classDTO.getCourseCode()
                            ),
                            safeText(
                                    classDTO.getCourseName()
                            ),
                            safeText(
                                    classDTO.getClassCode()
                            ),
                            safeText(
                                    classDTO.getTeacherName()
                            ),
                            safeText(
                                    classDTO.getScheduleText()
                            ),
                            safeText(
                                    classDTO.getRoom()
                            ),
                            classDTO.getRemainingSlots()
                                    + "/"
                                    + classDTO.getMaximumStudents(),
                            formatMoney(
                                    classDTO.getTuitionFee()
                            ),
                            "Chưa đăng ký"
                    }
            );
        }

        clearSelection();

        courseTable.revalidate();
        courseTable.repaint();
    }

    /* =====================================================
       THỐNG KÊ
       ===================================================== */

    private void updateMyCoursesSummary(
            List<StudentCourseDTO> courses
    ) {
        int total =
                courses == null
                        ? 0
                        : courses.size();

        totalCourseLabel.setText(
                total + " khóa học"
        );

        double averageProgress =
                courses == null
                        || courses.isEmpty()
                        ? 0.0
                        : courses.stream()
                        .filter(
                                course ->
                                        course != null
                        )
                        .mapToInt(
                                StudentCourseDTO
                                        ::getProgressPercent
                        )
                        .average()
                        .orElse(0.0);

        secondSummaryValueLabel.setText(
                String.format(
                        Locale.US,
                        "%.1f%%",
                        averageProgress
                )
        );
    }

    /* =====================================================
       CHỌN DÒNG
       ===================================================== */

    private void updateSelection() {
        int selectedViewRow =
                courseTable.getSelectedRow();

        if (selectedViewRow < 0) {
            clearSelection();
            return;
        }

        int selectedModelRow =
                courseTable.convertRowIndexToModel(
                        selectedViewRow
                );

        String courseCode =
                String.valueOf(
                        tableModel.getValueAt(
                                selectedModelRow,
                                1
                        )
                );

        String classCode =
                String.valueOf(
                        tableModel.getValueAt(
                                selectedModelRow,
                                3
                        )
                );

        if (registrationMode) {
            selectAvailableClass(
                    courseCode,
                    classCode
            );
        } else {
            selectRegisteredCourse(
                    courseCode,
                    classCode
            );
        }
    }

    private void selectAvailableClass(
            String courseCode,
            String classCode
    ) {
        selectedAvailableClass =
                availableClasses.stream()
                        .filter(
                                item ->
                                        item != null
                                                && safeText(
                                                item.getCourseCode()
                                        ).equals(courseCode)
                                                && safeText(
                                                item.getClassCode()
                                        ).equals(classCode)
                        )
                        .findFirst()
                        .orElse(null);

        selectedCourse = null;

        if (selectedAvailableClass == null) {
            clearSelection();
            return;
        }

        selectedCourseLabel.setText(
                selectedAvailableClass
                        .getDisplayCourseName()
                        + " | Lớp "
                        + safeText(
                        selectedAvailableClass
                                .getClassCode()
                )
                        + " | Còn "
                        + selectedAvailableClass
                        .getRemainingSlots()
                        + " chỗ"
        );

        actionButton.setEnabled(true);
    }

    private void selectRegisteredCourse(
            String courseCode,
            String classCode
    ) {
        selectedCourse =
                currentCourses.stream()
                        .filter(
                                course ->
                                        course != null
                                                && safeText(
                                                course.getCourseCode()
                                        ).equals(courseCode)
                                                && safeText(
                                                course.getClassCode()
                                        ).equals(classCode)
                        )
                        .findFirst()
                        .orElse(null);

        selectedAvailableClass = null;

        if (selectedCourse == null) {
            clearSelection();
            return;
        }

        selectedCourseLabel.setText(
                selectedCourse
                        .getDisplayCourseName()
                        + " | Lớp "
                        + selectedCourse
                        .getDisplayClassName()
        );

        actionButton.setEnabled(true);
    }

    private void clearSelection() {
        selectedCourse = null;
        selectedAvailableClass = null;

        courseTable.clearSelection();

        selectedCourseLabel.setText(
                registrationMode
                        ? "Chưa chọn lớp để đăng ký"
                        : "Chưa chọn khóa học"
        );

        actionButton.setEnabled(false);
    }

    /* =====================================================
       HÀNH ĐỘNG
       ===================================================== */

    private void performCurrentAction() {
        if (registrationMode) {
            registerSelectedClass();
        } else {
            showCourseDetail();
        }
    }

    private void registerSelectedClass() {
        if (selectedAvailableClass == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một lớp học.",
                    "Chưa chọn lớp",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String message =
                "Bạn có chắc muốn đăng ký?\n\n"
                        + "Khóa học: "
                        + selectedAvailableClass
                        .getDisplayCourseName()
                        + "\nLớp: "
                        + safeText(
                        selectedAvailableClass
                                .getClassCode()
                )
                        + "\nLịch học: "
                        + safeText(
                        selectedAvailableClass
                                .getScheduleText()
                )
                        + "\nHọc phí: "
                        + formatMoney(
                        selectedAvailableClass
                                .getTuitionFee()
                );

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        message,
                        "Xác nhận đăng ký",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        setLoading(true);

        try {
            RegistrationResult result =
                    registrationController
                            .registerCourse(
                                    studentId,
                                    selectedAvailableClass
                                            .getClassId()
                            );

            if (result == null
                    || !result.isSuccessful()) {

                JOptionPane.showMessageDialog(
                        this,
                        result == null
                                ? "Đăng ký không thành công."
                                : result.getMessage(),
                        "Không thể đăng ký",
                        JOptionPane.WARNING_MESSAGE
                );

                setLoading(false);
                loadAvailableClasses();
                return;
            }

            int returnAnswer =
                    JOptionPane.showConfirmDialog(
                            this,
                            result.getMessage()
                                    + "\n\n"
                                    + "Khoản học phí đã được tạo "
                                    + "trong mục Thanh toán."
                                    + "\nBạn có muốn quay lại "
                                    + "Khóa học của tôi không?",
                            "Đăng ký thành công",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                    );

            setLoading(false);

            if (returnAnswer
                    == JOptionPane.YES_OPTION) {

                showMyCoursesMode();

            } else {
                loadAvailableClasses();
            }

        } catch (RuntimeException exception) {
            showError(
                    "Không thể đăng ký khóa học.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       CHI TIẾT KHÓA HỌC
       ===================================================== */

    private void showCourseDetail() {
        if (selectedCourse == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một khóa học.",
                    "Chưa chọn khóa học",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (courseActionHandler != null) {
            courseActionHandler.onViewCourse(
                    selectedCourse
            );
            return;
        }

        JPanel detailPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 8",
                                "[right]12[grow,fill]",
                                "[]8[]8[]8[]8[]8[]8[]8[]8[]8[]"
                        )
                );

        addDetailRow(
                detailPanel,
                "Khóa học:",
                selectedCourse
                        .getDisplayCourseName()
        );

        addDetailRow(
                detailPanel,
                "Lớp:",
                selectedCourse
                        .getDisplayClassName()
        );

        addDetailRow(
                detailPanel,
                "Giảng viên:",
                safeText(
                        selectedCourse
                                .getTeacherName()
                )
        );

        addDetailRow(
                detailPanel,
                "Học kỳ:",
                safeText(
                        selectedCourse
                                .getDisplaySemester()
                )
        );

        addDetailRow(
                detailPanel,
                "Lịch học:",
                safeText(
                        selectedCourse
                                .getScheduleText()
                )
        );

        addDetailRow(
                detailPanel,
                "Phòng:",
                safeText(
                        selectedCourse.getRoom()
                )
        );

        addDetailRow(
                detailPanel,
                "Tiến độ:",
                selectedCourse
                        .getProgressPercent()
                        + "%"
        );

        addDetailRow(
                detailPanel,
                "Học phí:",
                formatMoney(
                        selectedCourse
                                .getTuitionFee()
                )
        );

        addDetailRow(
                detailPanel,
                "Trạng thái:",
                formatEnrollmentStatus(
                        selectedCourse
                                .getEnrollmentStatus()
                )
        );

        JOptionPane.showMessageDialog(
                this,
                detailPanel,
                "Chi tiết khóa học",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void addDetailRow(
            JPanel panel,
            String label,
            String value
    ) {
        JLabel labelComponent =
                new JLabel(label);

        labelComponent.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        JLabel valueComponent =
                new JLabel(value);

        valueComponent.setFont(
                UIConstants.FONT_NORMAL
        );

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    /* =====================================================
       CALLBACK
       ===================================================== */

    public void setCourseActionHandler(
            CourseActionHandler handler
    ) {
        this.courseActionHandler = handler;
    }

    public interface CourseActionHandler {
        void onViewCourse(
                StudentCourseDTO course
        );
    }

    /* =====================================================
       BUTTON
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

    /* =====================================================
       LOADING
       ===================================================== */

    private void setLoading(
            boolean loading
    ) {
        this.loading = loading;

        registrationModeButton.setEnabled(!loading);
        searchButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        searchField.setEnabled(!loading);
        courseTable.setEnabled(!loading);

        if (loading) {
            actionButton.setEnabled(false);
        } else {
            actionButton.setEnabled(
                    registrationMode
                            ? selectedAvailableClass != null
                            : selectedCourse != null
            );
        }

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

    private String formatEnrollmentStatus(
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
            case "ENROLLED" ->
                    "Đã đăng ký";

            case "COMPLETED" ->
                    "Hoàn thành";

            case "CANCELLED" ->
                    "Đã hủy";

            case "DROPPED" ->
                    "Đã rút";

            default ->
                    status.trim();
        };
    }

    private String formatMoney(
            BigDecimal amount
    ) {
        BigDecimal safeAmount =
                amount == null
                        ? BigDecimal.ZERO
                        : amount;

        NumberFormat formatter =
                NumberFormat.getCurrencyInstance(
                        new Locale("vi", "VN")
                );

        return formatter.format(
                safeAmount
        );
    }

    private String safeText(
            String value
    ) {
        return value == null
                || value.isBlank()
                ? "--"
                : value.trim();
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
            current = current.getCause();
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
       GETTER
       ===================================================== */

    public int getStudentId() {
        return studentId;
    }

    public StudentCourseDTO getSelectedCourse() {
        return selectedCourse;
    }

    public boolean isRegistrationMode() {
        return registrationMode;
    }
}
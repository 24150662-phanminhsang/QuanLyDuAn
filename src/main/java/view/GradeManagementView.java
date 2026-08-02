package view;

import model.ClassRoom;
import model.Grade;
import model.GradeManagementMode;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.GradeService;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class GradeManagementView extends JPanel {

    private final GradeService gradeService;

    private final GradeManagementMode mode;
    private final Integer teacherId;

    /*
     * Form nhập điểm.
     */
    private final JTextField studentIdField;

    /*
     * Admin nhập classId trực tiếp.
     */
    private final JTextField classIdField;

    /*
     * Teacher chọn lớp được phân công.
     */
    private final JComboBox<ClassRoom> teacherClassComboBox;

    private final JTextField attendanceScoreField;
    private final JTextField midtermScoreField;
    private final JTextField finalScoreField;

    /*
     * Bộ lọc dành cho Admin.
     */
    private final JTextField filterClassIdField;

    /*
     * Bộ lọc dành cho Teacher.
     */
    private final JComboBox<ClassRoom> filterTeacherClassComboBox;

    private final JLabel resultLabel;
    private final JLabel totalGradeLabel;
    private final JLabel permissionLabel;

    private final JButton saveButton;
    private final JButton refreshButton;
    private final JButton filterButton;

    private final DefaultTableModel tableModel;
    private final JTable gradeTable;

    private List<Grade> currentGrades =
            Collections.emptyList();

    private List<ClassRoom> teacherClasses =
            Collections.emptyList();

    private boolean loading;

    /* =====================================================
       CONSTRUCTOR ADMIN MẶC ĐỊNH
       ===================================================== */

    public GradeManagementView() {
        this(
                GradeManagementMode.ADMIN,
                null
        );
    }

    /* =====================================================
       CONSTRUCTOR DÙNG CHUNG ADMIN / TEACHER
       ===================================================== */

    public GradeManagementView(
            GradeManagementMode mode,
            Integer teacherId
    ) {
        this.mode =
                mode == null
                        ? GradeManagementMode.ADMIN
                        : mode;

        this.teacherId = teacherId;

        validateModeConfiguration();

        gradeService =
                new GradeService();

        studentIdField =
                new JTextField();

        classIdField =
                new JTextField();

        teacherClassComboBox =
                new JComboBox<>();

        attendanceScoreField =
                new JTextField();

        midtermScoreField =
                new JTextField();

        finalScoreField =
                new JTextField();

        filterClassIdField =
                new JTextField();

        filterTeacherClassComboBox =
                new JComboBox<>();

        resultLabel =
                new JLabel(
                        isTeacherMode()
                                ? "Chọn lớp để tải danh sách điểm"
                                : "Nhập mã lớp để tải danh sách điểm"
                );

        totalGradeLabel =
                new JLabel("0 kết quả");

        permissionLabel =
                new JLabel(
                        isTeacherMode()
                                ? "Bạn chỉ được quản lý điểm "
                                + "các lớp được phân công."
                                : "Quản trị viên có quyền quản lý "
                                + "điểm của tất cả lớp học."
                );

        saveButton =
                createPrimaryButton(
                        "Lưu điểm",
                        FontAwesomeSolid.SAVE
                );

        refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        filterButton =
                createPrimaryButton(
                        "Tải danh sách",
                        FontAwesomeSolid.SEARCH
                );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "Mã sinh viên",
                                "Mã lớp",
                                "Chuyên cần",
                                "Giữa kỳ",
                                "Cuối kỳ",
                                "Trung bình",
                                "Kết quả"
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

        gradeTable =
                new JTable(tableModel);

        configureClassComboBox(
                teacherClassComboBox
        );

        configureClassComboBox(
                filterTeacherClassComboBox
        );

        initializeView();
        registerEvents();

        if (isTeacherMode()) {
            loadTeacherClasses();
        }
    }

    /* =====================================================
       KIỂM TRA CHẾ ĐỘ
       ===================================================== */

    private void validateModeConfiguration() {
        if (isTeacherMode()
                && (teacherId == null
                || teacherId <= 0)) {

            throw new IllegalArgumentException(
                    "Không xác định được giảng viên "
                            + "đang đăng nhập."
            );
        }
    }

    private boolean isTeacherMode() {
        return mode
                == GradeManagementMode.TEACHER;
    }

    private boolean isAdminMode() {
        return mode
                == GradeManagementMode.ADMIN;
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

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, insets 16",
                                "[330!, fill]16[grow, fill]",
                                "[grow, fill]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createInputCard(),
                "growy"
        );

        wrapper.add(
                createTableCard(),
                "grow, push"
        );

        add(
                wrapper,
                BorderLayout.CENTER
        );
    }

    /* =====================================================
       CARD NHẬP ĐIỂM
       ===================================================== */

    private ContentCard createInputCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 20",
                        "[grow, fill]",
                        "[]8[]10[]18[]10[]10[]10[]10[]18[]10[]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        isTeacherMode()
                                ? "Nhập điểm lớp phụ trách"
                                : "Nhập điểm học viên"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "<html>Điểm được tính theo tỷ lệ "
                                + "<b>10% – 30% – 60%</b></html>"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        configurePermissionLabel();

        card.add(titleLabel);
        card.add(descriptionLabel);
        card.add(
                permissionLabel,
                "growx"
        );

        configureTextField(
                studentIdField,
                "Nhập ID sinh viên"
        );

        configureTextField(
                classIdField,
                "Nhập ID lớp học"
        );

        configureTextField(
                attendanceScoreField,
                "0 đến 10"
        );

        configureTextField(
                midtermScoreField,
                "0 đến 10"
        );

        configureTextField(
                finalScoreField,
                "0 đến 10"
        );

        card.add(
                createFormField(
                        "Mã sinh viên",
                        studentIdField
                ),
                "growx"
        );

        if (isAdminMode()) {
            card.add(
                    createFormField(
                            "Mã lớp học",
                            classIdField
                    ),
                    "growx"
            );
        } else {
            card.add(
                    createComboFormField(
                            "Lớp được phân công",
                            teacherClassComboBox
                    ),
                    "growx"
            );
        }

        card.add(
                createFormField(
                        "Điểm chuyên cần (10%)",
                        attendanceScoreField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Điểm giữa kỳ (30%)",
                        midtermScoreField
                ),
                "growx"
        );

        card.add(
                createFormField(
                        "Điểm cuối kỳ (60%)",
                        finalScoreField
                ),
                "growx"
        );

        JLabel formulaLabel =
                new JLabel(
                        "<html>"
                                + "Trung bình = Chuyên cần × 10% "
                                + "+ Giữa kỳ × 30% "
                                + "+ Cuối kỳ × 60%"
                                + "</html>"
                );

        formulaLabel.setFont(
                UIConstants.FONT_SMALL
        );

        formulaLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                formulaLabel,
                "growx"
        );

        JPanel buttonPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill][grow, fill]",
                                "[]"
                        )
                );

        buttonPanel.setOpaque(false);

        buttonPanel.add(refreshButton);
        buttonPanel.add(saveButton);

        card.add(
                buttonPanel,
                "growx"
        );

        return card;
    }

    private void configurePermissionLabel() {
        permissionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        permissionLabel.setOpaque(true);

        permissionLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        10,
                        8,
                        10
                )
        );

        if (isTeacherMode()) {
            permissionLabel.setBackground(
                    new Color(
                            255,
                            247,
                            237
                    )
            );

            permissionLabel.setForeground(
                    new Color(
                            194,
                            65,
                            12
                    )
            );

        } else {
            permissionLabel.setBackground(
                    new Color(
                            239,
                            246,
                            255
                    )
            );

            permissionLabel.setForeground(
                    UIConstants.PRIMARY
            );
        }
    }

    private JPanel createFormField(
            String labelText,
            JTextField textField
    ) {
        JPanel panel =
                createFieldContainer();

        JLabel label =
                createFieldLabel(
                        labelText
                );

        panel.add(label);

        panel.add(
                textField,
                "growx, height 40!"
        );

        return panel;
    }

    private JPanel createComboFormField(
            String labelText,
            JComboBox<ClassRoom> comboBox
    ) {
        JPanel panel =
                createFieldContainer();

        JLabel label =
                createFieldLabel(
                        labelText
                );

        panel.add(label);

        panel.add(
                comboBox,
                "growx, height 40!"
        );

        return panel;
    }

    private JPanel createFieldContainer() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 0",
                                "[grow, fill]",
                                "[]5[]"
                        )
                );

        panel.setOpaque(false);

        return panel;
    }

    private JLabel createFieldLabel(
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
       CARD DANH SÁCH ĐIỂM
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[]12[grow, fill]12[]"
                )
        );

        card.add(
                createTitlePanel(),
                "growx"
        );

        card.add(
                createFilterPanel(),
                "growx"
        );

        card.add(
                createTableScrollPane(),
                "grow, push"
        );

        card.add(
                createSummaryPanel(),
                "growx"
        );

        return card;
    }

    private JPanel createTitlePanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill][]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        isTeacherMode()
                                ? "Điểm các lớp đang phụ trách"
                                : "Danh sách điểm theo lớp"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        isTeacherMode()
                                ? "Chỉ hiển thị dữ liệu thuộc "
                                + "phạm vi giảng dạy của bạn"
                                : "Tra cứu và cập nhật "
                                + "kết quả học tập"
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalGradeLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        totalGradeLabel.setForeground(
                UIConstants.PRIMARY
        );

        totalGradeLabel.setHorizontalAlignment(
                SwingConstants.RIGHT
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
                totalGradeLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]10[]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        if (isAdminMode()) {
            configureTextField(
                    filterClassIdField,
                    "Nhập mã lớp cần xem điểm"
            );

            filterClassIdField.putClientProperty(
                    "JTextField.leadingIcon",
                    FontIcon.of(
                            FontAwesomeSolid.SEARCH,
                            14,
                            UIConstants.TEXT_SECONDARY
                    )
            );

            panel.add(
                    filterClassIdField,
                    "growx, height 40!"
            );

        } else {
            panel.add(
                    filterTeacherClassComboBox,
                    "growx, height 40!"
            );
        }

        panel.add(
                filterButton,
                "height 40!"
        );

        return panel;
    }

    private JScrollPane createTableScrollPane() {
        gradeTable.setRowHeight(42);

        gradeTable.setFillsViewportHeight(true);

        gradeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        gradeTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        gradeTable.setShowHorizontalLines(true);
        gradeTable.setShowVerticalLines(false);

        gradeTable.setGridColor(
                UIConstants.BORDER
        );

        gradeTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        gradeTable.setSelectionBackground(
                new Color(
                        239,
                        246,
                        255
                )
        );

        gradeTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        gradeTable
                .getTableHeader()
                .setReorderingAllowed(false);

        gradeTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(
                                0,
                                40
                        )
                );

        gradeTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(
                                        Font.BOLD
                                )
                );

        gradeTable
                .getTableHeader()
                .setForeground(
                        UIConstants.TEXT_PRIMARY
                );

        configureColumnWidths();
        configureTableRenderers();

        JScrollPane scrollPane =
                new JScrollPane(gradeTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane
                        .HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane
                        .VERTICAL_SCROLLBAR_AS_NEEDED
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

        return scrollPane;
    }

    private void configureColumnWidths() {
        setColumnWidth(0, 45);
        setColumnWidth(1, 100);
        setColumnWidth(2, 80);
        setColumnWidth(3, 95);
        setColumnWidth(4, 85);
        setColumnWidth(5, 85);
        setColumnWidth(6, 95);
        setColumnWidth(7, 105);
    }

    private void setColumnWidth(
            int columnIndex,
            int width
    ) {
        gradeTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(width);
    }

    private void configureTableRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        for (
                int column = 0;
                column < tableModel.getColumnCount();
                column++
        ) {
            gradeTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        gradeTable
                .getColumnModel()
                .getColumn(7)
                .setCellRenderer(
                        new ResultCellRenderer()
                );
    }

    private JPanel createSummaryPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

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
       COMBO BOX LỚP HỌC
       ===================================================== */

    private void configureClassComboBox(
            JComboBox<ClassRoom> comboBox
    ) {
        comboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        comboBox.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component
                    getListCellRendererComponent(
                            JList<?> list,
                            Object value,
                            int index,
                            boolean isSelected,
                            boolean cellHasFocus
                    ) {
                        super.getListCellRendererComponent(
                                list,
                                value,
                                index,
                                isSelected,
                                cellHasFocus
                        );

                        if (value instanceof ClassRoom classRoom) {
                            setText(
                                    formatClassRoom(
                                            classRoom
                                    )
                            );
                        } else {
                            setText(
                                    "Chọn lớp học"
                            );
                        }

                        return this;
                    }
                }
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

    private String formatClassRoom(
            ClassRoom classRoom
    ) {
        if (classRoom == null) {
            return "Chọn lớp học";
        }

        String className =
                classRoom.getClassName();

        if (className == null
                || className.isBlank()) {

            className =
                    "Lớp #" + classRoom.getClassId();
        }

        String schedule =
                classRoom.getSchedule();

        if (schedule == null
                || schedule.isBlank()) {

            return classRoom.getClassId()
                    + " - "
                    + className;
        }

        return classRoom.getClassId()
                + " - "
                + className
                + " ("
                + schedule
                + ")";
    }

    private void loadTeacherClasses() {
        try {
            setLoading(true);

            List<ClassRoom> classes =
                    gradeService.getTeacherClasses(
                            teacherId
                    );

            teacherClasses =
                    classes == null
                            ? Collections.emptyList()
                            : classes;

            teacherClassComboBox.removeAllItems();
            filterTeacherClassComboBox.removeAllItems();

            for (ClassRoom classRoom
                    : teacherClasses) {

                teacherClassComboBox.addItem(
                        classRoom
                );

                filterTeacherClassComboBox.addItem(
                        classRoom
                );
            }

            boolean hasClasses =
                    !teacherClasses.isEmpty();

            teacherClassComboBox.setEnabled(
                    hasClasses
            );

            filterTeacherClassComboBox.setEnabled(
                    hasClasses
            );

            saveButton.setEnabled(
                    hasClasses
            );

            filterButton.setEnabled(
                    hasClasses
            );

            if (!hasClasses) {
                resultLabel.setText(
                        "Giảng viên chưa được phân công lớp học"
                );

                totalGradeLabel.setText(
                        "0 kết quả"
                );

                return;
            }

            teacherClassComboBox.setSelectedIndex(0);
            filterTeacherClassComboBox.setSelectedIndex(0);

            ClassRoom firstClass =
                    teacherClasses.get(0);

            loadGradesByClass(
                    firstClass.getClassId()
            );

        } catch (RuntimeException exception) {
            teacherClasses =
                    Collections.emptyList();

            teacherClassComboBox.removeAllItems();
            filterTeacherClassComboBox.removeAllItems();

            showError(
                    "Không thể tải danh sách lớp "
                            + "của giảng viên.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        saveButton.addActionListener(
                event -> handleSaveGrade()
        );

        refreshButton.addActionListener(
                event -> clearForm()
        );

        filterButton.addActionListener(
                event -> handleFilter()
        );

        filterClassIdField.addActionListener(
                event -> {
                    if (isAdminMode()) {
                        handleFilter();
                    }
                }
        );

        teacherClassComboBox
                .addActionListener(
                        event -> {
                            if (isTeacherMode()
                                    && !loading) {

                                synchronizeTeacherClassSelection(
                                        teacherClassComboBox,
                                        filterTeacherClassComboBox
                                );
                            }
                        }
                );

        filterTeacherClassComboBox
                .addActionListener(
                        event -> {
                            if (isTeacherMode()
                                    && !loading) {

                                synchronizeTeacherClassSelection(
                                        filterTeacherClassComboBox,
                                        teacherClassComboBox
                                );
                            }
                        }
                );

        gradeTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event
                                    .getValueIsAdjusting()) {

                                loadSelectedGradeToForm();
                            }
                        }
                );

        gradeTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (event.getClickCount() == 2
                                && gradeTable
                                .getSelectedRow() >= 0) {

                            loadSelectedGradeToForm();
                        }
                    }
                }
        );
    }

    private void synchronizeTeacherClassSelection(
            JComboBox<ClassRoom> source,
            JComboBox<ClassRoom> target
    ) {
        ClassRoom selectedClass =
                getSelectedClass(source);

        if (selectedClass == null) {
            return;
        }

        selectClassById(
                target,
                selectedClass.getClassId()
        );
    }

    private void selectClassById(
            JComboBox<ClassRoom> comboBox,
            int classId
    ) {
        for (
                int index = 0;
                index < comboBox.getItemCount();
                index++
        ) {
            ClassRoom classRoom =
                    comboBox.getItemAt(index);

            if (classRoom != null
                    && classRoom.getClassId()
                    == classId) {

                comboBox.setSelectedIndex(
                        index
                );

                return;
            }
        }
    }

    /* =====================================================
       LƯU ĐIỂM
       ===================================================== */

    private void handleSaveGrade() {
        if (loading) {
            return;
        }

        try {
            setLoading(true);

            int studentId =
                    parsePositiveInt(
                            studentIdField.getText(),
                            "Mã sinh viên"
                    );

            int classId =
                    getInputClassId();

            double attendanceScore =
                    parseScore(
                            attendanceScoreField.getText(),
                            "Điểm chuyên cần"
                    );

            double midtermScore =
                    parseScore(
                            midtermScoreField.getText(),
                            "Điểm giữa kỳ"
                    );

            double finalScore =
                    parseScore(
                            finalScoreField.getText(),
                            "Điểm cuối kỳ"
                    );

            boolean successful;

            if (isTeacherMode()) {
                successful =
                        gradeService
                                .saveOrUpdateGradeByTeacher(
                                        teacherId,
                                        studentId,
                                        classId,
                                        attendanceScore,
                                        midtermScore,
                                        finalScore
                                );

            } else {
                successful =
                        gradeService
                                .saveOrUpdateGrade(
                                        studentId,
                                        classId,
                                        attendanceScore,
                                        midtermScore,
                                        finalScore
                                );
            }

            if (!successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể lưu điểm học viên.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    isTeacherMode()
                            ? "Lưu điểm lớp phụ trách thành công."
                            : "Lưu điểm thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            synchronizeFilterAfterSave(
                    classId
            );

            loadGradesByClass(classId);
            clearScoreFields();

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            showError(
                    "Không thể lưu điểm.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private int getInputClassId() {
        if (isAdminMode()) {
            return parsePositiveInt(
                    classIdField.getText(),
                    "Mã lớp học"
            );
        }

        ClassRoom selectedClass =
                getSelectedClass(
                        teacherClassComboBox
                );

        if (selectedClass == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn lớp học."
            );
        }

        return selectedClass.getClassId();
    }

    private void synchronizeFilterAfterSave(
            int classId
    ) {
        if (isAdminMode()) {
            filterClassIdField.setText(
                    String.valueOf(classId)
            );
        } else {
            selectClassById(
                    filterTeacherClassComboBox,
                    classId
            );
        }
    }

    /* =====================================================
       LỌC VÀ TẢI DỮ LIỆU
       ===================================================== */

    private void handleFilter() {
        try {
            int classId =
                    getFilterClassId();

            loadGradesByClass(classId);

        } catch (IllegalArgumentException
                 | IllegalStateException exception) {

            JOptionPane.showMessageDialog(
                    this,
                    exception.getMessage(),
                    "Dữ liệu chưa hợp lệ",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private int getFilterClassId() {
        if (isAdminMode()) {
            return parsePositiveInt(
                    filterClassIdField.getText(),
                    "Mã lớp"
            );
        }

        ClassRoom selectedClass =
                getSelectedClass(
                        filterTeacherClassComboBox
                );

        if (selectedClass == null) {
            throw new IllegalArgumentException(
                    "Vui lòng chọn lớp cần xem điểm."
            );
        }

        return selectedClass.getClassId();
    }

    private ClassRoom getSelectedClass(
            JComboBox<ClassRoom> comboBox
    ) {
        Object selectedItem =
                comboBox.getSelectedItem();

        if (selectedItem
                instanceof ClassRoom classRoom) {

            return classRoom;
        }

        return null;
    }

    private void loadGradesByClass(
            int classId
    ) {
        if (loading) {
            return;
        }

        try {
            loading = true;

            List<Grade> grades;

            if (isTeacherMode()) {
                grades =
                        gradeService
                                .getGradesByClassForTeacher(
                                        teacherId,
                                        classId
                                );

            } else {
                grades =
                        gradeService
                                .getGradesByClass(
                                        classId
                                );
            }

            currentGrades =
                    grades == null
                            ? Collections.emptyList()
                            : grades;

            displayGrades();

        } catch (RuntimeException exception) {
            currentGrades =
                    Collections.emptyList();

            displayGrades();

            showError(
                    "Không thể tải danh sách điểm.",
                    exception
            );

        } finally {
            loading = false;
        }
    }

    private void displayGrades() {
        tableModel.setRowCount(0);

        int index = 1;

        for (Grade grade : currentGrades) {
            if (grade == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            index++,
                            grade.getStudentId(),
                            grade.getClassId(),
                            formatScore(
                                    grade.getAttendanceScore()
                            ),
                            formatScore(
                                    grade.getMidtermScore()
                            ),
                            formatScore(
                                    grade.getFinalScore()
                            ),
                            formatScore(
                                    grade.getAverageScore()
                            ),
                            formatResult(
                                    grade.getResult()
                            )
                    }
            );
        }

        int total =
                currentGrades.size();

        totalGradeLabel.setText(
                total + " kết quả"
        );

        if (total == 0) {
            resultLabel.setText(
                    "Lớp chưa có dữ liệu điểm"
            );
        } else {
            resultLabel.setText(
                    "Đang hiển thị "
                            + total
                            + " kết quả học tập"
            );
        }

        gradeTable.clearSelection();
        gradeTable.revalidate();
        gradeTable.repaint();
    }

    /* =====================================================
       LOAD DỮ LIỆU ĐƯỢC CHỌN
       ===================================================== */

    private void loadSelectedGradeToForm() {
        int selectedRow =
                gradeTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        int modelRow =
                gradeTable.convertRowIndexToModel(
                        selectedRow
                );

        studentIdField.setText(
                safeCellValue(
                        tableModel.getValueAt(
                                modelRow,
                                1
                        )
                )
        );

        int classId =
                parseTableInteger(
                        tableModel.getValueAt(
                                modelRow,
                                2
                        )
                );

        if (isAdminMode()) {
            classIdField.setText(
                    String.valueOf(classId)
            );

        } else {
            selectClassById(
                    teacherClassComboBox,
                    classId
            );

            selectClassById(
                    filterTeacherClassComboBox,
                    classId
            );
        }

        attendanceScoreField.setText(
                safeCellValue(
                        tableModel.getValueAt(
                                modelRow,
                                3
                        )
                )
        );

        midtermScoreField.setText(
                safeCellValue(
                        tableModel.getValueAt(
                                modelRow,
                                4
                        )
                )
        );

        finalScoreField.setText(
                safeCellValue(
                        tableModel.getValueAt(
                                modelRow,
                                5
                        )
                )
        );
    }

    private int parseTableInteger(
            Object value
    ) {
        if (value == null) {
            return 0;
        }

        try {
            return Integer.parseInt(
                    String.valueOf(value)
            );

        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    /* =====================================================
       BUTTON VÀ TEXT FIELD
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
                        13,
                        Color.WHITE
                )
        );

        button.setBackground(
                UIConstants.PRIMARY
        );

        button.setForeground(
                Color.WHITE
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
                borderWidth: 0;
                focusWidth: 0;
                margin: 7,12,7,12;
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
                        13,
                        UIConstants.PRIMARY
                )
        );

        button.setBackground(
                Color.WHITE
        );

        button.setForeground(
                UIConstants.PRIMARY
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
                borderColor: #2563EB;
                focusWidth: 0;
                margin: 7,12,7,12;
                """
        );

        return button;
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
                """
        );
    }

    /* =====================================================
       HÀM PHỤ
       ===================================================== */

    private void clearForm() {
        studentIdField.setText("");

        if (isAdminMode()) {
            classIdField.setText("");
        }

        clearScoreFields();

        gradeTable.clearSelection();

        studentIdField.requestFocusInWindow();
    }

    private void clearScoreFields() {
        attendanceScoreField.setText("");
        midtermScoreField.setText("");
        finalScoreField.setText("");
    }

    private int parsePositiveInt(
            String value,
            String fieldName
    ) {
        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
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

    private double parseScore(
            String value,
            String fieldName
    ) {
        if (value == null
                || value.isBlank()) {

            throw new IllegalArgumentException(
                    fieldName
                            + " không được để trống."
            );
        }

        try {
            double score =
                    Double.parseDouble(
                            value.trim()
                                    .replace(",", ".")
                    );

            if (Double.isNaN(score)
                    || Double.isInfinite(score)
                    || score < 0
                    || score > 10) {

                throw new IllegalArgumentException(
                        fieldName
                                + " phải nằm trong khoảng "
                                + "từ 0 đến 10."
                );
            }

            return score;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải là số hợp lệ."
            );
        }
    }

    private String formatScore(
            Double score
    ) {
        if (score == null) {
            return "";
        }

        return String.format(
                Locale.US,
                "%.2f",
                score
        );
    }

    private String formatResult(
            String result
    ) {
        if (result == null
                || result.isBlank()) {

            return "";
        }

        return switch (
                result.trim().toUpperCase()
                ) {
            case "PASSED" -> "Đạt";
            case "FAILED" -> "Không đạt";
            default -> result;
        };
    }

    private String safeCellValue(
            Object value
    ) {
        if (value == null) {
            return "";
        }

        return String.valueOf(value)
                .replace(",", ".");
    }

    private void setLoading(
            boolean loading
    ) {
        this.loading = loading;

        studentIdField.setEnabled(!loading);

        if (isAdminMode()) {
            classIdField.setEnabled(!loading);
            filterClassIdField.setEnabled(!loading);
        } else {
            teacherClassComboBox.setEnabled(
                    !loading
                            && !teacherClasses.isEmpty()
            );

            filterTeacherClassComboBox.setEnabled(
                    !loading
                            && !teacherClasses.isEmpty()
            );
        }

        attendanceScoreField.setEnabled(!loading);
        midtermScoreField.setEnabled(!loading);
        finalScoreField.setEnabled(!loading);

        refreshButton.setEnabled(!loading);

        saveButton.setEnabled(
                !loading
                        && (
                        isAdminMode()
                                || !teacherClasses.isEmpty()
                )
        );

        filterButton.setEnabled(
                !loading
                        && (
                        isAdminMode()
                                || !teacherClasses.isEmpty()
                )
        );

        saveButton.setText(
                loading
                        ? "Đang xử lý..."
                        : "Lưu điểm"
        );

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    private void showError(
            String message,
            Exception exception
    ) {
        String detail =
                exception == null
                        || exception.getMessage() == null
                        || exception.getMessage().isBlank()
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
       RENDERER KẾT QUẢ
       ===================================================== */

    private static final class ResultCellRenderer
            extends DefaultTableCellRenderer {

        public ResultCellRenderer() {
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
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                String text =
                        value == null
                                ? ""
                                : value.toString();

                if ("Đạt".equalsIgnoreCase(text)) {
                    setForeground(
                            UIConstants.SUCCESS
                    );

                } else if (
                        "Không đạt"
                                .equalsIgnoreCase(text)
                ) {
                    setForeground(
                            UIConstants.DANGER
                    );

                } else {
                    setForeground(
                            UIConstants.TEXT_SECONDARY
                    );
                }

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
    }
}
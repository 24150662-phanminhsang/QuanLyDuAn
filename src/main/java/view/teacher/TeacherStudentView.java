package view.teacher;

import dao.EnrollmentDAO;
import dao.StudentDAO;
import model.ClassRoom;
import model.Enrollment;
import model.Student;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TeacherStudentView extends JPanel {

    private final int teacherId;

    private final EnrollmentDAO enrollmentDAO;
    private final StudentDAO studentDAO;

    private final JComboBox<ClassRoom> classComboBox;
    private final JTextField searchField;

    private final JButton loadButton;
    private final JButton refreshButton;
    private final JButton manageGradeButton;

    private final JLabel totalStudentLabel;
    private final JLabel selectedStudentLabel;

    private final DefaultTableModel tableModel;
    private final JTable studentTable;

    private List<ClassRoom> teacherClasses =
            Collections.emptyList();

    private List<StudentRow> currentStudents =
            Collections.emptyList();

    private StudentRow selectedStudent;

    private StudentActionHandler studentActionHandler;

    public TeacherStudentView(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;

        this.enrollmentDAO =
                new EnrollmentDAO();

        /*
         * StudentDAO trong project hiện tại là class trực tiếp,
         * không phải interface có StudentDAOImpl.
         */
        this.studentDAO =
                new StudentDAO();

        this.classComboBox =
                new JComboBox<>();

        this.searchField =
                new JTextField();

        this.loadButton =
                createPrimaryButton(
                        "Tải danh sách",
                        FontAwesomeSolid.SEARCH
                );

        this.refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        this.manageGradeButton =
                createPrimaryButton(
                        "Quản lý điểm",
                        FontAwesomeSolid.EDIT
                );

        this.totalStudentLabel =
                new JLabel("0 học viên");

        this.selectedStudentLabel =
                new JLabel("Chưa chọn học viên");

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "ID học viên",
                                "Mã học viên",
                                "Họ và tên",
                                "Email",
                                "Số điện thoại",
                                "Trạng thái",
                                "Tiến độ"
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

        this.studentTable =
                new JTable(tableModel);

        initializeView();
        configureClassComboBox();
        registerEvents();
        clearSelection();
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
                                "fill, wrap 1, insets 18",
                                "[grow, fill]",
                                "[]14[]14[grow, fill]12[]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        wrapper.add(
                createFilterCard(),
                "growx"
        );

        wrapper.add(
                createTableCard(),
                "grow, push"
        );

        wrapper.add(
                createActionCard(),
                "growx"
        );

        add(
                wrapper,
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
                                "fillx, insets 0",
                                "[grow][]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel titleLabel =
                new JLabel(
                        "Học viên trong lớp"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Chọn lớp để xem danh sách học viên đã đăng ký."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalStudentLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        totalStudentLabel.setForeground(
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
                totalStudentLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    /* =====================================================
       BỘ LỌC
       ===================================================== */

    private ContentCard createFilterCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 16",
                        "[240!, fill]10[grow, fill]10[]10[]",
                        "[]"
                )
        );

        configureSearchField();

        card.add(
                classComboBox,
                "height 40!"
        );

        card.add(
                searchField,
                "height 40!"
        );

        card.add(
                loadButton,
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
                "Tìm theo mã, tên, email hoặc số điện thoại"
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

    private void configureClassComboBox() {
        classComboBox.setFont(
                UIConstants.FONT_NORMAL
        );

        classComboBox.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderColor: #CBD5E1;
                focusedBorderColor: #2563EB;
                """
        );

        classComboBox.setRenderer(
                new DefaultListCellRenderer() {
                    @Override
                    public Component getListCellRendererComponent(
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
                                    formatClassRoom(classRoom)
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
    }

    /* =====================================================
       BẢNG HỌC VIÊN
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, insets 16",
                        "[grow, fill]",
                        "[grow, fill]"
                )
        );

        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(studentTable);

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
                "grow, push"
        );

        return card;
    }

    private void configureTable() {
        studentTable.setRowHeight(42);

        studentTable.setFillsViewportHeight(true);

        studentTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        studentTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        studentTable.setShowVerticalLines(false);
        studentTable.setShowHorizontalLines(true);

        studentTable.setGridColor(
                UIConstants.BORDER
        );

        studentTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        studentTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        studentTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        studentTable
                .getTableHeader()
                .setReorderingAllowed(false);

        studentTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 40)
                );

        studentTable
                .getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM
                                .deriveFont(Font.BOLD)
                );

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
            studentTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        studentTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(45);

        studentTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(80);

        studentTable
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(110);

        studentTable
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(180);

        studentTable
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(190);

        studentTable
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(120);

        studentTable
                .getColumnModel()
                .getColumn(6)
                .setPreferredWidth(110);

        studentTable
                .getColumnModel()
                .getColumn(7)
                .setPreferredWidth(80);
    }

    /* =====================================================
       CARD THAO TÁC
       ===================================================== */

    private ContentCard createActionCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 18",
                        "[grow][]",
                        "[][]"
                )
        );

        JLabel titleLabel =
                new JLabel(
                        "Học viên đang chọn"
                );

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        selectedStudentLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        selectedStudentLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                selectedStudentLabel,
                "cell 0 1"
        );

        card.add(
                manageGradeButton,
                "cell 1 0 1 2, height 38!"
        );

        return card;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        loadButton.addActionListener(
                event -> loadSelectedClassStudents()
        );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    loadSelectedClassStudents();
                }
        );

        searchField.addActionListener(
                event -> displayStudents()
        );

        classComboBox.addActionListener(
                event -> {
                    if (classComboBox.getSelectedItem() != null) {
                        loadSelectedClassStudents();
                    }
                }
        );

        studentTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateSelectedStudent();
                            }
                        }
                );

        studentTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (event.getClickCount() == 2
                                && selectedStudent != null) {

                            handleManageGrade();
                        }
                    }
                }
        );

        manageGradeButton.addActionListener(
                event -> handleManageGrade()
        );
    }

    /* =====================================================
       NHẬN DANH SÁCH LỚP TỪ DASHBOARD
       ===================================================== */

    public void setTeacherClasses(
            List<ClassRoom> classes
    ) {
        teacherClasses =
                classes == null
                        ? Collections.emptyList()
                        : new ArrayList<>(classes);

        classComboBox.removeAllItems();

        for (ClassRoom classRoom : teacherClasses) {
            if (classRoom != null) {
                classComboBox.addItem(classRoom);
            }
        }

        boolean hasClasses =
                !teacherClasses.isEmpty();

        classComboBox.setEnabled(hasClasses);
        loadButton.setEnabled(hasClasses);
        refreshButton.setEnabled(hasClasses);

        if (hasClasses) {
            classComboBox.setSelectedIndex(0);
            loadSelectedClassStudents();
        } else {
            currentStudents =
                    Collections.emptyList();

            displayStudents();
        }
    }

    public void selectClass(
            ClassRoom classRoom
    ) {
        if (classRoom == null) {
            return;
        }

        for (
                int index = 0;
                index < classComboBox.getItemCount();
                index++
        ) {
            ClassRoom item =
                    classComboBox.getItemAt(index);

            if (item != null
                    && item.getClassId()
                    == classRoom.getClassId()) {

                classComboBox.setSelectedIndex(index);
                loadSelectedClassStudents();
                return;
            }
        }
    }

    /* =====================================================
       TẢI HỌC VIÊN THEO LỚP
       ===================================================== */

    public void loadSelectedClassStudents() {
        ClassRoom selectedClass =
                getSelectedClass();

        if (selectedClass == null) {
            currentStudents =
                    Collections.emptyList();

            displayStudents();
            return;
        }

        try {
            List<Enrollment> enrollments =
                    enrollmentDAO.getByClassId(
                            selectedClass.getClassId()
                    );

            List<StudentRow> rows =
                    new ArrayList<>();

            if (enrollments != null) {
                for (Enrollment enrollment : enrollments) {
                    if (enrollment == null) {
                        continue;
                    }

                    Student student =
                            studentDAO.getStudentById(
                                    enrollment.getStudentId()
                            );

                    if (student == null) {
                        continue;
                    }

                    rows.add(
                            new StudentRow(
                                    student,
                                    enrollment
                            )
                    );
                }
            }

            currentStudents = rows;

            displayStudents();

        } catch (RuntimeException exception) {
            currentStudents =
                    Collections.emptyList();

            displayStudents();

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải danh sách học viên.\n"
                            + getErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displayStudents() {
        tableModel.setRowCount(0);

        String keyword =
                normalizeKeyword(
                        searchField.getText()
                );

        int sequence = 1;
        int displayedCount = 0;

        for (StudentRow row : currentStudents) {
            Student student =
                    row.student();

            if (!matchesKeyword(
                    student,
                    keyword
            )) {
                continue;
            }

            Enrollment enrollment =
                    row.enrollment();

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            student.getStudentId(),
                            safeText(
                                    student.getStudentCode()
                            ),
                            safeText(
                                    student.getFullName()
                            ),
                            safeText(
                                    student.getEmail()
                            ),
                            safeText(
                                    student.getPhone()
                            ),
                            formatStatus(
                                    enrollment.getStatus()
                            ),
                            enrollment.getProgressPercent()
                                    + "%"
                    }
            );

            displayedCount++;
        }

        totalStudentLabel.setText(
                displayedCount + " học viên"
        );

        clearSelection();

        studentTable.revalidate();
        studentTable.repaint();
    }

    /* =====================================================
       CHỌN HỌC VIÊN
       ===================================================== */

    private void updateSelectedStudent() {
        int selectedViewRow =
                studentTable.getSelectedRow();

        if (selectedViewRow < 0) {
            clearSelection();
            return;
        }

        int selectedModelRow =
                studentTable.convertRowIndexToModel(
                        selectedViewRow
                );

        int studentId =
                parseTableInteger(
                        tableModel.getValueAt(
                                selectedModelRow,
                                1
                        )
                );

        selectedStudent =
                currentStudents.stream()
                        .filter(
                                row ->
                                        row.student()
                                                .getStudentId()
                                                == studentId
                        )
                        .findFirst()
                        .orElse(null);

        if (selectedStudent == null) {
            clearSelection();
            return;
        }

        Student student =
                selectedStudent.student();

        selectedStudentLabel.setText(
                safeText(student.getStudentCode())
                        + " - "
                        + safeText(student.getFullName())
        );

        manageGradeButton.setEnabled(true);
    }

    private void clearSelection() {
        selectedStudent = null;

        studentTable.clearSelection();

        selectedStudentLabel.setText(
                "Chưa chọn học viên"
        );

        manageGradeButton.setEnabled(false);
    }

    private void handleManageGrade() {
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một học viên.",
                    "Chưa chọn học viên",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        ClassRoom selectedClass =
                getSelectedClass();

        if (selectedClass == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một lớp học.",
                    "Chưa chọn lớp",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (studentActionHandler != null) {
            studentActionHandler.onManageGrade(
                    selectedStudent.student(),
                    selectedClass
            );

            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Học viên: "
                        + selectedStudent
                        .student()
                        .getFullName()
                        + "\nLớp: "
                        + selectedClass.getClassName(),
                "Quản lý điểm",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /* =====================================================
       CALLBACK
       ===================================================== */

    public void setStudentActionHandler(
            StudentActionHandler handler
    ) {
        this.studentActionHandler = handler;
    }

    public interface StudentActionHandler {

        void onManageGrade(
                Student student,
                ClassRoom classRoom
        );
    }

    /* =====================================================
       GETTER HỖ TRỢ
       ===================================================== */

    public int getTeacherId() {
        return teacherId;
    }

    /**
     * Chỉ có duy nhất một phương thức getSelectedClass().
     */
    public ClassRoom getSelectedClass() {
        Object selectedItem =
                classComboBox.getSelectedItem();

        if (selectedItem instanceof ClassRoom classRoom) {
            return classRoom;
        }

        return null;
    }

    /* =====================================================
       BUTTON STYLE
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
                margin: 7,12,7,12;
                """
        );

        return button;
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private boolean matchesKeyword(
            Student student,
            String keyword
    ) {
        if (keyword.isBlank()) {
            return true;
        }

        return containsIgnoreCase(
                student.getStudentCode(),
                keyword
        )
                || containsIgnoreCase(
                student.getFullName(),
                keyword
        )
                || containsIgnoreCase(
                student.getEmail(),
                keyword
        )
                || containsIgnoreCase(
                student.getPhone(),
                keyword
        );
    }

    private boolean containsIgnoreCase(
            String value,
            String keyword
    ) {
        return value != null
                && value
                .toLowerCase(Locale.ROOT)
                .contains(keyword);
    }

    private String normalizeKeyword(
            String value
    ) {
        return value == null
                ? ""
                : value.trim()
                .toLowerCase(Locale.ROOT);
    }

    private String formatStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            return "";
        }

        return switch (
                status.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "ENROLLED" -> "Đang học";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            default -> status;
        };
    }

    private String formatClassRoom(
            ClassRoom classRoom
    ) {
        if (classRoom == null) {
            return "Chọn lớp học";
        }

        return classRoom.getClassId()
                + " - "
                + safeText(
                classRoom.getClassName()
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

    private String getErrorMessage(
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

        if (throwable.getMessage() != null
                && !throwable.getMessage().isBlank()) {

            return throwable.getMessage();
        }

        return "Không xác định";
    }

    private record StudentRow(
            Student student,
            Enrollment enrollment
    ) {
    }
}
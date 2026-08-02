package view.teacher;

import controller.ClassController;
import model.ClassRoom;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collections;
import java.util.List;

/**
 * Hiển thị các lớp được phân công cho giảng viên.
 *
 * Không hiển thị toàn bộ lớp trong hệ thống.
 */
public class TeacherClassView extends JPanel {

    private final int teacherId;
    private final ClassController classController;

    private final DefaultTableModel tableModel;
    private final JTable classTable;

    private final JLabel totalClassLabel;
    private final JLabel selectedClassLabel;

    private final JButton refreshButton;
    private final JButton viewStudentsButton;
    private final JButton enterGradeButton;

    private List<ClassRoom> currentClasses =
            Collections.emptyList();

    private ClassRoom selectedClass;

    /*
     * Callback để TeacherMainDashboard điều hướng.
     */
    private ClassActionHandler classActionHandler;

    public TeacherClassView(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;
        this.classController = new ClassController();

        totalClassLabel =
                new JLabel("0 lớp");

        selectedClassLabel =
                new JLabel("Chưa chọn lớp");

        refreshButton =
                createSecondaryButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT
                );

        viewStudentsButton =
                createPrimaryButton(
                        "Xem học viên",
                        FontAwesomeSolid.USER_GRADUATE
                );

        enterGradeButton =
                createPrimaryButton(
                        "Quản lý điểm",
                        FontAwesomeSolid.EDIT
                );

        tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "ID lớp",
                                "Mã khóa học",
                                "Tên lớp",
                                "Lịch học",
                                "Phòng",
                                "Sĩ số tối đa"
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

        classTable = new JTable(tableModel);

        initializeView();
        registerEvents();
        clearSelection();
        loadData();
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
                                "[]16[grow, fill]12[]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
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

        add(wrapper, BorderLayout.CENTER);
    }

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
                new JLabel("Lớp học của tôi");

        titleLabel.setFont(UIConstants.FONT_TITLE);
        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Danh sách lớp được phân công phụ trách."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalClassLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        totalClassLabel.setForeground(
                UIConstants.PRIMARY
        );

        refreshButton.setPreferredSize(
                new Dimension(105, 38)
        );

        JPanel rightPanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[]10[]",
                                "[]"
                        )
                );

        rightPanel.setOpaque(false);
        rightPanel.add(totalClassLabel);
        rightPanel.add(refreshButton);

        panel.add(
                titleLabel,
                "cell 0 0"
        );

        panel.add(
                descriptionLabel,
                "cell 0 1"
        );

        panel.add(
                rightPanel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    /* =====================================================
       BẢNG LỚP HỌC
       ===================================================== */

    private ContentCard createTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, insets 18",
                        "[grow, fill]",
                        "[grow, fill]"
                )
        );

        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(classTable);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        scrollPane.getViewport().setBackground(
                Color.WHITE
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.getHorizontalScrollBar()
                .setUnitIncrement(16);

        card.add(
                scrollPane,
                "grow, push"
        );

        return card;
    }

    private void configureTable() {
        classTable.setRowHeight(42);

        classTable.setFillsViewportHeight(true);

        classTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        classTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        classTable.setShowVerticalLines(false);
        classTable.setShowHorizontalLines(true);

        classTable.setGridColor(
                UIConstants.BORDER
        );

        classTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        classTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        classTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        classTable.getTableHeader()
                .setReorderingAllowed(false);

        classTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 40)
                );

        classTable.getTableHeader()
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
            classTable.getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(centerRenderer);
        }

        classTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(65);

        classTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        classTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(180);

        classTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(180);

        classTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(90);

        classTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(100);
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
                        "[grow][]10[]",
                        "[][]"
                )
        );

        JLabel titleLabel =
                new JLabel("Lớp đang chọn");

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        selectedClassLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        selectedClassLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                selectedClassLabel,
                "cell 0 1"
        );

        card.add(
                viewStudentsButton,
                "cell 1 0 1 2, height 38!"
        );

        card.add(
                enterGradeButton,
                "cell 2 0 1 2, height 38!"
        );

        return card;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        refreshButton.addActionListener(
                event -> loadData()
        );

        classTable.getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateSelectedClass();
                            }
                        }
                );

        classTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (event.getClickCount() == 2
                                && selectedClass != null) {

                            handleViewStudents();
                        }
                    }
                }
        );

        viewStudentsButton.addActionListener(
                event -> handleViewStudents()
        );

        enterGradeButton.addActionListener(
                event -> handleEnterGrade()
        );
    }

    private void updateSelectedClass() {
        int selectedRow =
                classTable.getSelectedRow();

        if (selectedRow < 0) {
            clearSelection();
            return;
        }

        int modelRow =
                classTable.convertRowIndexToModel(
                        selectedRow
                );

        if (modelRow < 0
                || modelRow >= currentClasses.size()) {

            clearSelection();
            return;
        }

        selectedClass =
                currentClasses.get(modelRow);

        selectedClassLabel.setText(
                formatSelectedClass(selectedClass)
        );

        viewStudentsButton.setEnabled(true);
        enterGradeButton.setEnabled(true);
    }

    private void handleViewStudents() {
        if (selectedClass == null) {
            showSelectClassWarning();
            return;
        }

        if (classActionHandler != null) {
            classActionHandler
                    .onViewStudents(selectedClass);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Lớp đã chọn: "
                        + selectedClass.getClassName()
                        + "\nID lớp: "
                        + selectedClass.getClassId(),
                "Danh sách học viên",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleEnterGrade() {
        if (selectedClass == null) {
            showSelectClassWarning();
            return;
        }

        if (classActionHandler != null) {
            classActionHandler
                    .onManageGrades(selectedClass);
            return;
        }

        JOptionPane.showMessageDialog(
                this,
                "Vui lòng mở mục Quản lý điểm "
                        + "để nhập điểm cho lớp "
                        + selectedClass.getClassName()
                        + ".",
                "Quản lý điểm",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void showSelectClassWarning() {
        JOptionPane.showMessageDialog(
                this,
                "Vui lòng chọn một lớp học.",
                "Chưa chọn lớp",
                JOptionPane.WARNING_MESSAGE
        );
    }

    /* =====================================================
       TẢI DỮ LIỆU
       ===================================================== */

    public void loadData() {
        try {
            List<ClassRoom> classRooms =
                    classController
                            .getClassesByTeacherId(
                                    teacherId
                            );

            currentClasses =
                    classRooms == null
                            ? Collections.emptyList()
                            : classRooms;

            displayClasses();

        } catch (RuntimeException exception) {
            currentClasses =
                    Collections.emptyList();

            displayClasses();

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải danh sách lớp học.\n"
                            + getErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displayClasses() {
        tableModel.setRowCount(0);

        for (ClassRoom classRoom : currentClasses) {
            if (classRoom == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            classRoom.getClassId(),
                            classRoom.getCourseId(),
                            safeText(
                                    classRoom.getClassName()
                            ),
                            safeText(
                                    classRoom.getSchedule()
                            ),
                            safeText(
                                    classRoom.getRoom()
                            ),
                            classRoom.getMaxStudents()
                    }
            );
        }

        totalClassLabel.setText(
                currentClasses.size() + " lớp"
        );

        clearSelection();

        classTable.revalidate();
        classTable.repaint();
    }

    private void clearSelection() {
        selectedClass = null;

        classTable.clearSelection();

        selectedClassLabel.setText(
                "Chưa chọn lớp"
        );

        viewStudentsButton.setEnabled(false);
        enterGradeButton.setEnabled(false);
    }

    /* =====================================================
       CALLBACK CHO DASHBOARD
       ===================================================== */

    public void setClassActionHandler(
            ClassActionHandler classActionHandler
    ) {
        this.classActionHandler =
                classActionHandler;
    }

    public ClassRoom getSelectedClass() {
        return selectedClass;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public interface ClassActionHandler {

        void onViewStudents(
                ClassRoom classRoom
        );

        void onManageGrades(
                ClassRoom classRoom
        );
    }

    /* =====================================================
       STYLE BUTTON
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

    private String formatSelectedClass(
            ClassRoom classRoom
    ) {
        if (classRoom == null) {
            return "Chưa chọn lớp";
        }

        return safeText(classRoom.getClassName())
                + " • "
                + safeText(classRoom.getSchedule())
                + " • Phòng "
                + safeText(classRoom.getRoom());
    }

    private String safeText(String value) {
        return value == null
                || value.isBlank()
                ? "--"
                : value.trim();
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

        return current.getMessage() == null
                || current.getMessage().isBlank()
                ? throwable.getMessage()
                : current.getMessage();
    }
}
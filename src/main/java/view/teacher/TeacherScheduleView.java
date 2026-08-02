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
 * Màn hình lịch dạy dành cho giảng viên.
 *
 * Dữ liệu được lấy từ các lớp đang được phân công
 * cho teacherId hiện tại.
 */
public class TeacherScheduleView extends JPanel {

    private final int teacherId;
    private final ClassController classController;

    private final JLabel totalScheduleLabel;
    private final JLabel nearestScheduleLabel;

    private final JButton refreshButton;

    private final DefaultTableModel tableModel;
    private final JTable scheduleTable;

    private List<ClassRoom> currentClasses =
            Collections.emptyList();

    public TeacherScheduleView(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "ID giảng viên không hợp lệ."
            );
        }

        this.teacherId = teacherId;
        this.classController = new ClassController();

        this.totalScheduleLabel =
                new JLabel("0 lịch dạy");

        this.nearestScheduleLabel =
                new JLabel("--");

        this.refreshButton =
                createRefreshButton();

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "ID lớp",
                                "Tên lớp",
                                "Mã khóa học",
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

        this.scheduleTable =
                new JTable(tableModel);

        initializeView();
        registerEvents();
        loadData();
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
                                "[]16[]16[grow, fill]"
                        )
                );

        wrapper.setOpaque(false);

        wrapper.add(
                createHeaderPanel(),
                "growx"
        );

        wrapper.add(
                createSummaryPanel(),
                "growx"
        );

        wrapper.add(
                createScheduleTableCard(),
                "grow, push"
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
                new JLabel("Lịch giảng dạy");

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Theo dõi lịch học và phòng học "
                                + "của các lớp đang phụ trách."
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
                refreshButton,
                "cell 1 0 1 2, align right, height 39!"
        );

        return panel;
    }

    /* =====================================================
       THỐNG KÊ NHANH
       ===================================================== */

    private JPanel createSummaryPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]14[grow, fill]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                createSummaryCard(
                        "Tổng lịch dạy",
                        totalScheduleLabel,
                        "Số lớp đang được phân công"
                ),
                "grow"
        );

        panel.add(
                createSummaryCard(
                        "Lịch gần nhất",
                        nearestScheduleLabel,
                        "Lịch đầu tiên trong danh sách hiện tại"
                ),
                "grow"
        );

        return panel;
    }

    private ContentCard createSummaryCard(
            String title,
            JLabel valueLabel,
            String description
    ) {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 16",
                        "[grow]",
                        "[]8[]5[]"
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        valueLabel.setFont(
                UIConstants.FONT_TITLE
                        .deriveFont(
                                Font.BOLD,
                                22f
                        )
        );

        valueLabel.setForeground(
                UIConstants.PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(titleLabel);
        card.add(valueLabel);
        card.add(descriptionLabel);

        return card;
    }

    /* =====================================================
       BẢNG LỊCH DẠY
       ===================================================== */

    private ContentCard createScheduleTableCard() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[grow, fill]",
                        "[]8[grow, fill]"
                )
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

        JLabel titleLabel =
                new JLabel(
                        "Danh sách lịch dạy"
                );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Lịch được lấy từ thông tin "
                                + "các lớp giảng viên đang phụ trách."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        titlePanel.add(titleLabel);
        titlePanel.add(descriptionLabel);

        card.add(
                titlePanel,
                "growx"
        );

        configureTable();

        JScrollPane scrollPane =
                new JScrollPane(scheduleTable);

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
        scheduleTable.setRowHeight(42);

        scheduleTable.setFillsViewportHeight(true);

        scheduleTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        scheduleTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        scheduleTable.setShowVerticalLines(false);
        scheduleTable.setShowHorizontalLines(true);

        scheduleTable.setGridColor(
                UIConstants.BORDER
        );

        scheduleTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        scheduleTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        scheduleTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        scheduleTable
                .getTableHeader()
                .setReorderingAllowed(false);

        scheduleTable
                .getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 40)
                );

        scheduleTable
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
            scheduleTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        scheduleTable
                .getColumnModel()
                .getColumn(0)
                .setPreferredWidth(45);

        scheduleTable
                .getColumnModel()
                .getColumn(1)
                .setPreferredWidth(70);

        scheduleTable
                .getColumnModel()
                .getColumn(2)
                .setPreferredWidth(180);

        scheduleTable
                .getColumnModel()
                .getColumn(3)
                .setPreferredWidth(100);

        scheduleTable
                .getColumnModel()
                .getColumn(4)
                .setPreferredWidth(200);

        scheduleTable
                .getColumnModel()
                .getColumn(5)
                .setPreferredWidth(90);

        scheduleTable
                .getColumnModel()
                .getColumn(6)
                .setPreferredWidth(100);
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

    private void registerEvents() {
        refreshButton.addActionListener(
                event -> loadData()
        );
    }

    /* =====================================================
       TẢI DỮ LIỆU
       ===================================================== */

    public void loadData() {
        try {
            List<ClassRoom> classes =
                    classController
                            .getClassesByTeacherId(
                                    teacherId
                            );

            currentClasses =
                    classes == null
                            ? Collections.emptyList()
                            : classes;

            displaySchedules();
            updateSummary();

        } catch (RuntimeException exception) {
            currentClasses =
                    Collections.emptyList();

            displaySchedules();
            updateSummary();

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải lịch giảng dạy.\n"
                            + getErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void displaySchedules() {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (ClassRoom classRoom : currentClasses) {
            if (classRoom == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            classRoom.getClassId(),
                            safeText(
                                    classRoom.getClassName()
                            ),
                            classRoom.getCourseId(),
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

        scheduleTable.clearSelection();
        scheduleTable.revalidate();
        scheduleTable.repaint();
    }

    private void updateSummary() {
        totalScheduleLabel.setText(
                currentClasses.size()
                        + " lịch dạy"
        );

        String nearestSchedule =
                currentClasses.stream()
                        .filter(
                                classRoom ->
                                        classRoom != null
                        )
                        .map(
                                ClassRoom::getSchedule
                        )
                        .filter(
                                schedule ->
                                        schedule != null
                                                && !schedule.isBlank()
                        )
                        .findFirst()
                        .orElse("--");

        nearestScheduleLabel.setText(
                nearestSchedule
        );
    }

    /* =====================================================
       BUTTON STYLE
       ===================================================== */

    private JButton createRefreshButton() {
        JButton button =
                new JButton("Làm mới");

        button.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.SYNC_ALT,
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

    public int getTeacherId() {
        return teacherId;
    }

    private String safeText(
            String value
    ) {
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

        if (throwable.getMessage() != null
                && !throwable.getMessage().isBlank()) {

            return throwable.getMessage();
        }

        return "Không xác định";
    }
}
package view.student;

import controller.StudentCourseController;
import model.dto.StudentCourseDTO;
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
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudentScheduleView extends JPanel {

    private final int studentId;
    private final StudentCourseController studentCourseController;

    private final JTextField searchField;
    private final JButton searchButton;
    private final JButton refreshButton;

    private final JLabel totalClassLabel;
    private final JLabel currentDateLabel;

    private final DefaultTableModel tableModel;
    private final JTable scheduleTable;

    private List<StudentCourseDTO> currentSchedules =
            Collections.emptyList();

    public StudentScheduleView(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        this.studentId = studentId;

        this.studentCourseController =
                new StudentCourseController();

        this.searchField =
                new JTextField();

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

        this.totalClassLabel =
                new JLabel("0 lớp");

        this.currentDateLabel =
                new JLabel();

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "Mã lớp",
                                "Khóa học",
                                "Lịch học",
                                "Phòng",
                                "Giảng viên",
                                "Học kỳ",
                                "Bắt đầu",
                                "Kết thúc",
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

        this.scheduleTable =
                new JTable(tableModel);

        initializeView();
        configureSearchField();
        configureTable();
        registerEvents();
        loadData();
    }

    private void initializeView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        JPanel wrapper =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 1, insets 18",
                                "[grow, fill]",
                                "[]14[]14[grow, fill]"
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
                "grow, push"
        );

        add(wrapper, BorderLayout.CENTER);
    }

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

        JLabel titleLabel =
                new JLabel("Lịch học");

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Theo dõi thời gian, phòng học "
                                + "và giảng viên của từng lớp."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel totalTitleLabel =
                new JLabel("Tổng số lớp");

        totalTitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        totalTitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalClassLabel.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(Font.BOLD)
        );

        totalClassLabel.setForeground(
                UIConstants.PRIMARY
        );

        JLabel dateTitleLabel =
                new JLabel("Hôm nay");

        dateTitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        dateTitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        currentDateLabel.setText(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy"
                        )
                )
        );

        currentDateLabel.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(Font.BOLD)
        );

        currentDateLabel.setForeground(
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
                totalClassLabel,
                "cell 1 1, align right"
        );

        panel.add(
                dateTitleLabel,
                "cell 2 0, align right"
        );

        panel.add(
                currentDateLabel,
                "cell 2 1, align right"
        );

        return panel;
    }

    private ContentCard createFilterPanel() {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 14 16",
                        "[grow, fill]10[]10[]",
                        "[]"
                )
        );

        card.add(
                searchField,
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
                "Tìm theo mã lớp, khóa học, "
                        + "lịch học, phòng hoặc giảng viên"
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
                JTable.AUTO_RESIZE_OFF
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
                        new Dimension(0, 42)
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

        setColumnWidth(0, 50);
        setColumnWidth(1, 105);
        setColumnWidth(2, 220);
        setColumnWidth(3, 210);
        setColumnWidth(4, 85);
        setColumnWidth(5, 170);
        setColumnWidth(6, 120);
        setColumnWidth(7, 105);
        setColumnWidth(8, 105);
        setColumnWidth(9, 110);
    }

    private void setColumnWidth(
            int columnIndex,
            int width
    ) {
        scheduleTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(width);

        scheduleTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setMinWidth(width);
    }

    private void registerEvents() {
        searchButton.addActionListener(
                event -> searchData()
        );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    loadData();
                }
        );

        searchField.addActionListener(
                event -> searchData()
        );
    }

    public void loadData() {
        setLoading(true);

        try {
            List<StudentCourseDTO> schedules =
                    studentCourseController
                            .getActiveCourses(
                                    studentId
                            );

            currentSchedules =
                    schedules == null
                            ? Collections.emptyList()
                            : schedules;

            displaySchedules(currentSchedules);

        } catch (RuntimeException exception) {
            currentSchedules =
                    Collections.emptyList();

            displaySchedules(currentSchedules);

            showError(
                    "Không thể tải lịch học.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void searchData() {
        String keyword =
                searchField.getText() == null
                        ? ""
                        : searchField.getText().trim();

        setLoading(true);

        try {
            List<StudentCourseDTO> schedules =
                    studentCourseController
                            .searchCourses(
                                    studentId,
                                    keyword
                            );

            currentSchedules =
                    schedules == null
                            ? Collections.emptyList()
                            : schedules;

            displaySchedules(currentSchedules);

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tìm kiếm lịch học.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void displaySchedules(
            List<StudentCourseDTO> schedules
    ) {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (StudentCourseDTO schedule : schedules) {
            if (schedule == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            safeText(
                                    schedule.getClassCode()
                            ),
                            schedule.getDisplayCourseName(),
                            safeText(
                                    schedule.getScheduleText()
                            ),
                            safeText(
                                    schedule.getRoom()
                            ),
                            safeText(
                                    schedule.getTeacherName()
                            ),
                            safeText(
                                    schedule.getDisplaySemester()
                            ),
                            formatDate(
                                    schedule.getStartDate()
                            ),
                            formatDate(
                                    schedule.getEndDate()
                            ),
                            formatStatus(
                                    schedule.getEnrollmentStatus()
                            )
                    }
            );
        }

        totalClassLabel.setText(
                tableModel.getRowCount()
                        + " lớp"
        );

        scheduleTable.revalidate();
        scheduleTable.repaint();
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

    private void setLoading(
            boolean loading
    ) {
        searchButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        searchField.setEnabled(!loading);

        setCursor(
                loading
                        ? Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR
                )
                        : Cursor.getDefaultCursor()
        );
    }

    private String formatDate(
            Date date
    ) {
        if (date == null) {
            return "--";
        }

        return date.toLocalDate().format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                )
        );
    }

    private String formatStatus(
            String status
    ) {
        if (status == null || status.isBlank()) {
            return "--";
        }

        return switch (
                status.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "ENROLLED" -> "Đang học";
            case "COMPLETED" -> "Hoàn thành";
            case "CANCELLED" -> "Đã hủy";
            case "DROPPED" -> "Đã rút";
            default -> status;
        };
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

    public int getStudentId() {
        return studentId;
    }
}
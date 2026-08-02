package view.student;

import controller.StudentGradeController;
import model.dto.StudentGradeDTO;
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
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StudentGradeView extends JPanel {

    private final int studentId;

    private final StudentGradeController
            studentGradeController;

    private final JTextField searchField;

    private final JButton searchButton;
    private final JButton refreshButton;
    private final JButton detailButton;

    private final JLabel totalSubjectLabel;
    private final JLabel averageScoreLabel;
    private final JLabel passedSubjectLabel;
    private final JLabel failedSubjectLabel;

    private final JLabel selectedGradeLabel;

    private final DefaultTableModel tableModel;
    private final JTable gradeTable;

    private List<StudentGradeDTO> currentGrades =
            Collections.emptyList();

    private StudentGradeDTO selectedGrade;

    public StudentGradeView(int studentId) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }

        this.studentId = studentId;

        this.studentGradeController =
                new StudentGradeController();

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

        this.detailButton =
                createPrimaryButton(
                        "Xem chi tiết",
                        FontAwesomeSolid.EYE
                );

        this.totalSubjectLabel =
                createSummaryValueLabel(
                        UIConstants.PRIMARY
                );

        this.averageScoreLabel =
                createSummaryValueLabel(
                        UIConstants.PURPLE
                );

        this.passedSubjectLabel =
                createSummaryValueLabel(
                        UIConstants.SUCCESS
                );

        this.failedSubjectLabel =
                createSummaryValueLabel(
                        UIConstants.DANGER
                );

        this.selectedGradeLabel =
                new JLabel(
                        "Chưa chọn môn học"
                );

        this.tableModel =
                new DefaultTableModel(
                        new Object[]{
                                "STT",
                                "Mã môn",
                                "Tên môn học",
                                "Mã lớp",
                                "Chuyên cần",
                                "Giữa kỳ",
                                "Cuối kỳ",
                                "Trung bình",
                                "Kết quả",
                                "Cập nhật"
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

        this.gradeTable =
                new JTable(tableModel);

        initializeView();
        configureSearchField();
        configureTable();
        registerEvents();
        clearSelection();

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
                                "[]14[]14[]14[grow, fill]12[]"
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
                createFilterPanel(),
                "growx"
        );

        wrapper.add(
                createTableCard(),
                "grow, push"
        );

        wrapper.add(
                createSelectedGradeCard(),
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
                        "Kết quả học tập"
                );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(
                        "Theo dõi điểm thành phần, "
                                + "điểm trung bình và kết quả từng môn."
                );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel studentLabel =
                new JLabel(
                        "Mã sinh viên: "
                                + studentId
                );

        studentLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        studentLabel.setForeground(
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
                studentLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    /* =====================================================
       THỐNG KÊ
       ===================================================== */

    private JPanel createSummaryPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 0",
                                "[grow, fill]12"
                                        + "[grow, fill]12"
                                        + "[grow, fill]12"
                                        + "[grow, fill]",
                                "[]"
                        )
                );

        panel.setOpaque(false);

        panel.add(
                createSummaryCard(
                        "Tổng môn",
                        totalSubjectLabel,
                        FontAwesomeSolid.BOOK_OPEN,
                        UIConstants.PRIMARY
                ),
                "growx"
        );

        panel.add(
                createSummaryCard(
                        "Điểm trung bình",
                        averageScoreLabel,
                        FontAwesomeSolid.STAR,
                        UIConstants.PURPLE
                ),
                "growx"
        );

        panel.add(
                createSummaryCard(
                        "Môn đạt",
                        passedSubjectLabel,
                        FontAwesomeSolid.CHECK_CIRCLE,
                        UIConstants.SUCCESS
                ),
                "growx"
        );

        panel.add(
                createSummaryCard(
                        "Môn chưa đạt",
                        failedSubjectLabel,
                        FontAwesomeSolid.TIMES_CIRCLE,
                        UIConstants.DANGER
                ),
                "growx"
        );

        return panel;
    }

    private ContentCard createSummaryCard(
            String title,
            JLabel valueLabel,
            FontAwesomeSolid icon,
            Color iconColor
    ) {
        ContentCard card =
                new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fillx, insets 16",
                        "[grow][]",
                        "[][]"
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        titleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel iconLabel =
                new JLabel(
                        FontIcon.of(
                                icon,
                                22,
                                iconColor
                        )
                );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                valueLabel,
                "cell 0 1"
        );

        card.add(
                iconLabel,
                "cell 1 0 1 2, align center"
        );

        return card;
    }

    private JLabel createSummaryValueLabel(
            Color color
    ) {
        JLabel label =
                new JLabel("0");

        label.setFont(
                UIConstants.FONT_HEADING
                        .deriveFont(
                                Font.BOLD,
                                22f
                        )
        );

        label.setForeground(color);

        return label;
    }

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

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
                "Tìm theo mã môn, tên môn, "
                        + "mã lớp hoặc kết quả"
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
       BẢNG ĐIỂM
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

        JScrollPane scrollPane =
                new JScrollPane(
                        gradeTable
                );

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
        gradeTable.setRowHeight(42);

        gradeTable.setFillsViewportHeight(true);

        gradeTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        gradeTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        gradeTable.setShowVerticalLines(false);
        gradeTable.setShowHorizontalLines(true);

        gradeTable.setGridColor(
                UIConstants.BORDER
        );

        gradeTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        gradeTable.setSelectionBackground(
                new Color(239, 246, 255)
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
                        new Dimension(0, 42)
                );

        gradeTable
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
            gradeTable
                    .getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(
                            centerRenderer
                    );
        }

        setColumnWidth(0, 50);
        setColumnWidth(1, 100);
        setColumnWidth(2, 220);
        setColumnWidth(3, 100);
        setColumnWidth(4, 95);
        setColumnWidth(5, 95);
        setColumnWidth(6, 95);
        setColumnWidth(7, 105);
        setColumnWidth(8, 110);
        setColumnWidth(9, 145);
    }

    private void setColumnWidth(
            int columnIndex,
            int width
    ) {
        gradeTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(width);

        gradeTable
                .getColumnModel()
                .getColumn(columnIndex)
                .setMinWidth(width);
    }

    /* =====================================================
       DÒNG ĐIỂM ĐANG CHỌN
       ===================================================== */

    private ContentCard createSelectedGradeCard() {
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
                        "Môn học đang chọn"
                );

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
                        .deriveFont(Font.BOLD)
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        selectedGradeLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        selectedGradeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        card.add(
                titleLabel,
                "cell 0 0"
        );

        card.add(
                selectedGradeLabel,
                "cell 0 1"
        );

        card.add(
                detailButton,
                "cell 1 0 1 2, height 38!"
        );

        return card;
    }

    /* =====================================================
       SỰ KIỆN
       ===================================================== */

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

        gradeTable
                .getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateSelectedGrade();
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
                                && selectedGrade != null) {

                            showGradeDetail();
                        }
                    }
                }
        );

        detailButton.addActionListener(
                event -> showGradeDetail()
        );
    }

    /* =====================================================
       TẢI DỮ LIỆU
       ===================================================== */

    public void loadData() {
        setLoading(true);

        try {
            List<StudentGradeDTO> grades =
                    studentGradeController
                            .getGrades(
                                    studentId
                            );

            currentGrades =
                    grades == null
                            ? Collections.emptyList()
                            : grades;

            displayGrades(
                    currentGrades
            );

            updateSummary(
                    currentGrades
            );

        } catch (RuntimeException exception) {
            currentGrades =
                    Collections.emptyList();

            displayGrades(
                    currentGrades
            );

            updateSummary(
                    currentGrades
            );

            showError(
                    "Không thể tải kết quả học tập.",
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
            List<StudentGradeDTO> grades =
                    studentGradeController
                            .searchGrades(
                                    studentId,
                                    keyword
                            );

            currentGrades =
                    grades == null
                            ? Collections.emptyList()
                            : grades;

            displayGrades(
                    currentGrades
            );

            updateSummary(
                    currentGrades
            );

        } catch (RuntimeException exception) {
            showError(
                    "Không thể tìm kiếm kết quả học tập.",
                    exception
            );

        } finally {
            setLoading(false);
        }
    }

    private void displayGrades(
            List<StudentGradeDTO> grades
    ) {
        tableModel.setRowCount(0);

        int sequence = 1;

        for (StudentGradeDTO grade : grades) {
            if (grade == null) {
                continue;
            }

            tableModel.addRow(
                    new Object[]{
                            sequence++,
                            safeText(
                                    grade.getCourseCode()
                            ),
                            safeText(
                                    grade.getCourseName()
                            ),
                            safeText(
                                    grade.getClassCode()
                            ),
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
                            ),
                            formatTimestamp(
                                    grade.getUpdatedAt()
                            )
                    }
            );
        }

        clearSelection();

        gradeTable.revalidate();
        gradeTable.repaint();
    }

    private void updateSummary(
            List<StudentGradeDTO> grades
    ) {
        int total =
                grades == null
                        ? 0
                        : grades.size();

        long passed =
                grades == null
                        ? 0
                        : grades.stream()
                        .filter(
                                grade ->
                                        grade != null
                                                && isPassed(
                                                grade.getResult()
                                        )
                        )
                        .count();

        long failed =
                grades == null
                        ? 0
                        : grades.stream()
                        .filter(
                                grade ->
                                        grade != null
                                                && isFailed(
                                                grade.getResult()
                                        )
                        )
                        .count();

        double average =
                grades == null
                        || grades.isEmpty()
                        ? 0.0
                        : grades.stream()
                        .filter(
                                grade ->
                                        grade != null
                                                && grade.getAverageScore()
                                                != null
                        )
                        .mapToDouble(
                                StudentGradeDTO
                                        ::getAverageScore
                        )
                        .average()
                        .orElse(0.0);

        totalSubjectLabel.setText(
                String.valueOf(total)
        );

        averageScoreLabel.setText(
                String.format(
                        Locale.US,
                        "%.2f",
                        average
                )
        );

        passedSubjectLabel.setText(
                String.valueOf(passed)
        );

        failedSubjectLabel.setText(
                String.valueOf(failed)
        );
    }

    /* =====================================================
       CHỌN MÔN HỌC
       ===================================================== */

    private void updateSelectedGrade() {
        int selectedRow =
                gradeTable.getSelectedRow();

        if (selectedRow < 0) {
            clearSelection();
            return;
        }

        int modelRow =
                gradeTable.convertRowIndexToModel(
                        selectedRow
                );

        if (modelRow < 0
                || modelRow >= currentGrades.size()) {

            clearSelection();
            return;
        }

        selectedGrade =
                currentGrades.get(
                        modelRow
                );

        if (selectedGrade == null) {
            clearSelection();
            return;
        }

        selectedGradeLabel.setText(
                safeText(
                        selectedGrade.getCourseCode()
                )
                        + " - "
                        + safeText(
                        selectedGrade.getCourseName()
                )
                        + " | Lớp "
                        + safeText(
                        selectedGrade.getClassCode()
                )
        );

        detailButton.setEnabled(true);
    }

    private void clearSelection() {
        selectedGrade = null;

        gradeTable.clearSelection();

        selectedGradeLabel.setText(
                "Chưa chọn môn học"
        );

        detailButton.setEnabled(false);
    }

    /* =====================================================
       CHI TIẾT ĐIỂM
       ===================================================== */

    private void showGradeDetail() {
        if (selectedGrade == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn một môn học.",
                    "Chưa chọn môn học",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JPanel detailPanel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 2, insets 8",
                                "[right]12[grow, fill]",
                                "[]8[]8[]8[]8[]8[]8[]8[]8[]"
                        )
                );

        addDetailRow(
                detailPanel,
                "Mã môn:",
                safeText(
                        selectedGrade.getCourseCode()
                )
        );

        addDetailRow(
                detailPanel,
                "Tên môn:",
                safeText(
                        selectedGrade.getCourseName()
                )
        );

        addDetailRow(
                detailPanel,
                "Mã lớp:",
                safeText(
                        selectedGrade.getClassCode()
                )
        );

        addDetailRow(
                detailPanel,
                "Điểm chuyên cần:",
                formatScore(
                        selectedGrade.getAttendanceScore()
                )
        );

        addDetailRow(
                detailPanel,
                "Điểm giữa kỳ:",
                formatScore(
                        selectedGrade.getMidtermScore()
                )
        );

        addDetailRow(
                detailPanel,
                "Điểm cuối kỳ:",
                formatScore(
                        selectedGrade.getFinalScore()
                )
        );

        addDetailRow(
                detailPanel,
                "Điểm trung bình:",
                formatScore(
                        selectedGrade.getAverageScore()
                )
        );

        addDetailRow(
                detailPanel,
                "Kết quả:",
                formatResult(
                        selectedGrade.getResult()
                )
        );

        addDetailRow(
                detailPanel,
                "Cập nhật:",
                formatTimestamp(
                        selectedGrade.getUpdatedAt()
                )
        );

        JOptionPane.showMessageDialog(
                this,
                detailPanel,
                "Chi tiết kết quả học tập",
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
       HÀM HỖ TRỢ
       ===================================================== */

    private void setLoading(
            boolean loading
    ) {
        searchButton.setEnabled(!loading);
        refreshButton.setEnabled(!loading);
        searchField.setEnabled(!loading);

        if (loading) {
            detailButton.setEnabled(false);
        } else {
            detailButton.setEnabled(
                    selectedGrade != null
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

    private String formatScore(
            Double score
    ) {
        if (score == null) {
            return "--";
        }

        return String.format(
                Locale.US,
                "%.1f",
                score
        );
    }

    private String formatResult(
            String result
    ) {
        if (result == null
                || result.isBlank()) {

            return "Chưa có";
        }

        return switch (
                result.trim()
                        .toUpperCase(Locale.ROOT)
                ) {
            case "PASSED",
                 "PASS",
                 "DAT",
                 "ĐẠT" -> "Đạt";

            case "FAILED",
                 "FAIL",
                 "KHONG_DAT",
                 "KHÔNG ĐẠT" -> "Không đạt";

            default -> result.trim();
        };
    }

    private boolean isPassed(
            String result
    ) {
        if (result == null) {
            return false;
        }

        String value =
                result.trim()
                        .toUpperCase(Locale.ROOT);

        return value.equals("PASSED")
                || value.equals("PASS")
                || value.equals("DAT")
                || value.equals("ĐẠT");
    }

    private boolean isFailed(
            String result
    ) {
        if (result == null) {
            return false;
        }

        String value =
                result.trim()
                        .toUpperCase(Locale.ROOT);

        return value.equals("FAILED")
                || value.equals("FAIL")
                || value.equals("KHONG_DAT")
                || value.equals("KHÔNG ĐẠT");
    }

    private String formatTimestamp(
            Timestamp timestamp
    ) {
        if (timestamp == null) {
            return "--";
        }

        return timestamp
                .toLocalDateTime()
                .format(
                        DateTimeFormatter.ofPattern(
                                "dd/MM/yyyy HH:mm"
                        )
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
            current =
                    current.getCause();
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

    public StudentGradeDTO getSelectedGrade() {
        return selectedGrade;
    }
}
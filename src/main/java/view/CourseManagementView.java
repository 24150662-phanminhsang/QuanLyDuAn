package view;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.regex.Pattern;

public class CourseManagementView extends JPanel {

    private static final int COLUMN_ID = 0;
    private static final int COLUMN_CODE = 1;
    private static final int COLUMN_NAME = 2;
    private static final int COLUMN_DESCRIPTION = 3;
    private static final int COLUMN_CREDITS = 4;
    private static final int COLUMN_FEE = 5;
    private static final int COLUMN_STATUS = 6;

    private final JButton btnAdd;
    private final JButton btnUpdate;
    private final JButton btnDelete;
    private final JButton btnRefresh;

    private final JTable tableCourse;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> tableSorter;

    private final JTextField txtSearch;
    private final JLabel lblCourseCount;
    private final JLabel lblResultSummary;
    private final JLabel lblMode;

    private boolean managementMode = true;

    public CourseManagementView() {
        btnAdd = createButton(
                "Thêm khóa học",
                FontAwesomeSolid.PLUS,
                UIConstants.PRIMARY,
                Color.WHITE
        );

        btnUpdate = createButton(
                "Cập nhật",
                FontAwesomeSolid.EDIT,
                new Color(245, 158, 11),
                Color.WHITE
        );

        btnDelete = createButton(
                "Xóa",
                FontAwesomeSolid.TRASH_ALT,
                UIConstants.DANGER,
                Color.WHITE
        );

        btnRefresh = createButton(
                "Làm mới",
                FontAwesomeSolid.SYNC_ALT,
                Color.WHITE,
                UIConstants.PRIMARY
        );

        txtSearch = new JTextField();

        lblCourseCount = new JLabel(
                "0 khóa học"
        );

        lblResultSummary = new JLabel(
                "Chưa có dữ liệu khóa học"
        );

        lblMode = new JLabel(
                "Chế độ quản lý"
        );

        tableModel = createTableModel();
        tableCourse = new JTable(tableModel);

        tableSorter = new TableRowSorter<>(
                tableModel
        );

        initializeView();
        configureTable();
        configureSorter();
        registerEvents();

        setManagementMode(true);
        updateCourseCount();
        updateActionButtonState();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, insets 16, wrap 1",
                        "[grow, fill]",
                        "[]14[grow, fill]"
                )
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        add(
                createTableCard(),
                "grow, push"
        );
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(
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
            public boolean isCellEditable(
                    int row,
                    int column
            ) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(
                    int columnIndex
            ) {
                return switch (columnIndex) {
                    case COLUMN_ID, COLUMN_CREDITS ->
                            Integer.class;

                    case COLUMN_FEE ->
                            Number.class;

                    default ->
                            String.class;
                };
            }
        };
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill]12[300!, fill]8[]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JPanel titlePanel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow]",
                        "[]2[]"
                )
        );

        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Quản lý khóa học"
        );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Quản lý danh sách, tín chỉ, học phí và trạng thái khóa học"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        configureSearchField();
        configureRefreshButton();

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

    private void configureSearchField() {
        txtSearch.setMinimumSize(
                new Dimension(210, 38)
        );

        txtSearch.setPreferredSize(
                new Dimension(300, 38)
        );

        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Tìm mã, tên hoặc trạng thái..."
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

    private void configureRefreshButton() {
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
    }

    private JPanel createTableCard() {
        ContentCard card = new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]12[grow, fill]10[]"
                )
        );

        card.setMinimumSize(
                new Dimension(560, 380)
        );

        card.add(
                createTableToolbar(),
                "growx"
        );

        card.add(
                createTableScrollPane(),
                "grow, push"
        );

        card.add(
                createTableFooter(),
                "growx"
        );

        return card;
    }

    private JPanel createTableToolbar() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill]10[]8[]8[]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JPanel informationPanel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow]",
                        "[]2[]"
                )
        );

        informationPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Danh sách khóa học"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JPanel summaryPanel = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[]8[]",
                        "[center]"
                )
        );

        summaryPanel.setOpaque(false);

        lblCourseCount.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        lblCourseCount.setForeground(
                UIConstants.PRIMARY
        );

        lblMode.setFont(
                UIConstants.FONT_SMALL
        );

        lblMode.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        summaryPanel.add(lblCourseCount);
        summaryPanel.add(lblMode);

        informationPanel.add(titleLabel);
        informationPanel.add(summaryPanel);

        panel.add(
                informationPanel,
                "growx"
        );

        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);

        return panel;
    }

    private JScrollPane createTableScrollPane() {
        JScrollPane scrollPane = new JScrollPane(
                tableCourse
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

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane.getHorizontalScrollBar()
                .setUnitIncrement(16);

        scrollPane.setMinimumSize(
                new Dimension(500, 280)
        );

        return scrollPane;
    }

    private JPanel createTableFooter() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        lblResultSummary.setFont(
                UIConstants.FONT_NORMAL
        );

        lblResultSummary.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(
                lblResultSummary,
                "growx"
        );

        return panel;
    }

    private void configureTable() {
        tableCourse.setRowHeight(42);
        tableCourse.setFillsViewportHeight(true);

        tableCourse.setFont(
                UIConstants.FONT_SMALL
        );

        tableCourse.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        /*
         * Các cột có kích thước cố định và bảng dùng thanh cuộn ngang.
         * Điều này tránh nội dung bị ép quá nhỏ.
         */
        tableCourse.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        tableCourse.setShowVerticalLines(false);
        tableCourse.setShowHorizontalLines(true);

        tableCourse.setGridColor(
                UIConstants.BORDER
        );

        tableCourse.setIntercellSpacing(
                new Dimension(0, 1)
        );

        tableCourse.setSelectionBackground(
                new Color(239, 246, 255)
        );

        tableCourse.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        tableCourse.getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM.deriveFont(
                                Font.BOLD
                        )
                );

        tableCourse.getTableHeader()
                .setBackground(
                        new Color(248, 250, 252)
                );

        tableCourse.getTableHeader()
                .setForeground(
                        UIConstants.TEXT_PRIMARY
                );

        tableCourse.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 42)
                );

        tableCourse.getTableHeader()
                .setReorderingAllowed(false);

        configureColumnWidths();
        configureRenderers();

        tableCourse.setRowSorter(
                tableSorter
        );
    }

    private void configureColumnWidths() {
        setColumnWidth(COLUMN_ID, 55);
        setColumnWidth(COLUMN_CODE, 125);
        setColumnWidth(COLUMN_NAME, 210);
        setColumnWidth(COLUMN_DESCRIPTION, 300);
        setColumnWidth(COLUMN_CREDITS, 80);
        setColumnWidth(COLUMN_FEE, 135);
        setColumnWidth(COLUMN_STATUS, 125);
    }

    private void setColumnWidth(
            int column,
            int width
    ) {
        tableCourse.getColumnModel()
                .getColumn(column)
                .setPreferredWidth(width);
    }

    private void configureRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tableCourse.getColumnModel()
                .getColumn(COLUMN_ID)
                .setCellRenderer(centerRenderer);

        tableCourse.getColumnModel()
                .getColumn(COLUMN_CREDITS)
                .setCellRenderer(centerRenderer);

        tableCourse.getColumnModel()
                .getColumn(COLUMN_FEE)
                .setCellRenderer(
                        new CurrencyCellRenderer()
                );

        tableCourse.getColumnModel()
                .getColumn(COLUMN_STATUS)
                .setCellRenderer(
                        new StatusCellRenderer()
                );
    }

    private void configureSorter() {
        /*
         * Mô tả và tên khóa học vẫn được tìm kiếm,
         * nhưng ID và học phí không tham gia lọc.
         */
        tableSorter.setSortable(
                COLUMN_DESCRIPTION,
                false
        );
    }

    private void registerEvents() {
        txtSearch.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent event
                            ) {
                                filterCourses();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event
                            ) {
                                filterCourses();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event
                            ) {
                                filterCourses();
                            }
                        }
                );

        btnRefresh.addActionListener(
                event -> resetViewState()
        );

        tableCourse.getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateActionButtonState();
                            }
                        }
                );

        tableCourse.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (
                                event.getClickCount() == 2
                                        && tableCourse.getSelectedRow() >= 0
                                        && managementMode
                        ) {
                            btnUpdate.doClick();
                        }
                    }
                }
        );

        tableModel.addTableModelListener(
                event -> updateCourseCount()
        );
    }

    private void filterCourses() {
        String keyword = txtSearch
                .getText()
                .trim();

        if (keyword.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)"
                                    + Pattern.quote(keyword),
                            COLUMN_CODE,
                            COLUMN_NAME,
                            COLUMN_DESCRIPTION,
                            COLUMN_STATUS
                    )
            );
        }

        tableCourse.clearSelection();

        updateCourseCount();
        updateActionButtonState();
    }

    private void resetViewState() {
        txtSearch.setText("");
        tableSorter.setRowFilter(null);
        tableCourse.clearSelection();

        updateCourseCount();
        updateActionButtonState();

        /*
         * Sự kiện của nút vẫn tiếp tục được truyền đến
         * Controller đã đăng ký bên ngoài.
         */
    }

    private void updateCourseCount() {
        int totalRows = tableModel.getRowCount();
        int visibleRows = tableCourse.getRowCount();

        lblCourseCount.setText(
                totalRows + " khóa học"
        );

        if (totalRows == 0) {
            lblResultSummary.setText(
                    "Chưa có khóa học trong hệ thống"
            );

        } else if (!txtSearch.getText().isBlank()) {
            lblResultSummary.setText(
                    "Tìm thấy "
                            + visibleRows
                            + " trong "
                            + totalRows
                            + " khóa học"
            );

        } else {
            lblResultSummary.setText(
                    "Đang hiển thị "
                            + totalRows
                            + " khóa học"
            );
        }
    }

    private void updateActionButtonState() {
        boolean hasSelection =
                tableCourse.getSelectedRow() >= 0;

        btnUpdate.setEnabled(
                managementMode && hasSelection
        );

        btnDelete.setEnabled(
                managementMode && hasSelection
        );
    }

    private JButton createButton(
            String text,
            FontAwesomeSolid icon,
            Color background,
            Color foreground
    ) {
        JButton button = new JButton(text);

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

    public int getSelectedCourseId() {
        int selectedRow =
                tableCourse.getSelectedRow();

        if (selectedRow < 0) {
            return -1;
        }

        int modelRow =
                tableCourse.convertRowIndexToModel(
                        selectedRow
                );

        Object idValue =
                tableModel.getValueAt(
                        modelRow,
                        COLUMN_ID
                );

        if (idValue instanceof Number number) {
            return number.intValue();
        }

        if (idValue == null) {
            return -1;
        }

        try {
            return Integer.parseInt(
                    idValue.toString().trim()
            );

        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public void setManagementMode(
            boolean managementMode
    ) {
        this.managementMode =
                managementMode;

        btnAdd.setVisible(managementMode);
        btnUpdate.setVisible(managementMode);
        btnDelete.setVisible(managementMode);

        btnRefresh.setText(
                managementMode
                        ? "Làm mới"
                        : "Tải lại"
        );

        lblMode.setText(
                managementMode
                        ? "• Chế độ quản lý"
                        : "• Chế độ xem"
        );

        lblMode.setForeground(
                managementMode
                        ? UIConstants.PRIMARY
                        : UIConstants.TEXT_SECONDARY
        );

        updateActionButtonState();

        revalidate();
        repaint();
    }

    public boolean isManagementMode() {
        return managementMode;
    }

    private static class CurrencyCellRenderer
            extends DefaultTableCellRenderer {

        private final DecimalFormat formatter =
                new DecimalFormat("#,##0 đ");

        public CurrencyCellRenderer() {
            setHorizontalAlignment(
                    SwingConstants.RIGHT
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
            Object displayValue =
                    formatCurrency(value);

            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            displayValue,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            setBorder(
                    BorderFactory.createEmptyBorder(
                            0,
                            0,
                            0,
                            12
                    )
            );

            return component;
        }

        private Object formatCurrency(
                Object value
        ) {
            if (value instanceof Number number) {
                return formatter.format(
                        number.doubleValue()
                );
            }

            if (value == null) {
                return "";
            }

            String text =
                    value.toString().trim();

            if (text.isEmpty()) {
                return "";
            }

            try {
                String normalized =
                        text.replace(".", "")
                                .replace(",", "")
                                .replace("đ", "")
                                .trim();

                return formatter.format(
                        Double.parseDouble(normalized)
                );

            } catch (NumberFormatException exception) {
                return text;
            }
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
                        case "ACTIVE", "OPEN" ->
                                "● Đang mở";

                        case "INACTIVE", "CLOSED" ->
                                "● Tạm dừng";

                        case "COMPLETED" ->
                                "● Hoàn thành";

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
                setBackground(Color.WHITE);

                setForeground(
                        switch (status) {
                            case "ACTIVE", "OPEN" ->
                                    UIConstants.SUCCESS;

                            case "INACTIVE", "CLOSED" ->
                                    UIConstants.DANGER;

                            case "COMPLETED" ->
                                    UIConstants.PRIMARY;

                            default ->
                                    UIConstants.TEXT_SECONDARY;
                        }
                );
            }

            return component;
        }
    }
}
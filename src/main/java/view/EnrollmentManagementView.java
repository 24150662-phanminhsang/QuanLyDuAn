package view;

import controller.ClassController;
import model.ClassRoom;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;

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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Giao diện quản lý đăng ký học.
 *
 * Được sử dụng như một JPanel để có thể nhúng trực tiếp
 * vào AdminDashboardView bằng CardLayout.
 */
public class EnrollmentManagementView extends JPanel {

    private final JTable tableEnrollment;
    private final DefaultTableModel tableModel;
    private final TableRowSorter<DefaultTableModel> tableSorter;

    private final JButton btnAdd;
    private final JButton btnUpdate;
    private final JButton btnDelete;
    private final JButton btnRefresh;

    private final JTextField txtSearch;
    private final JLabel lblEnrollmentCount;

    public EnrollmentManagementView() {
        setLayout(
                new MigLayout(
                        "fill, insets 20, wrap 1",
                        "[grow, fill]",
                        "[]15[grow, fill]"
                )
        );

        setBackground(UIConstants.BACKGROUND);

        btnAdd = createButton(
                "Thêm đăng ký",
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

        btnRefresh.setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.PRIMARY
                )
        );

        txtSearch = new JTextField();

        txtSearch.setPreferredSize(
                new Dimension(280, 38)
        );

        txtSearch.putClientProperty(
                "JTextField.placeholderText",
                "Tìm mã đăng ký, học viên, lớp học..."
        );

        txtSearch.putClientProperty(
                "JTextField.leadingIcon",
                FontIcon.of(
                        FontAwesomeSolid.SEARCH,
                        14,
                        UIConstants.TEXT_SECONDARY
                )
        );

        lblEnrollmentCount = new JLabel(
                "Tổng số: 0 đăng ký"
        );

        lblEnrollmentCount.setFont(
                UIConstants.FONT_SMALL
        );

        lblEnrollmentCount.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        /*
         * Bốn cột đầu vẫn giữ đúng thứ tự cơ bản để hạn chế
         * ảnh hưởng đến EnrollmentController hiện tại.
         */
        String[] columns = {
                "Mã đăng ký",
                "Mã học viên",
                "Mã lớp",
                "Ngày đăng ký",
                "Tên học viên",
                "Lớp học",
                "Khóa học",
                "Trạng thái"
        };

        tableModel = new DefaultTableModel(
                columns,
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

        tableEnrollment = new JTable(
                tableModel
        );

        configureTable();

        tableSorter = new TableRowSorter<>(
                tableModel
        );

        tableEnrollment.setRowSorter(
                tableSorter
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        add(
                createTableCard(),
                "grow, push"
        );

        registerEvents();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow][]10[]",
                        "[]"
                )
        );

        panel.setOpaque(false);

        JPanel titlePanel = new JPanel(
                new MigLayout(
                        "insets 0, wrap 1",
                        "[]",
                        "[]2[]"
                )
        );

        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Quản lý đăng ký học"
        );

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Quản lý học viên đăng ký tham gia các lớp học"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel);
        panel.add(txtSearch);
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel createTableCard() {
        JPanel card = new JPanel(
                new BorderLayout(0, 14)
        );

        card.setBackground(Color.WHITE);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.BORDER
                        ),
                        BorderFactory.createEmptyBorder(
                                18,
                                20,
                                18,
                                20
                        )
                )
        );

        card.putClientProperty(
                "FlatLaf.style",
                "arc: 14"
        );

        card.add(
                createTableToolbar(),
                BorderLayout.NORTH
        );

        JScrollPane scrollPane = new JScrollPane(
                tableEnrollment
        );

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
                BorderLayout.CENTER
        );

        return card;
    }

    private JPanel createTableToolbar() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow]push[]8[]8[]",
                        "[]"
                )
        );

        panel.setOpaque(false);

        JPanel informationPanel = new JPanel(
                new MigLayout(
                        "insets 0, wrap 1",
                        "[]",
                        "[]1[]"
                )
        );

        informationPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Danh sách đăng ký học"
        );

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD,
                        17f
                )
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        informationPanel.add(titleLabel);
        informationPanel.add(lblEnrollmentCount);

        panel.add(informationPanel);
        panel.add(btnAdd);
        panel.add(btnUpdate);
        panel.add(btnDelete);

        return panel;
    }

    private void configureTable() {
        tableEnrollment.setRowHeight(40);

        tableEnrollment.setFont(
                UIConstants.FONT_SMALL
        );

        tableEnrollment.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        tableEnrollment.setShowVerticalLines(false);

        tableEnrollment.setGridColor(
                UIConstants.BORDER
        );

        tableEnrollment.setSelectionBackground(
                new Color(219, 234, 254)
        );

        tableEnrollment.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        tableEnrollment.setFillsViewportHeight(true);

        tableEnrollment.setAutoResizeMode(
                JTable.AUTO_RESIZE_OFF
        );

        tableEnrollment.getTableHeader().setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        tableEnrollment.getTableHeader().setBackground(
                new Color(248, 250, 252)
        );

        tableEnrollment.getTableHeader().setForeground(
                UIConstants.TEXT_PRIMARY
        );

        tableEnrollment.getTableHeader().setPreferredSize(
                new Dimension(0, 42)
        );

        tableEnrollment.getTableHeader().setReorderingAllowed(
                false
        );

        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        tableEnrollment.getColumnModel()
                .getColumn(0)
                .setCellRenderer(centerRenderer);

        tableEnrollment.getColumnModel()
                .getColumn(1)
                .setCellRenderer(centerRenderer);

        tableEnrollment.getColumnModel()
                .getColumn(2)
                .setCellRenderer(centerRenderer);

        tableEnrollment.getColumnModel()
                .getColumn(3)
                .setCellRenderer(centerRenderer);

        tableEnrollment.getColumnModel()
                .getColumn(7)
                .setCellRenderer(centerRenderer);

        tableEnrollment.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(100);

        tableEnrollment.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(100);

        tableEnrollment.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(90);

        tableEnrollment.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(125);

        tableEnrollment.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(180);

        tableEnrollment.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(130);

        tableEnrollment.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(200);

        tableEnrollment.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(120);
    }

    private void registerEvents() {
        txtSearch.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(
                            DocumentEvent event
                    ) {
                        filterEnrollments();
                    }

                    @Override
                    public void removeUpdate(
                            DocumentEvent event
                    ) {
                        filterEnrollments();
                    }

                    @Override
                    public void changedUpdate(
                            DocumentEvent event
                    ) {
                        filterEnrollments();
                    }
                }
        );

        /*
         * Controller vẫn có thể gắn thêm sự kiện tải lại dữ liệu
         * thông qua getBtnRefresh().
         */
        btnRefresh.addActionListener(
                event -> {
                    txtSearch.setText("");
                    tableSorter.setRowFilter(null);
                    tableEnrollment.clearSelection();
                    updateEnrollmentCount();
                }
        );

        tableModel.addTableModelListener(
                event -> updateEnrollmentCount()
        );
    }

    private void filterEnrollments() {
        String keyword = txtSearch
                .getText()
                .trim();

        if (keyword.isEmpty()) {
            tableSorter.setRowFilter(null);
        } else {
            tableSorter.setRowFilter(
                    RowFilter.regexFilter(
                            "(?i)"
                                    + Pattern.quote(keyword)
                    )
            );
        }

        updateEnrollmentCount();
    }

    private void updateEnrollmentCount() {
        int visibleRowCount =
                tableEnrollment.getRowCount();

        lblEnrollmentCount.setText(
                "Tổng số: "
                        + visibleRowCount
                        + " đăng ký"
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

        button.setPreferredSize(
                new Dimension(125, 38)
        );

        button.putClientProperty(
                "FlatLaf.style",
                "arc: 10; borderWidth: 0"
        );

        return button;
    }

    /**
     * Dùng khi Admin mở trang:
     * setManagementMode(true);
     *
     * Dùng khi học viên chỉ xem đăng ký:
     * setManagementMode(false);
     */
    public void setManagementMode(
            boolean managementMode
    ) {
        btnAdd.setVisible(managementMode);
        btnUpdate.setVisible(managementMode);
        btnDelete.setVisible(managementMode);

        btnRefresh.setText(
                managementMode
                        ? "Làm mới"
                        : "Tải lại"
        );

        revalidate();
        repaint();
    }

    public void clearSelection() {
        tableEnrollment.clearSelection();
    }

    public int getSelectedEnrollmentId() {
        int selectedRow =
                tableEnrollment.getSelectedRow();

        if (selectedRow < 0) {
            return -1;
        }

        int modelRow =
                tableEnrollment.convertRowIndexToModel(
                        selectedRow
                );

        Object idValue =
                tableModel.getValueAt(
                        modelRow,
                        0
                );

        if (idValue == null) {
            return -1;
        }

        try {
            return Integer.parseInt(
                    idValue.toString()
            );
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    public JTable getTableEnrollment() {
        return tableEnrollment;
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

    public static class TeacherDashboardView extends JPanel {
        private JTable classTable;
        private DefaultTableModel tableModel;
        private final ClassController classController = new ClassController();

        public TeacherDashboardView() {
            setLayout(new MigLayout("fill, insets 15", "[grow]", "[][grow]"));

            JLabel lblTitle = new JLabel("BẢNG ĐIỀU KHIỂN GIẢNG VIÊN");
            lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
            add(lblTitle, "wrap 15");

            JPanel tablePanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
            tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách lớp học trong hệ thống"));

            tableModel = new DefaultTableModel(new String[]{"ID Lớp", "Tên Lớp", "Lịch Học", "Phòng", "Sĩ Số Tối Đa"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            classTable = new JTable(tableModel);
            classTable.setRowHeight(25);
            tablePanel.add(new JScrollPane(classTable), "grow");

            add(tablePanel, "grow");

            loadData();
        }

        private void loadData() {
            tableModel.setRowCount(0);
            List<ClassRoom> list = classController.getAllClasses();
            if (list != null) {
                for (ClassRoom c : list) {
                    tableModel.addRow(new Object[]{
                            c.getClassId(),
                            c.getClassName(),
                            c.getSchedule(),
                            c.getRoom(),
                            c.getMaxStudents()
                    });
                }
            }
        }
    }
}
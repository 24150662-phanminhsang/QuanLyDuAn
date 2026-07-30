package view;

import controller.UserController;
import model.AccountStatus;
import model.Role;
import model.User;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;
import view.dialog.UserFormDialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class UserManagementView extends JPanel {

    private static final int ROWS_PER_PAGE = 8;

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final UserController userController;

    private final DefaultTableModel tableModel;
    private final JTable userTable;

    private final JTextField searchField;
    private final JLabel resultLabel;
    private final JLabel pageLabel;
    private final JLabel totalUserLabel;

    private JButton previousButton;
    private JButton nextButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton exportButton;

    private List<User> allUsers = Collections.emptyList();

    private int currentPage = 1;
    private boolean loading;

    public UserManagementView() {
        userController = new UserController();

        searchField = new JTextField();
        resultLabel = new JLabel();
        pageLabel = new JLabel();
        totalUserLabel = new JLabel("0 tài khoản");

        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Tên đăng nhập",
                        "Họ và tên",
                        "Email",
                        "Số điện thoại",
                        "Vai trò",
                        "Trạng thái",
                        "Ngày tạo"
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

        userTable = new JTable(tableModel);

        initializeView();
        registerEvents();
        loadUsers();
    }

    private void initializeView() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.BACKGROUND);

        JPanel wrapper = new JPanel(
                new MigLayout(
                        "fill, insets 16",
                        "[grow, fill]",
                        "[grow, fill]"
                )
        );

        wrapper.setOpaque(false);

        ContentCard card = new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[]12[grow, fill]12[]"
                )
        );

        card.setMinimumSize(
                new Dimension(650, 500)
        );

        card.add(
                createTitlePanel(),
                "growx"
        );

        card.add(
                createToolbar(),
                "growx"
        );

        card.add(
                createTableScrollPane(),
                "grow, push"
        );

        card.add(
                createPaginationPanel(),
                "growx"
        );

        wrapper.add(
                card,
                "grow, push"
        );

        add(
                wrapper,
                BorderLayout.CENTER
        );
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill][]",
                        "[][]"
                )
        );

        panel.setOpaque(false);

        JLabel titleLabel = new JLabel(
                "Quản lý tài khoản"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                "Thêm, cập nhật và quản lý các tài khoản trong hệ thống"
        );

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        totalUserLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        totalUserLabel.setForeground(
                UIConstants.PRIMARY
        );

        totalUserLabel.setHorizontalAlignment(
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
                totalUserLabel,
                "cell 1 0 1 2, align right"
        );

        return panel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill]10[]",
                        "[]"
                )
        );

        toolbar.setOpaque(false);

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo tên đăng nhập, họ tên, email, vai trò..."
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
                """
        );

        searchField.setMinimumSize(
                new Dimension(220, 38)
        );

        searchField.setPreferredSize(
                new Dimension(390, 38)
        );

        JPanel buttonPanel = new JPanel(
                new MigLayout(
                        "insets 0",
                        "[][][][][]",
                        "[]"
                )
        );

        buttonPanel.setOpaque(false);

        JButton refreshButton =
                createToolbarButton(
                        "Làm mới",
                        FontAwesomeSolid.SYNC_ALT,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        JButton addButton =
                createToolbarButton(
                        "Thêm mới",
                        FontAwesomeSolid.PLUS,
                        UIConstants.PRIMARY,
                        Color.WHITE
                );

        editButton =
                createToolbarButton(
                        "Cập nhật",
                        FontAwesomeSolid.EDIT,
                        Color.WHITE,
                        UIConstants.PRIMARY
                );

        deleteButton =
                createToolbarButton(
                        "Xóa",
                        FontAwesomeSolid.TRASH_ALT,
                        Color.WHITE,
                        UIConstants.DANGER
                );

        exportButton =
                createToolbarButton(
                        "Xuất CSV",
                        FontAwesomeSolid.DOWNLOAD,
                        UIConstants.SUCCESS,
                        Color.WHITE
                );

        refreshButton.addActionListener(
                event -> {
                    searchField.setText("");
                    currentPage = 1;
                    loadUsers();
                }
        );

        addButton.addActionListener(
                event -> createUser()
        );

        editButton.addActionListener(
                event -> updateSelectedUser()
        );

        deleteButton.addActionListener(
                event -> deleteSelectedUser()
        );

        exportButton.addActionListener(
                event -> exportCsv()
        );

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        toolbar.add(
                searchField,
                "growx, height 38!"
        );

        toolbar.add(
                buttonPanel,
                "align right"
        );

        return toolbar;
    }

    private JButton createToolbarButton(
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

        button.setBackground(background);
        button.setForeground(foreground);

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
                focusWidth: 0;
                margin: 7,10,7,10;
                """
        );

        return button;
    }

    private JScrollPane createTableScrollPane() {
        userTable.setRowHeight(42);
        userTable.setFillsViewportHeight(true);

        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        userTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        userTable.setShowHorizontalLines(true);
        userTable.setShowVerticalLines(false);

        userTable.setGridColor(
                UIConstants.BORDER
        );

        userTable.setIntercellSpacing(
                new Dimension(0, 1)
        );

        userTable.setSelectionBackground(
                new Color(239, 246, 255)
        );

        userTable.setSelectionForeground(
                UIConstants.TEXT_PRIMARY
        );

        userTable.getTableHeader()
                .setReorderingAllowed(false);

        userTable.getTableHeader()
                .setPreferredSize(
                        new Dimension(0, 40)
                );

        userTable.getTableHeader()
                .setFont(
                        UIConstants.FONT_MEDIUM.deriveFont(
                                Font.BOLD
                        )
                );

        userTable.getTableHeader()
                .setForeground(
                        UIConstants.TEXT_PRIMARY
                );

        configureColumnWidths();
        configureRenderers();

        JScrollPane scrollPane =
                new JScrollPane(userTable);

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

    private void configureColumnWidths() {
        setColumnWidth(0, 45);
        setColumnWidth(1, 120);
        setColumnWidth(2, 165);
        setColumnWidth(3, 200);
        setColumnWidth(4, 125);
        setColumnWidth(5, 95);
        setColumnWidth(6, 105);
        setColumnWidth(7, 100);
    }

    private void setColumnWidth(
            int columnIndex,
            int preferredWidth
    ) {
        userTable.getColumnModel()
                .getColumn(columnIndex)
                .setPreferredWidth(preferredWidth);
    }

    private void configureRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        int[] centeredColumns = {
                0, 5, 6, 7
        };

        for (int column : centeredColumns) {
            userTable.getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(centerRenderer);
        }

        userTable.getColumnModel()
                .getColumn(5)
                .setCellRenderer(
                        new RoleCellRenderer()
                );

        userTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(
                        new StatusCellRenderer()
                );
    }

    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow][][][]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        resultLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        resultLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        previousButton =
                createPaginationButton("‹");

        nextButton =
                createPaginationButton("›");

        previousButton.setToolTipText(
                "Trang trước"
        );

        nextButton.setToolTipText(
                "Trang sau"
        );

        previousButton.addActionListener(
                event -> {
                    if (currentPage > 1) {
                        currentPage--;
                        displayCurrentPage();
                    }
                }
        );

        nextButton.addActionListener(
                event -> {
                    if (
                            currentPage
                                    < getTotalPages()
                    ) {
                        currentPage++;
                        displayCurrentPage();
                    }
                }
        );

        pageLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        pageLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        pageLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        panel.add(
                resultLabel,
                "growx"
        );

        panel.add(previousButton);
        panel.add(pageLabel, "width 58!");
        panel.add(nextButton);

        return panel;
    }

    private JButton createPaginationButton(
            String text
    ) {
        JButton button = new JButton(text);

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(36, 32)
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
                focusWidth: 0;
                margin: 5,8,5,8;
                """
        );

        return button;
    }

    private void registerEvents() {
        searchField.getDocument()
                .addDocumentListener(
                        new DocumentListener() {
                            @Override
                            public void insertUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }

                            @Override
                            public void removeUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }

                            @Override
                            public void changedUpdate(
                                    DocumentEvent event
                            ) {
                                applySearch();
                            }
                        }
                );

        userTable.getSelectionModel()
                .addListSelectionListener(
                        event -> {
                            if (!event.getValueIsAdjusting()) {
                                updateActionButtonState();
                            }
                        }
                );

        userTable.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        if (
                                event.getClickCount() == 2
                                        && userTable.getSelectedRow() >= 0
                        ) {
                            updateSelectedUser();
                        }
                    }
                }
        );

        updateActionButtonState();
    }

    private void applySearch() {
        currentPage = 1;
        displayCurrentPage();
    }

    public void loadUsers() {
        if (loading) {
            return;
        }

        loading = true;

        try {
            List<User> users =
                    userController.getAllUsers();

            allUsers = users == null
                    ? Collections.emptyList()
                    : users;

            int totalPages = getTotalPages();

            currentPage = Math.max(
                    1,
                    Math.min(
                            currentPage,
                            totalPages
                    )
            );

            displayCurrentPage();

        } catch (SQLException exception) {
            allUsers = Collections.emptyList();
            tableModel.setRowCount(0);

            updateSummaryState();

            showError(
                    "Không thể tải danh sách tài khoản.",
                    exception
            );

        } finally {
            loading = false;
        }
    }

    private void displayCurrentPage() {
        List<User> filteredUsers =
                getFilteredUsers();

        int totalRows =
                filteredUsers.size();

        int totalPages = Math.max(
                1,
                (int) Math.ceil(
                        totalRows
                                / (double) ROWS_PER_PAGE
                )
        );

        currentPage = Math.max(
                1,
                Math.min(
                        currentPage,
                        totalPages
                )
        );

        int startIndex =
                (currentPage - 1)
                        * ROWS_PER_PAGE;

        int endIndex = Math.min(
                startIndex + ROWS_PER_PAGE,
                totalRows
        );

        tableModel.setRowCount(0);

        if (startIndex < endIndex) {
            for (
                    User user :
                    filteredUsers.subList(
                            startIndex,
                            endIndex
                    )
            ) {
                if (user == null) {
                    continue;
                }

                tableModel.addRow(
                        new Object[]{
                                user.getUserId(),
                                safeText(user.getUsername()),
                                safeText(user.getFullName()),
                                safeText(user.getEmail()),
                                safeText(user.getPhone()),
                                user.getRole(),
                                user.getStatus(),
                                user.getCreatedAt() == null
                                        ? ""
                                        : user.getCreatedAt()
                                        .format(DATE_FORMATTER)
                        }
                );
            }
        }

        if (totalRows == 0) {
            resultLabel.setText(
                    searchField.getText().isBlank()
                            ? "Chưa có tài khoản trong hệ thống"
                            : "Không tìm thấy tài khoản phù hợp"
            );
        } else {
            resultLabel.setText(
                    "Hiển thị "
                            + (startIndex + 1)
                            + "–"
                            + endIndex
                            + " trong "
                            + totalRows
                            + " tài khoản"
            );
        }

        pageLabel.setText(
                currentPage
                        + " / "
                        + totalPages
        );

        previousButton.setEnabled(
                currentPage > 1
        );

        nextButton.setEnabled(
                currentPage < totalPages
        );

        userTable.clearSelection();

        updateSummaryState();
        updateActionButtonState();

        userTable.revalidate();
        userTable.repaint();
    }

    private void updateSummaryState() {
        int totalUsers =
                allUsers == null
                        ? 0
                        : allUsers.size();

        totalUserLabel.setText(
                totalUsers + " tài khoản"
        );

        exportButton.setEnabled(
                totalUsers > 0
        );
    }

    private void updateActionButtonState() {
        boolean hasSelection =
                userTable.getSelectedRow() >= 0;

        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }

    private int getTotalPages() {
        return Math.max(
                1,
                (int) Math.ceil(
                        getFilteredUsers().size()
                                / (double) ROWS_PER_PAGE
                )
        );
    }

    private List<User> getFilteredUsers() {
        if (
                allUsers == null
                        || allUsers.isEmpty()
        ) {
            return Collections.emptyList();
        }

        String keyword = searchField
                .getText()
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );

        if (keyword.isBlank()) {
            return allUsers;
        }

        return allUsers.stream()
                .filter(user -> user != null)
                .filter(user ->
                        contains(
                                user.getUsername(),
                                keyword
                        )
                                || contains(
                                user.getFullName(),
                                keyword
                        )
                                || contains(
                                user.getEmail(),
                                keyword
                        )
                                || contains(
                                user.getPhone(),
                                keyword
                        )
                                || contains(
                                user.getRole() == null
                                        ? null
                                        : user.getRole().name(),
                                keyword
                        )
                                || contains(
                                user.getStatus() == null
                                        ? null
                                        : user.getStatus().name(),
                                keyword
                        )
                )
                .toList();
    }

    private boolean contains(
            String value,
            String keyword
    ) {
        return value != null
                && value.toLowerCase(
                Locale.ROOT
        ).contains(keyword);
    }

    private void createUser() {
        UserFormDialog dialog =
                UserFormDialog.showCreate(this);

        if (!dialog.isConfirmed()) {
            return;
        }

        try {
            boolean successful =
                    userController.createUser(
                            dialog.getUsername(),
                            dialog.getPassword(),
                            dialog.getFullName(),
                            dialog.getEmail(),
                            dialog.getPhone(),
                            getRoleId(
                                    dialog.getSelectedRole()
                            )
                    );

            if (!successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể thêm tài khoản.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Thêm tài khoản thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            searchField.setText("");
            currentPage = 1;
            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể thêm tài khoản.",
                    exception
            );
        }
    }

    private void updateSelectedUser() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn tài khoản cần cập nhật.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        UserFormDialog dialog =
                UserFormDialog.showEdit(
                        this,
                        selectedUser
                );

        if (!dialog.isConfirmed()) {
            return;
        }

        try {
            Role selectedRole =
                    dialog.getSelectedRole();

            AccountStatus selectedStatus =
                    dialog.getSelectedStatus();

            selectedUser.setFullName(
                    dialog.getFullName()
            );

            selectedUser.setEmail(
                    dialog.getEmail()
            );

            selectedUser.setPhone(
                    dialog.getPhone()
            );

            selectedUser.setRole(
                    selectedRole
            );

            selectedUser.setRoleId(
                    getRoleId(selectedRole)
            );

            selectedUser.setStatus(
                    selectedStatus
            );

            boolean successful =
                    userController.updateUser(
                            selectedUser
                    );

            if (!successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể cập nhật tài khoản.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Cập nhật tài khoản thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể cập nhật tài khoản.",
                    exception
            );
        }
    }

    private void deleteSelectedUser() {
        User selectedUser =
                getSelectedUser();

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn tài khoản cần xóa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (
                "admin".equalsIgnoreCase(
                        selectedUser.getUsername()
                )
        ) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa tài khoản quản trị chính.",
                    "Không được phép",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa tài khoản \""
                                + safeText(
                                selectedUser.getUsername()
                        )
                                + "\"?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if (
                answer
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        try {
            boolean successful =
                    userController.deleteUser(
                            selectedUser.getUserId()
                    );

            if (!successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Không thể xóa tài khoản.",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xóa tài khoản thành công.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

            loadUsers();

        } catch (Exception exception) {
            showError(
                    "Không thể xóa tài khoản. "
                            + "Tài khoản có thể đang liên kết với dữ liệu khác.",
                    exception
            );
        }
    }

    private User getSelectedUser() {
        int selectedRow =
                userTable.getSelectedRow();

        if (selectedRow < 0) {
            return null;
        }

        int modelRow =
                userTable.convertRowIndexToModel(
                        selectedRow
                );

        Object idValue =
                tableModel.getValueAt(
                        modelRow,
                        0
                );

        int userId;

        if (
                idValue
                        instanceof Number number
        ) {
            userId = number.intValue();

        } else {
            try {
                userId = Integer.parseInt(
                        String.valueOf(idValue)
                );

            } catch (NumberFormatException exception) {
                return null;
            }
        }

        return allUsers.stream()
                .filter(user -> user != null)
                .filter(user ->
                        user.getUserId()
                                == userId
                )
                .findFirst()
                .orElse(null);
    }

    private int getRoleId(
            Role role
    ) {
        if (role == null) {
            return 3;
        }

        return switch (role) {
            case ADMIN -> 1;
            case TEACHER -> 2;
            case STUDENT -> 3;
        };
    }

    private void exportCsv() {
        List<User> usersToExport =
                getFilteredUsers();

        if (usersToExport.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không có dữ liệu để xuất.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        JFileChooser fileChooser =
                new JFileChooser();

        fileChooser.setDialogTitle(
                "Xuất danh sách tài khoản"
        );

        fileChooser.setSelectedFile(
                new File(
                        "danh-sach-tai-khoan.csv"
                )
        );

        fileChooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Tệp CSV (*.csv)",
                        "csv"
                )
        );

        if (
                fileChooser.showSaveDialog(this)
                        != JFileChooser.APPROVE_OPTION
        ) {
            return;
        }

        File file =
                ensureCsvExtension(
                        fileChooser.getSelectedFile()
                );

        if (
                file.exists()
                        && !confirmOverwrite(file)
        ) {
            return;
        }

        try (
                BufferedWriter writer =
                        Files.newBufferedWriter(
                                file.toPath(),
                                StandardCharsets.UTF_8
                        )
        ) {
            /*
             * BOM giúp Microsoft Excel nhận đúng UTF-8.
             */
            writer.write('\uFEFF');

            writer.write(
                    "ID,Tên đăng nhập,Họ và tên,"
                            + "Email,Số điện thoại,Vai trò,"
                            + "Trạng thái,Ngày tạo"
            );

            writer.newLine();

            for (User user : usersToExport) {
                if (user == null) {
                    continue;
                }

                writer.write(
                        user.getUserId()
                                + ","
                                + csv(user.getUsername())
                                + ","
                                + csv(user.getFullName())
                                + ","
                                + csv(user.getEmail())
                                + ","
                                + csv(user.getPhone())
                                + ","
                                + csv(
                                user.getRole() == null
                                        ? ""
                                        : user.getRole().name()
                        )
                                + ","
                                + csv(
                                user.getStatus() == null
                                        ? ""
                                        : user.getStatus().name()
                        )
                                + ","
                                + csv(
                                user.getCreatedAt() == null
                                        ? ""
                                        : user.getCreatedAt()
                                        .format(DATE_FORMATTER)
                        )
                );

                writer.newLine();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Đã xuất "
                            + usersToExport.size()
                            + " tài khoản tới:\n"
                            + file.getAbsolutePath(),
                    "Xuất dữ liệu thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException exception) {
            showError(
                    "Không thể xuất dữ liệu.",
                    exception
            );
        }
    }

    private File ensureCsvExtension(
            File file
    ) {
        if (
                file.getName()
                        .toLowerCase(Locale.ROOT)
                        .endsWith(".csv")
        ) {
            return file;
        }

        return new File(
                file.getAbsolutePath()
                        + ".csv"
        );
    }

    private boolean confirmOverwrite(
            File file
    ) {
        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Tệp \""
                                + file.getName()
                                + "\" đã tồn tại.\n"
                                + "Bạn có muốn ghi đè không?",
                        "Xác nhận ghi đè",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        return answer
                == JOptionPane.YES_OPTION;
    }

    private String csv(
            String value
    ) {
        if (value == null) {
            return "\"\"";
        }

        return "\""
                + value.replace(
                "\"",
                "\"\""
        )
                + "\"";
    }

    private String safeText(
            String value
    ) {
        return value == null
                ? ""
                : value.trim();
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

    private static class RoleCellRenderer
            extends DefaultTableCellRenderer {

        public RoleCellRenderer() {
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
                            getRoleText(value),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                setForeground(
                        getRoleColor(value)
                );

                setBackground(Color.WHITE);
            }

            setFont(
                    UIConstants.FONT_SMALL.deriveFont(
                            Font.BOLD
                    )
            );

            return component;
        }

        private static String getRoleText(
                Object value
        ) {
            if (!(value instanceof Role role)) {
                return "";
            }

            return switch (role) {
                case ADMIN -> "Quản trị";
                case TEACHER -> "Giảng viên";
                case STUDENT -> "Học viên";
            };
        }

        private static Color getRoleColor(
                Object value
        ) {
            if (!(value instanceof Role role)) {
                return UIConstants.TEXT_SECONDARY;
            }

            return switch (role) {
                case ADMIN -> UIConstants.DANGER;
                case TEACHER -> UIConstants.PURPLE;
                case STUDENT -> UIConstants.PRIMARY;
            };
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
            Component component =
                    super.getTableCellRendererComponent(
                            table,
                            getStatusText(value),
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            if (!isSelected) {
                setForeground(
                        getStatusColor(value)
                );

                setBackground(Color.WHITE);
            }

            setFont(
                    UIConstants.FONT_SMALL.deriveFont(
                            Font.BOLD
                    )
            );

            return component;
        }

        private static String getStatusText(
                Object value
        ) {
            if (!(value instanceof AccountStatus status)) {
                return "";
            }

            return switch (status) {
                case ACTIVE -> "🟢 Hoạt động";
                case LOCKED -> "🔒 Đã khóa";
                case INACTIVE -> "⚪ Không hoạt động";
            };
        }

        private static Color getStatusColor(
                Object value
        ) {
            if (!(value instanceof AccountStatus status)) {
                return UIConstants.TEXT_SECONDARY;
            }

            return switch (status) {
                case ACTIVE -> UIConstants.SUCCESS;
                case LOCKED -> UIConstants.DANGER;
                case INACTIVE -> UIConstants.WARNING;
            };
        }
    }
}
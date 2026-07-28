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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class UserManagementView extends JPanel {

    private static final int ROWS_PER_PAGE = 8;

    private final UserController userController;

    private final DefaultTableModel tableModel;
    private final JTable userTable;

    private final JTextField searchField;
    private final JLabel resultLabel;
    private final JLabel pageLabel;

    private JButton previousButton;
    private JButton nextButton;

    private List<User> allUsers = List.of();
    private int currentPage = 1;

    public UserManagementView() {
        userController = new UserController();

        searchField = new JTextField();
        resultLabel = new JLabel();
        pageLabel = new JLabel();

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
            public boolean isCellEditable(int row, int column) {
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
                        "fill, insets 14",
                        "[grow, fill]",
                        "[grow, fill]"
                )
        );

        wrapper.setOpaque(false);

        ContentCard card = new ContentCard();

        card.setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[grow, fill]",
                        "[]12[]12[grow, fill]12[]"
                )
        );

        card.add(createTitlePanel(), "growx");
        card.add(createToolbar(), "growx");
        card.add(createTableScrollPane(), "grow, push");
        card.add(createPaginationPanel(), "growx");

        wrapper.add(card, "grow, push");
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow]",
                        "[][]"
                )
        );

        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý tài khoản");
        titleLabel.setFont(UIConstants.FONT_HEADING);
        titleLabel.setForeground(UIConstants.TEXT_PRIMARY);

        JLabel descriptionLabel = new JLabel(
                "Quản lý danh sách tài khoản trong hệ thống"
        );

        descriptionLabel.setFont(UIConstants.FONT_NORMAL);
        descriptionLabel.setForeground(UIConstants.TEXT_SECONDARY);

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]8[]"
                )
        );

        toolbar.setOpaque(false);

        searchField.putClientProperty(
                "JTextField.placeholderText",
                "Tìm kiếm theo tên, email, vai trò..."
        );

        searchField.putClientProperty(
                "JTextField.leadingIcon",
                FontIcon.of(
                        FontAwesomeSolid.SEARCH,
                        15,
                        UIConstants.TEXT_SECONDARY
                )
        );

        searchField.setPreferredSize(
                new Dimension(300, 38)
        );

        JPanel buttonPanel = new JPanel(
                new FlowLayout(
                        FlowLayout.RIGHT,
                        7,
                        0
                )
        );

        buttonPanel.setOpaque(false);

        JButton refreshButton = createToolbarButton(
                "Làm mới",
                FontAwesomeSolid.SYNC_ALT,
                Color.WHITE,
                UIConstants.PRIMARY
        );

        JButton addButton = createToolbarButton(
                "Thêm mới",
                FontAwesomeSolid.PLUS,
                UIConstants.PRIMARY,
                Color.WHITE
        );

        JButton editButton = createToolbarButton(
                "Cập nhật",
                FontAwesomeSolid.EDIT,
                Color.WHITE,
                UIConstants.PRIMARY
        );

        JButton deleteButton = createToolbarButton(
                "Xóa",
                FontAwesomeSolid.TRASH_ALT,
                Color.WHITE,
                UIConstants.DANGER
        );

        JButton exportButton = createToolbarButton(
                "Xuất CSV",
                FontAwesomeSolid.DOWNLOAD,
                UIConstants.SUCCESS,
                Color.WHITE
        );

        refreshButton.addActionListener(event -> {
            searchField.setText("");
            currentPage = 1;
            loadUsers();
        });

        addButton.addActionListener(event -> createUser());
        editButton.addActionListener(event -> updateSelectedUser());
        deleteButton.addActionListener(event -> deleteSelectedUser());
        exportButton.addActionListener(event -> exportCsv());

        buttonPanel.add(refreshButton);
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        toolbar.add(searchField, "growx, height 38!");
        toolbar.add(buttonPanel, "growx");

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
        button.setFont(UIConstants.FONT_MEDIUM);
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

        /*
         * Bảng tự co theo vùng nội dung.
         * Không làm toàn trang bị tràn sang phải.
         */
        userTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
        );

        userTable.getTableHeader()
                .setReorderingAllowed(false);

        configureColumnWidths();
        configureRenderers();

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

        JScrollPane scrollPane = new JScrollPane(userTable);

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

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(15);

        scrollPane.getHorizontalScrollBar()
                .setUnitIncrement(15);

        scrollPane.setMinimumSize(
                new Dimension(400, 250)
        );

        return scrollPane;
    }

    private void configureColumnWidths() {
        userTable.getColumnModel()
                .getColumn(0)
                .setPreferredWidth(45);

        userTable.getColumnModel()
                .getColumn(1)
                .setPreferredWidth(120);

        userTable.getColumnModel()
                .getColumn(2)
                .setPreferredWidth(160);

        userTable.getColumnModel()
                .getColumn(3)
                .setPreferredWidth(190);

        userTable.getColumnModel()
                .getColumn(4)
                .setPreferredWidth(120);

        userTable.getColumnModel()
                .getColumn(5)
                .setPreferredWidth(90);

        userTable.getColumnModel()
                .getColumn(6)
                .setPreferredWidth(95);

        userTable.getColumnModel()
                .getColumn(7)
                .setPreferredWidth(100);
    }

    private void configureRenderers() {
        DefaultTableCellRenderer centerRenderer =
                new DefaultTableCellRenderer();

        centerRenderer.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        int[] centeredColumns = {0, 5, 6, 7};

        for (int column : centeredColumns) {
            userTable.getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(centerRenderer);
        }
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

        resultLabel.setFont(UIConstants.FONT_NORMAL);
        resultLabel.setForeground(UIConstants.TEXT_SECONDARY);

        previousButton = createPaginationButton("‹");
        nextButton = createPaginationButton("›");

        previousButton.addActionListener(event -> {
            if (currentPage > 1) {
                currentPage--;
                displayCurrentPage();
            }
        });

        nextButton.addActionListener(event -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                displayCurrentPage();
            }
        });

        pageLabel.setFont(UIConstants.FONT_MEDIUM);
        pageLabel.setForeground(UIConstants.TEXT_PRIMARY);

        panel.add(resultLabel, "growx");
        panel.add(previousButton);
        panel.add(pageLabel);
        panel.add(nextButton);

        return panel;
    }

    private JButton createPaginationButton(String text) {
        JButton button = new JButton(text);

        button.setFocusable(false);

        button.setPreferredSize(
                new Dimension(36, 32)
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 9;
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
    }

    private void applySearch() {
        currentPage = 1;
        displayCurrentPage();
    }

    public void loadUsers() {
        try {
            allUsers = userController.getAllUsers();

            int totalPages = getTotalPages();

            currentPage = Math.max(
                    1,
                    Math.min(
                            currentPage,
                            totalPages
                    )
            );

            displayCurrentPage();

            System.out.println(
                    "Đã tải "
                            + allUsers.size()
                            + " tài khoản."
            );

        } catch (SQLException exception) {
            allUsers = List.of();
            tableModel.setRowCount(0);

            showError(
                    "Không thể tải danh sách tài khoản.",
                    exception
            );
        }
    }

    private void displayCurrentPage() {
        List<User> filteredUsers =
                getFilteredUsers();

        int totalRows = filteredUsers.size();

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

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        if (startIndex < endIndex) {
            for (
                    User user :
                    filteredUsers.subList(
                            startIndex,
                            endIndex
                    )
            ) {
                tableModel.addRow(
                        new Object[]{
                                user.getUserId(),
                                user.getUsername(),
                                user.getFullName(),
                                user.getEmail(),
                                user.getPhone(),
                                user.getRole(),
                                user.getStatus(),
                                user.getCreatedAt() == null
                                        ? ""
                                        : user.getCreatedAt()
                                        .format(formatter)
                        }
                );
            }
        }

        if (totalRows == 0) {
            resultLabel.setText(
                    "Không tìm thấy tài khoản"
            );
        } else {
            resultLabel.setText(
                    "Hiển thị "
                            + (startIndex + 1)
                            + " đến "
                            + endIndex
                            + " trong tổng số "
                            + totalRows
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

        userTable.revalidate();
        userTable.repaint();
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
        String keyword = searchField
                .getText()
                .trim()
                .toLowerCase();

        if (keyword.isBlank()) {
            return allUsers;
        }

        return allUsers.stream()
                .filter(user ->
                        contains(user.getUsername(), keyword)
                                || contains(user.getFullName(), keyword)
                                || contains(user.getEmail(), keyword)
                                || contains(user.getPhone(), keyword)
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
                && value.toLowerCase()
                .contains(keyword);
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

            if (successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Thêm tài khoản thành công.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );

                searchField.setText("");
                currentPage = 1;
                loadUsers();
            }

        } catch (Exception exception) {
            showError(
                    "Không thể thêm tài khoản.",
                    exception
            );
        }
    }

    private void updateSelectedUser() {
        User selectedUser = getSelectedUser();

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

            if (successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Cập nhật tài khoản thành công.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadUsers();
            }

        } catch (Exception exception) {
            showError(
                    "Không thể cập nhật tài khoản.",
                    exception
            );
        }
    }

    private void deleteSelectedUser() {
        User selectedUser = getSelectedUser();

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hãy chọn tài khoản cần xóa.",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if ("admin".equalsIgnoreCase(
                selectedUser.getUsername()
        )) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể xóa tài khoản quản trị chính.",
                    "Không được phép",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc muốn xóa tài khoản \""
                        + selectedUser.getUsername()
                        + "\"?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean successful =
                    userController.deleteUser(
                            selectedUser.getUserId()
                    );

            if (successful) {
                JOptionPane.showMessageDialog(
                        this,
                        "Xóa tài khoản thành công.",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE
                );

                loadUsers();
            }

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

        if (idValue instanceof Integer integerValue) {
            userId = integerValue;
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
                .filter(user ->
                        user.getUserId() == userId
                )
                .findFirst()
                .orElse(null);
    }

    private int getRoleId(Role role) {
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
                        "Tệp CSV",
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
                fileChooser.getSelectedFile();

        if (
                !file.getName()
                        .toLowerCase()
                        .endsWith(".csv")
        ) {
            file = new File(
                    file.getAbsolutePath()
                            + ".csv"
            );
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy"
                );

        try (
                FileWriter writer =
                        new FileWriter(file)
        ) {
            writer.write(
                    "\uFEFF"
                            + "ID,Tên đăng nhập,Họ và tên,"
                            + "Email,Số điện thoại,Vai trò,"
                            + "Trạng thái,Ngày tạo\n"
            );

            for (User user : allUsers) {
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
                                        .format(formatter)
                        )
                                + "\n"
                );
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Xuất dữ liệu thành công:\n"
                            + file.getAbsolutePath(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException exception) {
            showError(
                    "Không thể xuất dữ liệu.",
                    exception
            );
        }
    }

    private String csv(String value) {
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

    private void showError(
            String message,
            Exception exception
    ) {
        JOptionPane.showMessageDialog(
                this,
                message
                        + "\nChi tiết: "
                        + exception.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
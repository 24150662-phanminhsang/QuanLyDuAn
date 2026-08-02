package view;

import controller.DiscountController;
import model.Discount;
import net.miginfocom.swing.MigLayout;
import util.UIConstants;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiscountManagementDialog extends JDialog {

    private final DiscountController controller = new DiscountController();
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private final JTextField idField = new JTextField();
    private final JTextField codeField = new JTextField();
    private final JTextField descriptionField = new JTextField();
    private final JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"PERCENT", "FIXED"});
    private final JTextField valueField = new JTextField();
    private final JTextField maxAmountField = new JTextField();
    private final JTextField minimumAmountField = new JTextField();
    private final JTextField startDateField = new JTextField();
    private final JTextField endDateField = new JTextField();
    private final JCheckBox activeCheckBox = new JCheckBox("Đang hoạt động", true);

    private final DefaultTableModel activeModel = createTableModel();
    private final DefaultTableModel expiredModel = createTableModel();
    private final JTable activeTable = new JTable(activeModel);
    private final JTable expiredTable = new JTable(expiredModel);

    private final JLabel summaryLabel = new JLabel();
    private List<Discount> activeDiscounts = new ArrayList<>();
    private List<Discount> expiredDiscounts = new ArrayList<>();

    public DiscountManagementDialog(Window owner) {
        super(owner, "Quản lý mã giảm giá", ModalityType.APPLICATION_MODAL);
        initializeView();
        registerEvents();
        loadDiscounts();
    }

    private void initializeView() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1040, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new MigLayout(
                "fill, insets 16, wrap 1",
                "[grow,fill]",
                "[]12[]12[grow,fill]"));
        root.setBackground(UIConstants.BACKGROUND);

        root.add(createHeader(), "growx");
        root.add(createFormPanel(), "growx");
        root.add(createTablesPanel(), "grow,push");
        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new MigLayout("fillx,insets 0", "[grow][]", "[][]"));
        panel.setOpaque(false);
        JLabel title = new JLabel("Quản lý mã giảm giá");
        title.setFont(UIConstants.FONT_TITLE);
        title.setForeground(UIConstants.TEXT_PRIMARY);
        JLabel description = new JLabel("Thêm, cập nhật và theo dõi mã còn hiệu lực hoặc hết hiệu lực");
        description.setFont(UIConstants.FONT_NORMAL);
        description.setForeground(UIConstants.TEXT_SECONDARY);
        summaryLabel.setFont(UIConstants.FONT_MEDIUM);
        summaryLabel.setForeground(UIConstants.PRIMARY);
        panel.add(title, "cell 0 0");
        panel.add(description, "cell 0 1");
        panel.add(summaryLabel, "cell 1 0 1 2,align right");
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new MigLayout(
                "fillx,wrap 5,insets 16",
                "[90!][grow,fill][110!][grow,fill][grow,fill]",
                "[]8[]8[]8[]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));

        idField.setEditable(false);
        configure(idField, "Tự động sinh");
        configure(codeField, "Ví dụ: SUMMER20");
        configure(descriptionField, "Mô tả mã giảm giá");
        configure(valueField, "Giá trị giảm");
        configure(maxAmountField, "0 nếu không giới hạn");
        configure(minimumAmountField, "0 nếu không yêu cầu");
        configure(startDateField, "yyyy-MM-dd");
        configure(endDateField, "yyyy-MM-dd");
        typeComboBox.putClientProperty("FlatLaf.style", "arc:9");
        activeCheckBox.setOpaque(false);

        panel.add(label("ID")); panel.add(idField, "height 36!");
        panel.add(label("Mã *")); panel.add(codeField, "height 36!");
        panel.add(activeCheckBox, "align center");

        panel.add(label("Mô tả")); panel.add(descriptionField, "span 2,growx,height 36!");
        panel.add(label("Loại *")); panel.add(typeComboBox, "height 36!");

        panel.add(label("Giá trị *")); panel.add(valueField, "height 36!");
        panel.add(label("Giảm tối đa")); panel.add(maxAmountField, "height 36!");
        panel.add(new JLabel());

        panel.add(label("Đơn tối thiểu")); panel.add(minimumAmountField, "height 36!");
        panel.add(label("Ngày bắt đầu")); panel.add(startDateField, "height 36!");
        panel.add(new JLabel());

        panel.add(label("Ngày kết thúc")); panel.add(endDateField, "height 36!");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton clearButton = button("Nhập lại", Color.WHITE, UIConstants.TEXT_PRIMARY);
        JButton deleteButton = button("Xóa", UIConstants.DANGER, Color.WHITE);
        JButton updateButton = button("Cập nhật", UIConstants.WARNING, Color.WHITE);
        JButton addButton = button("Thêm mã", UIConstants.PRIMARY, Color.WHITE);
        buttons.add(clearButton); buttons.add(deleteButton); buttons.add(updateButton); buttons.add(addButton);
        panel.add(buttons, "span 3,growx");

        clearButton.addActionListener(e -> clearForm());
        addButton.addActionListener(e -> createDiscount());
        updateButton.addActionListener(e -> updateDiscount());
        deleteButton.addActionListener(e -> deleteDiscount());
        return panel;
    }

    private JTabbedPane createTablesPanel() {
        configureTable(activeTable);
        configureTable(expiredTable);
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Còn hiệu lực", new JScrollPane(activeTable));
        tabs.addTab("Hết hiệu lực", new JScrollPane(expiredTable));
        return tabs;
    }

    private static DefaultTableModel createTableModel() {
        return new DefaultTableModel(new Object[]{
                "ID", "Mã", "Mô tả", "Loại", "Giá trị", "Đơn tối thiểu", "Bắt đầu", "Kết thúc", "Trạng thái"
        }, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void configureTable(JTable table) {
        table.setRowHeight(36);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);
        int[] widths = {55,110,210,90,115,130,110,110,120};
        for (int i=0;i<widths.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void registerEvents() {
        activeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectFromTable(activeTable, activeDiscounts);
        });
        expiredTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selectFromTable(expiredTable, expiredDiscounts);
        });
    }

    public void loadDiscounts() {
        List<Discount> all = controller.getAll();
        activeDiscounts = new ArrayList<>();
        expiredDiscounts = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Discount discount : all) {
            if (isCurrentlyValid(discount, today)) activeDiscounts.add(discount);
            else expiredDiscounts.add(discount);
        }
        fillModel(activeModel, activeDiscounts, "Còn hiệu lực");
        fillModel(expiredModel, expiredDiscounts, "Hết hiệu lực");
        summaryLabel.setText("Tổng: " + all.size() + "  |  Còn hiệu lực: " + activeDiscounts.size() + "  |  Hết hiệu lực: " + expiredDiscounts.size());
    }

    private boolean isCurrentlyValid(Discount d, LocalDate today) {
        if (d == null || !d.isActive()) return false;
        LocalDate start = d.getStartDate() == null ? null : new java.sql.Date(d.getStartDate().getTime()).toLocalDate();
        LocalDate end = d.getEndDate() == null ? null : new java.sql.Date(d.getEndDate().getTime()).toLocalDate();
        return (start == null || !today.isBefore(start)) && (end == null || !today.isAfter(end));
    }

    private void fillModel(DefaultTableModel model, List<Discount> list, String status) {
        model.setRowCount(0);
        for (Discount d : list) {
            String value = "PERCENT".equalsIgnoreCase(d.getDiscountType())
                    ? String.format(Locale.US, "%.0f%%", d.getDiscountValue())
                    : currencyFormat.format(d.getDiscountValue());
            model.addRow(new Object[]{d.getDiscountId(), d.getCode(), d.getDescription(), d.getDiscountType(), value,
                    currencyFormat.format(d.getMinimumAmount()), formatDate(d.getStartDate()), formatDate(d.getEndDate()), status});
        }
    }

    private void selectFromTable(JTable table, List<Discount> source) {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow < 0 || modelRow >= source.size()) return;
        fillForm(source.get(modelRow));
        JTable other = table == activeTable ? expiredTable : activeTable;
        other.clearSelection();
    }

    private void fillForm(Discount d) {
        idField.setText(String.valueOf(d.getDiscountId()));
        codeField.setText(safe(d.getCode()));
        descriptionField.setText(safe(d.getDescription()));
        typeComboBox.setSelectedItem(d.getDiscountType());
        valueField.setText(formatNumber(d.getDiscountValue()));
        maxAmountField.setText(formatNumber(d.getMaxDiscountAmount()));
        minimumAmountField.setText(formatNumber(d.getMinimumAmount()));
        startDateField.setText(formatDate(d.getStartDate()));
        endDateField.setText(formatDate(d.getEndDate()));
        activeCheckBox.setSelected(d.isActive());
    }

    private Discount readForm(boolean requireId) {
        Discount d = new Discount();
        if (requireId) d.setDiscountId(parseInt(idField.getText(), "ID"));
        String code = codeField.getText().trim();
        if (code.isEmpty()) throw new IllegalArgumentException("Mã giảm giá không được để trống.");
        d.setCode(code.toUpperCase(Locale.ROOT));
        d.setDescription(nullable(descriptionField.getText()));
        d.setDiscountType(String.valueOf(typeComboBox.getSelectedItem()));
        d.setDiscountValue(parseMoney(valueField.getText(), "Giá trị giảm", false));
        d.setMaxDiscountAmount(parseMoney(maxAmountField.getText(), "Giảm tối đa", true));
        d.setMinimumAmount(parseMoney(minimumAmountField.getText(), "Đơn tối thiểu", true));
        d.setStartDate(parseDate(startDateField.getText(), "Ngày bắt đầu"));
        d.setEndDate(parseDate(endDateField.getText(), "Ngày kết thúc"));
        d.setActive(activeCheckBox.isSelected());
        return d;
    }

    private void createDiscount() {
        try {
            if (!controller.create(readForm(false))) throw new IllegalStateException("Không thể thêm mã giảm giá.");
            JOptionPane.showMessageDialog(this, "Thêm mã giảm giá thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadDiscounts();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void updateDiscount() {
        try {
            if (!controller.update(readForm(true))) throw new IllegalStateException("Không thể cập nhật mã giảm giá.");
            JOptionPane.showMessageDialog(this, "Cập nhật mã giảm giá thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadDiscounts();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void deleteDiscount() {
        try {
            int id = parseInt(idField.getText(), "ID");
            if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa mã này?", "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            if (!controller.delete(id)) throw new IllegalStateException("Không thể xóa mã giảm giá.");
            JOptionPane.showMessageDialog(this, "Xóa mã giảm giá thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            clearForm(); loadDiscounts();
        } catch (RuntimeException ex) { showError(ex); }
    }

    private void clearForm() {
        idField.setText(""); codeField.setText(""); descriptionField.setText(""); typeComboBox.setSelectedIndex(0);
        valueField.setText(""); maxAmountField.setText("0"); minimumAmountField.setText("0");
        startDateField.setText(""); endDateField.setText(""); activeCheckBox.setSelected(true);
        activeTable.clearSelection(); expiredTable.clearSelection(); codeField.requestFocusInWindow();
    }

    private void configure(JTextField field, String placeholder) {
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.putClientProperty("FlatLaf.style", "arc:9;margin:6,9,6,9;");
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.FONT_SMALL.deriveFont(Font.BOLD));
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setBackground(background); button.setForeground(foreground); button.setFocusable(false);
        button.putClientProperty("FlatLaf.style", "arc:9;focusWidth:0;margin:7,12,7,12;");
        return button;
    }

    private int parseInt(String text, String name) {
        try { int value = Integer.parseInt(text.trim()); if (value <= 0) throw new NumberFormatException(); return value; }
        catch (Exception ex) { throw new IllegalArgumentException(name + " không hợp lệ."); }
    }

    private double parseMoney(String text, String name, boolean allowZero) {
        if (text == null || text.isBlank()) return allowZero ? 0 : invalidMoney(name);
        try {
            double value = Double.parseDouble(text.trim().replace(",", ""));
            if (value < 0 || (!allowZero && value <= 0)) return invalidMoney(name);
            return value;
        } catch (NumberFormatException ex) { return invalidMoney(name); }
    }

    private double invalidMoney(String name) { throw new IllegalArgumentException(name + " không hợp lệ."); }

    private java.util.Date parseDate(String text, String name) {
        if (text == null || text.isBlank()) return null;
        try { return Date.valueOf(LocalDate.parse(text.trim())); }
        catch (DateTimeParseException ex) { throw new IllegalArgumentException(name + " phải có định dạng yyyy-MM-dd."); }
    }

    private String formatDate(java.util.Date date) {
        return date == null ? "" : new Date(date.getTime()).toLocalDate().toString();
    }

    private String formatNumber(double value) { return String.format(Locale.US, "%.0f", value); }
    private String nullable(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String safe(String value) { return value == null ? "" : value; }

    private void showError(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) root = root.getCause();
        JOptionPane.showMessageDialog(this, root.getMessage() == null ? "Không xác định" : root.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}

package view;

import model.ClassRoom;
import net.miginfocom.swing.MigLayout;
import service.ClassService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ClassManagementView extends JPanel {
    private JTextField txtClassName, txtMaxStudents, txtTeacherId;
    private JButton btnSave, btnRefresh;
    private JTable classTable;
    private DefaultTableModel tableModel;
    private final ClassService classService = new ClassService();

    public ClassManagementView() {
        setLayout(new MigLayout("fill, insets 15", "[320pt][grow]", "[][grow]"));

        JLabel lblTitle = new JLabel("QUẢN LÝ LỚP HỌC");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblTitle, "span 2, wrap 15");

        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin lớp học"));

        formPanel.add(new JLabel("Tên Lớp:"));
        txtClassName = new JTextField();
        formPanel.add(txtClassName);

        formPanel.add(new JLabel("Mã Giảng Viên:"));
        txtTeacherId = new JTextField();
        formPanel.add(txtTeacherId);

        formPanel.add(new JLabel("Sĩ số tối đa:"));
        txtMaxStudents = new JTextField("30");
        formPanel.add(txtMaxStudents);

        btnSave = new JButton("Lưu Thông Tin");
        btnSave.putClientProperty("JButton.buttonType", "accent");
        btnRefresh = new JButton("Làm Mới");

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        buttonPanel.add(btnSave, "growx");
        buttonPanel.add(btnRefresh, "growx");

        formPanel.add(buttonPanel, "span 2, growx, gaptop 15");
        add(formPanel, "top");

        JPanel tablePanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách lớp học"));

        tableModel = new DefaultTableModel(new String[]{"ID", "Tên Lớp", "Mã GV", "Phòng Học", "Sĩ Số Tối Đa"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        classTable = new JTable(tableModel);
        classTable.setRowHeight(25);
        tablePanel.add(new JScrollPane(classTable), "grow");

        add(tablePanel, "grow");

        btnSave.addActionListener(e -> handleSave());
        btnRefresh.addActionListener(e -> loadData());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<ClassRoom> list = classService.getAllClasses();
        if (list != null) {
            for (ClassRoom c : list) {
                tableModel.addRow(new Object[]{
                        c.getClassId(),
                        c.getClassName(),
                        c.getTeacherId(),
                        c.getRoom(),
                        c.getMaxStudents()
                });
            }
        }
    }

    private void handleSave() {
        try {
            String name = txtClassName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên lớp không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ClassRoom c = new ClassRoom();
            c.setClassName(name);

            if (!txtTeacherId.getText().trim().isEmpty()) {
                c.setTeacherId(Integer.parseInt(txtTeacherId.getText().trim()));
            }
            if (!txtMaxStudents.getText().trim().isEmpty()) {
                c.setMaxStudents(Integer.parseInt(txtMaxStudents.getText().trim()));
            }

            if (classService.createClass(c)) {
                JOptionPane.showMessageDialog(this, "Thêm lớp học thành công!");
                loadData();
                txtClassName.setText("");
                txtTeacherId.setText("");
                txtMaxStudents.setText("30");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm lớp học thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã GV và Sĩ số phải là số nguyên!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
}
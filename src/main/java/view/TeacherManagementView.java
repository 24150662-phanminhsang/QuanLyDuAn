package view;

import model.Teacher;
import net.miginfocom.swing.MigLayout;
import service.TeacherService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherManagementView extends JPanel {
    private JTextField txtTeacherId;
    private JTextField txtName;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JButton btnSave;
    private JButton btnRefresh;
    private JTable teacherTable;
    private DefaultTableModel tableModel;

    private final TeacherService teacherService = new TeacherService();

    public TeacherManagementView() {
        setLayout(new MigLayout("fill, insets 15", "[320pt][grow]", "[][grow]"));

        // Tiêu đề
        JLabel lblTitle = new JLabel("QUẢN LÝ GIẢNG VIÊN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblTitle, "span 2, wrap 15");

        // Form nhập liệu
        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin giảng viên"));

        formPanel.add(new JLabel("Mã GV:"));
        txtTeacherId = new JTextField();
        txtTeacherId.setEditable(false); // Mã tự tăng hoặc để trống khi thêm mới
        txtTeacherId.setToolTipText("Tự động sinh khi thêm mới");
        formPanel.add(txtTeacherId);

        formPanel.add(new JLabel("Họ và Tên:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Số Điện Thoại:"));
        txtPhone = new JTextField();
        formPanel.add(txtPhone);

        btnSave = new JButton("Lưu Thông Tin");
        btnSave.putClientProperty("JButton.buttonType", "accent");
        btnRefresh = new JButton("Làm Mới");

        JPanel buttonPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        buttonPanel.add(btnSave, "growx");
        buttonPanel.add(btnRefresh, "growx");

        formPanel.add(buttonPanel, "span 2, growx, gaptop 15");
        add(formPanel, "top");

        // Bảng dữ liệu
        JPanel tablePanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[grow]"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách giảng viên"));

        tableModel = new DefaultTableModel(new String[]{"ID", "Họ Tên", "Email", "Số Điện Thoại"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        teacherTable = new JTable(tableModel);
        teacherTable.setRowHeight(25);
        tablePanel.add(new JScrollPane(teacherTable), "grow");

        add(tablePanel, "grow");

        // Bắt sự kiện
        btnSave.addActionListener(e -> handleSaveTeacher());
        btnRefresh.addActionListener(e -> clearFormAndReload());

        loadTeacherData();
    }

    private void loadTeacherData() {
        tableModel.setRowCount(0);
        List<Teacher> list = teacherService.getAllTeachers();
        for (Teacher t : list) {
            tableModel.addRow(new Object[]{t.getTeacherId(), t.getName(), t.getEmail(), t.getPhone()});
        }
    }

    private void handleSaveTeacher() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên giảng viên không được để trống!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Teacher teacher = new Teacher();
        // 1. Các thuộc tính cho View
        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setPhone(phone);

        // 2. BỔ SUNG: Các thuộc tính bắt buộc cho Database (DAO)
        teacher.setUserId(1); // Giả định ID user kết nối (hoặc ID user hợp lệ trong bảng USERS của bạn)
        teacher.setTeacherCode("GV" + System.currentTimeMillis() % 10000); // Mã GV tự sinh (VD: GV1234)
        teacher.setSpecialization("CNTT"); // Chuyên môn mặc định

        boolean success = teacherService.addTeacher(teacher);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm giảng viên thành công!");
            clearFormAndReload();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm giảng viên thất bại! Kiểm tra lại Console để xem chi tiết lỗi SQL.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFormAndReload() {
        txtTeacherId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        loadTeacherData();
    }
}
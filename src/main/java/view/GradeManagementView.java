package view;

import model.Grade;
import net.miginfocom.swing.MigLayout;
import service.GradeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GradeManagementView extends JPanel {
    private JTextField txtStudentId;
    private JTextField txtClassId;
    private JTextField txtScore;
    private JButton btnSave;
    private JButton btnFilter;
    private JTable gradeTable;
    private DefaultTableModel tableModel;

    private final GradeService gradeService = new GradeService();

    public GradeManagementView() {
        // Cấu hình Bố cục MigLayout: Cột 1 rộng 320pt (Form), Cột 2 giãn hết cỡ (Bảng)
        setLayout(new MigLayout("fill, insets 15", "[320pt][grow]", "[][grow]"));

        // 1. Tiêu đề Màn hình
        JLabel lblTitle = new JLabel("QUẢN LÝ VÀ NHẬP ĐIỂM HỌC VIÊN");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(lblTitle, "span 2, wrap 15");

        // 2. Form Nhập Điểm (Bên trái)
        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]", "[]10[]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhập điểm"));

        formPanel.add(new JLabel("Mã Học Viên:"));
        txtStudentId = new JTextField();
        formPanel.add(txtStudentId);

        formPanel.add(new JLabel("Mã Lớp Học:"));
        txtClassId = new JTextField();
        formPanel.add(txtClassId);

        formPanel.add(new JLabel("Điểm Số (0 - 10):"));
        txtScore = new JTextField();
        formPanel.add(txtScore);

        btnSave = new JButton("Lưu Điểm");
        btnSave.putClientProperty("JButton.buttonType", "accent"); // Style nút FlatLaf
        formPanel.add(btnSave, "span 2, growx, gaptop 15");

        add(formPanel, "top"); // Đặt Form ở cột 1, canh trên

        // 3. Bảng Hiển Thị Điểm (Bên phải)
        JPanel tablePanel = new JPanel(new MigLayout("fill, insets 0", "[grow]", "[][grow]"));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách điểm theo lớp"));

        // Thanh lọc dữ liệu theo mã lớp
        JPanel filterPanel = new JPanel(new MigLayout("insets 0", "[][grow][]"));
        filterPanel.add(new JLabel("Xem điểm Lớp (ID):"));
        JTextField txtFilterClassId = new JTextField();
        filterPanel.add(txtFilterClassId, "growx");
        btnFilter = new JButton("Tải danh sách");
        filterPanel.add(btnFilter);
        tablePanel.add(filterPanel, "wrap 10, growx");

        // Cấu hình Bảng JTable
        tableModel = new DefaultTableModel(new String[]{"STT", "Mã Học Viên", "Mã Lớp", "Điểm Số"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên ô
            }
        };
        gradeTable = new JTable(tableModel);
        gradeTable.setRowHeight(25);
        tablePanel.add(new JScrollPane(gradeTable), "grow");

        add(tablePanel, "grow"); // Đặt Bảng ở cột 2, tự động giãn tràn màn hình

        // 4. Bắt sự kiện Nút bấm
        btnSave.addActionListener(e -> handleSaveGrade());
        btnFilter.addActionListener(e -> {
            try {
                int classId = Integer.parseInt(txtFilterClassId.getText().trim());
                loadGradesByClass(classId);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Lớp hợp lệ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    private void handleSaveGrade() {
        try {
            int studentId = Integer.parseInt(txtStudentId.getText().trim());
            int classId = Integer.parseInt(txtClassId.getText().trim());
            double score = Double.parseDouble(txtScore.getText().trim());

            if (score < 0 || score > 10) {
                JOptionPane.showMessageDialog(this, "Điểm phải nằm trong khoảng từ 0.0 đến 10.0!", "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean success = gradeService.saveOrUpdateGrade(studentId, classId, score);
            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu điểm thành công!");
                loadGradesByClass(classId); // Tải lại bảng sau khi lưu
                txtStudentId.setText("");
                txtScore.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Lưu điểm thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi nhập liệu", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadGradesByClass(int classId) {

        tableModel.setRowCount(0);

        if (classId <= 0) {
            return;
        }

        List<Grade> list =
                gradeService.getGradesByClass(
                        classId
                );

        if (list == null) {
            return;
        }

        int stt = 1;

        for (Grade g : list) {

            tableModel.addRow(
                    new Object[]{
                            stt++,
                            g.getStudentId(),
                            g.getClassId(),
                            g.getScore()
                    }
            );
        }
    }
}
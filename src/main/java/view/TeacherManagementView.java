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
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    private JTable teacherTable;
    private DefaultTableModel tableModel;

    private final TeacherService teacherService =
            new TeacherService();

    public TeacherManagementView() {

        setLayout(new MigLayout(
                "fill, insets 15",
                "[320pt][grow]",
                "[][grow]"
        ));

        JLabel lblTitle =
                new JLabel("QUẢN LÝ GIẢNG VIÊN");

        lblTitle.setFont(
                new Font(
                        "SansSerif",
                        Font.BOLD,
                        20
                )
        );

        add(
                lblTitle,
                "span 2, wrap 15"
        );

        // =========================
        // FORM
        // =========================

        JPanel formPanel =
                new JPanel(
                        new MigLayout(
                                "wrap 2",
                                "[right][grow, fill]",
                                "[]10[]"
                        )
                );

        formPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Thông tin giảng viên"
                )
        );

        // ID
        formPanel.add(
                new JLabel("Mã GV:")
        );

        txtTeacherId =
                new JTextField();

        txtTeacherId.setEditable(false);

        formPanel.add(
                txtTeacherId
        );

        // NAME
        formPanel.add(
                new JLabel("Họ và Tên:")
        );

        txtName =
                new JTextField();

        formPanel.add(
                txtName
        );

        // EMAIL
        formPanel.add(
                new JLabel("Email:")
        );

        txtEmail =
                new JTextField();

        formPanel.add(
                txtEmail
        );

        // PHONE
        formPanel.add(
                new JLabel("Số Điện Thoại:")
        );

        txtPhone =
                new JTextField();

        formPanel.add(
                txtPhone
        );

        // BUTTONS
        btnSave =
                new JButton("Thêm");

        btnUpdate =
                new JButton("Cập Nhật");

        btnDelete =
                new JButton("Xóa");

        btnRefresh =
                new JButton("Làm Mới");

        JPanel buttonPanel =
                new JPanel(
                        new MigLayout(
                                "insets 0",
                                "[grow][grow]",
                                "[]5[]"
                        )
                );

        buttonPanel.add(
                btnSave,
                "growx"
        );

        buttonPanel.add(
                btnUpdate,
                "growx"
        );

        buttonPanel.add(
                btnDelete,
                "growx"
        );

        buttonPanel.add(
                btnRefresh,
                "growx"
        );

        formPanel.add(
                buttonPanel,
                "span 2, growx, gaptop 15"
        );

        add(
                formPanel,
                "top"
        );

        // =========================
        // TABLE
        // =========================

        JPanel tablePanel =
                new JPanel(
                        new MigLayout(
                                "fill, insets 0",
                                "[grow]",
                                "[grow]"
                        )
                );

        tablePanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Danh sách giảng viên"
                )
        );

        tableModel =
                new DefaultTableModel(
                        new String[]{
                                "ID",
                                "Họ Tên",
                                "Email",
                                "Số Điện Thoại"
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

        teacherTable =
                new JTable(
                        tableModel
                );

        teacherTable.setRowHeight(
                25
        );

        tablePanel.add(
                new JScrollPane(
                        teacherTable
                ),
                "grow"
        );

        add(
                tablePanel,
                "grow"
        );

        // =========================
        // EVENTS
        // =========================

        btnSave.addActionListener(
                e -> handleSaveTeacher()
        );

        btnUpdate.addActionListener(
                e -> handleUpdateTeacher()
        );

        btnDelete.addActionListener(
                e -> handleDeleteTeacher()
        );

        btnRefresh.addActionListener(
                e -> clearFormAndReload()
        );

        teacherTable
                .getSelectionModel()
                .addListSelectionListener(
                        e -> handleTableSelection()
                );

        // =========================
        // LOAD DATA
        // =========================

        loadTeacherData();
    }

    // =========================
    // LOAD DATA
    // =========================

    private void loadTeacherData() {

        tableModel.setRowCount(0);

        List<Teacher> list =
                teacherService.getAllTeachers();

        if (list == null) {
            return;
        }

        for (Teacher teacher : list) {

            tableModel.addRow(
                    new Object[]{
                            teacher.getTeacherId(),
                            teacher.getName(),
                            teacher.getEmail(),
                            teacher.getPhone()
                    }
            );
        }
    }

    // =========================
    // SELECT TABLE
    // =========================

    private void handleTableSelection() {

        int selectedRow =
                teacherTable.getSelectedRow();

        if (selectedRow < 0) {
            return;
        }

        txtTeacherId.setText(
                String.valueOf(
                        tableModel.getValueAt(
                                selectedRow,
                                0
                        )
                )
        );

        txtName.setText(
                String.valueOf(
                        tableModel.getValueAt(
                                selectedRow,
                                1
                        )
                )
        );

        txtEmail.setText(
                String.valueOf(
                        tableModel.getValueAt(
                                selectedRow,
                                2
                        )
                )
        );

        txtPhone.setText(
                String.valueOf(
                        tableModel.getValueAt(
                                selectedRow,
                                3
                        )
                )
        );
    }

    // =========================
    // ADD
    // =========================

    private void handleSaveTeacher() {

        String name =
                txtName.getText().trim();

        String email =
                txtEmail.getText().trim();

        String phone =
                txtPhone.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tên giảng viên không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        Teacher teacher =
                new Teacher();

        teacher.setName(name);
        teacher.setEmail(email);
        teacher.setPhone(phone);

        /*
         * Lưu ý:
         * Không nên cố định userId = 1.
         *
         * Nếu database yêu cầu userId,
         * cần lấy userId thực tế từ tài khoản
         * hoặc tạo User trước rồi mới tạo Teacher.
         */

        teacher.setTeacherCode(
                "GV"
                        + System.currentTimeMillis()
                        % 100000
        );

        teacher.setSpecialization(
                "CNTT"
        );

        teacher.setStatus(
                "ACTIVE"
        );

        boolean success =
                teacherService.addTeacher(
                        teacher
                );

        if (success) {

            JOptionPane.showMessageDialog(
                    this,
                    "Thêm giảng viên thành công!"
            );

            clearFormAndReload();

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Thêm giảng viên thất bại!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE
    // =========================

    private void handleUpdateTeacher() {

        if (txtTeacherId
                .getText()
                .trim()
                .isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn giảng viên cần cập nhật!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String name =
                txtName.getText().trim();

        if (name.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Tên giảng viên không được để trống!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            int teacherId =
                    Integer.parseInt(
                            txtTeacherId
                                    .getText()
                                    .trim()
                    );

            Teacher teacher =
                    teacherService
                            .getTeacherById(
                                    teacherId
                            );

            if (teacher == null) {

                JOptionPane.showMessageDialog(
                        this,
                        "Không tìm thấy giảng viên!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            teacher.setName(name);

            teacher.setEmail(
                    txtEmail
                            .getText()
                            .trim()
            );

            teacher.setPhone(
                    txtPhone
                            .getText()
                            .trim()
            );

            boolean success =
                    teacherService
                            .updateTeacher(
                                    teacher
                            );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cập nhật giảng viên thành công!"
                );

                clearFormAndReload();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Cập nhật thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "ID giảng viên không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DELETE
    // =========================

    private void handleDeleteTeacher() {

        if (txtTeacherId
                .getText()
                .trim()
                .isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Vui lòng chọn giảng viên cần xóa!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int confirm =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn xóa giảng viên này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION
                );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            int teacherId =
                    Integer.parseInt(
                            txtTeacherId
                                    .getText()
                                    .trim()
                    );

            boolean success =
                    teacherService
                            .deleteTeacher(
                                    teacherId
                            );

            if (success) {

                JOptionPane.showMessageDialog(
                        this,
                        "Xóa giảng viên thành công!"
                );

                clearFormAndReload();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Xóa giảng viên thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "ID giảng viên không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // CLEAR
    // =========================

    private void clearFormAndReload() {

        txtTeacherId.setText("");
        txtName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");

        teacherTable.clearSelection();

        loadTeacherData();
    }
}
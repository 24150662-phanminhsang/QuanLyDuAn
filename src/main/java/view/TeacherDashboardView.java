package view;

import controller.ClassController;
import model.ClassRoom;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TeacherDashboardView extends JPanel {
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
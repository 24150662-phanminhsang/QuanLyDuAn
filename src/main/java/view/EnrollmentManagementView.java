package view;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * EnrollmentManagementView
 */
public class EnrollmentManagementView extends JFrame{
    private JTable tableEnrollment;
    private DefaultTableModel tableModel;

    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    public EnrollmentManagementView() {

        setTitle("Quản lý đăng ký học");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        topPanel.add(btnAdd);
        topPanel.add(btnUpdate);
        topPanel.add(btnDelete);
        topPanel.add(btnRefresh);

        String[] columns = {
                "Mã đăng ký",
                "Mã học viên",
                "Mã khóa học",
                "Ngày đăng ký"
        };

        tableModel = new DefaultTableModel(columns, 0);
        tableEnrollment = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(tableEnrollment);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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

}

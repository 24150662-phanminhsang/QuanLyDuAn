package view;
import javax.swing.*;
import java.awt.*;

/**
 * CourseManagementView
 */
public class CourseManagementView extends JFrame {
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnRefresh;

    private JTable tableCourse;

    public CourseManagementView() {
        setTitle("Quản lý khóa học");
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
                "Mã khóa học",
                "Tên khóa học",
                "Giảng viên",
                "Học phí"
        };

        tableCourse = new JTable(
                new javax.swing.table.DefaultTableModel(
                        new Object[][]{},
                        columns
                )
        );

        JScrollPane scrollPane = new JScrollPane(tableCourse);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getTableCourse() {
        return tableCourse;
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

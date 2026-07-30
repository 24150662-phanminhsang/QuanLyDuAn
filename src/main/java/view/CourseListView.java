package view;
import controller.CourseController;
import model.Course;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * CourseListView
 */
public class CourseListView extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private CourseController controller;

    public CourseListView() {

        controller = new CourseController();

        setTitle("Course List");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel lblTitle = new JLabel("COURSE LIST");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        model = new DefaultTableModel();

        model.addColumn("Course ID");
        model.addColumn("Course Name");
        model.addColumn("Fee");
        model.addColumn("Duration");

        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnRefresh = new JButton("Refresh");
        JButton btnClose = new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);

        add(lblTitle, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadCourseData();

        btnRefresh.addActionListener(e -> loadCourseData());

        btnClose.addActionListener(e -> dispose());

        setVisible(true);
    }

    private void loadCourseData() {

        model.setRowCount(0);

        List<Course> list = controller.getAllCourses();

        for (Course c : list) {

            model.addRow(new Object[]{
                    c.getCourseID(),
                    c.getCourseName(),
                    c.getFee(),
                    c.getDuration()
            });

        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new CourseListView());

    }

}

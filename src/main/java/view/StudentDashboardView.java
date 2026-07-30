package view;

import javax.swing.*;
import java.awt.*;
/**
 * StudentDashboardView
 */
public class StudentDashboardView extends JFrame {
    public StudentDashboardView() {

        setTitle("Student Dashboard");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel lblTitle = new JLabel("STUDENT DASHBOARD");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);

        JButton btnProfile = new JButton("Thông tin cá nhân");
        JButton btnCourse = new JButton("Danh sách khóa học");
        JButton btnEnrollment = new JButton("Lớp đã đăng ký");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4,1,10,10));

        panel.add(lblTitle);
        panel.add(btnProfile);
        panel.add(btnCourse);
        panel.add(btnEnrollment);

        add(panel);

        setVisible(true);
    }

}

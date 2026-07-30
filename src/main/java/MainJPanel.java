import com.formdev.flatlaf.FlatLightLaf;
import view.GradeManagementView;
import view.TeacherManagementView;

import javax.swing.*;
import java.awt.*;

public class MainJPanel extends JPanel {

    public MainJPanel() {

        // Sử dụng JTabbedPane làm menu chuyển màn hình
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Thêm các màn hình Quản lý vào Tab
        tabbedPane.addTab("Quản Lý Điểm", new GradeManagementView());
        tabbedPane.addTab("Quản Lý Giảng Viên", new TeacherManagementView());

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Kích hoạt Giao diện FlatLaf
        try {
            FlatLightLaf.setup();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy ứng dụng trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("CourseManager");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setSize(1400, 800);
            window.setLocationRelativeTo(null);

            // Bọc MainJPanel vào trong cửa sổ JFrame
            window.add(new MainJPanel());

            window.setVisible(true);
        });
    }
}
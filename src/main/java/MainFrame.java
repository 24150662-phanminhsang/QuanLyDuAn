import com.formdev.flatlaf.FlatLightLaf;
import view.GradeManagementView;
import view.TeacherManagementView;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("HỆ THỐNG QUẢN LÝ DỰ ÁN / HỌC VIỆN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(null); // Hiển thị giữa màn hình

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
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
import com.formdev.flatlaf.FlatLightLaf;
import view.TeacherMainDashboard;

import javax.swing.*;

public class TestTeacherManagement {
    public static void main(String[] args) {
        // 1. Kích hoạt giao diện FlatLaf
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Teacher Management - Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1300, 750);
            frame.setLocationRelativeTo(null);

            // 2. Gọi Dashboard tổng (chứa Sidebar + Header)
            TeacherMainDashboard dashboard = new TeacherMainDashboard();

            // 3. Chuyển ngay sang Card/Màn hình "Quản lý giảng viên"
            dashboard.showCard("TEACHER_MANAGEMENT");

            frame.add(dashboard);
            frame.setVisible(true);
        });
    }
}
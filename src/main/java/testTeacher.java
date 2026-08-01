import com.formdev.flatlaf.FlatLightLaf;
import view.TeacherMainDashboard; // Import đúng lớp Main Dashboard

import javax.swing.*;

public class testTeacher {
    public static void main(String[] args) {
        // 1. Kích hoạt giao diện FlatLaf trước khi tạo JFrame
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("CourseManager - Teacher Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);

            // 2. CHÚ Ý: Phải 'new TeacherMainDashboard()' (chứa Sidebar + Header)
            // Thay vì 'new TeacherDashboardView()' (chỉ chứa bảng)
            TeacherMainDashboard mainDashboard = new TeacherMainDashboard();
            frame.add(mainDashboard);

            frame.setVisible(true);
        });
    }
}
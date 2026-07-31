
import view.TeacherDashboardView;

import javax.swing.*;

public class testTeacher {
    public static void main(String[] args) {
        // Đảm bảo giao diện khởi chạy trên luồng sự kiện của Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Kiểm tra - Teacher Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1100, 650);
            frame.setLocationRelativeTo(null);

            // Gắn TeacherDashboardView làm nội dung chính của cửa sổ
            frame.setContentPane(new TeacherDashboardView());

            frame.setVisible(true);
        });
    }
}
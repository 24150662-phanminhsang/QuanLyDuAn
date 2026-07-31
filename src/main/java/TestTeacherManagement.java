import com.formdev.flatlaf.FlatLightLaf;
import view.TeacherManagementView;
import javax.swing.JOptionPane;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestTeacherManagement {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());

                JFrame frame = new JFrame("Test Teacher Management");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(1400, 800);
                frame.setLocationRelativeTo(null);

                TeacherManagementView teacherManagementView =
                        new TeacherManagementView();

                frame.setContentPane(teacherManagementView);
                frame.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();

                JOptionPane.showMessageDialog(
                        null,
                        "Không thể mở TeacherManagementView:\n"
                                + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
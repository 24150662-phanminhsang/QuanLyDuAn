import com.formdev.flatlaf.FlatLightLaf;
import view.StudentDashboardView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Dimension;

public class TestStudent {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        new FlatLightLaf()
                );

                JFrame frame = new JFrame(
                        "Student Dashboard"
                );

                StudentDashboardView studentDashboard =
                        new StudentDashboardView();

                frame.setContentPane(
                        studentDashboard
                );

                frame.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE
                );

                frame.setMinimumSize(
                        new Dimension(1000, 650)
                );

                frame.setSize(
                        1280,
                        760
                );

                frame.setLocationRelativeTo(null);
                frame.setVisible(true);

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }
}
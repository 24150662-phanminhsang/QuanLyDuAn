import view.StudentDashboardView;

import javax.swing.SwingUtilities;

public class TestStudent {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            StudentDashboardView dashboard = new StudentDashboardView();
            dashboard.setVisible(true);

        });

    }

}
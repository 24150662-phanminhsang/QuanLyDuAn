import javax.swing.SwingUtilities;
import view.AdminDashboardView;

public class TestAdminDashboard {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {

                AdminDashboardView admin = new AdminDashboardView();
                admin.setVisible(true);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }
}
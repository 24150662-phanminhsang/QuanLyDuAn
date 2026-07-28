import util.UITheme;
import view.AdminDashboardView;

import javax.swing.SwingUtilities;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UITheme.setup();

            AdminDashboardView dashboard =
                    new AdminDashboardView();

            dashboard.setVisible(true);
        });
    }
}
import com.formdev.flatlaf.FlatLightLaf;
import controller.LandingController;
import view.LandingPageView;
import view.LoginView;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            try {
                FlatLightLaf.setup();

                UIManager.put(
                        "Button.arc",
                        10
                );

                UIManager.put(
                        "Component.arc",
                        10
                );

                UIManager.put(
                        "TextComponent.arc",
                        10
                );

            } catch (Exception exception) {
                System.err.println(
                        "Không thể thiết lập giao diện: "
                                + exception.getMessage()
                );
            }

            final LandingPageView[] landingReference =
                    new LandingPageView[1];

            LandingPageView landingPage =
                    new LandingPageView(() -> {

                        LoginView loginView =
                                new LoginView();

                        loginView.setDefaultCloseOperation(
                                JFrame.DISPOSE_ON_CLOSE
                        );

                        loginView.addWindowListener(
                                new java.awt.event.WindowAdapter() {

                                    @Override
                                    public void windowClosed(
                                            java.awt.event.WindowEvent event
                                    ) {
                                        landingReference[0]
                                                .setVisible(true);
                                    }
                                }
                        );

                        loginView.setVisible(true);
                    });

            landingReference[0] = landingPage;

            LandingController landingController =
                    new LandingController(
                            landingPage
                    );

            landingPage.setVisible(true);

            landingController.loadFeaturedCourses();
        });
    }
}
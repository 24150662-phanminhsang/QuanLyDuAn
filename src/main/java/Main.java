import com.formdev.flatlaf.FlatLightLaf;
import controller.LandingController;
import controller.LoginController;
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

                        /*
                         * Quan trọng:
                         * Gắn LoginController để nút Đăng nhập
                         * và nút Thoát hoạt động.
                         */
                        new LoginController(loginView);

                        loginView.setDefaultCloseOperation(
                                JFrame.DISPOSE_ON_CLOSE
                        );

                        /*
                         * Ẩn Landing Page khi mở Login.
                         */
                        landingReference[0].setVisible(false);

                        loginView.addWindowListener(
                                new java.awt.event.WindowAdapter() {

                                    @Override
                                    public void windowClosed(
                                            java.awt.event.WindowEvent event
                                    ) {
                                        /*
                                         * Khi đóng Login thì mở lại Landing Page.
                                         */
                                        landingReference[0]
                                                .setVisible(true);
                                    }
                                }
                        );

                        loginView.setLocationRelativeTo(
                                landingReference[0]
                        );

                        loginView.setVisible(true);
                    });

            landingReference[0] = landingPage;

            LandingController landingController =
                    new LandingController(
                            landingPage
                    );

            landingPage.setLocationRelativeTo(null);
            landingPage.setVisible(true);

            landingController.loadFeaturedCourses();
        });
    }
}
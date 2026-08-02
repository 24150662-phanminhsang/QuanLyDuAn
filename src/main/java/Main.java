import com.formdev.flatlaf.FlatLightLaf;
import controller.LandingController;
import controller.LoginController;
import controller.RegistrationController;
import view.LandingPageView;
import view.LoginView;
import view.RegisterView;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            setupLookAndFeel();

            final LandingPageView[] landingReference =
                    new LandingPageView[1];

            LandingPageView landingPage =
                    new LandingPageView(

                            // Mở màn hình đăng nhập
                            () -> openLoginView(
                                    landingReference[0]
                            ),

                            // Mở màn hình đăng ký
                            () -> openRegisterView(
                                    landingReference[0]
                            )
                    );

            landingReference[0] = landingPage;

            LandingController landingController =
                    new LandingController(landingPage);

            landingPage.setLocationRelativeTo(null);
            landingPage.setVisible(true);

            landingController.loadFeaturedCourses();
        });
    }

    /**
     * Thiết lập giao diện FlatLaf cho toàn bộ chương trình.
     */
    private static void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();

            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);

        } catch (Exception exception) {
            System.err.println(
                    "Không thể thiết lập giao diện: "
                            + exception.getMessage()
            );
        }
    }

    /**
     * Mở màn hình đăng nhập.
     */
    private static void openLoginView(
            LandingPageView landingPage
    ) {
        LoginView loginView = new LoginView();

        new LoginController(loginView);

        hideLandingPage(landingPage);

        loginView.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        loginView.addWindowListener(
                createReturnToLandingListener(landingPage)
        );

        loginView.setLocationRelativeTo(landingPage);
        loginView.setVisible(true);
    }

    /**
     * Mở màn hình đăng ký.
     */
    private static void openRegisterView(
            LandingPageView landingPage
    ) {
        RegisterView registerView = new RegisterView();

        // Tên class phải giống với import:
        // controller.RegistrationController
        new RegistrationController(registerView);

        hideLandingPage(landingPage);

        registerView.setDefaultCloseOperation(
                WindowConstants.DISPOSE_ON_CLOSE
        );

        registerView.addWindowListener(
                createReturnToLandingListener(landingPage)
        );

        registerView.setLocationRelativeTo(landingPage);
        registerView.setVisible(true);
    }

    /**
     * Ẩn trang giới thiệu khi mở đăng nhập hoặc đăng ký.
     */
    private static void hideLandingPage(
            LandingPageView landingPage
    ) {
        if (landingPage != null) {
            landingPage.setVisible(false);
        }
    }

    /**
     * Hiện lại LandingPage khi cửa sổ đăng nhập hoặc đăng ký đóng.
     */
    private static WindowAdapter createReturnToLandingListener(
            LandingPageView landingPage
    ) {
        return new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent event) {
                if (landingPage != null
                        && landingPage.isDisplayable()) {

                    landingPage.setVisible(true);
                    landingPage.toFront();
                    landingPage.requestFocus();
                }
            }
        };
    }
}
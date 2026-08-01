import com.formdev.flatlaf.FlatLightLaf;
import controller.RegistrationController;
import view.RegisterView;

import javax.swing.*;

public class TestRegister {

    public static void main(String[] args) {
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            RegisterView view =
                    new RegisterView();

            new RegistrationController(view);

            view.setVisible(true);
        });
    }
}
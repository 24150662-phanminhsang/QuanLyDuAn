package view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JFrame {

    public LoginView() {

        initializeFrame();
        initializeUI();

    }

    private void initializeFrame() {

        setTitle("Đăng nhập");

        setSize(1000,650);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    private void initializeUI() {

        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(
                "LOGIN PAGE",
                SwingConstants.CENTER
        );

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        32
                )
        );

        panel.add(
                label,
                BorderLayout.CENTER
        );

        setContentPane(panel);

    }

}
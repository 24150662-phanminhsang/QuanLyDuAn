package view;

import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;

    private JButton btnLogin;
    private JButton btnExit;
    private JButton btnShowPassword;

    private JCheckBox chkRemember;

    private boolean passwordVisible;
    private char defaultEchoChar;

    public LoginView() {
        initialize();
        initializeShortcuts();
    }

    private void initialize() {
        setTitle("Course Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(
                new MigLayout(
                        "fillx, insets 35 45 35 45",
                        "[grow,fill]",
                        "[]15[]30[]12[]18[]12[]15[]25[]"
                )
        );

        mainPanel.setBackground(new Color(245, 247, 250));
        setContentPane(mainPanel);

        createHeader(mainPanel);
        createForm(mainPanel);
        createButtons(mainPanel);

        /*
         * pack() tự tính kích thước dựa trên nội dung,
         * tránh việc các nút phía dưới bị cắt.
         */
        pack();

        setSize(720, 760);
        setMinimumSize(new Dimension(720, 760));
        setLocationRelativeTo(null);
    }

    private void createHeader(JPanel panel) {
        JLabel lblIcon = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.GRADUATION_CAP,
                        80,
                        new Color(52, 152, 219)
                )
        );

        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblTitle = new JLabel(
                "Course Management System"
        );

        lblTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        36
                )
        );

        lblTitle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        JLabel lblSubtitle = new JLabel(
                "Sign in to continue"
        );

        lblSubtitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        20
                )
        );

        lblSubtitle.setForeground(
                new Color(120, 120, 120)
        );

        lblSubtitle.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        panel.add(
                lblIcon,
                "growx, h 100!, wrap"
        );

        panel.add(
                lblTitle,
                "growx, wrap"
        );

        panel.add(
                lblSubtitle,
                "growx, wrap"
        );
    }

    private void createForm(JPanel panel) {
        JLabel lblUsername = createFormLabel(
                "Tên đăng nhập"
        );

        txtUsername = new JTextField();

        txtUsername.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        16
                )
        );

        txtUsername.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Nhập tên đăng nhập"
        );

        txtUsername.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16;"
                        + "borderWidth:1;"
                        + "focusWidth:1;"
                        + "margin:8,10,8,10"
        );

        JLabel lblPassword = createFormLabel(
                "Mật khẩu"
        );

        txtPassword = new JPasswordField();

        txtPassword.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        16
                )
        );

        txtPassword.putClientProperty(
                FlatClientProperties.PLACEHOLDER_TEXT,
                "Nhập mật khẩu"
        );

        txtPassword.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16;"
                        + "borderWidth:1;"
                        + "focusWidth:1;"
                        + "margin:8,10,8,10"
        );

        defaultEchoChar = txtPassword.getEchoChar();

        btnShowPassword = new JButton(
                FontIcon.of(
                        FontAwesomeSolid.EYE,
                        20,
                        new Color(40, 40, 40)
                )
        );

        btnShowPassword.setToolTipText(
                "Hiện mật khẩu"
        );

        btnShowPassword.setFocusable(false);
        btnShowPassword.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btnShowPassword.putClientProperty(
                FlatClientProperties.BUTTON_TYPE,
                FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON
        );

        btnShowPassword.addActionListener(
                e -> togglePasswordVisibility()
        );

        JPanel passwordPanel = new JPanel(
                new MigLayout(
                        "insets 0, fillx",
                        "[grow,fill]10[]",
                        "[50!]"
                )
        );

        passwordPanel.setOpaque(false);

        passwordPanel.add(
                txtPassword,
                "grow, h 50!"
        );

        passwordPanel.add(
                btnShowPassword,
                "w 42!, h 42!"
        );

        chkRemember = new JCheckBox(
                "Ghi nhớ đăng nhập"
        );

        chkRemember.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        15
                )
        );

        chkRemember.setOpaque(false);
        chkRemember.setFocusable(false);

        panel.add(
                lblUsername,
                "wrap"
        );

        panel.add(
                txtUsername,
                "growx, h 50!, wrap"
        );

        panel.add(
                lblPassword,
                "wrap"
        );

        panel.add(
                passwordPanel,
                "growx, wrap"
        );

        panel.add(
                chkRemember,
                "wrap"
        );
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        17
                )
        );

        label.setForeground(
                new Color(25, 25, 25)
        );

        return label;
    }

    private void createButtons(JPanel panel) {
        btnLogin = new JButton(
                "Đăng nhập",
                FontIcon.of(
                        FontAwesomeSolid.SIGN_IN_ALT,
                        18,
                        Color.WHITE
                )
        );

        btnExit = new JButton(
                "Thoát",
                FontIcon.of(
                        FontAwesomeSolid.TIMES,
                        18,
                        new Color(55, 65, 81)
                )
        );

        btnLogin.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        btnExit.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        16
                )
        );

        btnLogin.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btnExit.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        btnLogin.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16;"
                        + "background:#2563EB;"
                        + "foreground:#FFFFFF;"
                        + "borderWidth:0;"
                        + "focusWidth:1"
        );

        btnExit.putClientProperty(
                FlatClientProperties.STYLE,
                "arc:16;"
                        + "borderWidth:1;"
                        + "focusWidth:1"
        );

        JPanel buttonPanel = new JPanel(
                new MigLayout(
                        "insets 0, fillx",
                        "[grow,fill]20[grow,fill]",
                        "[50!]"
                )
        );

        buttonPanel.setOpaque(false);

        buttonPanel.add(
                btnLogin,
                "grow, h 50!"
        );

        buttonPanel.add(
                btnExit,
                "grow, h 50!"
        );

        panel.add(
                buttonPanel,
                "growx, wrap"
        );
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;

        if (passwordVisible) {
            txtPassword.setEchoChar((char) 0);

            btnShowPassword.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.EYE_SLASH,
                            20,
                            new Color(40, 40, 40)
                    )
            );

            btnShowPassword.setToolTipText(
                    "Ẩn mật khẩu"
            );
        } else {
            txtPassword.setEchoChar(
                    defaultEchoChar
            );

            btnShowPassword.setIcon(
                    FontIcon.of(
                            FontAwesomeSolid.EYE,
                            20,
                            new Color(40, 40, 40)
                    )
            );

            btnShowPassword.setToolTipText(
                    "Hiện mật khẩu"
            );
        }
    }

    private void initializeShortcuts() {
        /*
         * Nhấn Enter để kích hoạt nút đăng nhập.
         */
        getRootPane().setDefaultButton(
                btnLogin
        );

        /*
         * Nhấn ESC để đóng cửa sổ.
         */
        KeyStroke escapeKey = KeyStroke.getKeyStroke(
                "ESCAPE"
        );

        getRootPane()
                .getInputMap(
                        JComponent.WHEN_IN_FOCUSED_WINDOW
                )
                .put(
                        escapeKey,
                        "closeLogin"
                );

        getRootPane()
                .getActionMap()
                .put(
                        "closeLogin",
                        new AbstractAction() {
                            @Override
                            public void actionPerformed(
                                    ActionEvent event
                            ) {
                                dispose();
                            }
                        }
                );

        SwingUtilities.invokeLater(
                () -> txtUsername.requestFocusInWindow()
        );
    }

    public String getUsername() {
        return txtUsername
                .getText()
                .trim();
    }

    public char[] getPassword() {
        return txtPassword.getPassword();
    }

    public void clearPassword() {
        txtPassword.setText("");
        txtPassword.requestFocusInWindow();
    }

    public void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        chkRemember.setSelected(false);

        txtUsername.requestFocusInWindow();
    }

    public JTextField getTxtUsername() {
        return txtUsername;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JButton getBtnExit() {
        return btnExit;
    }

    public JButton getBtnShowPassword() {
        return btnShowPassword;
    }

    public JCheckBox getChkRemember() {
        return chkRemember;
    }
}
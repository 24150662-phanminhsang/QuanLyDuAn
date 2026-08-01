package view;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Đổi từ JFrame sang JPanel
public class TeacherMainDashboard extends JPanel {
    private static final String DASHBOARD_CARD = "DASHBOARD";
    private static final String TEACHER_MANAGEMENT_CARD = "TEACHER_MANAGEMENT";
    private static final String PAYMENT_CARD = "PAYMENT";

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private final TeacherDashboardView teacherDashboardView;
    private final TeacherManagementView teacherManagementView;
    private final PaymentManagementView paymentManagementView;

    private final List<JButton> menuButtons;
    private JLabel pageTitleLabel;
    private JScrollPane sidebarScrollPane;

    public TeacherMainDashboard() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(248, 250, 252));

        // Khởi tạo các view con
        teacherDashboardView = new TeacherDashboardView();
        teacherManagementView = new TeacherManagementView();
        paymentManagementView = new PaymentManagementView();

        menuButtons = new ArrayList<>();

        initializePanel();
        initializeContentCards();
    }

    private void initializePanel() {
        // Cấu hình Layout cho chính JPanel này
        setLayout(new BorderLayout());

        sidebarScrollPane = createSidebarScrollPane();
        add(sidebarScrollPane, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(createHeader(), BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        showDashboard();
    }

    private JScrollPane createSidebarScrollPane() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new MigLayout("fillx, wrap 1, insets 9", "[grow, fill]", "[]8[]4[][][][]push[]"));
        sidebar.setBackground(new Color(15, 23, 42)); // Màu nền tối đặc trưng của Sidebar

        // Panel Logo
        JPanel logoPanel = new JPanel(new MigLayout("fillx, insets 1", "20![grow]", "[][]"));
        logoPanel.setOpaque(false);
        JLabel icon = new JLabel(FontIcon.of(FontAwesomeSolid.GRADUATION_CAP, 22, new Color(59, 130, 246)));
        JLabel name = new JLabel("CourseManager");
        name.setFont(new Font("Segoe UI", Font.BOLD, 15));
        name.setForeground(Color.WHITE);
        logoPanel.add(icon, "cell 0 0 1 2");
        logoPanel.add(name, "cell 1 0");
        sidebar.add(logoPanel, "growx, wrap 15");

        JLabel menuTitle = new JLabel("DANH MỤC CHỨC NĂNG");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        menuTitle.setForeground(new Color(148, 163, 184));
        sidebar.add(menuTitle, "gapleft 7, gapbottom 5");

        // Các nút điều hướng menu bên trái
        JButton btnDashboard = createMenuButton("Tổng quan", FontAwesomeSolid.HOME);
        JButton btnTeacherMgmt = createMenuButton("Quản lý giảng viên", FontAwesomeSolid.CHALKBOARD_TEACHER);
        JButton btnPayment = createMenuButton("Thanh toán & Học phí", FontAwesomeSolid.CREDIT_CARD);

        btnDashboard.addActionListener(e -> showDashboard());
        btnTeacherMgmt.addActionListener(e -> showTeacherManagement());
        btnPayment.addActionListener(e -> showPayment());

        sidebar.add(btnDashboard);
        sidebar.add(btnTeacherMgmt);
        sidebar.add(btnPayment);

        JScrollPane scrollPane = new JScrollPane(sidebar);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JButton createMenuButton(String title, FontAwesomeSolid icon) {
        JButton button = new JButton(title);
        button.setIcon(FontIcon.of(icon, 16, new Color(148, 163, 184)));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(148, 163, 184));
        button.setBackground(new Color(15, 23, 42));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuButtons.add(button);
        return button;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new MigLayout("fillx, insets 11 16", "[][grow][]", "[center]"));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JButton menuButton = new JButton();
        menuButton.setIcon(FontIcon.of(FontAwesomeSolid.BARS, 17, new Color(30, 41, 59)));
        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        menuButton.addActionListener(e -> toggleSidebar());

        pageTitleLabel = new JLabel("Tổng quan giảng dạy");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pageTitleLabel.setForeground(new Color(30, 41, 59));

        header.add(menuButton);
        header.add(pageTitleLabel, "gapleft 7");

        return header;
    }

    private void initializeContentCards() {
        contentPanel.add(teacherDashboardView, DASHBOARD_CARD);
        contentPanel.add(teacherManagementView, TEACHER_MANAGEMENT_CARD);
        contentPanel.add(paymentManagementView, PAYMENT_CARD);
    }

    private void toggleSidebar() {
        sidebarScrollPane.setVisible(!sidebarScrollPane.isVisible());
        revalidate();
        repaint();
    }
    // Bổ sung hàm này vào bên trong file TeacherMainDashboard.java
    public void showCard(String cardName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, cardName);
        }
    }

    private void showDashboard() {
        cardLayout.show(contentPanel, DASHBOARD_CARD);
        pageTitleLabel.setText("Tổng quan giảng dạy");
        selectMenuButton(0);
    }

    private void showTeacherManagement() {
        cardLayout.show(contentPanel, TEACHER_MANAGEMENT_CARD);
        pageTitleLabel.setText("Quản lý giảng viên");
        selectMenuButton(1);
    }

    private void showPayment() {
        cardLayout.show(contentPanel, PAYMENT_CARD);
        pageTitleLabel.setText("Quản lý thanh toán & học phí");
        selectMenuButton(2);
    }

    private void selectMenuButton(int selectedIndex) {
        for (int i = 0; i < menuButtons.size(); i++) {
            JButton btn = menuButtons.get(i);
            if (i == selectedIndex) {
                btn.setBackground(new Color(59, 130, 246));
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(new Color(15, 23, 42));
                btn.setForeground(new Color(148, 163, 184));
            }
        }
    }
}
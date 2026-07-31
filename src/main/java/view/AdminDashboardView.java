package view;


import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.NotificationService;
import util.UIConstants;
import view.components.NotificationPopup;
import view.components.SidebarButton;
import view.components.SidebarPanel;
import controller.DashboardController;


import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


public class AdminDashboardView extends JFrame {


    private static final String DASHBOARD_CARD = "DASHBOARD";
    private static final String USERS_CARD = "USERS";
    private static final String STUDENTS_CARD = "STUDENTS";
    private static final String COURSES_CARD = "COURSES";


    private static final int DASHBOARD_MENU_INDEX = 0;
    private static final int USERS_MENU_INDEX = 1;
    private static final int STUDENTS_MENU_INDEX = 2;
    private static final int COURSES_MENU_INDEX = 4;


    private final CardLayout cardLayout;
    private final JPanel contentPanel;


    private final NotificationService notificationService;
    private final NotificationPopup notificationPopup;


    private final StatisticsView statisticsView;
    private final UserManagementView userManagementView;
    private final StudentManagementView studentManagementView;
    private final CourseManagementView courseManagementView;


    private final List<SidebarButton> menuButtons;


    private JScrollPane sidebarScrollPane;
    private JLabel pageTitleLabel;
    private JButton notificationButton;


    public AdminDashboardView() {
        cardLayout = new CardLayout();


        contentPanel = new JPanel(
                cardLayout
        );


        contentPanel.setBackground(
                UIConstants.BACKGROUND
        );


        notificationService =
                new NotificationService();


        notificationPopup =
                new NotificationPopup(
                        notificationService
                );


        statisticsView =
                new StatisticsView(
                        notificationService
                );


        userManagementView =
                new UserManagementView();


        studentManagementView =
                new StudentManagementView();


        courseManagementView =
                new CourseManagementView();


        /*
         * Admin được phép thêm, sửa và xóa khóa học.
         */
        courseManagementView.setManagementMode(
                true
        );


        new DashboardController(
                courseManagementView
        );


        menuButtons = new ArrayList<>();


        registerComponentEvents();
        initializeFrame();
    }


    private void registerComponentEvents() {
        statisticsView.setQuickActionHandler(
                this::handleQuickAction
        );


        notificationPopup.setChangeListener(
                this::refreshNotifications
        );
    }


    private void initializeFrame() {
        setTitle(
                "CourseManager - Hệ thống quản lý khóa học"
        );


        setDefaultCloseOperation(
                WindowConstants.EXIT_ON_CLOSE
        );


        setMinimumSize(
                new Dimension(930, 600)
        );


        setSize(1400, 800);
        setLocationRelativeTo(null);


        setExtendedState(
                JFrame.MAXIMIZED_BOTH
        );


        JPanel rootPanel = new JPanel(
                new BorderLayout()
        );


        rootPanel.setBackground(
                UIConstants.BACKGROUND
        );


        sidebarScrollPane =
                createSidebarScrollPane();


        rootPanel.add(
                sidebarScrollPane,
                BorderLayout.WEST
        );


        JPanel rightPanel = new JPanel(
                new BorderLayout()
        );


        rightPanel.setBackground(
                UIConstants.BACKGROUND
        );


        rightPanel.add(
                createHeader(),
                BorderLayout.NORTH
        );


        initializeContentCards();


        rightPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );


        rootPanel.add(
                rightPanel,
                BorderLayout.CENTER
        );


        setContentPane(rootPanel);


        showDashboard();
        refreshNotifications();
    }


    private void initializeContentCards() {
        contentPanel.add(
                createDashboardScrollPane(),
                DASHBOARD_CARD
        );


        contentPanel.add(
                createContentScrollPane(
                        userManagementView
                ),
                USERS_CARD
        );


        contentPanel.add(
                createContentScrollPane(
                        studentManagementView
                ),
                STUDENTS_CARD
        );


        contentPanel.add(
                createContentScrollPane(
                        courseManagementView
                ),
                COURSES_CARD
        );
    }


    private JScrollPane createSidebarScrollPane() {
        SidebarPanel sidebar =
                createSidebar();


        JScrollPane scrollPane =
                new JScrollPane(
                        sidebar,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                );


        /*
         * Giảm chiều rộng sidebar để phần nội dung bên phải
         * có nhiều không gian hơn.
         */
        scrollPane.setPreferredSize(
                new Dimension(205, 0)
        );


        scrollPane.setMinimumSize(
                new Dimension(190, 0)
        );


        scrollPane.setBorder(null);


        scrollPane.getViewport().setBackground(
                UIConstants.SIDEBAR
        );


        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(15);


        return scrollPane;
    }


    private SidebarPanel createSidebar() {
        SidebarPanel sidebar =
                new SidebarPanel();


        sidebar.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 9",
                        "[grow, fill]",
                        "[]8[]4[][][][][][][]push[]"
                )
        );


        sidebar.setBackground(
                UIConstants.SIDEBAR
        );


        sidebar.add(
                createLogoPanel(),
                "growx"
        );


        JLabel menuTitle = new JLabel(
                "DANH MỤC QUẢN LÝ"
        );


        menuTitle.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        java.awt.Font.BOLD
                )
        );


        menuTitle.setForeground(
                new Color(148, 163, 184)
        );


        sidebar.add(
                menuTitle,
                "gapleft 7, gapbottom 2"
        );


        SidebarButton dashboardButton =
                createMenuButton(
                        "Tổng quan",
                        FontAwesomeSolid.HOME
                );


        SidebarButton userButton =
                createMenuButton(
                        "Quản lý tài khoản",
                        FontAwesomeSolid.USERS
                );


        SidebarButton studentButton =
                createMenuButton(
                        "Quản lý học viên",
                        FontAwesomeSolid.USER_GRADUATE
                );


        SidebarButton teacherButton =
                createMenuButton(
                        "Quản lý giảng viên",
                        FontAwesomeSolid.CHALKBOARD_TEACHER
                );


        SidebarButton courseButton =
                createMenuButton(
                        "Quản lý khóa học",
                        FontAwesomeSolid.BOOK_OPEN
                );


        SidebarButton classButton =
                createMenuButton(
                        "Quản lý lớp học",
                        FontAwesomeSolid.SCHOOL
                );


        SidebarButton paymentButton =
                createMenuButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD
                );


        SidebarButton reportButton =
                createMenuButton(
                        "Báo cáo thống kê",
                        FontAwesomeSolid.CHART_BAR
                );


        dashboardButton.addActionListener(
                event -> showDashboard()
        );


        userButton.addActionListener(
                event -> showUsers()
        );


        studentButton.addActionListener(
                event -> showStudents()
        );


        courseButton.addActionListener(
                event -> showCourses()
        );


        teacherButton.addActionListener(
                event -> showPendingFeature(
                        "Quản lý giảng viên"
                )
        );


        classButton.addActionListener(
                event -> showPendingFeature(
                        "Quản lý lớp học"
                )
        );


        paymentButton.addActionListener(
                event -> showPendingFeature(
                        "Quản lý thanh toán"
                )
        );


        reportButton.addActionListener(
                event -> showDashboard()
        );


        teacherButton.setPending(true);
        classButton.setPending(true);
        paymentButton.setPending(true);


        sidebar.add(dashboardButton);
        sidebar.add(userButton);
        sidebar.add(studentButton);
        sidebar.add(teacherButton);
        sidebar.add(courseButton);
        sidebar.add(classButton);
        sidebar.add(paymentButton);
        sidebar.add(reportButton);


        sidebar.add(
                createAccountPanel(),
                "growx, gaptop 4"
        );


        return sidebar;
    }


    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 1",
                        "29![grow]",
                        "[][]"
                )
        );


        panel.setOpaque(false);


        JLabel icon = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.GRADUATION_CAP,
                        22,
                        UIConstants.PRIMARY
                )
        );


        JLabel name = new JLabel(
                "CourseManager"
        );


        name.setFont(
                new java.awt.Font(
                        "Segoe UI",
                        java.awt.Font.BOLD,
                        15
                )
        );


        name.setForeground(
                Color.WHITE
        );


        JLabel subtitle = new JLabel(
                "Quản lý khóa học"
        );


        subtitle.setFont(
                UIConstants.FONT_SMALL
        );


        subtitle.setForeground(
                new Color(203, 213, 225)
        );


        panel.add(
                icon,
                "cell 0 0 1 2"
        );


        panel.add(
                name,
                "cell 1 0"
        );


        panel.add(
                subtitle,
                "cell 1 1"
        );


        return panel;
    }


    private SidebarButton createMenuButton(
            String title,
            FontAwesomeSolid icon
    ) {
        SidebarButton button =
                new SidebarButton(
                        title,
                        icon
                );


        menuButtons.add(button);


        return button;
    }


    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 7",
                        "[grow, center]",
                        "[]2[]0[]0[]3[]"
                )
        );


        panel.setBackground(
                UIConstants.SIDEBAR_SECONDARY
        );


        panel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                borderWidth: 0;
                """
        );


        JLabel avatar = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.USER_CIRCLE,
                        24,
                        Color.WHITE
                )
        );


        JLabel name = new JLabel(
                "Quản trị viên"
        );


        name.setFont(
                UIConstants.FONT_MEDIUM
        );


        name.setForeground(
                Color.WHITE
        );


        JLabel username = new JLabel(
                "admin"
        );


        username.setFont(
                UIConstants.FONT_SMALL
        );


        username.setForeground(
                new Color(203, 213, 225)
        );


        JLabel online = new JLabel(
                "● Online"
        );


        online.setFont(
                UIConstants.FONT_SMALL
        );


        online.setForeground(
                new Color(74, 222, 128)
        );


        JButton logoutButton =
                new JButton("Đăng xuất");


        logoutButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.SIGN_OUT_ALT,
                        11,
                        new Color(248, 113, 113)
                )
        );


        logoutButton.setFont(
                UIConstants.FONT_MEDIUM
        );


        logoutButton.setForeground(
                new Color(248, 113, 113)
        );


        logoutButton.setBackground(
                UIConstants.SIDEBAR_SECONDARY
        );


        logoutButton.setFocusable(false);
        logoutButton.setBorderPainted(false);


        logoutButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );


        logoutButton.addActionListener(
                event -> confirmLogout()
        );


        panel.add(avatar);
        panel.add(name);
        panel.add(username);
        panel.add(online);


        panel.add(
                logoutButton,
                "growx"
        );


        return panel;
    }


    private JPanel createHeader() {
        JPanel header = new JPanel(
                new MigLayout(
                        "fillx, insets 11 16",
                        "[][grow][][]",
                        "[center]"
                )
        );


        header.setBackground(
                Color.WHITE
        );


        header.setBorder(
                BorderFactory.createMatteBorder(
                        0,
                        0,
                        1,
                        0,
                        UIConstants.BORDER
                )
        );


        JButton menuButton =
                new JButton();


        menuButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.BARS,
                        17,
                        UIConstants.TEXT_PRIMARY
                )
        );


        menuButton.setBorderPainted(false);
        menuButton.setContentAreaFilled(false);
        menuButton.setFocusable(false);


        menuButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );


        menuButton.addActionListener(
                event -> toggleSidebar()
        );


        pageTitleLabel = new JLabel(
                "Tổng quan hệ thống"
        );


        pageTitleLabel.setFont(
                UIConstants.FONT_TITLE
        );


        pageTitleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );


        notificationButton =
                new JButton();


        notificationButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.BELL,
                        17,
                        UIConstants.TEXT_PRIMARY
                )
        );


        notificationButton.setHorizontalTextPosition(
                SwingConstants.RIGHT
        );


        notificationButton.setIconTextGap(2);
        notificationButton.setBorderPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setFocusable(false);


        notificationButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );


        notificationButton.addActionListener(
                event -> showNotificationPopup()
        );


        header.add(menuButton);


        header.add(
                pageTitleLabel,
                "gapleft 7"
        );


        header.add(notificationButton);
        header.add(createHeaderAccountPanel());


        return header;
    }


    private JPanel createHeaderAccountPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "insets 0",
                        "30![grow]",
                        "[][]"
                )
        );


        panel.setOpaque(false);


        JLabel avatar = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.USER_CIRCLE,
                        27,
                        UIConstants.PRIMARY
                )
        );


        JLabel name = new JLabel(
                "Quản trị viên"
        );


        name.setFont(
                UIConstants.FONT_MEDIUM
        );


        name.setForeground(
                UIConstants.TEXT_PRIMARY
        );


        JLabel username = new JLabel(
                "admin"
        );


        username.setFont(
                UIConstants.FONT_SMALL
        );


        username.setForeground(
                UIConstants.TEXT_SECONDARY
        );


        panel.add(
                avatar,
                "cell 0 0 1 2"
        );


        panel.add(
                name,
                "cell 1 0"
        );


        panel.add(
                username,
                "cell 1 1"
        );


        return panel;
    }


    private JScrollPane createDashboardScrollPane() {
        return createContentScrollPane(
                statisticsView
        );
    }


    private JScrollPane createContentScrollPane(
            JPanel view
    ) {
        JScrollPane scrollPane =
                new JScrollPane(
                        view,
                        ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
                );


        scrollPane.setBorder(null);


        scrollPane.getViewport().setBackground(
                UIConstants.BACKGROUND
        );


        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(18);


        return scrollPane;
    }


    private void showNotificationPopup() {
        notificationPopup.reload();


        notificationPopup.show(
                notificationButton,
                notificationButton.getWidth() - 410,
                notificationButton.getHeight() + 6
        );
    }


    private void refreshNotifications() {
        int unreadCount =
                notificationService.countUnread();


        notificationButton.setText(
                unreadCount > 0
                        ? String.valueOf(unreadCount)
                        : ""
        );


        notificationButton.setForeground(
                unreadCount > 0
                        ? UIConstants.DANGER
                        : UIConstants.TEXT_PRIMARY
        );


        notificationButton.setToolTipText(
                unreadCount > 0
                        ? unreadCount
                          + " thông báo chưa đọc"
                        : "Không có thông báo chưa đọc"
        );


        statisticsView.refreshActivities();
    }


    private void toggleSidebar() {
        boolean visible =
                sidebarScrollPane.isVisible();


        sidebarScrollPane.setVisible(
                !visible
        );


        revalidate();
        repaint();
    }


    private void showDashboard() {
        cardLayout.show(
                contentPanel,
                DASHBOARD_CARD
        );


        pageTitleLabel.setText(
                "Tổng quan hệ thống"
        );


        selectMenuButton(
                DASHBOARD_MENU_INDEX
        );


        statisticsView.loadStatistics();
    }


    private void showUsers() {
        cardLayout.show(
                contentPanel,
                USERS_CARD
        );


        pageTitleLabel.setText(
                "Quản lý tài khoản"
        );


        selectMenuButton(
                USERS_MENU_INDEX
        );


        userManagementView.loadUsers();
    }


    private void showStudents() {
        cardLayout.show(
                contentPanel,
                STUDENTS_CARD
        );


        pageTitleLabel.setText(
                "Quản lý học viên"
        );


        selectMenuButton(
                STUDENTS_MENU_INDEX
        );
    }


    private void showCourses() {
        cardLayout.show(
                contentPanel,
                COURSES_CARD
        );


        pageTitleLabel.setText(
                "Quản lý khóa học"
        );


        selectMenuButton(
                COURSES_MENU_INDEX
        );
    }


    private void handleQuickAction(
            String actionKey
    ) {
        if (
                actionKey == null
                        || actionKey.isBlank()
        ) {
            return;
        }


        switch (actionKey.trim().toUpperCase()) {
            case "USERS", "ADD_USER" ->
                    showUsers();


            case "STUDENTS", "ADD_STUDENT" ->
                    showStudents();


            case "COURSES", "ADD_COURSE" ->
                    showCourses();


            case "CLASSES" ->
                    showPendingFeature(
                            "Quản lý lớp học"
                    );


            case "PAYMENTS" ->
                    showPendingFeature(
                            "Quản lý thanh toán"
                    );


            case "REPORTS" ->
                    showDashboard();


            case "NOTIFICATIONS", "MESSAGES" ->
                    showNotificationPopup();


            default -> showPendingFeature(
                    "Chức năng " + actionKey
            );
        }
    }


    private void selectMenuButton(
            int selectedIndex
    ) {
        for (
                int index = 0;
                index < menuButtons.size();
                index++
        ) {
            menuButtons
                    .get(index)
                    .setMenuSelected(
                            index == selectedIndex
                    );
        }
    }


    private void showPendingFeature(
            String featureName
    ) {
        JOptionPane.showMessageDialog(
                this,
                featureName
                        + " đang được hoàn thiện.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }


    private void confirmLogout() {
        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );


        if (
                answer
                        == JOptionPane.YES_OPTION
        ) {
            dispose();
        }
    }
}

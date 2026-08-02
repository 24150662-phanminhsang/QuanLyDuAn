package view;

import controller.ClassController;
import dao.TeacherDAO;
import dao.impl.TeacherDAOImpl;
import model.ClassRoom;
import model.Student;
import model.Teacher;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.SessionManager;
import util.UIConstants;
import view.components.SidebarButton;
import view.components.SidebarPanel;
import view.teacher.TeacherClassView;
import view.teacher.TeacherGradeView;
import view.teacher.TeacherHomeView;
import view.teacher.TeacherProfileView;
import view.teacher.TeacherScheduleView;
import view.teacher.TeacherStudentView;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class TeacherMainDashboard extends JPanel {

    /* =====================================================
       KÍCH THƯỚC
       ===================================================== */

    private static final int SIDEBAR_WIDTH = 260;
    private static final int TEACHER_CARD_WIDTH = 220;
    private static final int TEACHER_CARD_HEIGHT = 142;

    /* =====================================================
       TÊN CARD
       ===================================================== */

    private static final String HOME_CARD =
            "TEACHER_HOME";

    private static final String CLASS_CARD =
            "TEACHER_CLASSES";

    private static final String STUDENT_CARD =
            "TEACHER_STUDENTS";

    private static final String GRADE_CARD =
            "TEACHER_GRADES";

    private static final String SCHEDULE_CARD =
            "TEACHER_SCHEDULE";

    private static final String PROFILE_CARD =
            "TEACHER_PROFILE";

    /* =====================================================
       MÀU SIDEBAR
       ===================================================== */

    private static final Color SIDEBAR_BACKGROUND =
            new Color(15, 23, 42);

    private static final Color SIDEBAR_CARD_BACKGROUND =
            new Color(30, 41, 59);

    private static final Color SIDEBAR_MUTED_TEXT =
            new Color(148, 163, 184);

    private static final Color LOGOUT_COLOR =
            new Color(248, 113, 113);

    /* =====================================================
       DAO / CONTROLLER
       ===================================================== */

    private final TeacherDAO teacherDAO;
    private final ClassController classController;

    /* =====================================================
       THÔNG TIN GIẢNG VIÊN
       ===================================================== */

    private Teacher currentTeacher;
    private final int teacherId;

    /* =====================================================
       CARD LAYOUT
       ===================================================== */

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    /* =====================================================
       CÁC VIEW CON
       ===================================================== */

    private final TeacherHomeView teacherHomeView;
    private final TeacherClassView teacherClassView;
    private final TeacherStudentView teacherStudentView;
    private final TeacherGradeView teacherGradeView;
    private final TeacherScheduleView teacherScheduleView;
    private final TeacherProfileView teacherProfileView;

    /* =====================================================
       MENU SIDEBAR
       ===================================================== */

    private SidebarButton homeButton;
    private SidebarButton classButton;
    private SidebarButton studentButton;
    private SidebarButton gradeButton;
    private SidebarButton scheduleButton;
    private SidebarButton profileButton;

    private JScrollPane sidebarScrollPane;

    /* =====================================================
       LABEL HIỂN THỊ
       ===================================================== */

    private JLabel pageTitleLabel;
    private JLabel headerTeacherNameLabel;
    private JLabel sidebarTeacherCodeLabel;
    private JLabel avatarLabel;

    public TeacherMainDashboard() {
        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "Chưa có người dùng đăng nhập."
            );
        }

        teacherDAO =
                new TeacherDAOImpl();

        classController =
                new ClassController();

        currentTeacher =
                loadCurrentTeacher();

        teacherId =
                currentTeacher.getTeacherId();

        cardLayout =
                new CardLayout();

        contentPanel =
                new JPanel(cardLayout);

        contentPanel.setBackground(
                UIConstants.BACKGROUND
        );

        teacherHomeView =
                new TeacherHomeView(
                        teacherId
                );

        teacherClassView =
                new TeacherClassView(
                        teacherId
                );

        teacherStudentView =
                new TeacherStudentView(
                        teacherId
                );

        teacherGradeView =
                new TeacherGradeView(
                        teacherId
                );

        teacherScheduleView =
                new TeacherScheduleView(
                        teacherId
                );

        teacherProfileView =
                new TeacherProfileView(
                        teacherId
                );

        initializeDashboard();
        initializeContentCards();
        connectViewActions();
        initializeTeacherClasses();

        showHome();
    }

    /* =====================================================
       LẤY GIẢNG VIÊN ĐANG ĐĂNG NHẬP
       ===================================================== */

    private Teacher loadCurrentTeacher() {
        int userId =
                SessionManager.getCurrentUserId();

        Teacher teacher =
                teacherDAO.getByUserId(
                        userId
                );

        if (teacher == null) {
            throw new IllegalStateException(
                    "Tài khoản hiện tại chưa được liên kết "
                            + "với hồ sơ giảng viên."
            );
        }

        if (teacher.getTeacherId() <= 0) {
            throw new IllegalStateException(
                    "ID giảng viên không hợp lệ."
            );
        }

        return teacher;
    }

    /* =====================================================
       KHỞI TẠO DASHBOARD
       ===================================================== */

    private void initializeDashboard() {
        setLayout(
                new BorderLayout()
        );

        setBackground(
                UIConstants.BACKGROUND
        );

        sidebarScrollPane =
                createSidebarScrollPane();

        add(
                sidebarScrollPane,
                BorderLayout.WEST
        );

        JPanel rightPanel =
                new JPanel(
                        new BorderLayout()
                );

        rightPanel.setBackground(
                UIConstants.BACKGROUND
        );

        rightPanel.add(
                createHeader(),
                BorderLayout.NORTH
        );

        rightPanel.add(
                contentPanel,
                BorderLayout.CENTER
        );

        add(
                rightPanel,
                BorderLayout.CENTER
        );
    }

    /* =====================================================
       SIDEBAR
       ===================================================== */

    private JScrollPane createSidebarScrollPane() {
        SidebarPanel sidebar =
                new SidebarPanel();

        sidebar.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 12 10 20 10",
                        "[grow, fill]",
                        "[]18[]8[][][][][][]push[]50"
                )
        );

        sidebar.setBackground(
                SIDEBAR_BACKGROUND
        );

        sidebar.setPreferredSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        720
                )
        );

        sidebar.setMinimumSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        0
                )
        );

        sidebar.setMaximumSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        Integer.MAX_VALUE
                )
        );

        sidebar.add(
                createLogoPanel(),
                "growx"
        );

        JLabel menuTitle =
                new JLabel(
                        "DANH MỤC CHỨC NĂNG"
                );

        menuTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        menuTitle.setForeground(
                SIDEBAR_MUTED_TEXT
        );

        sidebar.add(
                menuTitle,
                "gapleft 8, gapbottom 5"
        );

        initializeMenuButtons();

        sidebar.add(homeButton, "growx");
        sidebar.add(classButton, "growx");
        sidebar.add(studentButton, "growx");
        sidebar.add(gradeButton, "growx");
        sidebar.add(scheduleButton, "growx");
        sidebar.add(profileButton, "growx");

        sidebar.add(
                createTeacherInfoPanel(),
                "width "
                        + TEACHER_CARD_WIDTH
                        + "!, alignx center, gaptop 18, gapbottom 50"
        );

        JScrollPane scrollPane =
                new JScrollPane(sidebar);

        scrollPane.setBorder(null);

        scrollPane.setPreferredSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        0
                )
        );

        scrollPane.setMinimumSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        0
                )
        );

        scrollPane.setMaximumSize(
                new Dimension(
                        SIDEBAR_WIDTH,
                        Integer.MAX_VALUE
                )
        );

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane
                .getVerticalScrollBar()
                .setUnitIncrement(16);

        scrollPane
                .getViewport()
                .setBackground(
                        SIDEBAR_BACKGROUND
                );

        return scrollPane;
    }

    /* =====================================================
       LOGO
       ===================================================== */

    private JPanel createLogoPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 4 8",
                                "36![grow, fill]",
                                "[][]"
                        )
                );

        panel.setOpaque(false);

        JLabel iconLabel =
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.GRADUATION_CAP,
                                24,
                                UIConstants.PRIMARY
                        )
                );

        JLabel applicationNameLabel =
                new JLabel(
                        "CourseManager"
                );

        applicationNameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        15
                )
        );

        applicationNameLabel.setForeground(
                Color.WHITE
        );

        JLabel roleLabel =
                new JLabel(
                        "Cổng thông tin giảng viên"
                );

        roleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        10
                )
        );

        roleLabel.setForeground(
                SIDEBAR_MUTED_TEXT
        );

        panel.add(
                iconLabel,
                "cell 0 0 1 2, align center"
        );

        panel.add(
                applicationNameLabel,
                "cell 1 0, growx"
        );

        panel.add(
                roleLabel,
                "cell 1 1, growx"
        );

        return panel;
    }

    /* =====================================================
       MENU
       ===================================================== */

    private void initializeMenuButtons() {
        homeButton =
                createMenuButton(
                        "Tổng quan",
                        FontAwesomeSolid.HOME
                );

        classButton =
                createMenuButton(
                        "Lớp học của tôi",
                        FontAwesomeSolid.CHALKBOARD_TEACHER
                );

        studentButton =
                createMenuButton(
                        "Học viên",
                        FontAwesomeSolid.USER_GRADUATE
                );

        gradeButton =
                createMenuButton(
                        "Quản lý điểm",
                        FontAwesomeSolid.EDIT
                );

        scheduleButton =
                createMenuButton(
                        "Lịch giảng dạy",
                        FontAwesomeSolid.CALENDAR_ALT
                );

        profileButton =
                createMenuButton(
                        "Hồ sơ cá nhân",
                        FontAwesomeSolid.USER
                );

        homeButton.addActionListener(
                event -> showHome()
        );

        classButton.addActionListener(
                event -> showClasses()
        );

        studentButton.addActionListener(
                event -> showStudents()
        );

        gradeButton.addActionListener(
                event -> showGrades()
        );

        scheduleButton.addActionListener(
                event -> showSchedule()
        );

        profileButton.addActionListener(
                event -> showProfile()
        );
    }

    private SidebarButton createMenuButton(
            String text,
            FontAwesomeSolid icon
    ) {
        SidebarButton button =
                new SidebarButton(
                        text,
                        icon
                );

        button.setPreferredSize(
                new Dimension(
                        SIDEBAR_WIDTH - 20,
                        46
                )
        );

        button.setMinimumSize(
                new Dimension(
                        150,
                        46
                )
        );

        button.setMaximumSize(
                new Dimension(
                        Integer.MAX_VALUE,
                        46
                )
        );

        return button;
    }

    /* =====================================================
       THẺ TÀI KHOẢN CUỐI SIDEBAR
       ===================================================== */

    private JPanel createTeacherInfoPanel() {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fillx, wrap 1, insets 14",
                                "[grow, center]",
                                "[]8[]12[]"
                        )
                );

        panel.setBackground(
                SIDEBAR_CARD_BACKGROUND
        );

        panel.setPreferredSize(
                new Dimension(
                        TEACHER_CARD_WIDTH,
                        TEACHER_CARD_HEIGHT
                )
        );

        panel.setMinimumSize(
                new Dimension(
                        TEACHER_CARD_WIDTH,
                        TEACHER_CARD_HEIGHT
                )
        );

        panel.setMaximumSize(
                new Dimension(
                        TEACHER_CARD_WIDTH,
                        TEACHER_CARD_HEIGHT
                )
        );

        panel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 12;
                """
        );

        avatarLabel =
                new JLabel(
                        createInitials(
                                getTeacherDisplayName()
                        ),
                        SwingConstants.CENTER
                );

        avatarLabel.setPreferredSize(
                new Dimension(
                        46,
                        46
                )
        );

        avatarLabel.setMinimumSize(
                new Dimension(
                        46,
                        46
                )
        );

        avatarLabel.setMaximumSize(
                new Dimension(
                        46,
                        46
                )
        );

        avatarLabel.setOpaque(true);

        avatarLabel.setBackground(
                UIConstants.PRIMARY
        );

        avatarLabel.setForeground(
                Color.WHITE
        );

        avatarLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        avatarLabel.putClientProperty(
                "FlatLaf.style",
                """
                arc: 999;
                """
        );

        sidebarTeacherCodeLabel =
                new JLabel(
                        safeText(
                                currentTeacher.getTeacherCode(),
                                "Giảng viên"
                        ),
                        SwingConstants.CENTER
                );

        sidebarTeacherCodeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        sidebarTeacherCodeLabel.setForeground(
                SIDEBAR_MUTED_TEXT
        );

        JButton logoutButton =
                createLogoutButton();

        logoutButton.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        panel.add(
                avatarLabel,
                "width 46!, height 46!"
        );

        panel.add(
                sidebarTeacherCodeLabel,
                "growx"
        );

        panel.add(
                logoutButton,
                "growx, height 36!"
        );

        return panel;
    }

    private JButton createLogoutButton() {
        JButton button =
                new JButton(
                        "Đăng xuất"
                );

        button.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.SIGN_OUT_ALT,
                        13,
                        LOGOUT_COLOR
                )
        );

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        button.setForeground(
                LOGOUT_COLOR
        );

        button.setBackground(
                SIDEBAR_CARD_BACKGROUND
        );

        button.setIconTextGap(8);
        button.setFocusable(false);
        button.setBorderPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 8;
                borderWidth: 0;
                focusWidth: 0;
                margin: 6,8,6,8;
                """
        );

        button.addActionListener(
                event -> handleLogout()
        );

        return button;
    }

    /* =====================================================
       HEADER
       ===================================================== */

    private JPanel createHeader() {
        JPanel header =
                new JPanel(
                        new MigLayout(
                                "fillx, insets 11 16",
                                "[][grow][]",
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

        JButton sidebarToggleButton =
                new JButton();

        sidebarToggleButton.setIcon(
                FontIcon.of(
                        FontAwesomeSolid.BARS,
                        17,
                        UIConstants.TEXT_PRIMARY
                )
        );

        sidebarToggleButton.setBorderPainted(false);
        sidebarToggleButton.setContentAreaFilled(false);
        sidebarToggleButton.setFocusPainted(false);

        sidebarToggleButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        sidebarToggleButton.addActionListener(
                event -> toggleSidebar()
        );

        pageTitleLabel =
                new JLabel(
                        "Tổng quan giảng dạy"
                );

        pageTitleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        18
                )
        );

        pageTitleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        headerTeacherNameLabel =
                new JLabel(
                        getTeacherDisplayName()
                );

        headerTeacherNameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        headerTeacherNameLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        header.add(
                sidebarToggleButton
        );

        header.add(
                pageTitleLabel,
                "gapleft 7"
        );

        header.add(
                headerTeacherNameLabel,
                "align right"
        );

        return header;
    }

    /* =====================================================
       CARD LAYOUT
       ===================================================== */

    private void initializeContentCards() {
        contentPanel.add(
                teacherHomeView,
                HOME_CARD
        );

        contentPanel.add(
                teacherClassView,
                CLASS_CARD
        );

        contentPanel.add(
                teacherStudentView,
                STUDENT_CARD
        );

        contentPanel.add(
                teacherGradeView,
                GRADE_CARD
        );

        contentPanel.add(
                teacherScheduleView,
                SCHEDULE_CARD
        );

        contentPanel.add(
                teacherProfileView,
                PROFILE_CARD
        );
    }

    /* =====================================================
       KẾT NỐI VIEW
       ===================================================== */

    private void connectViewActions() {
        teacherClassView.setClassActionHandler(
                new TeacherClassView.ClassActionHandler() {

                    @Override
                    public void onViewStudents(
                            ClassRoom classRoom
                    ) {
                        showStudents();

                        teacherStudentView.selectClass(
                                classRoom
                        );
                    }

                    @Override
                    public void onManageGrades(
                            ClassRoom classRoom
                    ) {
                        showGrades();
                    }
                }
        );

        teacherStudentView.setStudentActionHandler(
                new TeacherStudentView.StudentActionHandler() {

                    @Override
                    public void onManageGrade(
                            Student student,
                            ClassRoom classRoom
                    ) {
                        showGrades();
                    }
                }
        );
    }

    private void initializeTeacherClasses() {
        try {
            List<ClassRoom> classes =
                    classController
                            .getClassesByTeacherId(
                                    teacherId
                            );

            teacherStudentView.setTeacherClasses(
                    classes
            );

        } catch (RuntimeException exception) {
            teacherStudentView.setTeacherClasses(
                    Collections.emptyList()
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải danh sách lớp của giảng viên.\n"
                            + getErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /* =====================================================
       ĐIỀU HƯỚNG
       ===================================================== */

    private void showHome() {
        teacherHomeView.loadData();

        showPage(
                HOME_CARD,
                "Tổng quan giảng dạy",
                homeButton
        );
    }

    private void showClasses() {
        teacherClassView.loadData();

        showPage(
                CLASS_CARD,
                "Lớp học của tôi",
                classButton
        );
    }

    private void showStudents() {
        refreshTeacherClassesForStudentView();

        showPage(
                STUDENT_CARD,
                "Học viên trong lớp",
                studentButton
        );
    }

    private void showGrades() {
        teacherGradeView.refreshData();

        showPage(
                GRADE_CARD,
                "Quản lý điểm",
                gradeButton
        );
    }

    private void showSchedule() {
        teacherScheduleView.loadData();

        showPage(
                SCHEDULE_CARD,
                "Lịch giảng dạy",
                scheduleButton
        );
    }

    private void showProfile() {
        teacherProfileView.loadProfile();
        refreshTeacherIdentity();

        showPage(
                PROFILE_CARD,
                "Hồ sơ cá nhân",
                profileButton
        );
    }

    private void showPage(
            String cardName,
            String pageTitle,
            SidebarButton selectedButton
    ) {
        cardLayout.show(
                contentPanel,
                cardName
        );

        pageTitleLabel.setText(
                pageTitle
        );

        selectMenuButton(
                selectedButton
        );

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void selectMenuButton(
            SidebarButton selectedButton
    ) {
        SidebarButton[] buttons = {
                homeButton,
                classButton,
                studentButton,
                gradeButton,
                scheduleButton,
                profileButton
        };

        for (SidebarButton button : buttons) {
            if (button != null) {
                button.setMenuSelected(
                        button == selectedButton
                );
            }
        }
    }

    /**
     * Giữ tương thích với file test và code cũ.
     */
    public void showCard(
            String cardName
    ) {
        if (cardName == null
                || cardName.isBlank()) {
            return;
        }

        switch (
                cardName.trim()
                        .toUpperCase(Locale.ROOT)
        ) {
            case "DASHBOARD",
                 "HOME",
                 "TEACHER_HOME" ->
                    showHome();

            case "CLASS",
                 "CLASSES",
                 "TEACHER_CLASSES" ->
                    showClasses();

            case "STUDENT",
                 "STUDENTS",
                 "TEACHER_STUDENTS" ->
                    showStudents();

            case "GRADE",
                 "GRADES",
                 "GRADE_MANAGEMENT",
                 "TEACHER_GRADES" ->
                    showGrades();

            case "SCHEDULE",
                 "TEACHER_SCHEDULE" ->
                    showSchedule();

            case "PROFILE",
                 "TEACHER_PROFILE" ->
                    showProfile();

            default -> {
                cardLayout.show(
                        contentPanel,
                        cardName
                );

                contentPanel.revalidate();
                contentPanel.repaint();
            }
        }
    }

    /* =====================================================
       TẢI LẠI DỮ LIỆU
       ===================================================== */

    private void refreshTeacherClassesForStudentView() {
        try {
            List<ClassRoom> classes =
                    classController
                            .getClassesByTeacherId(
                                    teacherId
                            );

            teacherStudentView.setTeacherClasses(
                    classes
            );

        } catch (RuntimeException exception) {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải danh sách lớp của giảng viên.\n"
                            + getErrorMessage(exception),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshTeacherIdentity() {
        try {
            Teacher refreshedTeacher =
                    teacherDAO.getById(
                            teacherId
                    );

            if (refreshedTeacher == null) {
                return;
            }

            currentTeacher =
                    refreshedTeacher;

            String displayName =
                    getTeacherDisplayName();

            if (headerTeacherNameLabel != null) {
                headerTeacherNameLabel.setText(
                        displayName
                );
            }

            if (sidebarTeacherCodeLabel != null) {
                sidebarTeacherCodeLabel.setText(
                        safeText(
                                currentTeacher.getTeacherCode(),
                                "Giảng viên"
                        )
                );
            }

            if (avatarLabel != null) {
                avatarLabel.setText(
                        createInitials(
                                displayName
                        )
                );
            }

        } catch (RuntimeException exception) {
            System.err.println(
                    "Không thể làm mới thông tin giảng viên: "
                            + exception.getMessage()
            );
        }
    }

    /* =====================================================
       ẨN / HIỆN SIDEBAR
       ===================================================== */

    private void toggleSidebar() {
        sidebarScrollPane.setVisible(
                !sidebarScrollPane.isVisible()
        );

        revalidate();
        repaint();
    }

    /* =====================================================
       ĐĂNG XUẤT
       ===================================================== */

    private void handleLogout() {
        int answer =
                JOptionPane.showConfirmDialog(
                        this,
                        "Bạn có chắc muốn đăng xuất?",
                        "Xác nhận đăng xuất",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        SessionManager.logout();

        Window currentWindow =
                SwingUtilities.getWindowAncestor(
                        this
                );

        if (currentWindow != null) {
            currentWindow.dispose();
        }

        SwingUtilities.invokeLater(() -> {
            LoginView loginView =
                    new LoginView();

            /*
             * Nếu LoginView không tự tạo LoginController,
             * cần thêm:
             *
             * new LoginController(loginView);
             */

            loginView.setLocationRelativeTo(null);
            loginView.setVisible(true);
        });
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private String getTeacherDisplayName() {
        return safeText(
                currentTeacher == null
                        ? null
                        : currentTeacher.getFullName(),
                SessionManager.isLoggedIn()
                        ? SessionManager.getFullName()
                        : "Giảng viên"
        );
    }

    private String createInitials(
            String fullName
    ) {
        if (fullName == null
                || fullName.isBlank()) {

            return "GV";
        }

        String[] words =
                fullName.trim()
                        .split("\\s+");

        if (words.length == 1) {
            String word =
                    words[0];

            return word.substring(
                            0,
                            Math.min(
                                    2,
                                    word.length()
                            )
                    )
                    .toUpperCase(
                            Locale.ROOT
                    );
        }

        String firstLetter =
                words[0].substring(
                        0,
                        1
                );

        String lastLetter =
                words[words.length - 1]
                        .substring(
                                0,
                                1
                        );

        return (
                firstLetter
                        + lastLetter
        ).toUpperCase(
                Locale.ROOT
        );
    }

    private String safeText(
            String preferredValue,
            String fallbackValue
    ) {
        if (preferredValue != null
                && !preferredValue.isBlank()) {

            return preferredValue.trim();
        }

        if (fallbackValue != null
                && !fallbackValue.isBlank()) {

            return fallbackValue.trim();
        }

        return "Giảng viên";
    }

    private String getErrorMessage(
            Throwable throwable
    ) {
        if (throwable == null) {
            return "Không xác định";
        }

        Throwable current =
                throwable;

        while (current.getCause() != null) {
            current =
                    current.getCause();
        }

        if (current.getMessage() != null
                && !current.getMessage().isBlank()) {

            return current.getMessage();
        }

        return current.getClass()
                .getSimpleName();
    }

    public int getTeacherId() {
        return teacherId;
    }

    public Teacher getCurrentTeacher() {
        return currentTeacher;
    }
}
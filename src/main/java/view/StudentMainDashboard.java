package view;

import controller.LoginController;
import controller.StudentCourseController;
import controller.StudentGradeController;
import dao.StudentDAO;
import model.Student;
import model.dto.StudentCourseDTO;
import model.dto.StudentGradeDTO;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.SessionManager;
import util.UIConstants;
import view.components.SidebarButton;
import view.components.SidebarPanel;
import view.components.student.StudentCoursePanel;
import view.components.student.StudentNotificationPanel;
import view.components.student.StudentResultPanel;
import view.components.student.StudentSchedulePanel;
import view.components.student.UpcomingAssignmentPanel;
import view.student.StudentCourseView;
import view.student.StudentGradeView;
import view.student.StudentScheduleView;
import view.student.StudentPaymentView;
import view.student.StudentProfileView;

import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudentMainDashboard extends JPanel {

    /* =====================================================
       KÍCH THƯỚC
       ===================================================== */

    private static final int SIDEBAR_WIDTH = 260;
    private static final int STUDENT_CARD_WIDTH = 220;
    private static final int STUDENT_CARD_HEIGHT = 142;

    /* =====================================================
       TÊN CARD
       ===================================================== */

    private static final String HOME_CARD =
            "STUDENT_HOME";

    private static final String COURSE_CARD =
            "STUDENT_COURSES";

    private static final String SCHEDULE_CARD =
            "STUDENT_SCHEDULE";

    private static final String GRADE_CARD =
            "STUDENT_GRADES";

    private static final String PAYMENT_CARD =
            "STUDENT_PAYMENTS";

    private static final String PROFILE_CARD =
            "STUDENT_PROFILE";

    /* =====================================================
       MÀU GIAO DIỆN
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

    private final StudentDAO studentDAO;

    private final StudentCourseController
            studentCourseController;

    private final StudentGradeController
            studentGradeController;

    /* =====================================================
       THÔNG TIN SINH VIÊN
       ===================================================== */

    private Student currentStudent;
    private final int studentId;

    /* =====================================================
       CARD LAYOUT
       ===================================================== */

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    /* =====================================================
       CÁC VIEW CON
       ===================================================== */

    private final StudentDashboardView studentHomeView;
    private final StudentCourseView studentCourseView;
    private final StudentScheduleView studentScheduleView;
    private final StudentGradeView studentGradeView;

    /*
     * Hai trang này vẫn đang là placeholder.
     * Sẽ thay bằng StudentPaymentView và StudentProfileView sau.
     */
    private final StudentPaymentView studentPaymentView;
    private final StudentProfileView studentProfileView;

    /* =====================================================
       MENU SIDEBAR
       ===================================================== */

    private SidebarButton homeButton;
    private SidebarButton courseButton;
    private SidebarButton scheduleButton;
    private SidebarButton gradeButton;
    private SidebarButton paymentButton;
    private SidebarButton profileButton;

    private JScrollPane sidebarScrollPane;

    /* =====================================================
       LABEL HIỂN THỊ
       ===================================================== */

    private JLabel pageTitleLabel;
    private JLabel headerStudentNameLabel;
    private JLabel sidebarStudentCodeLabel;
    private JLabel avatarLabel;

    public StudentMainDashboard() {
        if (!SessionManager.isLoggedIn()) {
            throw new IllegalStateException(
                    "Chưa có người dùng đăng nhập."
            );
        }

        this.studentDAO =
                new StudentDAO();

        this.studentCourseController =
                new StudentCourseController();

        this.studentGradeController =
                new StudentGradeController();

        this.currentStudent =
                loadCurrentStudent();

        /*
         * Model Student hiện tại dùng getStudentID().
         */
        this.studentId =
                currentStudent.getStudentID();

        this.cardLayout =
                new CardLayout();

        this.contentPanel =
                new JPanel(cardLayout);

        this.contentPanel.setBackground(
                UIConstants.BACKGROUND
        );

        /*
         * Trang Tổng quan.
         */
        this.studentHomeView =
                new StudentDashboardView();

        this.studentHomeView.setStudentName(
                getStudentDisplayName()
        );

        /*
         * Các trang có dữ liệu thật.
         */
        this.studentCourseView =
                new StudentCourseView(
                        studentId
                );

        this.studentScheduleView =
                new StudentScheduleView(
                        studentId
                );

        this.studentGradeView =
                new StudentGradeView(
                        studentId
                );

        /*
         * Hai trang sẽ làm tiếp.
         */
        this.studentPaymentView =
                new StudentPaymentView(
                        studentId
                );

        this.studentProfileView =
                new StudentProfileView(
                        studentId,
                        this::refreshStudentIdentity
                );

        initializeDashboard();
        initializeContentCards();
        connectViewActions();

        showHome();
    }

    /* =====================================================
       LẤY SINH VIÊN ĐANG ĐĂNG NHẬP
       ===================================================== */

    private Student loadCurrentStudent() {
        int userId =
                SessionManager.getCurrentUserId();

        Student student =
                studentDAO.findByUserId(
                        userId
                );

        if (student == null) {
            throw new IllegalStateException(
                    "Tài khoản hiện tại chưa được liên kết "
                            + "với hồ sơ sinh viên."
            );
        }

        if (student.getStudentID() <= 0) {
            throw new IllegalStateException(
                    "ID sinh viên không hợp lệ."
            );
        }

        return student;
    }

    /* =====================================================
       KHỞI TẠO GIAO DIỆN CHÍNH
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

        sidebar.add(
                homeButton,
                "growx"
        );

        sidebar.add(
                courseButton,
                "growx"
        );

        sidebar.add(
                scheduleButton,
                "growx"
        );

        sidebar.add(
                gradeButton,
                "growx"
        );

        sidebar.add(
                paymentButton,
                "growx"
        );

        sidebar.add(
                profileButton,
                "growx"
        );

        sidebar.add(
                createStudentInfoPanel(),
                "width "
                        + STUDENT_CARD_WIDTH
                        + "!, alignx center, "
                        + "gaptop 18, gapbottom 50"
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
                        "Cổng thông tin sinh viên"
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

        courseButton =
                createMenuButton(
                        "Khóa học của tôi",
                        FontAwesomeSolid.BOOK_OPEN
                );

        scheduleButton =
                createMenuButton(
                        "Lịch học",
                        FontAwesomeSolid.CALENDAR_ALT
                );

        gradeButton =
                createMenuButton(
                        "Kết quả học tập",
                        FontAwesomeSolid.CHART_BAR
                );

        paymentButton =
                createMenuButton(
                        "Thanh toán",
                        FontAwesomeSolid.CREDIT_CARD
                );

        profileButton =
                createMenuButton(
                        "Hồ sơ cá nhân",
                        FontAwesomeSolid.USER
                );

        homeButton.addActionListener(
                event -> showHome()
        );

        courseButton.addActionListener(
                event -> showCourses()
        );

        scheduleButton.addActionListener(
                event -> showSchedule()
        );

        gradeButton.addActionListener(
                event -> showGrades()
        );

        paymentButton.addActionListener(
                event -> showPayments()
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
       THẺ SINH VIÊN CUỐI SIDEBAR
       ===================================================== */

    private JPanel createStudentInfoPanel() {
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

        Dimension cardSize =
                new Dimension(
                        STUDENT_CARD_WIDTH,
                        STUDENT_CARD_HEIGHT
                );

        panel.setPreferredSize(cardSize);
        panel.setMinimumSize(cardSize);
        panel.setMaximumSize(cardSize);

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 12;"
        );

        avatarLabel =
                new JLabel(
                        createInitials(
                                getStudentDisplayName()
                        ),
                        SwingConstants.CENTER
                );

        Dimension avatarSize =
                new Dimension(46, 46);

        avatarLabel.setPreferredSize(avatarSize);
        avatarLabel.setMinimumSize(avatarSize);
        avatarLabel.setMaximumSize(avatarSize);

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
                "arc: 999;"
        );

        sidebarStudentCodeLabel =
                new JLabel(
                        safeText(
                                currentStudent.getStudentCode(),
                                "Sinh viên"
                        ),
                        SwingConstants.CENTER
                );

        sidebarStudentCodeLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        sidebarStudentCodeLabel.setForeground(
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
                sidebarStudentCodeLabel,
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
                        "Tổng quan học tập"
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

        headerStudentNameLabel =
                new JLabel(
                        getStudentDisplayName()
                );

        headerStudentNameLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        headerStudentNameLabel.setForeground(
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
                headerStudentNameLabel,
                "align right"
        );

        return header;
    }

    /* =====================================================
       KHỞI TẠO CARD
       ===================================================== */

    private void initializeContentCards() {
        contentPanel.add(
                studentHomeView,
                HOME_CARD
        );

        contentPanel.add(
                studentCourseView,
                COURSE_CARD
        );

        contentPanel.add(
                studentScheduleView,
                SCHEDULE_CARD
        );

        contentPanel.add(
                studentGradeView,
                GRADE_CARD
        );

        contentPanel.add(
                studentPaymentView,
                PAYMENT_CARD
        );

        contentPanel.add(
                studentProfileView,
                PROFILE_CARD
        );
    }

    /* =====================================================
       KẾT NỐI VIEW
       ===================================================== */

    private void connectViewActions() {
        studentCourseView.setCourseActionHandler(
                course -> JOptionPane.showMessageDialog(
                        this,
                        "Khóa học: "
                                + course.getDisplayCourseName()
                                + "\nLớp: "
                                + course.getDisplayClassName()
                                + "\nGiảng viên: "
                                + safeText(
                                course.getTeacherName(),
                                "--"
                        ),
                        "Chi tiết khóa học",
                        JOptionPane.INFORMATION_MESSAGE
                )
        );

        /*
         * Khi bấm khóa học ở Tổng quan,
         * chuyển sang trang Khóa học của tôi.
         */
        studentHomeView.setCourseActionHandler(
                courseId -> showCourses()
        );
    }

    /* =====================================================
       PLACEHOLDER VIEW
       ===================================================== */

    private JPanel createPlaceholderView(
            String title,
            String description
    ) {
        JPanel panel =
                new JPanel(
                        new MigLayout(
                                "fill, wrap 1, insets 24",
                                "[grow, fill]",
                                "[]8[]push"
                        )
                );

        panel.setBackground(
                UIConstants.BACKGROUND
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                UIConstants.FONT_TITLE
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel =
                new JLabel(description);

        descriptionLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    /* =====================================================
       TẢI DỮ LIỆU TỔNG QUAN
       ===================================================== */

    private void loadHomeData() {
        try {
            List<StudentCourseDTO> courses =
                    studentCourseController
                            .getActiveCourses(
                                    studentId
                            );

            if (courses == null) {
                courses =
                        Collections.emptyList();
            }

            List<StudentGradeDTO> grades =
                    studentGradeController
                            .getGrades(
                                    studentId
                            );

            if (grades == null) {
                grades =
                        Collections.emptyList();
            }

            loadCourseOverview(courses);
            loadScheduleOverview(courses);
            loadGradeOverview(grades);

            /*
             * Chưa nối DAO cho bài tập và thông báo.
             * Truyền danh sách rỗng để không hiển thị dữ liệu demo.
             */
            studentHomeView.setAssignments(
                    Collections
                            .<UpcomingAssignmentPanel.AssignmentItem>
                                    emptyList()
            );

            studentHomeView.setNotifications(
                    Collections
                            .<StudentNotificationPanel.StudentNotificationItem>
                                    emptyList()
            );

        } catch (RuntimeException exception) {
            exception.printStackTrace();

            clearHomeData();

            JOptionPane.showMessageDialog(
                    this,
                    "Không thể tải dữ liệu tổng quan.\n"
                            + getRootErrorMessage(
                            exception
                    ),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void loadCourseOverview(
            List<StudentCourseDTO> courses
    ) {
        List<StudentCoursePanel.CourseProgressItem>
                courseItems =
                courses.stream()
                        .filter(
                                course ->
                                        course != null
                        )
                        .limit(5)
                        .map(
                                course ->
                                        new StudentCoursePanel
                                                .CourseProgressItem(
                                                course.getCourseId(),
                                                safeText(
                                                        course.getCourseName(),
                                                        course.getCourseCode()
                                                ),
                                                safeText(
                                                        course.getTeacherName(),
                                                        "Chưa phân công"
                                                ),
                                                course.getProgressPercent()
                                        )
                        )
                        .toList();

        studentHomeView.setCourses(
                courseItems
        );

        /*
         * StatCard phải hiển thị tổng số khóa học thật,
         * không chỉ số phần tử đang giới hạn trên panel.
         */
        studentHomeView.setCourseCount(
                courses.size()
        );
    }

    private void loadScheduleOverview(
            List<StudentCourseDTO> courses
    ) {
        List<StudentSchedulePanel.ScheduleItem>
                scheduleItems =
                courses.stream()
                        .filter(
                                course ->
                                        course != null
                        )
                        .filter(
                                course ->
                                        isScheduleToday(
                                                course.getScheduleText()
                                        )
                        )
                        .limit(5)
                        .map(
                                this::createScheduleItem
                        )
                        .toList();

        studentHomeView.setScheduleItems(
                scheduleItems
        );

        studentHomeView.setTodayScheduleCount(
                scheduleItems.size()
        );
    }

    private void loadGradeOverview(
            List<StudentGradeDTO> grades
    ) {
        List<StudentResultPanel.ResultItem>
                resultItems =
                grades.stream()
                        .filter(
                                grade ->
                                        grade != null
                                                && grade.getAverageScore()
                                                != null
                        )
                        .limit(5)
                        .map(
                                grade ->
                                        new StudentResultPanel
                                                .ResultItem(
                                                safeText(
                                                        grade.getCourseName(),
                                                        grade.getCourseCode()
                                                ),
                                                "Điểm trung bình",
                                                grade.getAverageScore()
                                        )
                        )
                        .toList();

        studentHomeView.setResults(
                resultItems
        );

        double average =
                grades.stream()
                        .filter(
                                grade ->
                                        grade != null
                                                && grade.getAverageScore()
                                                != null
                        )
                        .mapToDouble(
                                StudentGradeDTO::getAverageScore
                        )
                        .average()
                        .orElse(0.0);

        studentHomeView.setAverageScore(
                average
        );
    }

    private void clearHomeData() {
        studentHomeView.setCourses(
                Collections.emptyList()
        );

        studentHomeView.setScheduleItems(
                Collections.emptyList()
        );

        studentHomeView.setResults(
                Collections.emptyList()
        );

        studentHomeView.setAssignments(
                Collections.emptyList()
        );

        studentHomeView.setNotifications(
                Collections.emptyList()
        );

        studentHomeView.setCourseCount(0);
        studentHomeView.setTodayScheduleCount(0);
        studentHomeView.setPendingAssignmentCount(0);
        studentHomeView.setAverageScore(0);
    }

    /* =====================================================
       CHUYỂN DỮ LIỆU LỊCH HỌC
       ===================================================== */

    private StudentSchedulePanel.ScheduleItem
    createScheduleItem(
            StudentCourseDTO course
    ) {
        LocalTime[] classTimes =
                extractClassTimes(
                        course.getScheduleText()
                );

        LocalTime now =
                LocalTime.now();

        boolean active =
                !now.isBefore(
                        classTimes[0]
                )
                        && !now.isAfter(
                        classTimes[1]
                );

        String status;

        if (active) {
            status = "Đang học";

        } else if (
                now.isBefore(
                        classTimes[0]
                )
        ) {
            status = "Sắp tới";

        } else {
            status = "Đã kết thúc";
        }

        return new StudentSchedulePanel.ScheduleItem(
                safeText(
                        course.getCourseName(),
                        course.getCourseCode()
                ),
                safeText(
                        course.getTeacherName(),
                        "Chưa phân công"
                ),
                safeText(
                        course.getRoom(),
                        "Chưa có phòng"
                ),
                classTimes[0],
                classTimes[1],
                status,
                active
        );
    }

    private boolean isScheduleToday(
            String scheduleText
    ) {
        if (scheduleText == null
                || scheduleText.isBlank()) {

            return false;
        }

        String value =
                removeVietnameseAccents(
                        scheduleText
                                .trim()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                );

        DayOfWeek today =
                LocalDate.now()
                        .getDayOfWeek();

        return switch (today) {
            case MONDAY ->
                    value.contains("thu 2");

            case TUESDAY ->
                    value.contains("thu 3");

            case WEDNESDAY ->
                    value.contains("thu 4");

            case THURSDAY ->
                    value.contains("thu 5");

            case FRIDAY ->
                    value.contains("thu 6");

            case SATURDAY ->
                    value.contains("thu 7");

            case SUNDAY ->
                    value.contains("chu nhat");
        };
    }

    private LocalTime[] extractClassTimes(
            String scheduleText
    ) {
        int startPeriod = 1;
        int endPeriod = 3;

        if (scheduleText != null
                && !scheduleText.isBlank()) {

            String normalizedText =
                    removeVietnameseAccents(
                            scheduleText
                                    .toLowerCase(
                                            Locale.ROOT
                                    )
                    );

            Pattern pattern =
                    Pattern.compile(
                            "tiet\\s*(\\d+)"
                                    + "\\s*(?:den|-|–)\\s*(\\d+)"
                    );

            Matcher matcher =
                    pattern.matcher(
                            normalizedText
                    );

            if (matcher.find()) {
                try {
                    startPeriod =
                            Integer.parseInt(
                                    matcher.group(1)
                            );

                    endPeriod =
                            Integer.parseInt(
                                    matcher.group(2)
                            );

                } catch (
                        NumberFormatException ignored
                ) {
                    startPeriod = 1;
                    endPeriod = 3;
                }
            }
        }

        return new LocalTime[]{
                getPeriodStartTime(
                        startPeriod
                ),
                getPeriodEndTime(
                        endPeriod
                )
        };
    }

    private LocalTime getPeriodStartTime(
            int period
    ) {
        return switch (period) {
            case 1 -> LocalTime.of(7, 0);
            case 2 -> LocalTime.of(7, 50);
            case 3 -> LocalTime.of(8, 40);
            case 4 -> LocalTime.of(9, 35);
            case 5 -> LocalTime.of(10, 25);
            case 6 -> LocalTime.of(13, 0);
            case 7 -> LocalTime.of(13, 50);
            case 8 -> LocalTime.of(14, 40);
            case 9 -> LocalTime.of(15, 35);
            case 10 -> LocalTime.of(16, 25);
            case 11 -> LocalTime.of(17, 15);
            case 12 -> LocalTime.of(18, 5);
            default -> LocalTime.of(7, 0);
        };
    }

    private LocalTime getPeriodEndTime(
            int period
    ) {
        return switch (period) {
            case 1 -> LocalTime.of(7, 45);
            case 2 -> LocalTime.of(8, 35);
            case 3 -> LocalTime.of(9, 25);
            case 4 -> LocalTime.of(10, 20);
            case 5 -> LocalTime.of(11, 10);
            case 6 -> LocalTime.of(13, 45);
            case 7 -> LocalTime.of(14, 35);
            case 8 -> LocalTime.of(15, 25);
            case 9 -> LocalTime.of(16, 20);
            case 10 -> LocalTime.of(17, 10);
            case 11 -> LocalTime.of(18, 0);
            case 12 -> LocalTime.of(18, 50);
            default -> LocalTime.of(9, 25);
        };
    }

    private String removeVietnameseAccents(
            String text
    ) {
        if (text == null) {
            return "";
        }

        String normalized =
                java.text.Normalizer.normalize(
                        text,
                        java.text.Normalizer.Form.NFD
                );

        return normalized
                .replaceAll(
                        "\\p{M}",
                        ""
                )
                .replace('đ', 'd')
                .replace('Đ', 'D');
    }

    /* =====================================================
       ĐIỀU HƯỚNG
       ===================================================== */

    private void showHome() {
        studentHomeView.setStudentName(
                getStudentDisplayName()
        );

        loadHomeData();

        showPage(
                HOME_CARD,
                "Tổng quan học tập",
                homeButton
        );
    }

    private void showCourses() {
        studentCourseView.loadData();

        showPage(
                COURSE_CARD,
                "Khóa học của tôi",
                courseButton
        );
    }

    private void showSchedule() {
        studentScheduleView.loadData();

        showPage(
                SCHEDULE_CARD,
                "Lịch học",
                scheduleButton
        );
    }

    private void showGrades() {
        studentGradeView.loadData();

        showPage(
                GRADE_CARD,
                "Kết quả học tập",
                gradeButton
        );
    }

    private void showPayments() {
        studentPaymentView.loadData();

        showPage(
                PAYMENT_CARD,
                "Thanh toán học phí",
                paymentButton
        );
    }

    private void showProfile() {
        studentProfileView.loadData();

        refreshStudentIdentity();

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
                courseButton,
                scheduleButton,
                gradeButton,
                paymentButton,
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
     * Giữ tương thích với code test hoặc điều hướng cũ.
     */
    public void showCard(
            String cardName
    ) {
        if (cardName == null
                || cardName.isBlank()) {

            return;
        }

        switch (
                cardName
                        .trim()
                        .toUpperCase(
                                Locale.ROOT
                        )
        ) {
            case "DASHBOARD",
                 "HOME",
                 "STUDENT_HOME" ->
                    showHome();

            case "COURSE",
                 "COURSES",
                 "STUDENT_COURSES" ->
                    showCourses();

            case "SCHEDULE",
                 "STUDENT_SCHEDULE" ->
                    showSchedule();

            case "GRADE",
                 "GRADES",
                 "RESULT",
                 "RESULTS",
                 "STUDENT_GRADES" ->
                    showGrades();

            case "PAYMENT",
                 "PAYMENTS",
                 "STUDENT_PAYMENTS" ->
                    showPayments();

            case "PROFILE",
                 "STUDENT_PROFILE" ->
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
       LÀM MỚI THÔNG TIN SINH VIÊN
       ===================================================== */

    private void refreshStudentIdentity() {
        try {
            /*
             * studentId phải dùng getStudentById().
             * findByUserId() chỉ nhận userId.
             */
            Student refreshedStudent =
                    studentDAO.getStudentById(
                            studentId
                    );

            if (refreshedStudent == null) {
                return;
            }

            currentStudent =
                    refreshedStudent;

            String displayName =
                    getStudentDisplayName();

            if (headerStudentNameLabel != null) {
                headerStudentNameLabel.setText(
                        displayName
                );
            }

            if (sidebarStudentCodeLabel != null) {
                sidebarStudentCodeLabel.setText(
                        safeText(
                                currentStudent.getStudentCode(),
                                "Sinh viên"
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

            studentHomeView.setStudentName(
                    displayName
            );

        } catch (RuntimeException exception) {
            System.err.println(
                    "Không thể làm mới thông tin sinh viên: "
                            + getRootErrorMessage(
                            exception
                    )
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

        if (answer
                != JOptionPane.YES_OPTION) {

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

            new LoginController(
                    loginView
            );

            loginView.setDefaultCloseOperation(
                    WindowConstants.DISPOSE_ON_CLOSE
            );

            loginView.setLocationRelativeTo(null);
            loginView.setVisible(true);
        });
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private String getStudentDisplayName() {
        return safeText(
                currentStudent == null
                        ? null
                        : currentStudent.getFullName(),
                SessionManager.isLoggedIn()
                        ? SessionManager.getFullName()
                        : "Sinh viên"
        );
    }

    private String createInitials(
            String fullName
    ) {
        if (fullName == null
                || fullName.isBlank()) {

            return "SV";
        }

        String[] words =
                fullName
                        .trim()
                        .split("\\s+");

        if (words.length == 1) {
            String word =
                    words[0];

            return word
                    .substring(
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
                words[
                        words.length - 1
                        ].substring(
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

        return "--";
    }

    private String getRootErrorMessage(
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

            return current
                    .getMessage();
        }

        return current
                .getClass()
                .getSimpleName();
    }

    public int getStudentId() {
        return studentId;
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }

    public StudentCourseView getStudentCourseView() {
        return studentCourseView;
    }
}
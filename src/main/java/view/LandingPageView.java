package view;

import model.Course;
import view.components.PublicCourseCard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.List;

public class LandingPageView extends JFrame {

    private static final Color PRIMARY =
            new Color(37, 99, 235);

    private static final Color PRIMARY_HOVER =
            new Color(29, 78, 216);

    private static final Color PRIMARY_LIGHT =
            new Color(232, 240, 255);

    private static final Color TEXT_DARK =
            new Color(15, 35, 70);

    private static final Color TEXT_GRAY =
            new Color(91, 105, 129);

    private static final Color BORDER_COLOR =
            new Color(226, 232, 240);

    private static final Color SECTION_BACKGROUND =
            new Color(248, 250, 253);

    private static final Color FOOTER_BACKGROUND =
            new Color(10, 29, 58);

    private final Runnable loginAction;
    private final Runnable registerAction;

    private JScrollPane scrollPane;
    private ScrollableContentPanel mainContent;

    private JPanel homeSection;
    private JPanel courseSection;
    private JPanel programSection;
    private JPanel aboutSection;
    private JPanel contactSection;

    private JPanel courseCardsPanel;
    private JLabel courseStatusLabel;

    public LandingPageView(
            Runnable loginAction,
            Runnable registerAction
    ) {
        this.loginAction = loginAction;
        this.registerAction = registerAction;

        initializeFrame();
        initializeUI();
    }

    // =========================================================
    // KHỞI TẠO CỬA SỔ
    // =========================================================

    private void initializeFrame() {
        setTitle("Course Management System");

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setSize(1280, 760);

        setMinimumSize(
                new Dimension(1024, 650)
        );

        setLocationRelativeTo(null);
        setResizable(true);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        add(
                createHeader(),
                BorderLayout.NORTH
        );

        mainContent =
                new ScrollableContentPanel();

        mainContent.setLayout(
                new BoxLayout(
                        mainContent,
                        BoxLayout.Y_AXIS
                )
        );

        mainContent.setBackground(Color.WHITE);

        homeSection = createHeroSection();
        courseSection = createCourseSection();
        programSection = createProgramSection();
        aboutSection = createAboutSection();
        contactSection = createFooterSection();

        mainContent.add(homeSection);
        mainContent.add(courseSection);
        mainContent.add(programSection);
        mainContent.add(aboutSection);
        mainContent.add(contactSection);

        scrollPane =
                new JScrollPane(mainContent);

        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        scrollPane.getViewport()
                .setBackground(Color.WHITE);

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {
        JPanel header =
                new JPanel(new BorderLayout());

        header.setBackground(Color.WHITE);

        header.setPreferredSize(
                new Dimension(100, 64)
        );

        header.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0,
                                0,
                                1,
                                0,
                                BORDER_COLOR
                        ),
                        new EmptyBorder(
                                8,
                                42,
                                8,
                                42
                        )
                )
        );

        header.add(
                createLogoPanel(),
                BorderLayout.WEST
        );

        header.add(
                createNavigationPanel(),
                BorderLayout.CENTER
        );

        JButton registerButton =
                createOutlineButton("Đăng ký");

        registerButton.setPreferredSize(
                new Dimension(120, 38)
        );

        registerButton.addActionListener(
                event -> openRegister()
        );

        JButton loginButton =
                createPrimaryButton("Đăng nhập");

        loginButton.setPreferredSize(
                new Dimension(132, 38)
        );

        loginButton.addActionListener(
                event -> openLogin()
        );

        JPanel actionPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT,
                                10,
                                0
                        )
                );

        actionPanel.setOpaque(false);

        actionPanel.add(registerButton);
        actionPanel.add(loginButton);

        header.add(
                actionPanel,
                BorderLayout.EAST
        );

        return header;
    }

    private JPanel createLogoPanel() {
        JPanel panel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                10,
                                0
                        )
                );

        panel.setOpaque(false);

        LogoPanel logo =
                new LogoPanel();

        logo.setPreferredSize(
                new Dimension(40, 40)
        );

        JLabel projectName =
                new JLabel(
                        "<html>"
                                + "<b>Course Management</b>"
                                + "<br>"
                                + "System"
                                + "</html>"
                );

        projectName.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        projectName.setForeground(TEXT_DARK);

        panel.add(logo);
        panel.add(projectName);

        return panel;
    }

    private JPanel createNavigationPanel() {
        JPanel navigation =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.CENTER,
                                24,
                                4
                        )
                );

        navigation.setOpaque(false);

        navigation.add(
                createNavigationButton(
                        "Trang chủ",
                        () -> scrollTo(homeSection)
                )
        );

        navigation.add(
                createNavigationButton(
                        "Khóa học",
                        () -> scrollTo(courseSection)
                )
        );

        navigation.add(
                createNavigationButton(
                        "Chương trình",
                        () -> scrollTo(programSection)
                )
        );

        navigation.add(
                createNavigationButton(
                        "Giới thiệu",
                        () -> scrollTo(aboutSection)
                )
        );

        navigation.add(
                createNavigationButton(
                        "Liên hệ",
                        () -> scrollTo(contactSection)
                )
        );

        return navigation;
    }

    private JButton createNavigationButton(
            String text,
            Runnable action
    ) {
        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        button.setForeground(TEXT_DARK);
        button.setBackground(Color.WHITE);

        button.setBorder(
                new EmptyBorder(
                        8,
                        5,
                        8,
                        5
                )
        );

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addActionListener(
                event -> action.run()
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        button.setForeground(PRIMARY);
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        button.setForeground(TEXT_DARK);
                    }
                }
        );

        return button;
    }

    // =========================================================
    // HERO SECTION
    // =========================================================

    private JPanel createHeroSection() {
        GradientPanel hero =
                new GradientPanel(
                        new Color(247, 250, 255),
                        new Color(229, 239, 255)
                );

        hero.setLayout(
                new GridBagLayout()
        );

        hero.setBorder(
                new EmptyBorder(
                        32,
                        58,
                        32,
                        58
                )
        );

        hero.setPreferredSize(
                new Dimension(100, 340)
        );

        GridBagConstraints left =
                new GridBagConstraints();

        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 0.53;
        left.weighty = 1;
        left.fill = GridBagConstraints.BOTH;

        left.insets =
                new Insets(0, 0, 0, 30);

        GridBagConstraints right =
                new GridBagConstraints();

        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 0.47;
        right.weighty = 1;
        right.fill = GridBagConstraints.BOTH;

        hero.add(
                createHeroContent(),
                left
        );

        hero.add(
                createHeroIllustration(),
                right
        );

        return hero;
    }

    private JPanel createHeroContent() {
        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setOpaque(false);

        JLabel badge =
                new JLabel(
                        "  NỀN TẢNG QUẢN LÝ ĐÀO TẠO  "
                );

        badge.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        badge.setForeground(PRIMARY);
        badge.setBackground(PRIMARY_LIGHT);
        badge.setOpaque(true);

        badge.setBorder(
                new EmptyBorder(
                        6,
                        9,
                        6,
                        9
                )
        );

        badge.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel title =
                new JLabel(
                        "<html>"
                                + "Học tập hiện đại"
                                + "<br>"
                                + "<span style='color:#2563EB;'>"
                                + "Quản lý thông minh"
                                + "</span>"
                                + "</html>"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        34
                )
        );

        title.setForeground(TEXT_DARK);

        title.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel description =
                new JLabel(
                        "<html>"
                                + "<div style='width:430px;'>"
                                + "Hệ thống giúp đơn giản hóa việc quản lý "
                                + "khóa học, chương trình đào tạo và nâng cao "
                                + "chất lượng giảng dạy."
                                + "</div>"
                                + "</html>"
                );

        description.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        description.setForeground(TEXT_GRAY);

        description.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JPanel actions =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT,
                                12,
                                0
                        )
                );

        actions.setOpaque(false);

        actions.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JButton exploreButton =
                createPrimaryButton(
                        "Khám phá khóa học"
                );

        exploreButton.setPreferredSize(
                new Dimension(174, 40)
        );

        exploreButton.addActionListener(
                event -> scrollTo(courseSection)
        );

        JButton programButton =
                createOutlineButton(
                        "Xem chương trình"
                );

        programButton.setPreferredSize(
                new Dimension(168, 40)
        );

        programButton.addActionListener(
                event -> scrollTo(programSection)
        );

        actions.add(exploreButton);
        actions.add(programButton);

        panel.add(Box.createVerticalGlue());
        panel.add(badge);
        panel.add(Box.createVerticalStrut(15));
        panel.add(title);
        panel.add(Box.createVerticalStrut(13));
        panel.add(description);
        panel.add(Box.createVerticalStrut(22));
        panel.add(actions);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createHeroIllustration() {
        JPanel wrapper =
                new JPanel(new BorderLayout());

        wrapper.setOpaque(false);

        ImageIcon heroImage =
                loadImage(
                        "/images/landing-hero.png",
                        500,
                        280
                );

        if (heroImage != null) {
            JLabel imageLabel =
                    new JLabel(
                            heroImage,
                            SwingConstants.CENTER
                    );

            wrapper.add(
                    imageLabel,
                    BorderLayout.CENTER
            );

        } else {
            EducationIllustration illustration =
                    new EducationIllustration();

            wrapper.add(
                    illustration,
                    BorderLayout.CENTER
            );
        }

        return wrapper;
    }

    // =========================================================
    // COURSE SECTION
    // =========================================================

    private JPanel createCourseSection() {
        JPanel section = new JPanel();

        section.setLayout(
                new BoxLayout(
                        section,
                        BoxLayout.Y_AXIS
                )
        );

        section.setBackground(Color.WHITE);

        section.setBorder(
                new EmptyBorder(
                        36,
                        54,
                        42,
                        54
                )
        );

        JLabel title =
                createSectionTitle(
                        "Khóa học nổi bật"
                );

        JLabel subtitle =
                createSectionSubtitle(
                        "Các khóa học đang hoạt động trong hệ thống"
                );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        courseStatusLabel =
                new JLabel(
                        "Đang tải dữ liệu khóa học...",
                        SwingConstants.CENTER
                );

        courseStatusLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        courseStatusLabel.setForeground(TEXT_GRAY);

        courseStatusLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        courseCardsPanel = new JPanel();

        courseCardsPanel.setLayout(
                new GridLayout(
                        1,
                        4,
                        18,
                        0
                )
        );

        courseCardsPanel.setBackground(Color.WHITE);

        courseCardsPanel.setBorder(
                new EmptyBorder(
                        24,
                        0,
                        0,
                        0
                )
        );

        courseCardsPanel.add(
                courseStatusLabel
        );

        section.add(title);
        section.add(Box.createVerticalStrut(6));
        section.add(subtitle);
        section.add(courseCardsPanel);

        return section;
    }

    /**
     * Được LandingController gọi sau khi lấy dữ liệu từ SQL Server.
     */
    public void displayCourses(
            List<Course> courses
    ) {
        courseCardsPanel.removeAll();

        if (
                courses == null
                        || courses.isEmpty()
        ) {
            showEmptyCourseMessage();

        } else {
            int columns =
                    Math.min(4, courses.size());

            courseCardsPanel.setLayout(
                    new GridLayout(
                            1,
                            columns,
                            18,
                            0
                    )
            );

            for (Course course : courses) {
                PublicCourseCard card =
                        new PublicCourseCard(
                                course,
                                () -> openCourseDetail(course)
                        );

                courseCardsPanel.add(card);
            }
        }

        courseCardsPanel.revalidate();
        courseCardsPanel.repaint();

        mainContent.revalidate();
        mainContent.repaint();
    }

    private void showEmptyCourseMessage() {
        courseCardsPanel.setLayout(
                new BorderLayout()
        );

        JLabel emptyLabel =
                new JLabel(
                        "Hiện chưa có khóa học đang hoạt động.",
                        SwingConstants.CENTER
                );

        emptyLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        emptyLabel.setForeground(TEXT_GRAY);

        emptyLabel.setBorder(
                new EmptyBorder(
                        35,
                        10,
                        35,
                        10
                )
        );

        courseCardsPanel.add(
                emptyLabel,
                BorderLayout.CENTER
        );
    }

    private void openCourseDetail(
            Course course
    ) {
        if (course == null) {
            return;
        }

        String description =
                course.getDescription();

        if (
                description == null
                        || description.isBlank()
        ) {
            description =
                    "Thông tin đang được cập nhật.";
        }

        String tuitionFee =
                course.getTuitionFee() == null
                        ? "Liên hệ"
                        : String.format(
                        "%,.0fđ",
                        course.getTuitionFee()
                );

        String content =
                """
                Mã khóa học: %s

                Tên khóa học: %s

                Mô tả:
                %s

                Số tín chỉ: %d

                Học phí: %s
                """.formatted(
                        course.getCourseCode(),
                        course.getCourseName(),
                        description,
                        course.getCredits(),
                        tuitionFee
                );

        JTextArea textArea =
                new JTextArea(content);

        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);

        textArea.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        14
                )
        );

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        textArea.setPreferredSize(
                new Dimension(460, 260)
        );

        JOptionPane.showMessageDialog(
                this,
                textArea,
                "Chi tiết khóa học",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public void showCourseLoadingError(
            String message
    ) {
        displayCourses(List.of());

        JOptionPane.showMessageDialog(
                this,
                message,
                "Không thể tải khóa học",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =========================================================
    // PROGRAM SECTION
    // =========================================================

    private JPanel createProgramSection() {
        JPanel section = new JPanel();

        section.setLayout(
                new BoxLayout(
                        section,
                        BoxLayout.Y_AXIS
                )
        );

        section.setBackground(
                SECTION_BACKGROUND
        );

        section.setBorder(
                new EmptyBorder(
                        36,
                        54,
                        42,
                        54
                )
        );

        JLabel title =
                createSectionTitle(
                        "Chương trình đào tạo"
                );

        JLabel subtitle =
                createSectionSubtitle(
                        "Lựa chọn chương trình phù hợp với mục tiêu học tập"
                );

        title.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        subtitle.setAlignmentX(
                Component.CENTER_ALIGNMENT
        );

        JPanel programs =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                18,
                                0
                        )
                );

        programs.setOpaque(false);

        programs.setBorder(
                new EmptyBorder(
                        24,
                        0,
                        0,
                        0
                )
        );

        programs.add(
                createProgramCard(
                        "01",
                        "Đào tạo ngắn hạn",
                        "Khóa học tập trung, giúp tiếp cận kiến thức nhanh chóng."
                )
        );

        programs.add(
                createProgramCard(
                        "02",
                        "Chứng chỉ chuyên nghiệp",
                        "Chương trình chuyên sâu phù hợp với từng lĩnh vực."
                )
        );

        programs.add(
                createProgramCard(
                        "03",
                        "Đào tạo doanh nghiệp",
                        "Giải pháp đào tạo phù hợp với nhu cầu của tổ chức."
                )
        );

        programs.add(
                createProgramCard(
                        "04",
                        "Học tập linh hoạt",
                        "Tiếp cận nội dung học tập thuận tiện và chủ động."
                )
        );

        section.add(title);
        section.add(Box.createVerticalStrut(6));
        section.add(subtitle);
        section.add(programs);

        return section;
    }

    private JPanel createProgramCard(
            String number,
            String title,
            String description
    ) {
        RoundedPanel card =
                new RoundedPanel(
                        15,
                        Color.WHITE,
                        false
                );

        card.setLayout(new BorderLayout());

        card.setBorder(
                new EmptyBorder(
                        18,
                        18,
                        18,
                        18
                )
        );

        JLabel numberLabel =
                new JLabel(
                        number,
                        SwingConstants.CENTER
                );

        numberLabel.setOpaque(true);
        numberLabel.setBackground(PRIMARY_LIGHT);
        numberLabel.setForeground(PRIMARY);

        numberLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        14
                )
        );

        numberLabel.setPreferredSize(
                new Dimension(42, 42)
        );

        JPanel textPanel = new JPanel();

        textPanel.setLayout(
                new BoxLayout(
                        textPanel,
                        BoxLayout.Y_AXIS
                )
        );

        textPanel.setOpaque(false);

        textPanel.setBorder(
                new EmptyBorder(
                        0,
                        14,
                        0,
                        0
                )
        );

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        titleLabel.setForeground(TEXT_DARK);

        JLabel descriptionLabel =
                new JLabel(
                        "<html>"
                                + "<div style='width:175px;'>"
                                + description
                                + "</div>"
                                + "</html>"
                );

        descriptionLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        descriptionLabel.setForeground(TEXT_GRAY);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(7));
        textPanel.add(descriptionLabel);

        card.add(
                numberLabel,
                BorderLayout.WEST
        );

        card.add(
                textPanel,
                BorderLayout.CENTER
        );

        return card;
    }

    // =========================================================
    // ABOUT SECTION
    // =========================================================

    private JPanel createAboutSection() {
        JPanel section =
                new JPanel(new GridBagLayout());

        section.setBackground(Color.WHITE);

        section.setBorder(
                new EmptyBorder(
                        44,
                        64,
                        44,
                        64
                )
        );

        GridBagConstraints left =
                new GridBagConstraints();

        left.gridx = 0;
        left.gridy = 0;
        left.weightx = 0.42;
        left.weighty = 1;
        left.fill = GridBagConstraints.BOTH;

        left.insets =
                new Insets(0, 0, 0, 34);

        GridBagConstraints right =
                new GridBagConstraints();

        right.gridx = 1;
        right.gridy = 0;
        right.weightx = 0.58;
        right.weighty = 1;
        right.fill = GridBagConstraints.BOTH;

        RoundedPanel illustration =
                new RoundedPanel(
                        20,
                        PRIMARY_LIGHT,
                        false
                );

        illustration.setLayout(
                new GridBagLayout()
        );

        JLabel illustrationLabel =
                new JLabel(
                        "<html>"
                                + "<div style='text-align:center;'>"
                                + "<span style='font-size:28px;"
                                + "color:#2563EB;'>CMS</span>"
                                + "<br>"
                                + "<span style='font-size:13px;"
                                + "color:#5B6981;'>"
                                + "Education Management"
                                + "</span>"
                                + "</div>"
                                + "</html>"
                );

        illustration.add(illustrationLabel);

        JPanel content = new JPanel();

        content.setLayout(
                new BoxLayout(
                        content,
                        BoxLayout.Y_AXIS
                )
        );

        content.setOpaque(false);

        JLabel smallTitle =
                new JLabel(
                        "GIỚI THIỆU HỆ THỐNG"
                );

        smallTitle.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        11
                )
        );

        smallTitle.setForeground(PRIMARY);

        JLabel title =
                new JLabel(
                        "<html>"
                                + "Giải pháp quản lý đào tạo"
                                + "<br>"
                                + "toàn diện và hiện đại"
                                + "</html>"
                );

        title.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        25
                )
        );

        title.setForeground(TEXT_DARK);

        JLabel description =
                new JLabel(
                        "<html>"
                                + "<div style='width:470px;'>"
                                + "Course Management System hỗ trợ quản lý "
                                + "khóa học, lớp học, học viên, giảng viên, "
                                + "điểm số và thanh toán trên một nền tảng "
                                + "thống nhất."
                                + "<br><br>"
                                + "Ứng dụng giúp giảm công việc thủ công, "
                                + "nâng cao độ chính xác và tối ưu quy trình "
                                + "quản lý giáo dục."
                                + "</div>"
                                + "</html>"
                );

        description.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        description.setForeground(TEXT_GRAY);

        content.add(Box.createVerticalGlue());
        content.add(smallTitle);
        content.add(Box.createVerticalStrut(10));
        content.add(title);
        content.add(Box.createVerticalStrut(15));
        content.add(description);
        content.add(Box.createVerticalGlue());

        section.add(
                illustration,
                left
        );

        section.add(
                content,
                right
        );

        return section;
    }

    // =========================================================
    // FOOTER
    // =========================================================

    private JPanel createFooterSection() {
        JPanel footer =
                new JPanel(
                        new GridLayout(
                                1,
                                4,
                                35,
                                0
                        )
                );

        footer.setBackground(
                FOOTER_BACKGROUND
        );

        footer.setBorder(
                new EmptyBorder(
                        30,
                        54,
                        32,
                        54
                )
        );

        footer.add(
                createFooterColumn(
                        "Course Management System",
                        "Nền tảng quản lý đào tạo toàn diện.\n"
                                + "Tối ưu hóa việc học tập và giảng dạy."
                )
        );

        footer.add(
                createFooterColumn(
                        "Khám phá",
                        "Khóa học\n"
                                + "Chương trình\n"
                                + "Giới thiệu\n"
                                + "Liên hệ"
                )
        );

        footer.add(
                createFooterColumn(
                        "Hỗ trợ",
                        "Câu hỏi thường gặp\n"
                                + "Hướng dẫn sử dụng\n"
                                + "Chính sách bảo mật\n"
                                + "Điều khoản sử dụng"
                )
        );

        footer.add(
                createFooterColumn(
                        "Liên hệ",
                        "TP. Hồ Chí Minh\n"
                                + "(028) 1234 5678\n"
                                + "support@cms.edu.vn"
                )
        );

        return footer;
    }

    private JPanel createFooterColumn(
            String title,
            String content
    ) {
        JPanel column = new JPanel();

        column.setLayout(
                new BoxLayout(
                        column,
                        BoxLayout.Y_AXIS
                )
        );

        column.setOpaque(false);

        JLabel titleLabel =
                new JLabel(title);

        titleLabel.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        titleLabel.setForeground(Color.WHITE);

        JTextArea contentArea =
                new JTextArea(content);

        contentArea.setEditable(false);
        contentArea.setFocusable(false);
        contentArea.setOpaque(false);

        contentArea.setForeground(
                new Color(203, 213, 225)
        );

        contentArea.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        11
                )
        );

        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);

        column.add(titleLabel);
        column.add(Box.createVerticalStrut(11));
        column.add(contentArea);

        return column;
    }

    // =========================================================
    // COMPONENT DÙNG CHUNG
    // =========================================================

    private JLabel createSectionTitle(
            String text
    ) {
        JLabel label = new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        23
                )
        );

        label.setForeground(TEXT_DARK);

        return label;
    }

    private JLabel createSectionSubtitle(
            String text
    ) {
        JLabel label = new JLabel(text);

        label.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        13
                )
        );

        label.setForeground(TEXT_GRAY);

        return label;
    }

    private JButton createPrimaryButton(
            String text
    ) {
        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        button.setBackground(
                                PRIMARY_HOVER
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        button.setBackground(PRIMARY);
                    }
                }
        );

        return button;
    }

    private JButton createOutlineButton(
            String text
    ) {
        JButton button = new JButton(text);

        button.setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        13
                )
        );

        button.setForeground(PRIMARY);
        button.setBackground(Color.WHITE);

        button.setFocusPainted(false);

        button.setBorder(
                BorderFactory.createLineBorder(
                        PRIMARY,
                        1
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.addMouseListener(
                new MouseAdapter() {

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        button.setBackground(
                                PRIMARY_LIGHT
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        button.setBackground(Color.WHITE);
                    }
                }
        );

        return button;
    }

    private ImageIcon loadImage(
            String resourcePath,
            int width,
            int height
    ) {
        try {
            URL url =
                    getClass().getResource(
                            resourcePath
                    );

            if (url == null) {
                return null;
            }

            Image original =
                    new ImageIcon(url).getImage();

            Image scaled =
                    original.getScaledInstance(
                            width,
                            height,
                            Image.SCALE_SMOOTH
                    );

            return new ImageIcon(scaled);

        } catch (Exception exception) {
            return null;
        }
    }

    private void openLogin() {
        if (loginAction != null) {
            loginAction.run();

            /*
             * Không dispose Landing Page.
             * Khi đóng Login có thể mở Landing Page lại.
             */
            setVisible(false);
        }
    }
    private void openRegister() {

        if (registerAction != null) {

            registerAction.run();

            setVisible(false);
        }
    }

    private void scrollTo(
            JPanel target
    ) {
        if (
                target == null
                        || scrollPane == null
                        || mainContent == null
        ) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            Point point =
                    SwingUtilities.convertPoint(
                            target.getParent(),
                            target.getLocation(),
                            mainContent
                    );

            scrollPane.getVerticalScrollBar()
                    .setValue(
                            Math.max(
                                    0,
                                    point.y - 8
                            )
                    );
        });
    }

    // =========================================================
    // CUSTOM COMPONENTS
    // =========================================================

    private static class ScrollableContentPanel
            extends JPanel
            implements Scrollable {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(
                Rectangle visibleRect,
                int orientation,
                int direction
        ) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(
                Rectangle visibleRect,
                int orientation,
                int direction
        ) {
            return 100;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    private static class GradientPanel
            extends JPanel {

        private final Color startColor;
        private final Color endColor;

        public GradientPanel(
                Color startColor,
                Color endColor
        ) {
            this.startColor = startColor;
            this.endColor = endColor;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            Graphics2D graphics2D =
                    (Graphics2D) graphics.create();

            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            GradientPaint paint =
                    new GradientPaint(
                            0,
                            0,
                            startColor,
                            getWidth(),
                            getHeight(),
                            endColor
                    );

            graphics2D.setPaint(paint);

            graphics2D.fillRect(
                    0,
                    0,
                    getWidth(),
                    getHeight()
            );

            graphics2D.dispose();

            super.paintComponent(graphics);
        }
    }

    private static class RoundedPanel
            extends JPanel {

        private final int radius;
        private final Color backgroundColor;
        private final boolean drawShadow;

        public RoundedPanel(
                int radius,
                Color backgroundColor,
                boolean drawShadow
        ) {
            this.radius = radius;
            this.backgroundColor = backgroundColor;
            this.drawShadow = drawShadow;

            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            Graphics2D graphics2D =
                    (Graphics2D) graphics.create();

            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            if (drawShadow) {
                graphics2D.setColor(
                        new Color(
                                15,
                                23,
                                42,
                                20
                        )
                );

                graphics2D.fillRoundRect(
                        3,
                        4,
                        getWidth() - 6,
                        getHeight() - 6,
                        radius,
                        radius
                );
            }

            graphics2D.setColor(
                    backgroundColor
            );

            graphics2D.fillRoundRect(
                    1,
                    1,
                    getWidth() - 4,
                    getHeight() - 5,
                    radius,
                    radius
            );

            graphics2D.dispose();

            super.paintComponent(graphics);
        }
    }

    private static class LogoPanel
            extends JPanel {

        public LogoPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            super.paintComponent(graphics);

            Graphics2D graphics2D =
                    (Graphics2D) graphics.create();

            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            graphics2D.setColor(PRIMARY);

            graphics2D.fillRoundRect(
                    3,
                    3,
                    width - 6,
                    height - 6,
                    11,
                    11
            );

            graphics2D.setColor(Color.WHITE);

            graphics2D.setFont(
                    new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    )
            );

            FontMetrics metrics =
                    graphics2D.getFontMetrics();

            String text = "CMS";

            int x =
                    (width
                            - metrics.stringWidth(text))
                            / 2;

            int y =
                    (
                            height
                                    - metrics.getHeight()
                    ) / 2
                            + metrics.getAscent();

            graphics2D.drawString(
                    text,
                    x,
                    y
            );

            graphics2D.dispose();
        }
    }

    private static class EducationIllustration
            extends JPanel {

        public EducationIllustration() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(
                Graphics graphics
        ) {
            super.paintComponent(graphics);

            Graphics2D graphics2D =
                    (Graphics2D) graphics.create();

            graphics2D.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            int laptopWidth =
                    Math.min(
                            360,
                            Math.max(260, width - 70)
                    );

            int laptopHeight = 185;

            int laptopX =
                    (width - laptopWidth) / 2;

            int laptopY =
                    Math.max(
                            22,
                            (height - laptopHeight) / 2
                    );

            graphics2D.setColor(
                    new Color(45, 55, 72)
            );

            graphics2D.fillRoundRect(
                    laptopX,
                    laptopY,
                    laptopWidth,
                    laptopHeight - 25,
                    14,
                    14
            );

            graphics2D.setColor(
                    new Color(245, 248, 255)
            );

            graphics2D.fillRoundRect(
                    laptopX + 12,
                    laptopY + 12,
                    laptopWidth - 24,
                    laptopHeight - 49,
                    8,
                    8
            );

            graphics2D.setColor(PRIMARY);

            graphics2D.fillRoundRect(
                    laptopX + 30,
                    laptopY + 30,
                    laptopWidth - 60,
                    27,
                    8,
                    8
            );

            graphics2D.setColor(
                    new Color(207, 222, 247)
            );

            for (
                    int index = 0;
                    index < 3;
                    index++
            ) {
                graphics2D.fillRoundRect(
                        laptopX + 30,
                        laptopY + 72
                                + index * 23,
                        laptopWidth - 105
                                + index * 14,
                        10,
                        7,
                        7
                );
            }

            Polygon base =
                    new Polygon();

            base.addPoint(
                    laptopX - 20,
                    laptopY + laptopHeight - 25
            );

            base.addPoint(
                    laptopX + laptopWidth + 20,
                    laptopY + laptopHeight - 25
            );

            base.addPoint(
                    laptopX + laptopWidth - 5,
                    laptopY + laptopHeight
            );

            base.addPoint(
                    laptopX + 5,
                    laptopY + laptopHeight
            );

            graphics2D.setColor(
                    new Color(94, 105, 125)
            );

            graphics2D.fillPolygon(base);

            graphics2D.dispose();
        }
    }

}
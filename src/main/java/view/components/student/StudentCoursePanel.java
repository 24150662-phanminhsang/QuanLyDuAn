package view.components.student;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class StudentCoursePanel extends ContentCard {

    private final JPanel courseListPanel;

    private List<CourseProgressItem> courses =
            new ArrayList<>();

    private Consumer<Integer> courseActionHandler =
            courseId -> {
            };

    public StudentCoursePanel() {
        courseListPanel = new JPanel();
        courseListPanel.setOpaque(false);

        initializeView();
        refreshCourses();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[]"
                )
        );

        setMinimumSize(
                new Dimension(420, 260)
        );

        setPreferredSize(
                new Dimension(650, 350)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        courseListPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]10[]10[]"
                )
        );

        add(
                courseListPanel,
                "growx"
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[][grow][]",
                        "[center]"
                )
        );

        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.BOOK_OPEN,
                        16,
                        UIConstants.SUCCESS
                )
        );

        JLabel titleLabel = new JLabel(
                "Khóa học của tôi"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Tiến độ học tập"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(iconLabel);

        panel.add(
                titleLabel,
                "gapleft 7"
        );

        panel.add(
                subtitleLabel,
                "alignx right"
        );

        return panel;
    }

    public void setCourseActionHandler(
            Consumer<Integer> handler
    ) {
        courseActionHandler =
                handler == null
                        ? courseId -> {
                }
                        : handler;
    }

    public void setCourses(
            List<CourseProgressItem> courseItems
    ) {
        courses =
                courseItems == null
                        ? new ArrayList<>()
                        : new ArrayList<>(courseItems);

        refreshCourses();
    }

    public void addCourse(
            CourseProgressItem course
    ) {
        if (course == null) {
            return;
        }

        courses.add(course);
        refreshCourses();
    }

    public void clearCourses() {
        courses.clear();
        refreshCourses();
    }

    public List<CourseProgressItem> getCourses() {
        return Collections.unmodifiableList(
                courses
        );
    }

    public void refreshCourses() {
        courseListPanel.removeAll();

        if (courses.isEmpty()) {
            courseListPanel.add(
                    createEmptyState(),
                    "growx"
            );
        } else {
            for (CourseProgressItem course : courses) {
                courseListPanel.add(
                        createCourseRow(course),
                        "growx"
                );
            }
        }

        courseListPanel.revalidate();
        courseListPanel.repaint();
    }

    private JPanel createCourseRow(
            CourseProgressItem course
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 11 12",
                        "[44!]12[grow, fill]12[88!]",
                        "[]3[]8[]3[]"
                )
        );

        row.setBackground(
                UIConstants.BACKGROUND
        );

        row.putClientProperty(
                "FlatLaf.style",
                "arc: 14; borderWidth: 0"
        );

        JPanel iconPanel =
                createCourseIconPanel();

        JLabel courseNameLabel =
                createLimitedLabel(
                        safeText(
                                course.courseName(),
                                "Khóa học"
                        ),
                        UIConstants.FONT_MEDIUM,
                        UIConstants.TEXT_PRIMARY
                );

        JLabel teacherLabel =
                createLimitedLabel(
                        safeText(
                                course.teacherName(),
                                "Chưa có giảng viên"
                        ),
                        UIConstants.FONT_SMALL,
                        UIConstants.TEXT_SECONDARY
                );

        JProgressBar progressBar =
                createProgressBar(
                        course.progressPercent()
                );

        JLabel progressLabel = new JLabel(
                "Đã hoàn thành "
                        + clampProgress(
                        course.progressPercent()
                )
                        + "%"
        );

        progressLabel.setFont(
                UIConstants.FONT_SMALL
        );

        progressLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JButton continueButton =
                createContinueButton(
                        course.courseId()
                );

        row.add(
                iconPanel,
                "span 1 4, aligny top"
        );

        row.add(
                courseNameLabel,
                "growx"
        );

        row.add(
                continueButton,
                "span 1 4, alignx right, aligny center"
        );

        row.add(
                teacherLabel,
                "growx, wrap"
        );

        row.add(
                progressBar,
                "growx, height 8!, wrap"
        );

        row.add(
                progressLabel,
                "growx"
        );

        return row;
    }

    private JPanel createCourseIconPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        panel.setPreferredSize(
                new Dimension(44, 44)
        );

        panel.setMinimumSize(
                new Dimension(44, 44)
        );

        panel.setMaximumSize(
                new Dimension(44, 44)
        );

        panel.setBackground(
                UIConstants.SUCCESS_LIGHT
        );

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 12; borderWidth: 0"
        );

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.BOOK,
                                18,
                                UIConstants.SUCCESS
                        )
                )
        );

        return panel;
    }

    private JProgressBar createProgressBar(
            int progress
    ) {
        JProgressBar progressBar =
                new JProgressBar(0, 100);

        progressBar.setValue(
                clampProgress(progress)
        );

        progressBar.setStringPainted(false);

        progressBar.setForeground(
                UIConstants.PRIMARY
        );

        progressBar.setBackground(
                UIConstants.BORDER
        );

        progressBar.putClientProperty(
                "FlatLaf.style",
                "arc: 999"
        );

        return progressBar;
    }

    private JButton createContinueButton(
            int courseId
    ) {
        JButton button = new JButton(
                "Tiếp tục"
        );

        button.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        button.setForeground(
                UIConstants.PRIMARY
        );

        button.setBackground(
                UIConstants.PRIMARY_LIGHT
        );

        button.setFocusable(false);

        button.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        button.setPreferredSize(
                new Dimension(88, 34)
        );

        button.setMinimumSize(
                new Dimension(88, 34)
        );

        button.setBorder(
                BorderFactory.createEmptyBorder(
                        7,
                        10,
                        7,
                        10
                )
        );

        button.putClientProperty(
                "FlatLaf.style",
                """
                arc: 10;
                borderWidth: 0;
                focusWidth: 0;
                """
        );

        button.addActionListener(
                event ->
                        courseActionHandler.accept(
                                courseId
                        )
        );

        return button;
    }

    private JLabel createLimitedLabel(
            String text,
            Font font,
            java.awt.Color color
    ) {
        JLabel label = new JLabel(text);

        label.setFont(font);
        label.setForeground(color);

        label.setToolTipText(text);

        return label;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 30",
                        "[center]",
                        "[]10[]"
                )
        );

        panel.setOpaque(false);

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.BOOK_OPEN,
                                28,
                                UIConstants.TEXT_SECONDARY
                        )
                )
        );

        JLabel messageLabel = new JLabel(
                "Bạn chưa tham gia khóa học nào"
        );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(messageLabel);

        return panel;
    }

    private int clampProgress(
            int progress
    ) {
        return Math.max(
                0,
                Math.min(progress, 100)
        );
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        return value == null || value.isBlank()
                ? defaultValue
                : value.trim();
    }

    public record CourseProgressItem(
            int courseId,
            String courseName,
            String teacherName,
            int progressPercent
    ) {
    }
}
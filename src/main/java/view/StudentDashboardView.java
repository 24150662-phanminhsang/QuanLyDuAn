package view;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import util.UIConstants;
import view.components.ScrollablePanel;
import view.components.StatCard;
import view.components.student.StudentCoursePanel;
import view.components.student.StudentNotificationPanel;
import view.components.student.StudentResultPanel;
import view.components.student.StudentSchedulePanel;
import view.components.student.UpcomingAssignmentPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentDashboardView extends JPanel {

    private final JLabel greetingLabel;
    private final JLabel dateLabel;

    private final StatCard courseCountCard;
    private final StatCard todayScheduleCard;
    private final StatCard assignmentCard;
    private final StatCard averageScoreCard;

    private final StudentSchedulePanel schedulePanel;
    private final StudentCoursePanel coursePanel;
    private final StudentNotificationPanel notificationPanel;
    private final StudentResultPanel resultPanel;
    private final UpcomingAssignmentPanel assignmentPanel;

    public StudentDashboardView() {
        greetingLabel = new JLabel(
                "Xin chào, Học viên!"
        );

        dateLabel = new JLabel();

        courseCountCard = new StatCard(
                "Khóa học",
                "Đang tham gia",
                FontAwesomeSolid.BOOK_OPEN,
                UIConstants.PRIMARY,
                UIConstants.PRIMARY_LIGHT
        );

        todayScheduleCard = new StatCard(
                "Lịch hôm nay",
                "Buổi học trong ngày",
                FontAwesomeSolid.CALENDAR_ALT,
                UIConstants.SUCCESS,
                UIConstants.SUCCESS_LIGHT
        );

        assignmentCard = new StatCard(
                "Bài tập",
                "Chưa hoàn thành",
                FontAwesomeSolid.FILE_ALT,
                UIConstants.WARNING,
                UIConstants.WARNING_LIGHT
        );

        averageScoreCard = new StatCard(
                "Điểm trung bình",
                "Kết quả hiện tại",
                FontAwesomeSolid.STAR,
                UIConstants.PURPLE,
                UIConstants.PURPLE_LIGHT
        );

        schedulePanel =
                new StudentSchedulePanel();

        coursePanel =
                new StudentCoursePanel();

        notificationPanel =
                new StudentNotificationPanel();

        resultPanel =
                new StudentResultPanel();

        assignmentPanel =
                new UpcomingAssignmentPanel();

        initializeView();

    }

    private void initializeView() {
        setLayout(new BorderLayout());

        setBackground(
                UIConstants.BACKGROUND
        );

        ScrollablePanel contentPanel =
                new ScrollablePanel();

        contentPanel.setBackground(
                UIConstants.BACKGROUND
        );

        contentPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 18 20 24 20",
                        "[grow, fill]",
                        "[]14[]14[]14[]"
                )
        );

        contentPanel.add(
                createHeaderPanel(),
                "growx"
        );

        contentPanel.add(
                createStatisticPanel(),
                "growx"
        );

        contentPanel.add(
                createMainContentPanel(),
                "growx"
        );

        contentPanel.add(
                createBottomContentPanel(),
                "growx"
        );

        JScrollPane scrollPane =
                new JScrollPane(contentPanel);

        scrollPane.setBorder(
                BorderFactory.createEmptyBorder()
        );

        scrollPane.setBackground(
                UIConstants.BACKGROUND
        );

        scrollPane.getViewport().setBackground(
                UIConstants.BACKGROUND
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(18);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        add(
                scrollPane,
                BorderLayout.CENTER
        );
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill][]",
                        "[]5[]"
                )
        );

        panel.setOpaque(false);

        greetingLabel.setFont(
                UIConstants.FONT_TITLE
        );

        greetingLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Theo dõi lịch học, tiến độ và kết quả học tập của bạn."
        );

        subtitleLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        dateLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        dateLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        dateLabel.setText(
                LocalDate.now().format(
                        DateTimeFormatter.ofPattern(
                                "'Ngày' dd/MM/yyyy"
                        )
                )
        );

        panel.add(greetingLabel);
        panel.add(
                dateLabel,
                "span 1 2, alignx right, aligny top"
        );

        panel.add(
                subtitleLabel
        );

        return panel;
    }

    private JPanel createStatisticPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow, fill]10"
                                + "[grow, fill]10"
                                + "[grow, fill]10"
                                + "[grow, fill]",
                        "[]"
                )
        );

        panel.setOpaque(false);

        panel.add(courseCountCard, "growx");
        panel.add(todayScheduleCard, "growx");
        panel.add(assignmentCard, "growx");
        panel.add(averageScoreCard, "growx");

        return panel;
    }

    private JPanel createMainContentPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, insets 0",
                        "[grow 62, fill]16[grow 38, fill]",
                        "[top]"
                )
        );

        panel.setOpaque(false);

        panel.add(
                schedulePanel,
                "growx, aligny top"
        );

        panel.add(
                notificationPanel,
                "growx, aligny top"
        );

        return panel;
    }
    private JPanel createBottomContentPanel() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fillx, wrap 2, insets 0",
                        "[grow 58, fill]16[grow 42, fill]",
                        "[top]16[top]"
                )
        );

        panel.setOpaque(false);

        panel.add(
                coursePanel,
                "growx, aligny top"
        );

        panel.add(
                resultPanel,
                "growx, aligny top"
        );

        panel.add(
                assignmentPanel,
                "span 2, growx, aligny top"
        );

        return panel;
    }

    public void setStudentName(
            String studentName
    ) {
        String displayName =
                studentName == null
                        || studentName.isBlank()
                        ? "Học viên"
                        : studentName.trim();

        greetingLabel.setText(
                "Xin chào, "
                        + displayName
                        + "!"
        );
    }

    public void setCourseCount(
            int count
    ) {
        courseCountCard.setValue(count);
    }

    public void setTodayScheduleCount(
            int count
    ) {
        todayScheduleCard.setValue(count);
    }

    public void setPendingAssignmentCount(
            int count
    ) {
        assignmentCard.setValue(count);
    }

    public void setAverageScore(
            double score
    ) {
        averageScoreCard.setValue(
                String.format("%.1f", score)
        );

        resultPanel.setAverageScore(score);
    }

    public void setScheduleItems(
            List<StudentSchedulePanel.ScheduleItem>
                    items
    ) {
        schedulePanel.setScheduleItems(items);

        todayScheduleCard.setValue(
                items == null
                        ? 0
                        : items.size()
        );
    }

    public void setCourses(
            List<StudentCoursePanel.CourseProgressItem>
                    courses
    ) {
        coursePanel.setCourses(courses);

        courseCountCard.setValue(
                courses == null
                        ? 0
                        : courses.size()
        );
    }

    public void setNotifications(
            List<StudentNotificationPanel
                    .StudentNotificationItem> items
    ) {
        notificationPanel.setNotifications(
                items
        );
    }

    public void setResults(
            List<StudentResultPanel.ResultItem>
                    results
    ) {
        resultPanel.setResults(results);

        if (results == null || results.isEmpty()) {
            setAverageScore(0);
            return;
        }

        double average =
                results.stream()
                        .mapToDouble(
                                StudentResultPanel
                                        .ResultItem::score
                        )
                        .average()
                        .orElse(0);

        setAverageScore(average);
    }

    public void setAssignments(
            List<UpcomingAssignmentPanel
                    .AssignmentItem> assignments
    ) {
        assignmentPanel.setAssignments(
                assignments
        );

        assignmentCard.setValue(
                assignments == null
                        ? 0
                        : assignments.size()
        );
    }

    public void setCourseActionHandler(
            java.util.function.Consumer<Integer>
                    handler
    ) {
        coursePanel.setCourseActionHandler(
                handler
        );
    }

    public void setAssignmentActionHandler(
            java.util.function.Consumer<Integer>
                    handler
    ) {
        assignmentPanel
                .setAssignmentActionHandler(
                        handler
                );
    }

    public StudentSchedulePanel
    getSchedulePanel() {
        return schedulePanel;
    }

    public StudentCoursePanel
    getCoursePanel() {
        return coursePanel;
    }

    public StudentNotificationPanel
    getNotificationPanel() {
        return notificationPanel;
    }

    public StudentResultPanel
    getResultPanel() {
        return resultPanel;
    }

    public UpcomingAssignmentPanel
    getAssignmentPanel() {
        return assignmentPanel;
    }


}
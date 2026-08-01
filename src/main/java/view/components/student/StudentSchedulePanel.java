package view.components.student;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentSchedulePanel extends ContentCard {

    private final JPanel scheduleListPanel;
    private final JLabel dateLabel;

    private List<ScheduleItem> scheduleItems =
            new ArrayList<>();

    public StudentSchedulePanel() {
        scheduleListPanel = new JPanel();
        scheduleListPanel.setOpaque(false);

        dateLabel = new JLabel();

        initializeView();
        setCurrentDateText("Lịch học hôm nay");
        refreshSchedule();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]14[grow, fill]"
                )
        );

        setMinimumSize(
                new Dimension(360, 280)
        );

        setPreferredSize(
                new Dimension(620, 320)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        scheduleListPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]8[]8[]"
                )
        );

        add(
                scheduleListPanel,
                "grow, push"
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
                        FontAwesomeSolid.CALENDAR_ALT,
                        16,
                        UIConstants.PRIMARY
                )
        );

        JLabel titleLabel = new JLabel(
                "Lịch học"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        dateLabel.setFont(
                UIConstants.FONT_SMALL
        );

        dateLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(iconLabel);
        panel.add(
                titleLabel,
                "gapleft 7"
        );
        panel.add(
                dateLabel,
                "align right"
        );

        return panel;
    }

    public void setCurrentDateText(
            String text
    ) {
        dateLabel.setText(
                text == null || text.isBlank()
                        ? "Lịch học hôm nay"
                        : text.trim()
        );
    }

    public void setScheduleItems(
            List<ScheduleItem> items
    ) {
        scheduleItems =
                items == null
                        ? new ArrayList<>()
                        : new ArrayList<>(items);

        refreshSchedule();
    }

    public void addScheduleItem(
            ScheduleItem item
    ) {
        if (item == null) {
            return;
        }

        scheduleItems.add(item);
        refreshSchedule();
    }

    public void clearSchedule() {
        scheduleItems.clear();
        refreshSchedule();
    }

    public List<ScheduleItem> getScheduleItems() {
        return Collections.unmodifiableList(
                scheduleItems
        );
    }

    public void refreshSchedule() {
        scheduleListPanel.removeAll();

        if (scheduleItems.isEmpty()) {
            scheduleListPanel.add(
                    createEmptyState(),
                    "grow, push"
            );
        } else {
            for (ScheduleItem item : scheduleItems) {
                scheduleListPanel.add(
                        createScheduleRow(item),
                        "growx"
                );
            }
        }

        scheduleListPanel.revalidate();
        scheduleListPanel.repaint();
    }

    private JPanel createScheduleRow(
            ScheduleItem item
    ) {
        JPanel row = new JPanel(
                new BorderLayout(14, 0)
        );

        row.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        14,
                        10,
                        14
                )
        );

        row.setBackground(
                item.active()
                        ? UIConstants.PRIMARY_LIGHT
                        : UIConstants.BACKGROUND
        );

        row.putClientProperty(
                "FlatLaf.style",
                """
                arc: 14;
                borderWidth: 0;
                """
        );

        JPanel timePanel =
                createTimePanel(item);

        JPanel informationPanel =
                createInformationPanel(item);

        JLabel statusLabel =
                createStatusLabel(item);

        timePanel.setPreferredSize(
                new Dimension(82, 54)
        );

        timePanel.setMinimumSize(
                new Dimension(82, 54)
        );

        statusLabel.setPreferredSize(
                new Dimension(96, 34)
        );

        statusLabel.setMinimumSize(
                new Dimension(96, 34)
        );

        statusLabel.setMaximumSize(
                new Dimension(96, 34)
        );

        JPanel statusWrapper = new JPanel(
                new BorderLayout()
        );

        statusWrapper.setOpaque(false);

        statusWrapper.setPreferredSize(
                new Dimension(96, 54)
        );

        statusWrapper.add(
                statusLabel,
                BorderLayout.CENTER
        );

        row.add(
                timePanel,
                BorderLayout.WEST
        );

        row.add(
                informationPanel,
                BorderLayout.CENTER
        );

        row.add(
                statusWrapper,
                BorderLayout.EAST
        );

        return row;
    }

    private JPanel createTimePanel(
            ScheduleItem item
    ) {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 0",
                        "[center]",
                        "[]3[]"
                )
        );

        panel.setOpaque(false);

        JLabel startLabel = new JLabel(
                formatTime(item.startTime())
        );

        startLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        startLabel.setForeground(
                item.active()
                        ? UIConstants.PRIMARY
                        : UIConstants.TEXT_PRIMARY
        );

        JLabel endLabel = new JLabel(
                formatTime(item.endTime())
        );

        endLabel.setFont(
                UIConstants.FONT_SMALL
        );

        endLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(startLabel);
        panel.add(endLabel);

        return panel;
    }

    private JPanel createInformationPanel(
            ScheduleItem item
    ) {
        JPanel panel = new JPanel();

        panel.setLayout(
                new BoxLayout(
                        panel,
                        BoxLayout.Y_AXIS
                )
        );

        panel.setOpaque(false);

        JLabel courseLabel = new JLabel(
                safeText(
                        item.courseName(),
                        "Khóa học"
                )
        );

        courseLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        courseLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        courseLabel.setAlignmentX(
                JLabel.LEFT_ALIGNMENT
        );

        courseLabel.setToolTipText(
                courseLabel.getText()
        );

        String teacherName = safeText(
                item.teacherName(),
                "Chưa có giảng viên"
        );

        String room = safeText(
                item.room(),
                "Chưa có phòng"
        );

        JLabel detailLabel = new JLabel(
                teacherName + "  •  " + room
        );

        detailLabel.setFont(
                UIConstants.FONT_SMALL
        );

        detailLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        detailLabel.setAlignmentX(
                JLabel.LEFT_ALIGNMENT
        );

        detailLabel.setToolTipText(
                detailLabel.getText()
        );

        panel.add(
                Box.createVerticalGlue()
        );

        panel.add(courseLabel);

        panel.add(
                Box.createVerticalStrut(5)
        );

        panel.add(detailLabel);

        panel.add(
                Box.createVerticalGlue()
        );

        return panel;
    }

    private JLabel createStatusLabel(
            ScheduleItem item
    ) {
        String text;
        Color foreground;
        Color background;

        if (item.active()) {
            text = "Đang học";
            foreground = UIConstants.PRIMARY;
            background = Color.WHITE;
        } else {
            text = safeText(
                    item.status(),
                    "Sắp tới"
            );

            foreground = UIConstants.SUCCESS;
            background = UIConstants.SUCCESS_LIGHT;
        }

        JLabel label = new JLabel(
                text,
                SwingConstants.CENTER
        );

        label.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        label.setForeground(foreground);
        label.setBackground(background);
        label.setOpaque(true);

        label.setPreferredSize(
                new Dimension(96, 34)
        );

        label.setMinimumSize(
                new Dimension(96, 34)
        );

        label.setMaximumSize(
                new Dimension(96, 34)
        );

        label.putClientProperty(
                "FlatLaf.style",
                """
                arc: 999;
                borderWidth: 0;
                """
        );

        return label;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 28",
                        "[center]",
                        "[]10[]"
                )
        );

        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.CALENDAR_CHECK,
                        28,
                        UIConstants.TEXT_SECONDARY
                )
        );

        JLabel messageLabel = new JLabel(
                "Hôm nay chưa có lịch học"
        );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(iconLabel);
        panel.add(messageLabel);

        return panel;
    }

    private String formatTime(
            LocalTime time
    ) {
        if (time == null) {
            return "--:--";
        }

        return time.format(
                DateTimeFormatter.ofPattern("HH:mm")
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

    public record ScheduleItem(
            String courseName,
            String teacherName,
            String room,
            LocalTime startTime,
            LocalTime endTime,
            String status,
            boolean active
    ) {
    }
}
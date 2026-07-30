package view.components;

import model.NotificationItem;
import model.NotificationType;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.NotificationService;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class ActivityPanel extends ContentCard {

    private final NotificationService notificationService;
    private final JPanel listPanel;
    private final JLabel viewAllLabel;

    private int visibleLimit = 4;

    public ActivityPanel(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;

        listPanel = new JPanel();
        listPanel.setOpaque(false);

        viewAllLabel = new JLabel(
                "Xem tất cả hoạt động  →"
        );

        initializeView();
        refreshActivities();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 18 20",
                        "[grow, fill]",
                        "[]12[grow, fill]10[]"
                )
        );

        setMinimumSize(
                new Dimension(330, 210)
        );

        setPreferredSize(
                new Dimension(560, 230)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        listPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        ""
                )
        );

        add(
                listPanel,
                "grow, push"
        );

        configureViewAllLabel();

        add(
                viewAllLabel,
                "align right"
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

        JLabel titleIcon = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.HISTORY,
                        15,
                        UIConstants.PRIMARY
                )
        );

        JLabel titleLabel = new JLabel(
                "Hoạt động gần đây"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Cập nhật mới nhất"
        );

        subtitleLabel.setFont(
                UIConstants.FONT_SMALL
        );

        subtitleLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(titleIcon);
        panel.add(
                titleLabel,
                "gapleft 6"
        );
        panel.add(
                subtitleLabel,
                "align right"
        );

        return panel;
    }

    private void configureViewAllLabel() {
        viewAllLabel.setFont(
                UIConstants.FONT_NORMAL.deriveFont(
                        Font.BOLD
                )
        );

        viewAllLabel.setForeground(
                UIConstants.PRIMARY
        );

        viewAllLabel.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        viewAllLabel.setToolTipText(
                "Xem toàn bộ hoạt động"
        );
    }

    public void setVisibleLimit(
            int limit
    ) {
        int newLimit = Math.max(1, limit);

        if (visibleLimit == newLimit) {
            return;
        }

        visibleLimit = newLimit;
        refreshActivities();
    }

    public void refreshActivities() {
        listPanel.removeAll();

        List<NotificationItem> activities =
                getRecentActivities();

        if (activities.isEmpty()) {
            listPanel.add(
                    createEmptyState(),
                    "grow, push"
            );
        } else {
            for (
                    int index = 0;
                    index < activities.size();
                    index++
            ) {
                NotificationItem activity =
                        activities.get(index);

                listPanel.add(
                        createActivityRow(activity),
                        "growx"
                );

                if (
                        index
                                < activities.size() - 1
                ) {
                    listPanel.add(
                            createSeparator(),
                            "growx, height 1!"
                    );
                }
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private List<NotificationItem> getRecentActivities() {
        if (notificationService == null) {
            return Collections.emptyList();
        }

        List<NotificationItem> activities =
                notificationService.getRecent(
                        visibleLimit
                );

        return activities == null
                ? Collections.emptyList()
                : activities;
    }

    private JPanel createActivityRow(
            NotificationItem item
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 7 0",
                        "38![grow, fill]12[90!, right]",
                        "[center]"
                )
        );

        row.setOpaque(false);

        NotificationType type =
                item == null
                        || item.getType() == null
                        ? NotificationType.ACCOUNT
                        : item.getType();

        JPanel iconPanel =
                createIconPanel(type);

        JLabel messageLabel =
                new JLabel(
                        formatMessage(item)
                );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        messageLabel.setToolTipText(
                item == null
                        ? ""
                        : item.getMessage()
        );

        JLabel timeLabel =
                new JLabel(
                        formatTime(
                                item == null
                                        ? null
                                        : item.getCreatedAt()
                        ),
                        SwingConstants.RIGHT
                );

        timeLabel.setFont(
                UIConstants.FONT_SMALL
        );

        timeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        row.add(
                iconPanel,
                "align center"
        );

        row.add(
                messageLabel,
                "growx"
        );

        row.add(
                timeLabel,
                "align right"
        );

        return row;
    }

    private JPanel createIconPanel(
            NotificationType type
    ) {
        Color iconColor = getColor(type);
        Color backgroundColor =
                createLightColor(iconColor);

        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        panel.setPreferredSize(
                new Dimension(34, 34)
        );

        panel.setMinimumSize(
                new Dimension(34, 34)
        );

        panel.setBackground(
                backgroundColor
        );

        panel.putClientProperty(
                "FlatLaf.style",
                "arc: 999; borderWidth: 0"
        );

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        getIcon(type),
                        14,
                        iconColor
                )
        );

        panel.add(iconLabel);

        return panel;
    }

    private JPanel createSeparator() {
        JPanel separator = new JPanel();

        separator.setBackground(
                UIConstants.BORDER
        );

        separator.setPreferredSize(
                new Dimension(0, 1)
        );

        return separator;
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel(
                new MigLayout(
                        "fill, wrap 1, insets 18",
                        "[center]",
                        "[]8[]"
                )
        );

        panel.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.CLOCK,
                        25,
                        UIConstants.TEXT_SECONDARY
                )
        );

        JLabel messageLabel = new JLabel(
                "Chưa có hoạt động gần đây"
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

    private String formatMessage(
            NotificationItem item
    ) {
        if (
                item == null
                        || item.getMessage() == null
                        || item.getMessage().isBlank()
        ) {
            return "Hoạt động hệ thống";
        }

        return item.getMessage();
    }

    private FontAwesomeSolid getIcon(
            NotificationType type
    ) {
        if (type == null) {
            return FontAwesomeSolid.BELL;
        }

        return switch (type) {
            case PAYMENT ->
                    FontAwesomeSolid.CREDIT_CARD;

            case ENROLLMENT ->
                    FontAwesomeSolid.USER_PLUS;

            case ACCOUNT ->
                    FontAwesomeSolid.USER;

            case COURSE ->
                    FontAwesomeSolid.BOOK_OPEN;

            case CLASS_START ->
                    FontAwesomeSolid.CALENDAR_ALT;
        };
    }

    private Color getColor(
            NotificationType type
    ) {
        if (type == null) {
            return UIConstants.PRIMARY;
        }

        return switch (type) {
            case PAYMENT ->
                    UIConstants.SUCCESS;

            case ENROLLMENT ->
                    UIConstants.PURPLE;

            case COURSE ->
                    UIConstants.WARNING;

            case CLASS_START ->
                    UIConstants.DANGER;

            case ACCOUNT ->
                    UIConstants.PRIMARY;
        };
    }

    private Color createLightColor(
            Color color
    ) {
        if (color == null) {
            return UIConstants.PRIMARY_LIGHT;
        }

        int red =
                color.getRed()
                        + (255 - color.getRed()) * 82 / 100;

        int green =
                color.getGreen()
                        + (255 - color.getGreen()) * 82 / 100;

        int blue =
                color.getBlue()
                        + (255 - color.getBlue()) * 82 / 100;

        return new Color(
                Math.min(red, 255),
                Math.min(green, 255),
                Math.min(blue, 255)
        );
    }

    private String formatTime(
            LocalDateTime createdAt
    ) {
        if (createdAt == null) {
            return "";
        }

        LocalDateTime now =
                LocalDateTime.now();

        if (createdAt.isAfter(now)) {
            return "Vừa xong";
        }

        Duration duration =
                Duration.between(
                        createdAt,
                        now
                );

        long minutes =
                duration.toMinutes();

        if (minutes < 1) {
            return "Vừa xong";
        }

        if (minutes < 60) {
            return minutes
                    + " phút trước";
        }

        long hours =
                duration.toHours();

        if (hours < 24) {
            return hours
                    + " giờ trước";
        }

        long days =
                duration.toDays();

        if (days < 7) {
            return days
                    + " ngày trước";
        }

        long weeks =
                days / 7;

        if (weeks < 5) {
            return weeks
                    + " tuần trước";
        }

        long months =
                days / 30;

        if (months < 12) {
            return months
                    + " tháng trước";
        }

        return days / 365
                + " năm trước";
    }
}
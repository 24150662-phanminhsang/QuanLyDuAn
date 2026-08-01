package view.components.student;

import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import util.UIConstants;
import view.components.ContentCard;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentNotificationPanel extends ContentCard {

    private final JPanel notificationListPanel;

    private List<StudentNotificationItem> notifications =
            new ArrayList<>();

    public StudentNotificationPanel() {
        notificationListPanel = new JPanel();
        notificationListPanel.setOpaque(false);

        initializeView();
        refreshNotifications();
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
                new Dimension(300, 260)
        );

        setPreferredSize(
                new Dimension(420, 310)
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        notificationListPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]8[]8[]"
                )
        );

        add(
                notificationListPanel,
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
                        FontAwesomeSolid.BELL,
                        16,
                        UIConstants.WARNING
                )
        );

        JLabel titleLabel = new JLabel(
                "Thông báo"
        );

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel subtitleLabel = new JLabel(
                "Mới nhất"
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
                "align right"
        );

        return panel;
    }

    public void setNotifications(
            List<StudentNotificationItem> items
    ) {
        notifications =
                items == null
                        ? new ArrayList<>()
                        : new ArrayList<>(items);

        refreshNotifications();
    }

    public void addNotification(
            StudentNotificationItem item
    ) {
        if (item == null) {
            return;
        }

        notifications.add(0, item);
        refreshNotifications();
    }

    public void clearNotifications() {
        notifications.clear();
        refreshNotifications();
    }

    public List<StudentNotificationItem>
    getNotifications() {
        return Collections.unmodifiableList(
                notifications
        );
    }

    public void refreshNotifications() {
        notificationListPanel.removeAll();

        if (notifications.isEmpty()) {
            notificationListPanel.add(
                    createEmptyState(),
                    "grow, push"
            );
        } else {
            int limit =
                    Math.min(
                            notifications.size(),
                            4
                    );

            for (int index = 0;
                 index < limit;
                 index++) {

                notificationListPanel.add(
                        createNotificationRow(
                                notifications.get(index)
                        ),
                        "growx"
                );
            }
        }

        notificationListPanel.revalidate();
        notificationListPanel.repaint();
    }

    private JPanel createNotificationRow(
            StudentNotificationItem item
    ) {
        NotificationLevel level =
                item.level() == null
                        ? NotificationLevel.INFORMATION
                        : item.level();

        Color foreground =
                getForegroundColor(level);

        Color background =
                getBackgroundColor(level);

        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 10 12",
                        "34![grow, fill]",
                        "[]3[]"
                )
        );

        row.setBackground(background);

        row.putClientProperty(
                "FlatLaf.style",
                "arc: 12; borderWidth: 0"
        );

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        getIcon(level),
                        15,
                        foreground
                )
        );

        JPanel textPanel = new JPanel(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]3[]"
                )
        );

        textPanel.setOpaque(false);

        JLabel messageLabel =
                new JLabel(
                        safeText(
                                item.message(),
                                "Thông báo học tập"
                        )
                );

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel timeLabel = new JLabel(
                formatDateTime(
                        item.createdAt()
                )
        );

        timeLabel.setFont(
                UIConstants.FONT_SMALL
        );

        timeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        textPanel.add(messageLabel);
        textPanel.add(timeLabel);

        row.add(
                iconLabel,
                "aligny top"
        );

        row.add(
                textPanel,
                "growx"
        );

        return row;
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

        panel.add(
                new JLabel(
                        FontIcon.of(
                                FontAwesomeSolid.BELL_SLASH,
                                26,
                                UIConstants.TEXT_SECONDARY
                        )
                )
        );

        JLabel messageLabel = new JLabel(
                "Chưa có thông báo mới"
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

    private FontAwesomeSolid getIcon(
            NotificationLevel level
    ) {
        return switch (level) {
            case SUCCESS ->
                    FontAwesomeSolid.CHECK_CIRCLE;

            case WARNING ->
                    FontAwesomeSolid.EXCLAMATION_TRIANGLE;

            case DANGER ->
                    FontAwesomeSolid.EXCLAMATION_CIRCLE;

            case INFORMATION ->
                    FontAwesomeSolid.INFO_CIRCLE;
        };
    }

    private Color getForegroundColor(
            NotificationLevel level
    ) {
        return switch (level) {
            case SUCCESS ->
                    UIConstants.SUCCESS;

            case WARNING ->
                    UIConstants.WARNING;

            case DANGER ->
                    UIConstants.DANGER;

            case INFORMATION ->
                    UIConstants.PRIMARY;
        };
    }

    private Color getBackgroundColor(
            NotificationLevel level
    ) {
        return switch (level) {
            case SUCCESS ->
                    UIConstants.SUCCESS_LIGHT;

            case WARNING ->
                    UIConstants.WARNING_LIGHT;

            case DANGER ->
                    UIConstants.DANGER_LIGHT;

            case INFORMATION ->
                    UIConstants.PRIMARY_LIGHT;
        };
    }

    private String formatDateTime(
            LocalDateTime dateTime
    ) {
        if (dateTime == null) {
            return "";
        }

        return dateTime.format(
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm"
                )
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

    public enum NotificationLevel {
        INFORMATION,
        SUCCESS,
        WARNING,
        DANGER
    }

    public record StudentNotificationItem(
            String message,
            LocalDateTime createdAt,
            NotificationLevel level
    ) {
    }
}
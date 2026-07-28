package view.components;

import model.NotificationItem;
import model.NotificationType;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.NotificationService;
import util.UIConstants;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ActivityPanel extends ContentCard {

    private final NotificationService notificationService;
    private final JPanel listPanel;

    private int visibleLimit = 4;

    public ActivityPanel(
            NotificationService notificationService
    ) {
        this.notificationService =
                notificationService;

        listPanel = new JPanel();
        listPanel.setOpaque(false);

        initializeView();
        refreshActivities();
    }

    private void initializeView() {
        setLayout(
                new MigLayout(
                        "fill, wrap 1, insets 16",
                        "[grow, fill]",
                        "[]8[grow, fill][]"
                )
        );

        setMinimumSize(
                new Dimension(320, 205)
        );

        setPreferredSize(
                new Dimension(520, 220)
        );

        JLabel titleLabel =
                new JLabel("Hoạt động gần đây");

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        listPanel.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        "[]4[]4[]4[]"
                )
        );

        JLabel viewAllLabel =
                new JLabel(
                        "Xem tất cả hoạt động  →"
                );

        viewAllLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        viewAllLabel.setForeground(
                UIConstants.PRIMARY
        );

        add(
                titleLabel,
                "growx"
        );

        add(
                listPanel,
                "grow, push"
        );

        add(
                viewAllLabel,
                "align right"
        );
    }

    public void setVisibleLimit(int limit) {
        visibleLimit = Math.max(1, limit);
        refreshActivities();
    }

    public void refreshActivities() {
        listPanel.removeAll();

        List<NotificationItem> activities =
                notificationService.getRecent(
                        visibleLimit
                );

        for (NotificationItem activity : activities) {
            listPanel.add(
                    createActivityRow(activity),
                    "growx"
            );
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createActivityRow(
            NotificationItem item
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 2 0",
                        "26![grow][]",
                        "[center]"
                )
        );

        row.setOpaque(false);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        getIcon(item.getType()),
                        13,
                        getColor(item.getType())
                )
        );

        JLabel messageLabel =
                new JLabel(item.getMessage());

        messageLabel.setFont(
                UIConstants.FONT_NORMAL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel timeLabel =
                new JLabel(
                        formatTime(
                                item.getCreatedAt()
                        )
                );

        timeLabel.setFont(
                UIConstants.FONT_SMALL
        );

        timeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        row.add(
                iconLabel,
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

    private FontAwesomeSolid getIcon(
            NotificationType type
    ) {
        return switch (type) {
            case PAYMENT ->
                    FontAwesomeSolid.DOLLAR_SIGN;

            case ENROLLMENT, ACCOUNT ->
                    FontAwesomeSolid.USER;

            case COURSE ->
                    FontAwesomeSolid.BOOK;

            case CLASS_START ->
                    FontAwesomeSolid.CALENDAR_ALT;
        };
    }

    private Color getColor(
            NotificationType type
    ) {
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

    private String formatTime(
            LocalDateTime createdAt
    ) {
        Duration duration =
                Duration.between(
                        createdAt,
                        LocalDateTime.now()
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

        return duration.toDays()
                + " ngày trước";
    }
}
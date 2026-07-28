package view.components;

import model.NotificationItem;
import model.NotificationType;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.NotificationService;
import util.UIConstants;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Dimension;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class NotificationPopup extends JPopupMenu {

    private final NotificationService service;
    private final JPanel notificationList;

    private Runnable changeListener =
            () -> {
            };

    public NotificationPopup(
            NotificationService service
    ) {
        this.service = service;

        notificationList = new JPanel();
        notificationList.setBackground(Color.WHITE);

        initializePopup();
        reload();
    }

    private void initializePopup() {
        setLayout(
                new MigLayout(
                        "fill, insets 0",
                        "[grow, fill]",
                        "[][grow, fill][]"
                )
        );

        setBorderPainted(true);

        JPanel header = new JPanel(
                new MigLayout(
                        "fillx, insets 14",
                        "[grow][]",
                        "[center]"
                )
        );

        header.setBackground(Color.WHITE);

        JLabel titleLabel =
                new JLabel("Thông báo");

        titleLabel.setFont(
                UIConstants.FONT_HEADING
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JButton markAllButton =
                new JButton(
                        "Đánh dấu tất cả đã đọc"
                );

        markAllButton.setFont(
                UIConstants.FONT_SMALL
        );

        markAllButton.setForeground(
                UIConstants.PRIMARY
        );

        markAllButton.setBorderPainted(false);
        markAllButton.setContentAreaFilled(false);
        markAllButton.setFocusable(false);

        markAllButton.addActionListener(
                event -> {
                    service.markAllAsRead();
                    reload();
                    changeListener.run();
                }
        );

        header.add(titleLabel, "growx");
        header.add(markAllButton);

        notificationList.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        ""
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(notificationList);

        scrollPane.setBorder(null);

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setPreferredSize(
                new Dimension(390, 350)
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(15);

        JButton viewAllButton =
                new JButton(
                        "Xem tất cả thông báo  →"
                );

        viewAllButton.setFont(
                UIConstants.FONT_MEDIUM
        );

        viewAllButton.setForeground(
                UIConstants.PRIMARY
        );

        viewAllButton.setBackground(Color.WHITE);
        viewAllButton.setBorderPainted(false);
        viewAllButton.setFocusable(false);

        add(header, "growx");
        add(scrollPane, "grow");
        add(viewAllButton, "growx");
    }

    public void setChangeListener(
            Runnable changeListener
    ) {
        if (changeListener != null) {
            this.changeListener =
                    changeListener;
        }
    }

    public void reload() {
        notificationList.removeAll();

        List<NotificationItem> notifications =
                service.getAll();

        for (NotificationItem item : notifications) {
            notificationList.add(
                    createNotificationRow(item),
                    "growx"
            );
        }

        notificationList.revalidate();
        notificationList.repaint();
    }

    private JPanel createNotificationRow(
            NotificationItem item
    ) {
        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 12 14",
                        "42![grow]18!",
                        "[][][]"
                )
        );

        row.setBackground(
                item.isRead()
                        ? Color.WHITE
                        : new Color(248, 250, 255)
        );

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        getIcon(item.getType()),
                        18,
                        getColor(item.getType())
                )
        );

        iconLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        JLabel titleLabel =
                new JLabel(item.getTitle());

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel messageLabel =
                new JLabel(item.getMessage());

        messageLabel.setFont(
                UIConstants.FONT_SMALL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel timeLabel =
                new JLabel(formatTime(
                        item.getCreatedAt()
                ));

        timeLabel.setFont(
                UIConstants.FONT_SMALL
        );

        timeLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel unreadDot = new JLabel(
                item.isRead() ? "●" : "●"
        );

        unreadDot.setForeground(
                item.isRead()
                        ? UIConstants.BORDER
                        : UIConstants.PRIMARY
        );

        row.add(
                iconLabel,
                "cell 0 0 1 3, align center"
        );

        row.add(
                titleLabel,
                "cell 1 0, growx"
        );

        row.add(
                messageLabel,
                "cell 1 1, growx"
        );

        row.add(
                timeLabel,
                "cell 1 2, growx"
        );

        row.add(
                unreadDot,
                "cell 2 0 1 3, align center"
        );

        row.setCursor(
                new java.awt.Cursor(
                        java.awt.Cursor.HAND_CURSOR
                )
        );

        row.addMouseListener(
                new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            java.awt.event.MouseEvent event
                    ) {
                        service.markAsRead(
                                item.getNotificationId()
                        );

                        reload();
                        changeListener.run();
                    }
                }
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

        long minutes = duration.toMinutes();

        if (minutes < 1) {
            return "Vừa xong";
        }

        if (minutes < 60) {
            return minutes + " phút trước";
        }

        long hours = duration.toHours();

        if (hours < 24) {
            return hours + " giờ trước";
        }

        return duration.toDays()
                + " ngày trước";
    }
}
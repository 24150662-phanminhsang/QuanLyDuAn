package view.components;

import model.NotificationItem;
import model.NotificationType;
import net.miginfocom.swing.MigLayout;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import service.NotificationService;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class NotificationPopup extends JPopupMenu {

    private final NotificationService service;
    private final JPanel notificationList;

    private final JLabel unreadCountLabel;
    private final JButton markAllButton;
    private final JButton viewAllButton;

    private Runnable changeListener = () -> {
    };

    public NotificationPopup(
            NotificationService service
    ) {
        this.service = service;

        notificationList = new JPanel();
        notificationList.setBackground(Color.WHITE);

        unreadCountLabel = new JLabel("0 chưa đọc");
        markAllButton = new JButton(
                "Đánh dấu tất cả đã đọc"
        );
        viewAllButton = new JButton(
                "Xem tất cả thông báo  →"
        );

        initializePopup();
        reload();
    }

    private void initializePopup() {
        setLayout(
                new MigLayout(
                        "fill, insets 0, wrap 1",
                        "[grow, fill]",
                        "[]0[grow, fill]0[]"
                )
        );

        setBackground(Color.WHITE);

        setBorder(
                BorderFactory.createLineBorder(
                        UIConstants.BORDER
                )
        );

        putClientProperty(
                "FlatLaf.style",
                """
                arc: 14;
                borderWidth: 1;
                """
        );

        add(
                createHeaderPanel(),
                "growx"
        );

        add(
                createNotificationScrollPane(),
                "grow"
        );

        add(
                createFooterPanel(),
                "growx"
        );
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(
                new MigLayout(
                        "fillx, insets 14 16",
                        "[][grow][]",
                        "[][]"
                )
        );

        header.setBackground(Color.WHITE);

        JLabel titleIcon = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.BELL,
                        16,
                        UIConstants.PRIMARY
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

        unreadCountLabel.setFont(
                UIConstants.FONT_SMALL.deriveFont(
                        Font.BOLD
                )
        );

        unreadCountLabel.setForeground(
                UIConstants.PRIMARY
        );

        markAllButton.setFont(
                UIConstants.FONT_SMALL
        );

        markAllButton.setForeground(
                UIConstants.PRIMARY
        );

        markAllButton.setBackground(
                Color.WHITE
        );

        markAllButton.setBorderPainted(false);
        markAllButton.setContentAreaFilled(false);
        markAllButton.setFocusable(false);

        markAllButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        markAllButton.addActionListener(
                event -> markAllAsRead()
        );

        header.add(
                titleIcon,
                "cell 0 0 1 2, align center"
        );

        header.add(
                titleLabel,
                "cell 1 0, gapleft 7"
        );

        header.add(
                unreadCountLabel,
                "cell 1 1, gapleft 7"
        );

        header.add(
                markAllButton,
                "cell 2 0 1 2, align right"
        );

        return header;
    }

    private JScrollPane createNotificationScrollPane() {
        notificationList.setLayout(
                new MigLayout(
                        "fillx, wrap 1, insets 0",
                        "[grow, fill]",
                        ""
                )
        );

        JScrollPane scrollPane = new JScrollPane(
                notificationList
        );

        scrollPane.setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        0,
                        1,
                        0,
                        UIConstants.BORDER
                )
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setPreferredSize(
                new Dimension(410, 360)
        );

        scrollPane.setMinimumSize(
                new Dimension(360, 260)
        );

        scrollPane.getViewport().setBackground(
                Color.WHITE
        );

        scrollPane.getVerticalScrollBar()
                .setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(
                new MigLayout(
                        "fillx, insets 8 12",
                        "[grow, center]",
                        "[]"
                )
        );

        footer.setBackground(Color.WHITE);

        viewAllButton.setFont(
                UIConstants.FONT_MEDIUM
        );

        viewAllButton.setForeground(
                UIConstants.PRIMARY
        );

        viewAllButton.setBackground(
                Color.WHITE
        );

        viewAllButton.setBorderPainted(false);
        viewAllButton.setContentAreaFilled(false);
        viewAllButton.setFocusable(false);

        viewAllButton.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        footer.add(
                viewAllButton,
                "growx"
        );

        return footer;
    }

    public void setChangeListener(
            Runnable changeListener
    ) {
        this.changeListener =
                changeListener == null
                        ? () -> {
                }
                        : changeListener;
    }

    public void reload() {
        notificationList.removeAll();

        List<NotificationItem> notifications =
                getNotifications();

        int unreadCount = 0;

        for (NotificationItem item : notifications) {
            if (
                    item != null
                            && !item.isRead()
            ) {
                unreadCount++;
            }
        }

        updateHeaderState(unreadCount);

        if (notifications.isEmpty()) {
            notificationList.add(
                    createEmptyState(),
                    "grow, push"
            );
        } else {
            for (
                    int index = 0;
                    index < notifications.size();
                    index++
            ) {
                NotificationItem item =
                        notifications.get(index);

                if (item == null) {
                    continue;
                }

                notificationList.add(
                        createNotificationRow(item),
                        "growx"
                );

                if (
                        index
                                < notifications.size() - 1
                ) {
                    notificationList.add(
                            createSeparator(),
                            "growx, height 1!"
                    );
                }
            }
        }

        notificationList.revalidate();
        notificationList.repaint();

        packPopup();
    }

    private List<NotificationItem> getNotifications() {
        if (service == null) {
            return Collections.emptyList();
        }

        List<NotificationItem> notifications =
                service.getAll();

        return notifications == null
                ? Collections.emptyList()
                : notifications;
    }

    private void updateHeaderState(
            int unreadCount
    ) {
        unreadCountLabel.setText(
                unreadCount > 0
                        ? unreadCount + " chưa đọc"
                        : "Đã đọc tất cả"
        );

        unreadCountLabel.setForeground(
                unreadCount > 0
                        ? UIConstants.PRIMARY
                        : UIConstants.TEXT_SECONDARY
        );

        markAllButton.setEnabled(
                unreadCount > 0
        );

        markAllButton.setVisible(
                unreadCount > 0
        );
    }

    private JPanel createNotificationRow(
            NotificationItem item
    ) {
        NotificationType type =
                item.getType() == null
                        ? NotificationType.ACCOUNT
                        : item.getType();

        JPanel row = new JPanel(
                new MigLayout(
                        "fillx, insets 11 14",
                        "42![grow, fill]12[18!]",
                        "[]3[]5[]"
                )
        );

        row.setBackground(
                item.isRead()
                        ? Color.WHITE
                        : new Color(248, 250, 255)
        );

        row.setCursor(
                Cursor.getPredefinedCursor(
                        Cursor.HAND_CURSOR
                )
        );

        JPanel iconPanel = createIconPanel(
                type
        );

        JLabel titleLabel = new JLabel(
                safeText(
                        item.getTitle(),
                        "Thông báo hệ thống"
                )
        );

        titleLabel.setFont(
                item.isRead()
                        ? UIConstants.FONT_MEDIUM
                        : UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel messageLabel = new JLabel(
                createHtmlMessage(
                        safeText(
                                item.getMessage(),
                                "Không có nội dung"
                        )
                )
        );

        messageLabel.setFont(
                UIConstants.FONT_SMALL
        );

        messageLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        JLabel timeLabel = new JLabel(
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

        JLabel unreadDot = new JLabel(
                item.isRead() ? "" : "●",
                SwingConstants.CENTER
        );

        unreadDot.setForeground(
                UIConstants.PRIMARY
        );

        row.add(
                iconPanel,
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

        row.addMouseListener(
                new MouseAdapter() {
                    @Override
                    public void mouseClicked(
                            MouseEvent event
                    ) {
                        markNotificationAsRead(
                                item
                        );
                    }

                    @Override
                    public void mouseEntered(
                            MouseEvent event
                    ) {
                        row.setBackground(
                                new Color(
                                        243,
                                        247,
                                        252
                                )
                        );
                    }

                    @Override
                    public void mouseExited(
                            MouseEvent event
                    ) {
                        row.setBackground(
                                item.isRead()
                                        ? Color.WHITE
                                        : new Color(
                                        248,
                                        250,
                                        255
                                )
                        );
                    }
                }
        );

        return row;
    }

    private JPanel createIconPanel(
            NotificationType type
    ) {
        Color iconColor = getColor(type);

        JPanel iconPanel = new JPanel(
                new MigLayout(
                        "fill, insets 0",
                        "[center]",
                        "[center]"
                )
        );

        iconPanel.setPreferredSize(
                new Dimension(36, 36)
        );

        iconPanel.setMinimumSize(
                new Dimension(36, 36)
        );

        iconPanel.setBackground(
                createLightColor(iconColor)
        );

        iconPanel.putClientProperty(
                "FlatLaf.style",
                "arc: 999; borderWidth: 0"
        );

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        getIcon(type),
                        15,
                        iconColor
                )
        );

        iconPanel.add(iconLabel);

        return iconPanel;
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
                        "fill, wrap 1, insets 35 20",
                        "[center]",
                        "[]10[]4[]"
                )
        );

        panel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(
                FontIcon.of(
                        FontAwesomeSolid.BELL_SLASH,
                        30,
                        UIConstants.TEXT_SECONDARY
                )
        );

        JLabel titleLabel = new JLabel(
                "Chưa có thông báo"
        );

        titleLabel.setFont(
                UIConstants.FONT_MEDIUM.deriveFont(
                        Font.BOLD
                )
        );

        titleLabel.setForeground(
                UIConstants.TEXT_PRIMARY
        );

        JLabel descriptionLabel = new JLabel(
                "Các thông báo mới sẽ xuất hiện tại đây"
        );

        descriptionLabel.setFont(
                UIConstants.FONT_SMALL
        );

        descriptionLabel.setForeground(
                UIConstants.TEXT_SECONDARY
        );

        panel.add(iconLabel);
        panel.add(titleLabel);
        panel.add(descriptionLabel);

        return panel;
    }

    private void markAllAsRead() {
        if (service == null) {
            return;
        }

        service.markAllAsRead();

        reload();
        changeListener.run();
    }

    private void markNotificationAsRead(
            NotificationItem item
    ) {
        if (
                service == null
                        || item == null
        ) {
            return;
        }

        if (!item.isRead()) {
            service.markAsRead(
                    item.getNotificationId()
            );
        }

        reload();
        changeListener.run();
    }

    private void packPopup() {
        Dimension preferredSize =
                getPreferredSize();

        if (
                preferredSize.width < 410
                        || preferredSize.height < 200
        ) {
            setPreferredSize(
                    new Dimension(
                            410,
                            Math.max(
                                    preferredSize.height,
                                    440
                            )
                    )
            );
        }
    }

    private String createHtmlMessage(
            String message
    ) {
        String escaped = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");

        return """
                <html>
                    <div style='width:270px;'>
                        %s
                    </div>
                </html>
                """.formatted(escaped);
    }

    private String safeText(
            String value,
            String defaultValue
    ) {
        return value == null
                || value.isBlank()
                ? defaultValue
                : value;
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

        int red = color.getRed()
                + (255 - color.getRed()) * 82 / 100;

        int green = color.getGreen()
                + (255 - color.getGreen()) * 82 / 100;

        int blue = color.getBlue()
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

        long days = duration.toDays();

        if (days < 7) {
            return days + " ngày trước";
        }

        long weeks = days / 7;

        if (weeks < 5) {
            return weeks + " tuần trước";
        }

        long months = days / 30;

        if (months < 12) {
            return months + " tháng trước";
        }

        return days / 365 + " năm trước";
    }
}
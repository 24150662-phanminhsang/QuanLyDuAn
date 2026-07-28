package model;

import java.time.LocalDateTime;

public class NotificationItem {

    private final int notificationId;
    private final String title;
    private final String message;
    private final NotificationType type;
    private final LocalDateTime createdAt;

    private boolean read;

    public NotificationItem(
            int notificationId,
            String title,
            String message,
            NotificationType type,
            LocalDateTime createdAt,
            boolean read
    ) {
        this.notificationId = notificationId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.createdAt = createdAt;
        this.read = read;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
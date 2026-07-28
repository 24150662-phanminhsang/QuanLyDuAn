package service;

import model.NotificationItem;
import model.NotificationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NotificationService {

    private final List<NotificationItem> notifications;

    public NotificationService() {
        notifications = new ArrayList<>();
        createDemoNotifications();
    }

    private void createDemoNotifications() {
        notifications.add(
                new NotificationItem(
                        1,
                        "Thanh toán thành công",
                        "Nguyễn Văn An đã thanh toán khóa Java Core",
                        NotificationType.PAYMENT,
                        LocalDateTime.now().minusMinutes(5),
                        false
                )
        );

        notifications.add(
                new NotificationItem(
                        2,
                        "Có sinh viên đăng ký mới",
                        "Có 1 sinh viên đăng ký khóa học mới",
                        NotificationType.ENROLLMENT,
                        LocalDateTime.now().minusMinutes(20),
                        false
                )
        );

        notifications.add(
                new NotificationItem(
                        3,
                        "Khóa học mới được tạo",
                        "Khóa học Spring Boot nâng cao vừa được tạo",
                        NotificationType.COURSE,
                        LocalDateTime.now().minusHours(1),
                        false
                )
        );

        notifications.add(
                new NotificationItem(
                        4,
                        "Giảng viên cập nhật thông tin",
                        "Giảng viên Trần Thị Mai vừa cập nhật thông tin",
                        NotificationType.ACCOUNT,
                        LocalDateTime.now().minusHours(2),
                        true
                )
        );

        notifications.add(
                new NotificationItem(
                        5,
                        "Lớp học sắp khai giảng",
                        "Lớp Java 2026A sẽ khai giảng sau 2 ngày",
                        NotificationType.CLASS_START,
                        LocalDateTime.now().minusHours(3),
                        true
                )
        );
    }

    public List<NotificationItem> getAll() {
        return notifications.stream()
                .sorted(
                        Comparator.comparing(
                                NotificationItem::getCreatedAt
                        ).reversed()
                )
                .toList();
    }

    public List<NotificationItem> getRecent(int limit) {
        if (limit <= 0) {
            return List.of();
        }

        return getAll()
                .stream()
                .limit(limit)
                .toList();
    }

    public int countUnread() {
        return (int) notifications
                .stream()
                .filter(item -> !item.isRead())
                .count();
    }

    public void markAsRead(int notificationId) {
        notifications.stream()
                .filter(
                        item ->
                                item.getNotificationId()
                                        == notificationId
                )
                .findFirst()
                .ifPresent(
                        item -> item.setRead(true)
                );
    }

    public void markAllAsRead() {
        notifications.forEach(
                item -> item.setRead(true)
        );
    }
}
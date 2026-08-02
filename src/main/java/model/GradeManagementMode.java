package model;

/**
 * Xác định phạm vi sử dụng màn hình quản lý điểm.
 *
 * ADMIN:
 * - Xem và cập nhật điểm của mọi lớp.
 *
 * TEACHER:
 * - Chỉ xem và cập nhật điểm các lớp được phân công.
 */
public enum GradeManagementMode {
    ADMIN,
    TEACHER
}
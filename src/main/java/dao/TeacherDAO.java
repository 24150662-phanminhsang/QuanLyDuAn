package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import model.Teacher;

public interface TeacherDAO {

    /**
     * Thêm giảng viên bằng kết nối riêng.
     */
    boolean insert(Teacher teacher);

    /**
     * Thêm giảng viên trong transaction hiện tại.
     *
     * Dùng cho luồng:
     * Users + Teachers + EmailVerifications.
     *
     * @return teacher_id vừa tạo
     */
    int insert(
            Connection connection,
            Teacher teacher
    ) throws SQLException;

    /**
     * Cập nhật hồ sơ giảng viên.
     */
    boolean update(Teacher teacher);

    /**
     * Cập nhật hồ sơ trong transaction hiện tại.
     */
    boolean update(
            Connection connection,
            Teacher teacher
    ) throws SQLException;

    /**
     * Xóa giảng viên.
     */
    boolean delete(int teacherId);

    /**
     * Tìm giảng viên theo teacher_id.
     */
    Teacher getById(int teacherId);

    /**
     * Tìm giảng viên theo user_id.
     */
    Teacher getByUserId(int userId);

    /**
     * Tìm giảng viên theo mã giảng viên.
     */
    Teacher getByCode(String teacherCode);

    /**
     * Kiểm tra mã giảng viên đã tồn tại.
     */
    boolean existsByTeacherCode(String teacherCode);

    /**
     * Kiểm tra tài khoản đã có hồ sơ giảng viên.
     */
    boolean existsByUserId(int userId);

    /**
     * Lấy toàn bộ danh sách giảng viên.
     */
    List<Teacher> getAll();

    /**
     * Tìm kiếm giảng viên.
     */
    List<Teacher> search(String keyword);
}
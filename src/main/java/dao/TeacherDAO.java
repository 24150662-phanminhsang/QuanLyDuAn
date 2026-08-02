package dao;

import model.Teacher;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface TeacherDAO {

    /* =====================================================
       THÊM / CẬP NHẬT
       ===================================================== */

    boolean insert(Teacher teacher);

    int insert(
            Connection connection,
            Teacher teacher
    ) throws SQLException;

    boolean update(Teacher teacher);

    boolean update(
            Connection connection,
            Teacher teacher
    ) throws SQLException;

    boolean delete(int teacherId);

    /* =====================================================
       TÌM KIẾM
       ===================================================== */

    Teacher getById(int teacherId);

    Teacher getByUserId(int userId);

    Teacher getByCode(String teacherCode);

    List<Teacher> getAll();

    List<Teacher> search(String keyword);

    List<Teacher> getByStatus(String status);

    /* =====================================================
       KIỂM TRA TỒN TẠI
       ===================================================== */

    boolean existsByTeacherCode(String teacherCode);

    boolean existsByTeacherCodeExceptId(
            String teacherCode,
            int excludedTeacherId
    );

    boolean existsByUserId(int userId);

    /* =====================================================
       TRẠNG THÁI GIẢNG VIÊN
       ===================================================== */

    boolean updateStatus(
            int teacherId,
            String status
    );

    boolean activateTeacher(int teacherId);

    boolean deactivateTeacher(int teacherId);

    /* =====================================================
       KIỂM TRA LỚP PHỤ TRÁCH
       ===================================================== */

    boolean hasAssignedClasses(int teacherId);

    int countAssignedClasses(int teacherId);

    int countActiveClasses(int teacherId);
}
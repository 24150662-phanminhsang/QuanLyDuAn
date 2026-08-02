package dao;

import model.ClassRoom;

import java.util.List;

public interface ClassDAO {

    /* =====================================================
       CRUD CƠ BẢN
       ===================================================== */

    boolean insert(
            ClassRoom classRoom
    );

    boolean update(
            ClassRoom classRoom
    );

    boolean delete(
            int classId
    );

    ClassRoom getById(
            int classId
    );

    List<ClassRoom> getAll();

    /* =====================================================
       TÌM KIẾM VÀ LỌC
       ===================================================== */

    List<ClassRoom> search(
            String keyword,
            String status
    );

    List<ClassRoom> getByStatus(
            String status
    );

    List<ClassRoom> getByTeacherId(
            int teacherId
    );

    List<ClassRoom> getByCourseId(
            int courseId
    );

    /* =====================================================
       PHÂN CÔNG GIẢNG VIÊN
       ===================================================== */

    boolean assignTeacher(
            int classId,
            int teacherId
    );

    boolean unassignTeacher(
            int classId
    );

    boolean isTeacherAssignedToClass(
            int teacherId,
            int classId
    );

    /* =====================================================
       TRẠNG THÁI LỚP
       ===================================================== */

    boolean updateStatus(
            int classId,
            String status
    );

    boolean openRegistration(
            int classId
    );

    boolean closeRegistration(
            int classId
    );

    boolean suspendClass(
            int classId
    );

    boolean resumeClass(
            int classId
    );

    boolean markOngoing(
            int classId
    );

    boolean completeClass(
            int classId
    );

    boolean cancelClass(
            int classId
    );

    /* =====================================================
       KIỂM TRA ĐĂNG KÝ HỌC
       ===================================================== */

    boolean hasEnrollments(
            int classId
    );

    int countEnrollments(
            int classId
    );

    int countActiveEnrollments(
            int classId
    );

    /* =====================================================
       KIỂM TRA DỮ LIỆU TRÙNG
       ===================================================== */

    boolean existsByClassCode(
            String classCode
    );

    boolean existsByClassCodeExceptId(
            String classCode,
            int excludedClassId
    );

    boolean existsTeacherScheduleConflict(
            int excludedClassId,
            int teacherId,
            String scheduleText
    );

    boolean existsRoomScheduleConflict(
            int excludedClassId,
            String room,
            String scheduleText
    );

    /* =====================================================
       THỐNG KÊ NHANH
       ===================================================== */

    int countByStatus(
            String status
    );
}

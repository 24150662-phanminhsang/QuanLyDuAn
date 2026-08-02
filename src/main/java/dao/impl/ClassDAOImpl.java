package dao.impl;

import dao.ClassDAO;
import model.ClassRoom;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClassDAOImpl implements ClassDAO {

    private static final String BASE_SELECT = """
            SELECT class_id, class_code, course_id, teacher_id,
                   semester, school_year, room, schedule_text,
                   maximum_students, start_date, end_date,
                   status, created_at
            FROM dbo.CourseClasses
            """;

    @Override
    public boolean insert(ClassRoom classRoom) {
        validateClassRoom(classRoom, false);
        String sql = """
                INSERT INTO dbo.CourseClasses
                (class_code, course_id, teacher_id, semester, school_year,
                 room, schedule_text, maximum_students, start_date, end_date, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, classRoom);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw sqlError("Không thể thêm lớp học", ex);
        }
    }

    @Override
    public boolean update(ClassRoom classRoom) {
        validateClassRoom(classRoom, true);
        String sql = """
                UPDATE dbo.CourseClasses
                SET class_code=?, course_id=?, teacher_id=?, semester=?, school_year=?,
                    room=?, schedule_text=?, maximum_students=?, start_date=?, end_date=?, status=?
                WHERE class_id=?
                """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, classRoom);
            ps.setInt(12, classRoom.getClassId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw sqlError("Không thể cập nhật lớp học", ex);
        }
    }

    @Override
    public boolean delete(int classId) {
        requirePositive(classId, "ID lớp học");
        if (hasEnrollments(classId)) {
            throw new IllegalStateException("Không thể xóa lớp đã có đăng ký học. Hãy chuyển lớp sang CANCELLED.");
        }
        String sql = "DELETE FROM dbo.CourseClasses WHERE class_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, classId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw sqlError("Không thể xóa lớp học", ex);
        }
    }

    @Override
    public ClassRoom getById(int classId) {
        if (classId <= 0) return null;
        String sql = BASE_SELECT + " WHERE class_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, classId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException ex) {
            throw sqlError("Không thể tải lớp học", ex);
        }
    }

    @Override
    public List<ClassRoom> getAll() {
        return query(BASE_SELECT + " ORDER BY class_id DESC", ps -> {});
    }

    @Override
    public List<ClassRoom> search(String keyword, String status) {
        StringBuilder sql = new StringBuilder(BASE_SELECT).append(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (class_code LIKE ? OR semester LIKE ? OR school_year LIKE ? OR room LIKE ? OR schedule_text LIKE ?) ");
            String like = "%" + keyword.trim() + "%";
            for (int i = 0; i < 5; i++) params.add(like);
        }
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            sql.append(" AND status=? ");
            params.add(normalizeStatus(status));
        }
        sql.append(" ORDER BY class_id DESC");
        return query(sql.toString(), ps -> {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
        });
    }

    @Override
    public List<ClassRoom> getByStatus(String status) {
        return search(null, status);
    }

    @Override
    public List<ClassRoom> getByTeacherId(int teacherId) {
        requirePositive(teacherId, "ID giảng viên");
        return query(BASE_SELECT + " WHERE teacher_id=? ORDER BY class_id DESC", ps -> ps.setInt(1, teacherId));
    }

    @Override
    public List<ClassRoom> getByCourseId(int courseId) {
        requirePositive(courseId, "ID khóa học");
        return query(BASE_SELECT + " WHERE course_id=? ORDER BY class_id DESC", ps -> ps.setInt(1, courseId));
    }

    @Override
    public boolean assignTeacher(int classId, int teacherId) {
        requirePositive(classId, "ID lớp học");
        requirePositive(teacherId, "ID giảng viên");
        return executeUpdate("UPDATE dbo.CourseClasses SET teacher_id=? WHERE class_id=?", ps -> {
            ps.setInt(1, teacherId); ps.setInt(2, classId);
        });
    }

    @Override
    public boolean unassignTeacher(int classId) {
        requirePositive(classId, "ID lớp học");
        return executeUpdate("UPDATE dbo.CourseClasses SET teacher_id=NULL WHERE class_id=?", ps -> ps.setInt(1, classId));
    }

    @Override
    public boolean isTeacherAssignedToClass(int teacherId, int classId) {
        if (teacherId <= 0 || classId <= 0) return false;
        return count("SELECT COUNT(*) FROM dbo.CourseClasses WHERE teacher_id=? AND class_id=?", ps -> {
            ps.setInt(1, teacherId); ps.setInt(2, classId);
        }) > 0;
    }

    @Override
    public boolean updateStatus(int classId, String status) {
        requirePositive(classId, "ID lớp học");
        String normalized = normalizeStatus(status);
        return executeUpdate("UPDATE dbo.CourseClasses SET status=? WHERE class_id=?", ps -> {
            ps.setString(1, normalized); ps.setInt(2, classId);
        });
    }

    @Override public boolean openRegistration(int classId) { return updateStatus(classId, "OPEN"); }
    @Override public boolean closeRegistration(int classId) { return updateStatus(classId, "CLOSED"); }
    @Override public boolean suspendClass(int classId) { return closeRegistration(classId); }
    @Override public boolean resumeClass(int classId) { return openRegistration(classId); }
    @Override public boolean markOngoing(int classId) { return closeRegistration(classId); }
    @Override public boolean completeClass(int classId) { return updateStatus(classId, "COMPLETED"); }
    @Override public boolean cancelClass(int classId) { return updateStatus(classId, "CANCELLED"); }

    @Override
    public boolean hasEnrollments(int classId) { return countEnrollments(classId) > 0; }

    @Override
    public int countEnrollments(int classId) {
        if (classId <= 0) return 0;
        return count("SELECT COUNT(*) FROM dbo.Enrollments WHERE class_id=?", ps -> ps.setInt(1, classId));
    }

    @Override
    public int countActiveEnrollments(int classId) {
        if (classId <= 0) return 0;
        return count("SELECT COUNT(*) FROM dbo.Enrollments WHERE class_id=? AND status='ENROLLED'", ps -> ps.setInt(1, classId));
    }

    @Override
    public boolean existsByClassCode(String classCode) {
        if (classCode == null || classCode.isBlank()) return false;
        return count("SELECT COUNT(*) FROM dbo.CourseClasses WHERE UPPER(class_code)=UPPER(?)", ps -> ps.setString(1, classCode.trim())) > 0;
    }

    @Override
    public boolean existsByClassCodeExceptId(String classCode, int excludedClassId) {
        if (classCode == null || classCode.isBlank()) return false;
        return count("SELECT COUNT(*) FROM dbo.CourseClasses WHERE UPPER(class_code)=UPPER(?) AND class_id<>?", ps -> {
            ps.setString(1, classCode.trim()); ps.setInt(2, excludedClassId);
        }) > 0;
    }

    @Override
    public boolean existsTeacherScheduleConflict(int excludedClassId, int teacherId, String scheduleText) {
        if (teacherId <= 0 || scheduleText == null || scheduleText.isBlank()) return false;
        return count("""
                SELECT COUNT(*) FROM dbo.CourseClasses
                WHERE teacher_id=? AND UPPER(LTRIM(RTRIM(schedule_text)))=UPPER(LTRIM(RTRIM(?)))
                  AND class_id<>? AND status IN ('OPEN','CLOSED')
                """, ps -> {
            ps.setInt(1, teacherId); ps.setString(2, scheduleText.trim()); ps.setInt(3, excludedClassId);
        }) > 0;
    }

    @Override
    public boolean existsRoomScheduleConflict(int excludedClassId, String room, String scheduleText) {
        if (room == null || room.isBlank() || scheduleText == null || scheduleText.isBlank()) return false;
        return count("""
                SELECT COUNT(*) FROM dbo.CourseClasses
                WHERE UPPER(LTRIM(RTRIM(room)))=UPPER(LTRIM(RTRIM(?)))
                  AND UPPER(LTRIM(RTRIM(schedule_text)))=UPPER(LTRIM(RTRIM(?)))
                  AND class_id<>? AND status IN ('OPEN','CLOSED')
                """, ps -> {
            ps.setString(1, room.trim()); ps.setString(2, scheduleText.trim()); ps.setInt(3, excludedClassId);
        }) > 0;
    }

    @Override
    public int countByStatus(String status) {
        return count("SELECT COUNT(*) FROM dbo.CourseClasses WHERE status=?", ps -> ps.setString(1, normalizeStatus(status)));
    }

    private void bind(PreparedStatement ps, ClassRoom c) throws SQLException {
        ps.setString(1, c.getClassCode());
        ps.setInt(2, c.getCourseId());
        if (c.getTeacherId() <= 0) ps.setNull(3, Types.INTEGER); else ps.setInt(3, c.getTeacherId());
        setNullableString(ps, 4, c.getSemester());
        setNullableString(ps, 5, c.getSchoolYear());
        setNullableString(ps, 6, c.getRoom());
        setNullableString(ps, 7, c.getScheduleText());
        ps.setInt(8, c.getMaximumStudents());
        if (c.getStartDate() == null) ps.setNull(9, Types.DATE); else ps.setDate(9, c.getStartDate());
        if (c.getEndDate() == null) ps.setNull(10, Types.DATE); else ps.setDate(10, c.getEndDate());
        ps.setString(11, normalizeStatus(c.getStatus()));
    }

    private ClassRoom map(ResultSet rs) throws SQLException {
        ClassRoom c = new ClassRoom();
        c.setClassId(rs.getInt("class_id"));
        c.setClassCode(rs.getString("class_code"));
        c.setCourseId(rs.getInt("course_id"));
        int teacherId = rs.getInt("teacher_id");
        c.setTeacherId(rs.wasNull() ? 0 : teacherId);
        c.setSemester(rs.getString("semester"));
        c.setSchoolYear(rs.getString("school_year"));
        c.setRoom(rs.getString("room"));
        c.setScheduleText(rs.getString("schedule_text"));
        c.setMaximumStudents(rs.getInt("maximum_students"));
        c.setStartDate(rs.getDate("start_date"));
        c.setEndDate(rs.getDate("end_date"));
        c.setStatus(rs.getString("status"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    private List<ClassRoom> query(String sql, SqlBinder binder) {
        List<ClassRoom> result = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(map(rs));
            }
            return result;
        } catch (SQLException ex) {
            throw sqlError("Không thể tải danh sách lớp học", ex);
        }
    }

    private boolean executeUpdate(String sql, SqlBinder binder) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw sqlError("Không thể cập nhật dữ liệu lớp học", ex);
        }
    }

    private int count(String sql, SqlBinder binder) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        } catch (SQLException ex) {
            throw sqlError("Không thể kiểm tra dữ liệu lớp học", ex);
        }
    }

    private void validateClassRoom(ClassRoom c, boolean update) {
        if (c == null) throw new IllegalArgumentException("Thông tin lớp học không được null.");
        if (update) requirePositive(c.getClassId(), "ID lớp học");
        if (c.getClassCode() == null || c.getClassCode().isBlank()) throw new IllegalArgumentException("Mã lớp không được để trống.");
        requirePositive(c.getCourseId(), "ID khóa học");
        if (c.getMaximumStudents() <= 0) throw new IllegalArgumentException("Sĩ số tối đa phải lớn hơn 0.");
        normalizeStatus(c.getStatus());
    }

    private String normalizeStatus(String status) {
        String s = status == null || status.isBlank() ? "OPEN" : status.trim().toUpperCase(Locale.ROOT);
        if (!List.of("OPEN", "CLOSED", "COMPLETED", "CANCELLED").contains(s)) {
            throw new IllegalArgumentException("Trạng thái lớp không hợp lệ: " + status);
        }
        return s;
    }

    private void requirePositive(int id, String name) {
        if (id <= 0) throw new IllegalArgumentException(name + " phải lớn hơn 0.");
    }

    private void setNullableString(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.isBlank()) ps.setNull(index, Types.NVARCHAR); else ps.setString(index, value.trim());
    }

    private RuntimeException sqlError(String message, SQLException ex) {
        if (ex.getErrorCode() == 2627 || ex.getErrorCode() == 2601) {
            return new IllegalArgumentException("Mã lớp đã tồn tại hoặc dữ liệu bị trùng.", ex);
        }
        return new RuntimeException(message + ". Chi tiết: " + ex.getMessage(), ex);
    }

    @FunctionalInterface
    private interface SqlBinder { void bind(PreparedStatement ps) throws SQLException; }
}

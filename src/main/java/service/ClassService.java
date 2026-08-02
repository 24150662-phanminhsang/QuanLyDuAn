package service;

import dao.ClassDAO;
import dao.impl.ClassDAOImpl;
import model.ClassRoom;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ClassService {
    private final ClassDAO classDAO;

    public ClassService() { this(new ClassDAOImpl()); }
    public ClassService(ClassDAO classDAO) {
        if (classDAO == null) throw new IllegalArgumentException("ClassDAO không được null.");
        this.classDAO = classDAO;
    }

    public boolean createClass(ClassRoom c) {
        validate(c, false); normalize(c);
        if (classDAO.existsByClassCode(c.getClassCode())) throw new IllegalArgumentException("Mã lớp đã tồn tại.");
        checkConflicts(c, 0);
        return classDAO.insert(c);
    }

    public boolean updateClass(ClassRoom c) {
        validate(c, true); normalize(c);
        if (classDAO.getById(c.getClassId()) == null) throw new IllegalArgumentException("Không tìm thấy lớp học.");
        if (classDAO.existsByClassCodeExceptId(c.getClassCode(), c.getClassId())) throw new IllegalArgumentException("Mã lớp đã được sử dụng.");
        if (classDAO.countActiveEnrollments(c.getClassId()) > c.getMaximumStudents()) {
            throw new IllegalStateException("Sĩ số tối đa không thể nhỏ hơn số học viên đang đăng ký.");
        }
        checkConflicts(c, c.getClassId());
        return classDAO.update(c);
    }

    public boolean deleteClass(int id) {
        requireId(id, "ID lớp học");
        if (classDAO.hasEnrollments(id)) throw new IllegalStateException("Lớp đã có học viên, không thể xóa. Hãy hủy lớp.");
        return classDAO.delete(id);
    }

    public ClassRoom getClassById(int id) { requireId(id, "ID lớp học"); return classDAO.getById(id); }
    public List<ClassRoom> getAllClasses() { return safe(classDAO.getAll()); }
    public List<ClassRoom> searchClasses(String keyword, String status) { return safe(classDAO.search(keyword, status)); }
    public List<ClassRoom> getClassesByTeacherId(int id) { requireId(id, "ID giảng viên"); return safe(classDAO.getByTeacherId(id)); }
    public List<ClassRoom> getClassesByTeacher(int id) { return getClassesByTeacherId(id); }
    public List<ClassRoom> getClassesByCourseId(int id) { requireId(id, "ID khóa học"); return safe(classDAO.getByCourseId(id)); }

    public boolean assignTeacherToClass(int classId, int teacherId) {
        requireId(classId, "ID lớp học"); requireId(teacherId, "ID giảng viên");
        ClassRoom c = classDAO.getById(classId);
        if (c == null) throw new IllegalArgumentException("Không tìm thấy lớp học.");
        if (classDAO.existsTeacherScheduleConflict(classId, teacherId, c.getScheduleText())) throw new IllegalStateException("Giảng viên bị trùng lịch.");
        return classDAO.assignTeacher(classId, teacherId);
    }

    public boolean isTeacherAssignedToClass(int teacherId, int classId) {
        return teacherId > 0 && classId > 0 && classDAO.isTeacherAssignedToClass(teacherId, classId);
    }

    public boolean openClass(int id) { requireExisting(id); return classDAO.openRegistration(id); }
    public boolean closeClass(int id) { requireExisting(id); return classDAO.closeRegistration(id); }
    public boolean completeClass(int id) { requireExisting(id); return classDAO.completeClass(id); }
    public boolean cancelClass(int id) {
        ClassRoom c = requireExisting(id);
        if ("COMPLETED".equals(c.getStatus())) throw new IllegalStateException("Không thể hủy lớp đã hoàn thành.");
        return classDAO.cancelClass(id);
    }
    public int countEnrollments(int id) { requireId(id, "ID lớp học"); return classDAO.countEnrollments(id); }
    public int countActiveEnrollments(int id) { requireId(id, "ID lớp học"); return classDAO.countActiveEnrollments(id); }

    private void checkConflicts(ClassRoom c, int excludedId) {
        if (c.getTeacherId() > 0 && classDAO.existsTeacherScheduleConflict(excludedId, c.getTeacherId(), c.getScheduleText())) {
            throw new IllegalStateException("Giảng viên đã có lớp cùng lịch học.");
        }
        if (classDAO.existsRoomScheduleConflict(excludedId, c.getRoom(), c.getScheduleText())) {
            throw new IllegalStateException("Phòng học đã được sử dụng trong lịch này.");
        }
    }

    private void validate(ClassRoom c, boolean update) {
        if (c == null) throw new IllegalArgumentException("Thông tin lớp học không được null.");
        if (update) requireId(c.getClassId(), "ID lớp học");
        if (c.getClassCode() == null || c.getClassCode().isBlank()) throw new IllegalArgumentException("Mã lớp không được để trống.");
        requireId(c.getCourseId(), "ID khóa học");
        if (c.getTeacherId() < 0) throw new IllegalArgumentException("ID giảng viên không hợp lệ.");
        if (c.getSemester() == null || c.getSemester().isBlank()) throw new IllegalArgumentException("Học kỳ không được để trống.");
        if (c.getSchoolYear() == null || c.getSchoolYear().isBlank()) throw new IllegalArgumentException("Năm học không được để trống.");
        if (c.getMaximumStudents() <= 0) throw new IllegalArgumentException("Sĩ số tối đa phải lớn hơn 0.");
        Date start = c.getStartDate(), end = c.getEndDate();
        if (start != null && end != null && end.before(start)) throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        normalizeStatus(c.getStatus());
    }

    private void normalize(ClassRoom c) {
        c.setClassCode(c.getClassCode());
        c.setSemester(trim(c.getSemester())); c.setSchoolYear(trim(c.getSchoolYear()));
        c.setRoom(trim(c.getRoom())); c.setScheduleText(trim(c.getScheduleText()));
        c.setStatus(normalizeStatus(c.getStatus()));
    }

    private String normalizeStatus(String value) {
        String s = value == null || value.isBlank() ? "OPEN" : value.trim().toUpperCase(Locale.ROOT);
        if (!List.of("OPEN", "CLOSED", "COMPLETED", "CANCELLED").contains(s)) throw new IllegalArgumentException("Trạng thái lớp không hợp lệ.");
        return s;
    }
    private String trim(String s) { return s == null || s.isBlank() ? null : s.trim(); }
    private void requireId(int id, String name) { if (id <= 0) throw new IllegalArgumentException(name + " phải lớn hơn 0."); }
    private ClassRoom requireExisting(int id) { requireId(id, "ID lớp học"); ClassRoom c = classDAO.getById(id); if (c == null) throw new IllegalArgumentException("Không tìm thấy lớp học."); return c; }
    private List<ClassRoom> safe(List<ClassRoom> list) { return list == null ? Collections.emptyList() : list; }
}

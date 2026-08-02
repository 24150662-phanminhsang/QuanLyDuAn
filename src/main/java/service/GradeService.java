package service;

import dao.ClassDAO;
import dao.EnrollmentDAO;
import dao.GradeDAO;
import dao.impl.ClassDAOImpl;
import dao.impl.GradeDAOImpl;
import model.ClassRoom;
import model.Grade;

import java.util.Collections;
import java.util.List;

public class GradeService {

    private final GradeDAO gradeDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final ClassDAO classDAO;

    public GradeService() {
        this.gradeDAO = new GradeDAOImpl();
        this.enrollmentDAO = new EnrollmentDAO();
        this.classDAO = new ClassDAOImpl();
    }

    /* =====================================================
       ADMIN - THÊM HOẶC CẬP NHẬT ĐIỂM
       ===================================================== */

    /**
     * Admin thêm hoặc cập nhật điểm cho sinh viên.
     *
     * Quy tắc:
     * - Sinh viên phải đăng ký lớp.
     * - Mỗi loại điểm từ 0 đến 10.
     * - Điểm trung bình:
     *      Chuyên cần: 10%
     *      Giữa kỳ:    30%
     *      Cuối kỳ:    60%
     */
    public boolean saveOrUpdateGrade(
            int studentId,
            int classId,
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        validateGradeInput(
                studentId,
                classId,
                attendanceScore,
                midtermScore,
                finalScore
        );

        int enrollmentId =
                enrollmentDAO.findEnrollmentId(
                        studentId,
                        classId
                );

        if (enrollmentId <= 0) {
            throw new IllegalArgumentException(
                    "Sinh viên chưa đăng ký lớp học này."
            );
        }

        double averageScore =
                calculateAverage(
                        attendanceScore,
                        midtermScore,
                        finalScore
                );

        String result =
                determineResult(averageScore);

        Grade existingGrade =
                gradeDAO.getByEnrollmentId(
                        enrollmentId
                );

        if (existingGrade == null) {
            Grade grade = new Grade();

            grade.setEnrollmentId(
                    enrollmentId
            );

            grade.setStudentId(
                    studentId
            );

            grade.setClassId(
                    classId
            );

            grade.setAttendanceScore(
                    attendanceScore
            );

            grade.setMidtermScore(
                    midtermScore
            );

            grade.setFinalScore(
                    finalScore
            );

            grade.setAverageScore(
                    averageScore
            );

            grade.setResult(
                    result
            );

            return gradeDAO.insert(grade);
        }

        existingGrade.setStudentId(
                studentId
        );

        existingGrade.setClassId(
                classId
        );

        existingGrade.setAttendanceScore(
                attendanceScore
        );

        existingGrade.setMidtermScore(
                midtermScore
        );

        existingGrade.setFinalScore(
                finalScore
        );

        existingGrade.setAverageScore(
                averageScore
        );

        existingGrade.setResult(
                result
        );

        return gradeDAO.update(
                existingGrade
        );
    }

    /* =====================================================
       TEACHER - THÊM HOẶC CẬP NHẬT ĐIỂM
       ===================================================== */

    /**
     * Giáo viên thêm hoặc cập nhật điểm.
     *
     * Giáo viên chỉ được nhập điểm cho lớp
     * mà mình được phân công phụ trách.
     */
    public boolean saveOrUpdateGradeByTeacher(
            int teacherId,
            int studentId,
            int classId,
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        validateTeacherClassPermission(
                teacherId,
                classId
        );

        return saveOrUpdateGrade(
                studentId,
                classId,
                attendanceScore,
                midtermScore,
                finalScore
        );
    }

    /* =====================================================
       LẤY ĐIỂM THEO SINH VIÊN VÀ LỚP
       ===================================================== */

    public Grade getGradeByStudentAndClass(
            int studentId,
            int classId
    ) {
        validateId(
                studentId,
                "ID sinh viên"
        );

        validateId(
                classId,
                "ID lớp học"
        );

        return gradeDAO.getByStudentAndClass(
                studentId,
                classId
        );
    }

    /**
     * Giáo viên lấy điểm của một sinh viên trong lớp mình.
     */
    public Grade getGradeByStudentAndClassForTeacher(
            int teacherId,
            int studentId,
            int classId
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        validateTeacherClassPermission(
                teacherId,
                classId
        );

        return getGradeByStudentAndClass(
                studentId,
                classId
        );
    }

    /* =====================================================
       DANH SÁCH ĐIỂM THEO SINH VIÊN
       ===================================================== */

    public List<Grade> getGradesByStudent(
            int studentId
    ) {
        validateId(
                studentId,
                "ID sinh viên"
        );

        List<Grade> grades =
                gradeDAO.getByStudentId(
                        studentId
                );

        return safeList(grades);
    }

    /* =====================================================
       DANH SÁCH ĐIỂM THEO LỚP
       ===================================================== */

    /**
     * Admin lấy điểm của bất kỳ lớp nào.
     */
    public List<Grade> getGradesByClass(
            int classId
    ) {
        validateId(
                classId,
                "ID lớp học"
        );

        List<Grade> grades =
                gradeDAO.getByClassId(
                        classId
                );

        return safeList(grades);
    }

    /**
     * Giáo viên chỉ được lấy điểm lớp mình phụ trách.
     */
    public List<Grade> getGradesByClassForTeacher(
            int teacherId,
            int classId
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        validateTeacherClassPermission(
                teacherId,
                classId
        );

        return getGradesByClass(
                classId
        );
    }

    /* =====================================================
       DANH SÁCH TOÀN BỘ ĐIỂM
       ===================================================== */

    /**
     * Chỉ nên dùng cho Admin.
     */
    public List<Grade> getAllGrades() {
        List<Grade> grades =
                gradeDAO.getAll();

        return safeList(grades);
    }

    /* =====================================================
       XÓA ĐIỂM
       ===================================================== */

    /**
     * Admin xóa điểm theo ID.
     */
    public boolean deleteGrade(
            int gradeId
    ) {
        validateId(
                gradeId,
                "ID điểm"
        );

        return gradeDAO.delete(
                gradeId
        );
    }

    /**
     * Giáo viên xóa điểm nếu bản ghi thuộc lớp mình phụ trách.
     *
     * Có thể không đưa nút này ra giao diện Teacher
     * nếu muốn hạn chế quyền xóa.
     */
    public boolean deleteGradeByTeacher(
            int teacherId,
            int gradeId
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        validateId(
                gradeId,
                "ID điểm"
        );

        Grade grade =
                gradeDAO.getById(
                        gradeId
                );

        if (grade == null) {
            return false;
        }

        validateTeacherClassPermission(
                teacherId,
                grade.getClassId()
        );

        return gradeDAO.delete(
                gradeId
        );
    }

    /* =====================================================
       LỚP CỦA GIẢNG VIÊN
       ===================================================== */

    /**
     * Lấy danh sách lớp giáo viên đang phụ trách.
     */
    public List<ClassRoom> getTeacherClasses(
            int teacherId
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        List<ClassRoom> classes =
                classDAO.getByTeacherId(
                        teacherId
                );

        return classes == null
                ? Collections.emptyList()
                : classes;
    }

    /**
     * Kiểm tra giáo viên có phụ trách lớp hay không.
     */
    public boolean canTeacherManageClass(
            int teacherId,
            int classId
    ) {
        if (teacherId <= 0
                || classId <= 0) {

            return false;
        }

        return classDAO
                .isTeacherAssignedToClass(
                        teacherId,
                        classId
                );
    }

    /* =====================================================
       TÍNH ĐIỂM
       ===================================================== */

    /**
     * Tính điểm trung bình:
     *
     * Chuyên cần: 10%
     * Giữa kỳ:    30%
     * Cuối kỳ:    60%
     */
    public double calculateAverage(
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        validateScore(
                attendanceScore,
                "Điểm chuyên cần"
        );

        validateScore(
                midtermScore,
                "Điểm giữa kỳ"
        );

        validateScore(
                finalScore,
                "Điểm cuối kỳ"
        );

        double average =
                attendanceScore * 0.1
                        + midtermScore * 0.3
                        + finalScore * 0.6;

        return Math.round(
                average * 100.0
        ) / 100.0;
    }

    /**
     * Xác định kết quả học tập.
     */
    public String determineResult(
            double averageScore
    ) {
        validateScore(
                averageScore,
                "Điểm trung bình"
        );

        return averageScore >= 5
                ? "PASSED"
                : "FAILED";
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateGradeInput(
            int studentId,
            int classId,
            double attendanceScore,
            double midtermScore,
            double finalScore
    ) {
        validateId(
                studentId,
                "ID sinh viên"
        );

        validateId(
                classId,
                "ID lớp học"
        );

        validateScore(
                attendanceScore,
                "Điểm chuyên cần"
        );

        validateScore(
                midtermScore,
                "Điểm giữa kỳ"
        );

        validateScore(
                finalScore,
                "Điểm cuối kỳ"
        );
    }

    /**
     * Kiểm tra quyền quản lý lớp của giáo viên.
     */
    private void validateTeacherClassPermission(
            int teacherId,
            int classId
    ) {
        validateId(
                teacherId,
                "ID giảng viên"
        );

        validateId(
                classId,
                "ID lớp học"
        );

        boolean assigned =
                classDAO.isTeacherAssignedToClass(
                        teacherId,
                        classId
                );

        if (!assigned) {
            throw new IllegalArgumentException(
                    "Giáo viên không được phân công "
                            + "phụ trách lớp học này."
            );
        }
    }

    private void validateId(
            int id,
            String fieldName
    ) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    fieldName
                            + " phải lớn hơn 0."
            );
        }
    }

    private void validateScore(
            double score,
            String fieldName
    ) {
        if (Double.isNaN(score)
                || Double.isInfinite(score)
                || score < 0
                || score > 10) {

            throw new IllegalArgumentException(
                    fieldName
                            + " phải nằm trong khoảng từ 0 đến 10."
            );
        }
    }

    /* =====================================================
       HÀM HỖ TRỢ
       ===================================================== */

    private List<Grade> safeList(
            List<Grade> grades
    ) {
        return grades == null
                ? Collections.emptyList()
                : grades;
    }
}
package service;

import dao.StudentRegistrationDAO;
import model.dto.AvailableClassDTO;
import model.dto.RegistrationResult;

import java.util.Collections;
import java.util.List;

public class StudentRegistrationService {

    private final StudentRegistrationDAO
            studentRegistrationDAO;

    public StudentRegistrationService() {
        this.studentRegistrationDAO =
                new StudentRegistrationDAO();
    }

    /* =====================================================
       LẤY DANH SÁCH LỚP CÓ THỂ ĐĂNG KÝ
       ===================================================== */

    public List<AvailableClassDTO> getAvailableClasses(
            int studentId
    ) {
        validateStudentId(studentId);

        List<AvailableClassDTO> classes =
                studentRegistrationDAO
                        .getAvailableClasses(
                                studentId
                        );

        return classes == null
                ? Collections.emptyList()
                : classes;
    }

    /* =====================================================
       TÌM KIẾM LỚP CÓ THỂ ĐĂNG KÝ
       ===================================================== */

    public List<AvailableClassDTO> searchAvailableClasses(
            int studentId,
            String keyword
    ) {
        validateStudentId(studentId);

        String normalizedKeyword =
                keyword == null
                        ? ""
                        : keyword.trim();

        List<AvailableClassDTO> classes =
                studentRegistrationDAO
                        .searchAvailableClasses(
                                studentId,
                                normalizedKeyword
                        );

        return classes == null
                ? Collections.emptyList()
                : classes;
    }

    /* =====================================================
       ĐĂNG KÝ KHÓA HỌC
       ===================================================== */

    public RegistrationResult registerClass(
            int studentId,
            int classId
    ) {
        validateStudentId(studentId);
        validateClassId(classId);

        RegistrationResult result =
                studentRegistrationDAO
                        .registerClass(
                                studentId,
                                classId
                        );

        if (result == null) {
            return RegistrationResult.failure(
                    "Không nhận được kết quả đăng ký."
            );
        }

        return result;
    }

    /* =====================================================
       VALIDATION
       ===================================================== */

    private void validateStudentId(
            int studentId
    ) {
        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "ID sinh viên không hợp lệ."
            );
        }
    }

    private void validateClassId(
            int classId
    ) {
        if (classId <= 0) {
            throw new IllegalArgumentException(
                    "ID lớp học không hợp lệ."
            );
        }
    }
}
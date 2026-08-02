package controller;

import model.dto.AvailableClassDTO;
import model.dto.RegistrationResult;
import service.StudentRegistrationService;

import java.util.List;

public class StudentRegistrationController {

    private final StudentRegistrationService
            registrationService;

    public StudentRegistrationController() {

        registrationService =
                new StudentRegistrationService();

    }

    //==================================================
    // Danh sách lớp có thể đăng ký
    //==================================================

    public List<AvailableClassDTO> getAvailableClasses(
            int studentId
    ) {

        return registrationService
                .getAvailableClasses(
                        studentId
                );

    }

    //==================================================
    // Tìm kiếm
    //==================================================

    public List<AvailableClassDTO> searchAvailableClasses(
            int studentId,
            String keyword
    ) {

        return registrationService
                .searchAvailableClasses(
                        studentId,
                        keyword
                );

    }

    //==================================================
    // Đăng ký
    //==================================================

    public RegistrationResult registerCourse(
            int studentId,
            int classId
    ) {

        return registrationService
                .registerClass(
                        studentId,
                        classId
                );

    }

    //==================================================
    // Refresh
    //==================================================

    public List<AvailableClassDTO> refresh(
            int studentId
    ) {

        return getAvailableClasses(
                studentId
        );

    }

    //==================================================
    // Kiểm tra có lớp hay không
    //==================================================

    public boolean hasAvailableCourse(
            int studentId
    ) {

        List<AvailableClassDTO> list =
                registrationService
                        .getAvailableClasses(
                                studentId
                        );

        return !list.isEmpty();

    }

    //==================================================
    // Đếm số lớp có thể đăng ký
    //==================================================

    public int countAvailableCourse(
            int studentId
    ) {

        return registrationService
                .getAvailableClasses(
                        studentId
                )
                .size();

    }

    //==================================================
    // Lấy 1 lớp
    //==================================================

    public AvailableClassDTO getClassById(
            int studentId,
            int classId
    ) {

        List<AvailableClassDTO> list =
                registrationService
                        .getAvailableClasses(
                                studentId
                        );

        for (AvailableClassDTO dto : list) {

            if (dto.getClassId() == classId) {

                return dto;

            }

        }

        return null;

    }

}
package controller;

import model.Teacher;
import service.TeacherService;

import java.util.Collections;
import java.util.List;

public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController() {
        this(new TeacherService());
    }

    public TeacherController(
            TeacherService teacherService
    ) {
        if (teacherService == null) {
            throw new IllegalArgumentException(
                    "TeacherService không được null."
            );
        }

        this.teacherService = teacherService;
    }

    public boolean createTeacherAccount(
            Teacher teacher,
            String username,
            String password,
            String confirmPassword
    ) {
        return teacherService.createTeacherAccount(
                teacher,
                username,
                password,
                confirmPassword
        );
    }

    public boolean addTeacher(Teacher teacher) {
        return teacherService.addTeacher(teacher);
    }

    public boolean updateTeacher(Teacher teacher) {
        return teacherService.updateTeacher(teacher);
    }

    public boolean updateTeacherAndUser(
            Teacher teacher
    ) {
        return teacherService.updateTeacherAndUser(
                teacher
        );
    }

    public boolean updateProfile(Teacher teacher) {
        return updateTeacherAndUser(teacher);
    }

    public boolean deleteTeacher(int teacherId) {
        return teacherService.deleteTeacher(
                teacherId
        );
    }

    public boolean canDeleteTeacher(int teacherId) {
        return teacherService.canDeleteTeacher(
                teacherId
        );
    }

    public Teacher getTeacherById(int teacherId) {
        return teacherService.getTeacherById(
                teacherId
        );
    }

    public Teacher getTeacherByUserId(int userId) {
        return teacherService.getTeacherByUserId(
                userId
        );
    }

    public Teacher getTeacherByCode(String code) {
        return teacherService.getTeacherByCode(code);
    }

    public List<Teacher> getAllTeachers() {
        List<Teacher> list =
                teacherService.getAllTeachers();

        return list == null
                ? Collections.emptyList()
                : list;
    }

    public List<Teacher> searchTeachers(
            String keyword
    ) {
        List<Teacher> list =
                teacherService.searchTeachers(keyword);

        return list == null
                ? Collections.emptyList()
                : list;
    }

    public List<Teacher> getTeachersByStatus(
            String status
    ) {
        List<Teacher> list =
                teacherService.getTeachersByStatus(status);

        return list == null
                ? Collections.emptyList()
                : list;
    }

    public List<Teacher> getActiveTeachers() {
        return getTeachersByStatus("ACTIVE");
    }

    public List<Teacher> getInactiveTeachers() {
        return getTeachersByStatus("INACTIVE");
    }

    public boolean activateTeacher(int teacherId) {
        return teacherService.activateTeacher(
                teacherId
        );
    }

    public boolean deactivateTeacher(int teacherId) {
        return teacherService.deactivateTeacher(
                teacherId
        );
    }

    public boolean updateTeacherStatus(
            int teacherId,
            String status
    ) {
        return teacherService.updateTeacherStatus(
                teacherId,
                status
        );
    }

    public boolean hasAssignedClasses(int teacherId) {
        return teacherService.hasAssignedClasses(
                teacherId
        );
    }

    public int countAssignedClasses(int teacherId) {
        return teacherService.countAssignedClasses(
                teacherId
        );
    }

    public int countActiveClasses(int teacherId) {
        return teacherService.countActiveClasses(
                teacherId
        );
    }

    public boolean existsByTeacherCode(String code) {
        return teacherService.existsByTeacherCode(code);
    }

    public boolean existsByUserId(int userId) {
        return teacherService.existsByUserId(userId);
    }
}

package controller;

import dao.TeacherDAO;
import dao.impl.TeacherDAOImpl;
import model.Teacher;

import java.util.Collections;
import java.util.List;

public class TeacherController {

    private final TeacherDAO teacherDAO;

    public TeacherController() {
        this.teacherDAO = new TeacherDAOImpl();
    }

    public TeacherController(TeacherDAO teacherDAO) {
        if (teacherDAO == null) {
            throw new IllegalArgumentException(
                    "TeacherDAO không được null"
            );
        }

        this.teacherDAO = teacherDAO;
    }

    public List<Teacher> getAllTeachers() {
        try {
            List<Teacher> teachers = teacherDAO.getAll();

            return teachers == null
                    ? Collections.emptyList()
                    : teachers;

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể tải danh sách giảng viên.",
                    e
            );
        }
    }

    public Teacher getTeacherById(int teacherId) {
        validateId(teacherId);

        try {
            return teacherDAO.getById(teacherId);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể lấy thông tin giảng viên.",
                    e
            );
        }
    }

    public Teacher getTeacherByUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "User ID phải lớn hơn 0."
            );
        }

        try {
            return teacherDAO.getByUserId(userId);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể lấy giảng viên theo tài khoản.",
                    e
            );
        }
    }

    public boolean addTeacher(Teacher teacher) {
        validateTeacher(teacher);

        try {
            return teacherDAO.insert(teacher);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể thêm giảng viên.",
                    e
            );
        }
    }

    public boolean updateTeacher(Teacher teacher) {
        validateTeacher(teacher);

        try {
            return teacherDAO.update(teacher);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể cập nhật giảng viên.",
                    e
            );
        }
    }

    public boolean deleteTeacher(int teacherId) {
        validateId(teacherId);

        try {
            return teacherDAO.delete(teacherId);

        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "Không thể xóa giảng viên.",
                    e
            );
        }
    }

    private void validateTeacher(Teacher teacher) {
        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Thông tin giảng viên không được null."
            );
        }
    }

    private void validateId(int teacherId) {
        if (teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Teacher ID phải lớn hơn 0."
            );
        }
    }
}
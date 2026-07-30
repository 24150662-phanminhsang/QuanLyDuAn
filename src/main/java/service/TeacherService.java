package service;

import dao.TeacherDAO;
import dao.impl.TeacherDAOImpl;
import model.Teacher;

import java.util.List;

public class TeacherService {
    private final TeacherDAO teacherDAO = new TeacherDAOImpl();

    public boolean addTeacher(Teacher teacher) {
        if (teacher == null || teacher.getTeacherCode() == null || teacher.getTeacherCode().isBlank()) {
            return false; // Validation cơ bản
        }
        return teacherDAO.insert(teacher);
    }

    public boolean updateTeacher(Teacher teacher) {
        return teacherDAO.update(teacher);
    }

    public boolean deleteTeacher(int teacherId) {
        return teacherDAO.delete(teacherId);
    }

    public Teacher getTeacherById(int teacherId) {
        return teacherDAO.getById(teacherId);
    }

    public Teacher getTeacherByUserId(int userId) {
        return teacherDAO.getByUserId(userId);
    }

    public List<Teacher> getAllTeachers() {
        return teacherDAO.getAll();
    }
}
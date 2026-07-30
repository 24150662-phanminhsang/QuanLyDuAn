package service;

import dao.ClassDAO;
import dao.impl.ClassDAOImpl;
import model.ClassRoom;

import java.util.List;

public class ClassService {
    private final ClassDAO classDAO = new ClassDAOImpl();

    public boolean createClass(ClassRoom classRoom) {
        if (classRoom == null || classRoom.getClassName().isBlank()) {
            return false;
        }
        return classDAO.insert(classRoom);
    }

    public boolean updateClass(ClassRoom classRoom) {
        return classDAO.update(classRoom);
    }

    public boolean deleteClass(int classId) {
        return classDAO.delete(classId);
    }

    public List<ClassRoom> getAllClasses() {
        return classDAO.getAll();
    }

    public List<ClassRoom> getClassesByTeacher(int teacherId) {
        return classDAO.getByTeacherId(teacherId);
    }

    public boolean assignTeacherToClass(int classId, int teacherId) {
        return classDAO.assignTeacher(classId, teacherId);
    }
}
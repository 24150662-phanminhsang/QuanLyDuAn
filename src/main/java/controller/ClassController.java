package controller;

import model.ClassRoom;
import service.ClassService;

import java.util.List;

public class ClassController {
    private final ClassService classService = new ClassService();

    public List<ClassRoom> getAllClasses() {
        return classService.getAllClasses();
    }

    public boolean createClass(ClassRoom classRoom) {
        return classService.createClass(classRoom);
    }

    public void assignTeacher(int classId, int teacherId) {
        classService.assignTeacherToClass(classId, teacherId);
    }
}
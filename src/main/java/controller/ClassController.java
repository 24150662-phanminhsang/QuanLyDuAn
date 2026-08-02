package controller;

import model.ClassRoom;
import service.ClassService;

import java.util.List;

public class ClassController {
    private final ClassService service;
    public ClassController() { this.service = new ClassService(); }

    public List<ClassRoom> getAllClasses() { return service.getAllClasses(); }
    public List<ClassRoom> searchClasses(String keyword, String status) { return service.searchClasses(keyword, status); }
    public List<ClassRoom> getClassesByTeacherId(int teacherId) { return service.getClassesByTeacherId(teacherId); }
    public List<ClassRoom> getClassesByTeacher(int teacherId) { return service.getClassesByTeacher(teacherId); }
    public List<ClassRoom> getClassesByCourseId(int courseId) { return service.getClassesByCourseId(courseId); }
    public ClassRoom getClassById(int classId) { return service.getClassById(classId); }
    public boolean createClass(ClassRoom classRoom) { return service.createClass(classRoom); }
    public boolean updateClass(ClassRoom classRoom) { return service.updateClass(classRoom); }
    public boolean deleteClass(int classId) { return service.deleteClass(classId); }
    public boolean assignTeacher(int classId, int teacherId) { return service.assignTeacherToClass(classId, teacherId); }
    public boolean isTeacherAssignedToClass(int teacherId, int classId) { return service.isTeacherAssignedToClass(teacherId, classId); }
    public boolean openClass(int classId) { return service.openClass(classId); }
    public boolean closeClass(int classId) { return service.closeClass(classId); }
    public boolean completeClass(int classId) { return service.completeClass(classId); }
    public boolean cancelClass(int classId) { return service.cancelClass(classId); }
    public int countEnrollments(int classId) { return service.countEnrollments(classId); }
    public int countActiveEnrollments(int classId) { return service.countActiveEnrollments(classId); }
}

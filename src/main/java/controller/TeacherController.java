package controller;

import service.TeacherService;
import model.Teacher;

public class TeacherController {
    private TeacherService teacherService = new TeacherService();

    public void createTeacher(Teacher teacher) {
        teacherService.addTeacher(teacher);
    }
}

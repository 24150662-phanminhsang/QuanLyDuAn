
package controller;

import service.ClassService;

public class ClassController {
    private ClassService classService = new ClassService();

    public void assignTeacher(int classId, int teacherId) {
        classService.assignTeacherToClass(classId, teacherId);
    }
}


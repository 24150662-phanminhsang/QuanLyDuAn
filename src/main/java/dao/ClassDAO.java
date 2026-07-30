package dao;

import model.ClassRoom;
import java.util.List;

public interface ClassDAO {
    boolean insert(ClassRoom classRoom);
    boolean update(ClassRoom classRoom);
    boolean delete(int classId);
    ClassRoom getById(int classId);
    List<ClassRoom> getAll();
    List<ClassRoom> getByTeacherId(int teacherId);
    boolean assignTeacher(int classId, int teacherId);
}
package dao;


import model.Teacher;
import java.util.List;

public interface TeacherDAO {
    boolean insert(Teacher teacher);
    boolean update(Teacher teacher);
    boolean delete(int teacherId);
    Teacher getById(int teacherId);
    Teacher getByUserId(int userId);
    List<Teacher> getAll();
}
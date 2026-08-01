package dao;

import model.Grade;

import java.util.List;

public interface GradeDAO {

    boolean insert(Grade grade);

    boolean update(Grade grade);

    boolean delete(int gradeId);

    Grade getById(int gradeId);

    Grade getByEnrollmentId(int enrollmentId);

    Grade getByStudentAndClass(
            int studentId,
            int classId
    );

    List<Grade> getByStudentId(int studentId);

    List<Grade> getByClassId(int classId);

    List<Grade> getAll();
}
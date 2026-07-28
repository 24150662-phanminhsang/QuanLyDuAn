package dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {

    List<T> findAll() throws SQLException;

    Optional<T> findById(int id) throws SQLException;

    boolean insert(T entity) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean deleteById(int id) throws SQLException;
}
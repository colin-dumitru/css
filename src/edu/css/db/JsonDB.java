package edu.css.db;

import java.util.List;

/**
 * Catalin Dumitru
 * Universitatea Alexandru Ioan Cuza
 */
public interface JsonDB {
    void begin();

    void end(boolean saveChanges);

    <T> List<T> getAll(Class<T> clazz);

    <T> T find(Object id, Class<T> clazz);

    <T> void delete(T entity);

    <T> void save(T entity);
}

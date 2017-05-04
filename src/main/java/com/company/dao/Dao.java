package com.company.dao;

import java.util.List;

public interface Dao<T, ID> {

    T findById(ID id);

    List<T> findAll();

    T save(T entity);

    void delete(T entity);

    void flush();

    void clear();

    T update(T entity);
}


package com.company.dao;

import java.util.Collection;

public interface Dao<T, ID> {

    T findById(ID id);

    Collection<T> findAll();

    T save(T entity);

    void delete(T entity);

    T update(T entity);

    void flush();

    void clear();
}


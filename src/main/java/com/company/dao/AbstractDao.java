package com.company.dao;

import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;

public class AbstractDao<T, ID extends Serializable> implements Dao<T, ID> {

    protected final SessionFactory sessionFactory;

    private Class<T> persistentClass;

    public AbstractDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    }

    @Override
    public T findById(ID id) {
        return sessionFactory.getCurrentSession().get(getPersistentClass(), id);
    }

    @Override
    public Collection<T> findAll() {
        return sessionFactory.getCurrentSession().createQuery("FROM " + getPersistentClass().getSimpleName(),
                getPersistentClass()).list();
    }

    @Override
    public T save(T entity) {
        sessionFactory.getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    @Override
    public void delete(T entity) {
        sessionFactory.getCurrentSession().delete(entity);
    }

    @Override
    public T update(T entity) {
        sessionFactory.getCurrentSession().merge(entity);
        return entity;
    }

    @Override
    public void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    @Override
    public void clear() {
        sessionFactory.getCurrentSession().clear();
    }

    protected Class<T> getPersistentClass() {
        return persistentClass;
    }
}


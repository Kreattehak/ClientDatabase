package com.company.configuration.security.repository;

import com.company.dao.AbstractDao;
import com.company.model.security.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class UserRepositoryImpl extends AbstractDao<User, Long> implements UserRepository {

    @Override
    public User findByUsername(String username) {
        String query = String.format("FROM %s WHERE USERNAME = '%s'",
                getPersistentClass().getSimpleName(), username);
        return sessionFactory.getCurrentSession().createQuery(query, User.class).getSingleResult();
    }
}

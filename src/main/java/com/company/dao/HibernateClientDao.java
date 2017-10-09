package com.company.dao;

import com.company.model.Client;
import com.company.model.security.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public class HibernateClientDao extends AbstractDao<Client, Long> implements ClientDao {

    @Autowired
    public HibernateClientDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    //further implementations

}
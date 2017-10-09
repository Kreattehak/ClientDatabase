package com.company.dao;

import com.company.model.Client;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateClientDao extends AbstractDao<Client, Long> implements ClientDao {

    @Autowired
    public HibernateClientDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    //further implementations

}
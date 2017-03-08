package com.company.dao;

import com.company.model.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
public class ClientDaoImpl implements ClientDao {

    @Autowired
    private SessionFactory sessionFactory;

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public Serializable save(Client client) {
        return getSession().save(client);
    }

    @Override
    public Client findById(final Serializable id) {
        return getSession().get(Client.class, id);
    }
}
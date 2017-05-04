package com.company.repository;

import com.company.model.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Deprecated - use classes from dao package
 */

public class ClientRepoImpl implements ClientRepo {

    private List<Client> allClients = new ArrayList<>();
    private Session session;

    @Autowired
    private SessionFactory sessionFactory;
    private Session openSession() {
        return this.sessionFactory.openSession();
    }

    @Override
    public Serializable save(Client client) {
        session = openSession();
        session.beginTransaction();
        Serializable id = session.save(client);
        session.getTransaction().commit();
        session.close();
        return id;
    }

    @Override
    public Client findById(final Serializable id) {
        session = openSession();
        session.beginTransaction();
        Client client = session.get(Client.class, id);
        session.getTransaction().commit();
        session.close();
        return client;
    }

    @Override
    public void delete(Client client) {
        session = openSession();
        session.beginTransaction();
        session.delete(client);
        session.getTransaction().commit();
        session.close();
        allClients.remove(client.getId());
    }

    @Override
    public void update(Client client) {
        session = openSession();
        session.beginTransaction();
        session.update(client);
        session.getTransaction().commit();
        session.close();
    }

    @Override
    public List<Client> getAllClients() {
        return allClients.size() == 0 ? openSession().createQuery("FROM Client").getResultList() : allClients;
    }

}

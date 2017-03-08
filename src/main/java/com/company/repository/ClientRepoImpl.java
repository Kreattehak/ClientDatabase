package com.company.repository;

import com.company.model.Client;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClientRepoImpl implements ClientRepo {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected Session getSession() {
        return this.sessionFactory.getCurrentSession();
    }

    @Override
    public List<Client> getAllClients() {
        return getSession().createQuery("FROM Client").getResultList();
    }

    @Override
    public List<Client> fullFillDatabase() {
        return jdbcTemplate.query("SELECT customer_id, first_name, last_name FROM sakila.customer LIMIT 10",
                (rs, rowNum) -> new Client(rs.getString(2), rs.getString(3),
                        Math.random() > 0.5 ? "Senior Developer" : "Junior Developer", (int) Math.round(Math.random() * 2720)));
    }
}

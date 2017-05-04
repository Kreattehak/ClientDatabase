package com.company.dao;

import com.company.model.Client;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HibernateClientDao extends AbstractDao<Client, Long> implements ClientDao {

    @Override
    public List<Client> findByFirstName(String firstName) {
        return null;
    }

    @Override
    public List<Client> findByLastName(String lastName) {
        return null;
    }
}
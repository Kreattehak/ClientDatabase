package com.company.dao;

import com.company.model.Client;

import java.util.List;

public interface ClientDao extends Dao<Client, Long> {

    List<Client> findByFirstName(String firstName);

    List<Client> findByLastName(String lastName);

}


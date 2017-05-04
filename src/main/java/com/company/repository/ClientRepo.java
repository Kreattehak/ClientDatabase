package com.company.repository;

import com.company.model.Client;

import java.io.Serializable;
import java.util.List;

/**
 * Deprecated - use classes from dao package
 */

public interface ClientRepo {

    Serializable save(Client client);

    Client findById(Serializable id);

    void delete(Client client);

    void update(Client client);

    List<Client> getAllClients();

}


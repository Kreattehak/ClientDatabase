package com.company.dao;

import com.company.model.Client;

import java.io.Serializable;

public interface ClientDao {

    Serializable save(Client client);

    Client findById(Serializable id);

}
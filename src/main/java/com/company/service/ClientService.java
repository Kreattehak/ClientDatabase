package com.company.service;

import com.company.model.AddClientForm;
import com.company.model.Client;

import java.util.List;

public interface ClientService {

    Client findClientById(Long id);

    List<Client> findAllClients();

    Client saveClient(AddClientForm clientForm);

    void deleteClient(Client client);

    Client updateClient(Client client);
}

package com.company.service;

import com.company.model.Client;

import java.util.List;
import java.util.stream.Stream;

public interface ClientService {

    Client getClient(Long id);

    void addNewClient(Client client);

    List<Client> getAllClients();

    List<Client> fullFillDatabase();
}

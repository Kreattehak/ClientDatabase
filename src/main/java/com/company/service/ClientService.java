package com.company.service;

import com.company.model.Client;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ClientService {

    Client findClientById(Long clientId, HttpServletRequest request);

    Client findClientByIdAndCleanUnnecessaryData(Long clientId, HttpServletRequest request);

    List<Client> getAllClients();

    Client[] getAllClientsAsArray();

    Client saveClient(Client client, HttpServletRequest request);

    void deleteClient(Long clientId, HttpServletRequest request);

    Client updateClient(Client client, HttpServletRequest request);

    void flush();
}

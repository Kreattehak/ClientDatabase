package com.company.service;

import com.company.dao.ClientDao;
import com.company.model.AddClientForm;
import com.company.model.Address;
import com.company.model.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

//    @InjectLogger("com.company.ClientServiceImpl")
//    private static Logger logger;

    @Autowired
    private ClientDao clientDao;

    @Override
    public Client findClientById(Long id) {
        return clientDao.findById(id);
    }

    @Override
    public List<Client> findAllClients() {
        return clientDao.findAll();
    }

    @Override
    @Transactional(readOnly = false)
    public Client saveClient(AddClientForm clientForm) {
//        logger.debug("New client added " + client);
        Client client = new Client(clientForm.getFirstName(), clientForm.getLastName(), clientForm.getDateOfRegistration());
        Address address = new Address(clientForm.getStreetName(), clientForm.getCityName(), clientForm.getZipCode());
        client.getAddress().add(address);
        address.setClient(client);
        return clientDao.save(client);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteClient(Client client) {
        clientDao.delete(client);
//        logger.debug("Client with id" + client.getId() + " was deleted.");
    }

    @Override
    @Transactional(readOnly = false)
    public Client updateClient(Client client) {
//        logger.debug("Client with id" + client.getId() + " was edited.");
        Client oldDataClient = clientDao.findById(client.getId());
        clientDao.update(client);
        return client;
    }
}
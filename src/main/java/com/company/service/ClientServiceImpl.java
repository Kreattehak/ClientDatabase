package com.company.service;

import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    @InjectLogger("com.company.service.ClientServiceImpl")
    private static Logger logger;

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
    public Client saveClient(Client client) {
        logger.debug("New client added " + client);
        return clientDao.save(client);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteClient(Client client) {
        clientDao.delete(client);
        logger.debug("Client with id" + client.getId() + " was deleted.");
    }

    @Override
    @Transactional(readOnly = false)
    public Client updateClient(Client client) {
        logger.debug("Client with id" + client.getId() + " was edited.");
        return clientDao.update(client);
    }

    @Override
    public void flush() {
        clientDao.flush();
    }
}
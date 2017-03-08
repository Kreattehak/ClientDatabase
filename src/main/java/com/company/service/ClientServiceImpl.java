package com.company.service;

import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.repository.ClientRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    final static Logger logger = LogManager.getLogger(ClientServiceImpl.class);

    @Autowired
    ClientDao clientDao;

    @Autowired
    private ClientRepo clientRepo;

    public Client getClient(Long id) {
        logger.debug("Getting employee with id " + id);
        return clientDao.findById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void addNewClient(Client client) {
        Long id = (Long) clientDao.save(client);
        logger.debug("Id of new Client " + id);
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepo.getAllClients();
    }

    @Override
    public List<Client> fullFillDatabase() {
        return clientRepo.fullFillDatabase();
    }
}
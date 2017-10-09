package com.company.service;

import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.util.InjectLogger;
import com.company.util.SyntacticallyIncorrectRequestException;
import com.company.util.WebDataResolverAndCreator;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.company.util.Mappings.CLIENT_SERVICE_LOGGER_NAME;
import static com.company.util.WebDataResolverAndCreator.getUserData;

@Service
@Transactional(readOnly = true)
public class HibernateClientService implements ClientService {

    @Value("${exception.findClient}")
    private String findClientExceptionMessage;
    @Value("${exception.updateClient}")
    private String updateClientExceptionMessage;
    @Value("${exception.deleteClient}")
    private String deleteClientExceptionMessage;

    @InjectLogger(CLIENT_SERVICE_LOGGER_NAME)
    private static Logger logger;

    private final ClientDao clientDao;

    @Autowired
    public HibernateClientService(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public Client findClientById(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientDao.findById(clientId);
        boolean isRequestProper = (clientFromDatabase != null);
        if (!isRequestProper) {
            logger.warn("{} tried to get client with id {}, but that client doesn't exist. "
                    + "This request was handmade.", getUserData(request), clientId);
            throw new SyntacticallyIncorrectRequestException(findClientExceptionMessage);
        }

        return clientFromDatabase;
    }

    @Override
    public Client findClientByIdAndCleanUnnecessaryData(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = findClientById(clientId, request);
        WebDataResolverAndCreator.cleanClientData(clientFromDatabase);
        return clientFromDatabase;
    }

    @Override
    public List<Client> getAllClients() {
        return new ArrayList<>(clientDao.findAll());
    }

    @Override
    public Client[] getAllClientsAsArray() {
        return clientDao.findAll().toArray(new Client[0]);
    }

    //TODO: SHOULD I ADD UserData
    @Override
    @Transactional(readOnly = false)
    public Client saveClient(Client client, HttpServletRequest request) {
        Client clientStoredInDatabase = clientDao.save(client);

        logger.info("New client added " + client.getId());
        logger.trace("New client added " + client);

        return clientStoredInDatabase;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteClient(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = findClientById(clientId, request);
//        Client clientFromDatabase = clientDao.findById(clientId);
//        boolean isRequestProper = (clientFromDatabase != null);
//        if (!isRequestProper) {
//            logger.warn("{} tried to delete client with id {}. This request was handmade.",
//                    getUserData(request), clientId);
//            throw new SyntacticallyIncorrectRequestException(deleteClientExceptionMessage);
//        }

        clientDao.delete(clientFromDatabase);

        logger.info("Client with id " + clientId + " was deleted.");
        logger.trace("Client {} {} with id {} was deleted.",
                clientFromDatabase.getFirstName(), clientFromDatabase.getLastName(), clientFromDatabase.getId());
    }

    @Override
    @Transactional(readOnly = false)
    public Client updateClient(Client editedClient, HttpServletRequest request) {
        Long clientId = editedClient.getId();
        boolean isRequestProper = (clientId != null);
        if (!isRequestProper) {
            logger.warn("{} tried to update client. This request was handmade, with data: {}",
                    getUserData(request), editedClient);
            throw new SyntacticallyIncorrectRequestException(updateClientExceptionMessage);
        }
        Client clientFromDatabase = findClientById(clientId, request);

        String oldClientData = "Client " + clientFromDatabase.getFirstName() + " "
                + clientFromDatabase.getLastName() + " with id " + clientFromDatabase.getId();
        clientFromDatabase.setFirstName(editedClient.getFirstName());
        clientFromDatabase.setLastName(editedClient.getLastName());

        logger.info("Client with id {} was edited with data firstName= {}, lastName= {}",
                clientFromDatabase.getId(), clientFromDatabase.getFirstName(), clientFromDatabase.getLastName());
        logger.trace("{} was edited with data firstName= {}, lastName= {}",
                oldClientData, clientFromDatabase.getFirstName(), clientFromDatabase.getLastName());

        return clientDao.update(clientFromDatabase);
    }

    @Override
    public void flush() {
        clientDao.flush();
    }
}
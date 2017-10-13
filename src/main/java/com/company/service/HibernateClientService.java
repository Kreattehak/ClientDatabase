package com.company.service;

import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.util.InjectLogger;
import com.company.util.ProcessUserRequestException;
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
    private final WebDataResolverAndCreator webDataResolverAndCreator;

    @Autowired
    public HibernateClientService(ClientDao clientDao, WebDataResolverAndCreator webDataResolverAndCreator) {
        this.clientDao = clientDao;
        this.webDataResolverAndCreator = webDataResolverAndCreator;
    }

    @Override
    public Client findClientById(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = clientDao.findById(clientId);
        boolean isRequestProper = (clientFromDatabase != null);
        if (!isRequestProper) {
            logger.warn("{} tried to get client with id {}, but that client doesn't exist. "
                            + "This request was handmade.",
                    webDataResolverAndCreator.getUserData(request), clientId);
            System.out.println(findClientExceptionMessage);
            throw new ProcessUserRequestException(findClientExceptionMessage);
        }

        return clientFromDatabase;
    }

    @Override
    public Client findClientByIdAndCleanUnnecessaryData(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase = findClientById(clientId, request);
        webDataResolverAndCreator.cleanClientData(clientFromDatabase);

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

    @Override
    @Transactional(readOnly = false)
    public Client saveClient(Client client, HttpServletRequest request) {
        Client clientStoredInDatabase = clientDao.save(client);

        logger.info("New client added {}.", client.getId());
        logger.trace("{} added a new client firstName={}, lastName={}.",
                webDataResolverAndCreator.getUserData(request), client.getFirstName(), client.getLastName());

        return clientStoredInDatabase;
    }

    //TODO: Is catching and rethrowing exception really a good idea?
    @Override
    @Transactional(readOnly = false)
    public void deleteClient(Long clientId, HttpServletRequest request) {
        Client clientFromDatabase;
        try {
            clientFromDatabase = findClientById(clientId, request);
        } catch (ProcessUserRequestException cause) {
            logger.warn("{} tried to delete client with id {}. This request was handmade.",
                    webDataResolverAndCreator.getUserData(request), clientId);
            throw new ProcessUserRequestException(deleteClientExceptionMessage, cause);
        }

        clientDao.delete(clientFromDatabase);

        logger.info("Client with id {} was deleted.", clientId);
        logger.trace(webDataResolverAndCreator.getUserData(request) + " deleted client {} {} with id {}.",
                clientFromDatabase.getFirstName(), clientFromDatabase.getLastName(), clientFromDatabase.getId());
    }

    @Override
    @Transactional(readOnly = false)
    public Client updateClient(Client editedClient, HttpServletRequest request) {
        Long clientId = editedClient.getId();
        boolean isRequestProper = (clientId != null);
        if (!isRequestProper) {
            logger.warn("{} tried to update client. This request was handmade, with data: {}.",
                    webDataResolverAndCreator.getUserData(request), editedClient);
            throw new ProcessUserRequestException(updateClientExceptionMessage);
        }
        Client clientFromDatabase = findClientById(clientId, request);

        String oldClientData = "client " + clientFromDatabase.getFirstName() + " "
                + clientFromDatabase.getLastName() + " with id " + clientFromDatabase.getId();
        clientFromDatabase.setFirstName(editedClient.getFirstName());
        clientFromDatabase.setLastName(editedClient.getLastName());

        logger.info("Client with id {} was edited with data firstName{}, lastName={}.",
                clientFromDatabase.getId(), clientFromDatabase.getFirstName(), clientFromDatabase.getLastName());
        logger.trace("{} edited {} with data firstName={}, lastName={}.",
                webDataResolverAndCreator.getUserData(request), oldClientData,
                clientFromDatabase.getFirstName(), clientFromDatabase.getLastName());

        return clientDao.update(clientFromDatabase);
    }

    @Override
    public void flush() {
        clientDao.flush();
    }
}
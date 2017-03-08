package com.company;

import com.company.model.Client;
import com.company.service.ClientService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MyApplication {

    final static Logger logger = LogManager.getLogger(MyApplication.class);

    @Autowired
    private ClientService clientService;

    public void performDbTasks() {
        Client clientToPersist = new Client(1l, "DANY", "DEVITO", "Senior Developer", 2000);

        clientService.addNewClient(clientToPersist);

        Client retrivedClient = clientService.getClient(clientToPersist.getId());
        logger.debug("Retrieving saved employee " + retrivedClient);
    }

    public void queryAllClients() {
        clientService.getAllClients().forEach(System.out::println);
    }

    public void fullFillDatabase() {
        clientService.fullFillDatabase().forEach(clientService::addNewClient);
    }
}
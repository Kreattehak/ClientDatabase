package com.company.controller;

import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
public class ClientRestController {

    @InjectLogger("com.company.controller.ClientController")
    private static Logger logger;

    private ClientService clientService;
    private AddressService addressService;

    @Autowired
    public ClientRestController(ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @GetMapping(value = "/getAllClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client[] allClients() {
        return clientService.findAllClients().toArray(new Client[0]);
    }

    @GetMapping(value = "/admin/getClient", produces = MediaType.APPLICATION_JSON_VALUE)
    public Client getClient(@RequestParam() long id) {
        return clientService.findClientById(id);
    }

    @PostMapping(value = "/admin/deleteClient", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteClient(@RequestBody() Client client) {
        Client clientToBeRemoved = clientService.findClientById(client.getId());
        clientService.deleteClient(clientToBeRemoved);
        logger.info("Client removed ->" + clientToBeRemoved);
        return true;
    }

    @PostMapping(value = "/admin/updateClient", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateClient(@Valid @RequestBody() Client clientEditData, BindingResult result) {
        if (result.hasErrors()) {
            return false;
        }
        Client clientToChange = clientService.findClientById(clientEditData.getId());
        clientToChange.setFirstName(clientEditData.getFirstName());
        clientToChange.setLastName(clientEditData.getLastName());
        clientService.updateClient(clientToChange);
        logger.info("Client edited ->" + clientToChange);
        return true;
    }

    @PostMapping(value = "/admin/saveNewClient", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Long processAddNewClient(@RequestBody() Client newClient, BindingResult result) {
        if (result.hasErrors()) {
            return -1L;
        }
        Client client = clientService.saveClient(newClient);
        logger.info("Client added ->" + client);
        return client.getId();
    }
}

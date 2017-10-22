package com.company.controller;

import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.LocalizedMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.company.util.Mappings.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(REST_API_PREFIX)
public class ClientRestController {

    private final String clientSuccessfullyRemoved = "clientSuccessfullyRemoved";
    private final String clientNotFound = "clientNotFound";
    private final String clientSuccessfullyEdited = "clientSuccessfullyEdited";
    private final ClientService clientService;
    private final LocalizedMessages localizedMessages;

    @Autowired
    public ClientRestController(ClientService clientService, LocalizedMessages localizedMessages) {
        this.clientService = clientService;
        this.localizedMessages = localizedMessages;
    }

    @GetMapping(value = REST_GET_ALL_CLIENTS, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Client[]> getAllClients() {
        Client[] clients = clientService.getAllClientsAsArray();
        return new ResponseEntity<>(clients, OK);
    }

    @GetMapping(value = REST_GET_CLIENT, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Client> getClient(@RequestParam long id, HttpServletRequest request) {
        Client client = clientService.findClientById(id, request);
        return new ResponseEntity<>(client, OK);
    }

    //Passing a request with a body to an HTTP DELETE action is not currently supported in Angular 2
    @PostMapping(value = REST_DELETE_CLIENT, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteClient(@Valid @RequestBody Client client, HttpServletRequest request) {
        clientService.deleteClient(client.getId(), request);
        return new ResponseEntity<>(localizedMessages.getMessage(clientSuccessfullyRemoved), OK);
    }

    @PutMapping(value = REST_UPDATE_CLIENT, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateClient(@Valid @RequestBody Client clientEditData, BindingResult result,
                                               HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(localizedMessages.getMessage(clientNotFound), UNPROCESSABLE_ENTITY);
        }
        clientService.updateClient(clientEditData, request);
        return new ResponseEntity<>(localizedMessages.getMessage(clientSuccessfullyEdited), OK);
    }

    @PostMapping(value = REST_SAVE_NEW_CLIENT, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> processAddNewClient(@Valid @RequestBody Client newClient, BindingResult result,
                                                    HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(ID_NOT_FOUND, UNPROCESSABLE_ENTITY);
        }
        clientService.saveClient(newClient, request);
        return new ResponseEntity<>(newClient.getId(), OK);
    }
}

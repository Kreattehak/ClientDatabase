package com.company.controller;

import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.SyntacticallyIncorrectRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

import static com.company.util.Mappings.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping(REST_API_PREFIX)
public class ClientRestController {

    @Value("${clientSuccessfullyRemoved}")
    private String clientSuccessfullyRemoved;
    @Value("${clientNotFound}")
    private String clientNotFound;
    @Value("${clientSuccessfullyEdited}")
    private String clientSuccessfullyEdited;

    private final ClientService clientService;

    @Autowired
    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = REST_GET_ALL_CLIENTS, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Client[]> getAllClients(){
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
        return new ResponseEntity<>(clientSuccessfullyRemoved, OK);
    }

    @PutMapping(value = REST_UPDATE_CLIENT, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateClient(@Valid @RequestBody Client clientEditData, BindingResult result,
                                               HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(clientNotFound, UNPROCESSABLE_ENTITY);
        }
        clientService.updateClient(clientEditData, request);
        return new ResponseEntity<>(clientSuccessfullyEdited, OK);
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

    //TODO: REQUEST
    @ExceptionHandler(SyntacticallyIncorrectRequestException.class)
    public ResponseEntity<Map<String, String>> conflict(Exception e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(ERROR_MESSAGE, e.getMessage());
        responseBody.put(HTTP_STATUS, String.valueOf(UNPROCESSABLE_ENTITY.value()));
        return new ResponseEntity<>(responseBody, UNPROCESSABLE_ENTITY);
    }
}

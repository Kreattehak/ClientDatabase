package com.company.controller;

import com.company.model.Client;
import com.company.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.company.util.Mappings.*;

@Controller
public class ClientController {

    private final ClientService clientService;

    static final String CLIENTS = "clients";
    static final String NEW_CLIENT = "newClient";
    static final String CLIENT_TO_BE_EDITED = "clientToBeEdited";
    static final String WITH_CLIENT_ID_GET_PARAMETER = "?clientId=";

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(TABLE_OF_CLIENTS)
    public String mainPage(Model model) {
        model.addAttribute(CLIENTS, clientService.getAllClients());
        return extractViewName(TABLE_OF_CLIENTS);
    }

    @GetMapping(ADD_CLIENT)
    public String addNewClient(@ModelAttribute(NEW_CLIENT) Client newClient) {
        return extractViewName(ADD_CLIENT);
    }

    @PostMapping(ADD_CLIENT)
    public String processAddNewClient(@Valid @ModelAttribute(NEW_CLIENT) Client newClient, BindingResult result,
                                      @RequestParam(defaultValue = "false") boolean shouldAddAddress,
                                      HttpServletRequest request, HttpServletResponse response) {
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return extractViewName(ADD_CLIENT);
        }
        Client client = clientService.saveClient(newClient, request);
        return shouldAddAddress ? REDIRECT + ADD_ADDRESS + WITH_CLIENT_ID_GET_PARAMETER + client.getId() :
                REDIRECT + TABLE_OF_CLIENTS;
    }

    @GetMapping(REMOVE_CLIENT)
    public String removeClient(@RequestParam long clientId, HttpServletRequest request) {
        clientService.deleteClient(clientId, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @GetMapping(EDIT_CLIENT)
    public String editClient(@RequestParam long clientId, Model model, HttpServletRequest request) {
        model.addAttribute(CLIENT_TO_BE_EDITED,
                clientService.findClientByIdAndCleanUnnecessaryData(clientId, request));
        return extractViewName(EDIT_CLIENT);
    }

    @PutMapping(EDIT_CLIENT)
    public String processEditClient(@Valid @ModelAttribute(CLIENT_TO_BE_EDITED) Client clientToBeEdited,
                                    BindingResult result, HttpServletRequest request,
                                    HttpServletResponse response) {
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return extractViewName(EDIT_CLIENT);
        }
        clientService.updateClient(clientToBeEdited, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }
}

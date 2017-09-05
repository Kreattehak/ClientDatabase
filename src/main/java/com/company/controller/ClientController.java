package com.company.controller;

import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.FormDataCleaner;
import com.company.util.InjectLogger;
import com.company.util.Mappings;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ClientController {

    @InjectLogger("com.company.controller.ClientController")
    private static Logger logger;

    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(Mappings.TABLE_OF_CLIENTS)
    public String hello(Model model) {
        model.addAttribute("clients", clientService.findAllClients());
        return "clientsTable";
    }

    @GetMapping(Mappings.ADD_CLIENT)
    public String addNewClient(@ModelAttribute("newClient") Client newClient) {
        return "addClient";
    }

    @PostMapping(Mappings.ADD_CLIENT)
    public String processAddNewClient(@Valid @ModelAttribute("newClient") Client newClient, BindingResult result,
                                      @RequestParam(defaultValue = "false") boolean shouldAddAddress) {
        if (result.hasErrors()) {
            return "addClient";
        }
        Client client = clientService.saveClient(newClient);
        logger.info("Client added ->" + client);
        return shouldAddAddress ? "redirect:/admin/addAddress?id=" + client.getId() : "redirect:/clientsTable";
    }

    @GetMapping(value = Mappings.REMOVE_CLIENT)
    public String removeClient(@RequestParam long id) {
        Client clientToBeRemoved = clientService.findClientById(id);
        clientService.deleteClient(clientToBeRemoved);
        logger.info("Client removed ->" + clientToBeRemoved);
        return "redirect:/clientsTable";
    }

    @GetMapping(value = Mappings.EDIT_CLIENT)
    public String editClient(@RequestParam long id, Model model) {
        Client clientFromDatabase = clientService.findClientById(id);
        FormDataCleaner.cleanClientData(clientFromDatabase);
        model.addAttribute("clientToBeEdited", clientFromDatabase);
        return "editClient";
    }

    @PutMapping(Mappings.EDIT_CLIENT)
    public String processEditClient(@Valid @ModelAttribute Client clientToBeEdited,
                                    BindingResult result) {
        if (result.hasErrors()) {
            return "editClient";
        }
        Client clientToChange = clientService.findClientById(clientToBeEdited.getId());
        clientToChange.setFirstName(clientToBeEdited.getFirstName());
        clientToChange.setLastName(clientToBeEdited.getLastName());
        Client client = clientService.updateClient(clientToChange);
        logger.info("Client edited ->" + clientToChange);
        return "redirect:/clientsTable";
    }

}

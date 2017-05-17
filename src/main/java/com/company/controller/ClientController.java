package com.company.controller;

import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.FormDataCleaner;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@Profile("!test")
public class ClientController {

    @InjectLogger("com.company.controller.ClientController")
    private static Logger logger;

    private ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/clientsTable")
    public String hello(Model model) {
        model.addAttribute("clients", clientService.findAllClients());
        return "clientsTable";
    }

    @GetMapping("/admin/addClient")
    public String addNewClient(@ModelAttribute("newClient") Client newClient) {
        return "addClient";
    }

    @PostMapping("/admin/addClient")
    public String processAddNewClient(@ModelAttribute("newClient") @Valid Client newClient, BindingResult result,
                                      @RequestParam(defaultValue = "false") boolean shouldAddAddress) {
        if (result.hasErrors()) {
            return "addClient";
        }
        Client client = clientService.saveClient(newClient);
        logger.info("Client added ->" + client);
        return shouldAddAddress ? "redirect:/addAddress?id=" + client.getId() : "redirect:/clientsTable";
    }

    @GetMapping(value = "/admin/removeClient")
    public String removeClient(@RequestParam() long id) {
        Client clientToBeRemoved = clientService.findClientById(id);
        clientService.deleteClient(clientToBeRemoved);
        logger.info("Client removed ->" + clientToBeRemoved);
        return "redirect:/clientsTable";
    }

    @GetMapping(value = "/admin/editClient")
    public String editClient(@RequestParam() long id, Model model) {
        Client clientFromDatabase = clientService.findClientById(id);
        FormDataCleaner.cleanClientData(clientFromDatabase);
        model.addAttribute("clientToBeEdited", clientFromDatabase);
        return "editClient";
    }

    @PostMapping("/admin/editClient")
    public String processEditClient(@Valid @ModelAttribute("clientToBeEdited") Client clientEditData,
                                    BindingResult result) {
        if (result.hasErrors()) {
            return "editClient";
        }
        Client clientToChange = clientService.findClientById(clientEditData.getId());
        clientToChange.setFirstName(clientEditData.getFirstName());
        clientToChange.setLastName(clientEditData.getLastName());
        Client client = clientService.updateClient(clientToChange);
        logger.info("Client edited ->" + clientToChange);
        return "redirect:/clientsTable";
    }

}

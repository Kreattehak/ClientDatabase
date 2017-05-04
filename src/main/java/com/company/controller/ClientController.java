package com.company.controller;

import com.company.model.AddClientForm;
import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import com.company.util.SpringAndHibernateValidator;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@Profile("!test")
public class ClientController {

    @InjectLogger("com.company.controller.ClientController")
    private static Logger logger;

    private ClientService clientService;
    private SpringAndHibernateValidator sahValidator;

    @Autowired
    public ClientController(ClientService clientService, SpringAndHibernateValidator sahValidator) {
        this.clientService = clientService;
        this.sahValidator = sahValidator;
    }

    @GetMapping("/clientsTable")
    public String hello(Model model) {
        model.addAttribute("clients", clientService.findAllClients());
        return "clientsTable";
    }

    @GetMapping("/addClient")
    public String addNewClient(@ModelAttribute("newClient") AddClientForm newClient, Model model) {
        return "addClient";
    }

    @PostMapping("/addClient")
    public String processAddNewClient(@Valid @ModelAttribute("newClient") AddClientForm newClient,
                                      BindingResult result) {
        if (result.hasErrors()) {
            return "addClient";
        }
        Client client = clientService.saveClient(newClient);
        logger.info("Client added ->" + client);
        return "redirect:/clientsTable";
    }

    @GetMapping(value = "/removeClient")
    public String removeClient(@RequestParam() long id) {
        Client clientToBeRemoved = clientService.findClientById(id);
        clientService.deleteClient(clientToBeRemoved);
        logger.info("Client removed ->" + clientToBeRemoved);
        return "redirect:/";
    }

    @GetMapping(value = "/editClient")
    public String editClient(@RequestParam() long id, Model model) {
        Client clientToBeEdited = clientService.findClientById(id);
        System.out.println(clientToBeEdited);
        model.addAttribute("clientToBeEdited", clientToBeEdited);
        return "editClient";
    }

    @PostMapping("/editClient")
    public String processEditClient(@Valid @ModelAttribute("clientToBeEdited") Client clientToBeEdited,
                                      BindingResult result) {
        if (result.hasErrors()) {
            return "editClient";
        }
        System.out.println(clientToBeEdited);
        Client client = clientService.updateClient(clientToBeEdited);
        logger.info("Client to be edited ->" + clientToBeEdited);
        return "redirect:/clientsTable";
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setValidator(sahValidator);
//        binder.registerCustomEditor(Long.class, new MyCustomNumberEditor());
    }

}

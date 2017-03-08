package com.company.controller;

import com.company.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    private ClientService clientService;

    @RequestMapping("/")
    public String hello(Model model) {
        model.addAttribute("clients", clientService.getAllClients());
        return "welcome";
    }
}

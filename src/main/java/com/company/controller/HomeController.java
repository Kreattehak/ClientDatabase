package com.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/")
    public String hello() {
        return "redirect:/clientsTable";
    }

    @RequestMapping("/blank")
    public String blank() {
        return "blank";
    }

    @RequestMapping("/aboutUs")
    public String aboutUs() {
        return "aboutUs";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }
}

package com.company.controller;

import com.company.util.Mappings;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping(Mappings.DEFAULT_PAGE)
    public String hello() {
        return "redirect:/clientsTable";
    }

    @RequestMapping(Mappings.BLANK_PAGE)
    public String blank() {
        return "blank";
    }

    @RequestMapping(Mappings.ABOUT_US_PAGE)
    public String aboutUs() {
        return "aboutUs";
    }

    @RequestMapping(Mappings.LOGIN_PAGE)
    public String login() {
        return "login";
    }
}

package com.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.company.util.Mappings.REDIRECT;
import static com.company.util.Mappings.TABLE_OF_CLIENTS;
import static com.company.util.Mappings.extractViewName;

@Controller
public class HomeController {

    static final String DEFAULT_PAGE = "/";
    static final String BLANK_PAGE = "/blank";
    static final String ABOUT_US_PAGE = "/aboutUs";
    static final String LOGIN_PAGE = "/login";

    @RequestMapping(DEFAULT_PAGE)
    public String hello() {
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @RequestMapping(BLANK_PAGE)
    public String blank() {
        return extractViewName(BLANK_PAGE);
    }

    @RequestMapping(ABOUT_US_PAGE)
    public String aboutUs() {
        return extractViewName(ABOUT_US_PAGE);
    }

    @RequestMapping(LOGIN_PAGE)
    public String login() {
        return extractViewName(LOGIN_PAGE);
    }
}

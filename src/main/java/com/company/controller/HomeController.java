package com.company.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.company.util.Mappings.*;

@Controller
public class HomeController {

    @RequestMapping(SLASH)
    public String hello() {
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @RequestMapping(BLANK_PAGE)
    public String blank() {
        return extractViewName(BLANK_PAGE);
    }

    @RequestMapping(ABOUT_AUTHOR_PAGE)
    public String aboutAuthor() {
        return extractViewName(ABOUT_AUTHOR_PAGE);
    }

    @RequestMapping(LOGIN_PAGE)
    public String login() {
        return extractViewName(LOGIN_PAGE);
    }
}

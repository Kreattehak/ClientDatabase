package com.company.configuration;

import com.company.controller.AddressController;
import com.company.controller.ClientController;
import com.company.util.SpringAndHibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice(assignableTypes = { AddressController.class, ClientController.class })
public class GlobalControllerAdvice {

    private SpringAndHibernateValidator sahValidator;

    @Autowired
    public GlobalControllerAdvice(SpringAndHibernateValidator sahValidator) {
        this.sahValidator = sahValidator;
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setValidator(sahValidator);
    }

}
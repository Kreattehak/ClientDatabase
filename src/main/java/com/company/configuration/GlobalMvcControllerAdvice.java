package com.company.configuration;

import com.company.controller.AddressController;
import com.company.controller.ClientController;
import com.company.util.ProcessUserRequestException;
import com.company.util.SpringAndHibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletResponse;

import static com.company.util.Mappings.ERROR_MESSAGE;
import static com.company.util.Mappings.ERROR_PAGE;
import static com.company.util.Mappings.extractViewName;

@ControllerAdvice(assignableTypes = {AddressController.class, ClientController.class})
public class GlobalMvcControllerAdvice {

    private SpringAndHibernateValidator sahValidator;

    @Autowired
    public GlobalMvcControllerAdvice(SpringAndHibernateValidator sahValidator) {
        this.sahValidator = sahValidator;
    }

    @InitBinder
    public void initialiseBinder(WebDataBinder binder) {
        binder.setValidator(sahValidator);
    }

    @ExceptionHandler(ProcessUserRequestException.class)
    public String conflict(Model model, HttpServletResponse response, Exception e) {
        response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        model.addAttribute(ERROR_MESSAGE, e.getMessage());
        return extractViewName(ERROR_PAGE);
    }
}
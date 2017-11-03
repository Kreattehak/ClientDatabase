package com.company.configuration;

import com.company.controller.AddressController;
import com.company.controller.AddressRestController;
import com.company.controller.ClientController;
import com.company.controller.ClientRestController;
import com.company.util.ProcessUserRequestException;
import com.company.util.SpringAndHibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

import static com.company.util.Mappings.ERROR_MESSAGE;
import static com.company.util.Mappings.ERROR_PAGE;
import static com.company.util.Mappings.extractViewName;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice(assignableTypes = {AddressController.class, ClientController.class})
class GlobalMvcControllerAdvice {

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

@ControllerAdvice(assignableTypes = {AddressRestController.class, ClientRestController.class})
class GlobalRestControllerAdvice {

    public GlobalRestControllerAdvice() {
    }

    @ExceptionHandler(ProcessUserRequestException.class)
    public ResponseEntity<Map<String, String>> conflict(Exception e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(ERROR_MESSAGE, e.getMessage());
        return new ResponseEntity<>(responseBody, UNPROCESSABLE_ENTITY);
    }
}
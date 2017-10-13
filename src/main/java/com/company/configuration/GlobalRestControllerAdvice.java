package com.company.configuration;

import com.company.controller.AddressRestController;
import com.company.controller.ClientRestController;
import com.company.util.ProcessUserRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static com.company.util.Mappings.ERROR_MESSAGE;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ControllerAdvice(assignableTypes = {AddressRestController.class, ClientRestController.class})
public class GlobalRestControllerAdvice {

    public GlobalRestControllerAdvice() {
    }

    @ExceptionHandler(ProcessUserRequestException.class)
    public ResponseEntity<Map<String, String>> conflict(Exception e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(ERROR_MESSAGE, e.getMessage());
        return new ResponseEntity<>(responseBody, UNPROCESSABLE_ENTITY);
    }
}
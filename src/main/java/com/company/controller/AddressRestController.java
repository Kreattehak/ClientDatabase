package com.company.controller;

import com.company.model.Address;
import com.company.service.AddressService;
import com.company.util.LocalizedMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.Serializable;

import static com.company.util.Mappings.ID_NOT_FOUND;
import static com.company.util.Mappings.REST_API_PREFIX;
import static com.company.util.Mappings.REST_DELETE_ADDRESS;
import static com.company.util.Mappings.REST_EDIT_MAIN_ADDRESS;
import static com.company.util.Mappings.REST_GET_ALL_ADDRESSES;
import static com.company.util.Mappings.REST_SAVE_NEW_ADDRESS;
import static com.company.util.Mappings.REST_UPDATE_ADDRESS;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping(REST_API_PREFIX)
public class AddressRestController {

    private final String addressSuccessfullyRemoved = "addressSuccessfullyRemoved";
    private final String addressNotFound = "addressNotFound";
    private final String addressSuccessfullyEdited = "addressSuccessfullyEdited";
    private final String mainAddressSuccessfullyEdited = "mainAddressSuccessfullyEdited";
    private final AddressService addressService;
    private final LocalizedMessages localizedMessages;

    @Autowired
    public AddressRestController(AddressService addressService, LocalizedMessages localizedMessages) {
        this.addressService = addressService;
        this.localizedMessages = localizedMessages;
    }

    @GetMapping(value = REST_GET_ALL_ADDRESSES, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Address[]> getAllClientAddresses(@RequestParam long id, HttpServletRequest request) {
        Address[] addresses = addressService.getAllClientAddressesAsArray(id, request);
        return new ResponseEntity<>(addresses, OK);
    }

    @PutMapping(value = REST_UPDATE_ADDRESS, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> updateAddress(@Valid @RequestBody Address addressEditData,
                                                BindingResult result, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(localizedMessages.getMessage(addressNotFound), UNPROCESSABLE_ENTITY);
        }
        addressService.updateAddress(addressEditData, request);
        return new ResponseEntity<>(localizedMessages.getMessage(addressSuccessfullyEdited), OK);
    }

    @PostMapping(value = REST_SAVE_NEW_ADDRESS, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Long> processAddNewAddress(@Valid @RequestBody Address newAddress, BindingResult result,
                                                     @RequestParam Long id, HttpServletRequest request) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(ID_NOT_FOUND, UNPROCESSABLE_ENTITY);
        }
        Address savedAddress = addressService.saveAddress(newAddress, id, request);
        return new ResponseEntity<>(savedAddress.getId(), OK);
    }

    //Passing a request with body to an HTTP DELETE action is not currently supported in Angular 2
    @PostMapping(value = REST_DELETE_ADDRESS, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> deleteAddress(@RequestBody Params params, HttpServletRequest request) {
        addressService.deleteAddress(params.getAddressId(), request);
        return new ResponseEntity<>(localizedMessages.getMessage(addressSuccessfullyRemoved), OK);
    }

    @PutMapping(value = REST_EDIT_MAIN_ADDRESS, consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> processEditClientMainAddress(@RequestBody Params params,
                                                               HttpServletRequest request) {
        addressService.updateMainAddress(params.getAddressId(), params.getClientId(), request);
        return new ResponseEntity<>(localizedMessages.getMessage(mainAddressSuccessfullyEdited), OK);
    }

    static class Params implements Serializable {

        private static final long serialVersionUID = -3722622423938546175L;
        private Long addressId;
        private Long clientId;

        public Long getAddressId() {
            return addressId;
        }

        void setAddressId(Long addressId) {
            this.addressId = addressId;
        }

        public Long getClientId() {
            return clientId;
        }

        void setClientId(Long clientId) {
            this.clientId = clientId;
        }
    }
}

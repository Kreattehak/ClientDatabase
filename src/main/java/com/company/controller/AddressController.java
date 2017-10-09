package com.company.controller;

import com.company.model.Address;
import com.company.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.company.util.Mappings.*;

@Controller
public class AddressController {

    private final AddressService addressService;

    static final String NEW_ADDRESS = "newAddress";
    static final String CLIENT_ADDRESSES = "clientAddresses";
    static final String ADDRESS_TO_BE_EDITED = "addressToBeEdited";

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping(ADD_ADDRESS)
    public String addNewAddress(@ModelAttribute(NEW_ADDRESS) Address newAddress) {
        return extractViewName(ADD_ADDRESS);
    }

    @PostMapping(ADD_ADDRESS)
    public String processAddNewAddress(@Valid @ModelAttribute(NEW_ADDRESS) Address newAddress,
                                       BindingResult result, @RequestParam Long clientId,
                                       HttpServletResponse response, HttpServletRequest request) {
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return extractViewName(ADD_ADDRESS);
        }
        addressService.saveAddress(newAddress, clientId, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @GetMapping(EDIT_ADDRESSES)
    public String editClientAddresses(@RequestParam Long clientId, Model model, HttpServletRequest request) {
        model.addAttribute(CLIENT_ADDRESSES, addressService.getAllClientAddressesAsMap(clientId, request));
        return extractViewName(EDIT_ADDRESSES);
    }

    @GetMapping(EDIT_ADDRESS)
    public String editClientAddress(@RequestParam Long addressId, Model model, HttpServletRequest request) {
        model.addAttribute(ADDRESS_TO_BE_EDITED, addressService.findAddressById(addressId, request));
        return extractViewName(EDIT_ADDRESS);
    }

    @PutMapping(EDIT_ADDRESS)
    public String processEditClientAddress(@Valid @ModelAttribute(ADDRESS_TO_BE_EDITED) Address addressToBeEdited,
                                           BindingResult result, HttpServletResponse response,
                                           HttpServletRequest request) {
        if (result.hasErrors()) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            return extractViewName(EDIT_ADDRESS);
        }
        addressService.updateAddress(addressToBeEdited, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @GetMapping(EDIT_MAIN_ADDRESS)
    public String editClientMainAddress(@RequestParam Long clientId, Model model, HttpServletRequest request) {
        model.addAttribute(CLIENT_ADDRESSES,
                addressService.getAllClientAddressesWithoutMainAddressAsMap(clientId, request));
        return extractViewName(EDIT_MAIN_ADDRESS);
    }

    @PutMapping(EDIT_MAIN_ADDRESS)
    public String processEditClientMainAddress(@RequestParam Long addressId, @RequestParam Long clientId,
                                               HttpServletRequest request) {
        addressService.updateMainAddress(addressId, clientId, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }

    @GetMapping(REMOVE_ADDRESS_FROM_CLIENT)
    public String deleteAddress(@RequestParam Long clientId, Model model, HttpServletRequest request) {
        model.addAttribute(CLIENT_ADDRESSES,
                addressService.getAllClientAddressesWithoutMainAddressAsMap(clientId, request));
        return extractViewName(REMOVE_ADDRESS);
    }

    @GetMapping(REMOVE_ADDRESS)
    public String processRemoveAddress(@RequestParam Long addressId, HttpServletRequest request) {
        addressService.deleteAddress(addressId, request);
        return REDIRECT + TABLE_OF_CLIENTS;
    }
}
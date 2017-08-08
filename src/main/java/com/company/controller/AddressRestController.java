package com.company.controller;

import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Comparator;

@RestController
@RequestMapping("/api")
public class AddressRestController {

    @InjectLogger("com.company.controller.ClientController")
    private static Logger logger;

    private ClientService clientService;
    private AddressService addressService;

    @Autowired
    public AddressRestController(ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @GetMapping(value = "/admin/getAllAddresses", produces = MediaType.APPLICATION_JSON_VALUE)
    public Address[] getAllClientAddresses(@RequestParam() long id) {
        Address[] addresses = clientService.findClientById(id).getAddress().toArray(new Address[0]);
        Arrays.sort(addresses, Comparator.comparing(Address::getId));
        return addresses;
    }

    @PostMapping(value = "/admin/updateAddress", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateAddress(@Valid @RequestBody() Address addressEditData, BindingResult result) {
        if (result.hasErrors()) {
            return false;
        }
        Address addressFromDatabase = addressService.findAddressById(addressEditData.getId());
        addressFromDatabase.setCityName(addressEditData.getCityName());
        addressFromDatabase.setStreetName(addressEditData.getStreetName());
        addressFromDatabase.setZipCode(addressEditData.getZipCode());
        Address address = addressService.updateAddress(addressFromDatabase);
        logger.info("Address edited ->" + addressEditData);
        return true;
    }

    @PostMapping("/admin/saveNewAddress")
    public boolean processAddNewAddress(@RequestBody() Address newAddress, BindingResult result,
                                        @RequestParam() Long id) {
        if (result.hasErrors()) {
            return false;
        }
        System.out.println(newAddress);
        Client clientFromDatabase = clientService.findClientById(id);
        clientFromDatabase.addAddress(newAddress);
        newAddress.setClient(clientFromDatabase);
        clientService.updateClient(clientFromDatabase);
        logger.info("Address added ->" + newAddress + " for client " + clientFromDatabase);
        return true;
    }

    @PostMapping(value = "/admin/deleteAddress", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteClient(@RequestBody() Address address) {
        Address addressToBeDeleted = addressService.findAddressById(address.getId());
        addressService.deleteAddress(addressToBeDeleted);
        logger.info("Address removed ->" + address);
        return true;
    }

    @PostMapping("/admin/editMainAddress")
    public boolean processEditUsersMainAddress(@RequestParam() Long addressId, @RequestParam() Long clientId) {
        Client clientFromDatabase = clientService.findClientById(clientId);
        Address addressFromDatabase = addressService.findAddressById(addressId);
        clientFromDatabase.setMainAddress(addressFromDatabase);
        clientService.updateClient(clientFromDatabase);
        logger.info("Main address for " + clientFromDatabase + " was changed to ->" + addressFromDatabase);
        return true;
    }
}

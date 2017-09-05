package com.company.controller;

import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import com.company.util.Mappings;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Comparator;

@RestController
@RequestMapping(Mappings.REST_API_PREFIX)
public class AddressRestController {

    @InjectLogger("com.company.controller.AddressRestController")
    private static Logger logger;

    private ClientService clientService;
    private AddressService addressService;

    @Autowired
    public AddressRestController(ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @GetMapping(value = Mappings.REST_GET_ALL_ADDRESSES, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Address[] getAllClientAddresses(@RequestParam long id) {
        Client client = clientService.findClientById(id);
        Address[] addresses = addressService.getAllClientAddresses(client).toArray(new Address[0]);
        Arrays.sort(addresses, Comparator.comparing(Address::getId));
        return addresses;
    }

    @PutMapping(value = Mappings.REST_UPDATE_ADDRESS, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean updateAddress(@Valid @RequestBody Address addressEditData, BindingResult result) {
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

    @PostMapping(Mappings.REST_SAVE_NEW_ADDRESS)
    public boolean processAddNewAddress(@RequestBody Address newAddress, BindingResult result,
                                        @RequestParam Long id) {
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

    //Passing a body message to an HTTP DELETE action is not currently supported in Angular 2
    @PostMapping(value = Mappings.REST_DELETE_ADDRESS, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean deleteClient(@RequestBody Address address) {
        Address addressToBeDeleted = addressService.findAddressById(address.getId());
        addressService.deleteAddress(addressToBeDeleted);
        logger.info("Address removed ->" + address);
        return true;
    }

    @PutMapping(Mappings.REST_EDIT_MAIN_ADDRESS)
    public boolean processEditUsersMainAddress(@RequestBody Params params) {
        Client clientFromDatabase = clientService.findClientById(Long.valueOf(params.getClientId()));
        Address addressFromDatabase = addressService.findAddressById(Long.valueOf(params.getAddressId()));
        clientFromDatabase.setMainAddress(addressFromDatabase);
        clientService.updateClient(clientFromDatabase);
        logger.info("Main address for " + clientFromDatabase + " was changed to ->" + addressFromDatabase);
        return true;
    }

    static class Params {
        private String addressId;
        private String clientId;

        public String getAddressId() {
            return addressId;
        }

        public void setAddressId(String addressId) {
            this.addressId = addressId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }
    }
}

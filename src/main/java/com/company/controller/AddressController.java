package com.company.controller;

import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import com.company.util.Mappings;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AddressController {

    @InjectLogger("com.company.controller.AddressController")
    private static Logger logger;

    private ClientService clientService;
    private AddressService addressService;

    @Autowired
    public AddressController(ClientService clientService, AddressService addressService) {
        this.clientService = clientService;
        this.addressService = addressService;
    }

    @GetMapping(Mappings.ADD_ADDRESS)
    public String addNewAddress(@ModelAttribute("newAddress") Address newAddress) {
        return "addAddress";
    }

    @PostMapping(Mappings.ADD_ADDRESS)
    public String processAddNewAddress(@Valid @ModelAttribute("newAddress") Address newAddress, BindingResult result,
                                       @RequestParam() Long id) {
        if (result.hasErrors()) {
            return "addAddress";
        }
        Client clientFromDatabase = clientService.findClientById(id);
        clientFromDatabase.addAddress(newAddress);
        newAddress.setClient(clientFromDatabase);
        clientService.updateClient(clientFromDatabase);
        logger.info("Address added ->" + newAddress + " for client " + clientFromDatabase);
        return "redirect:/clientsTable";
    }

    @GetMapping(Mappings.EDIT_ADDRESSES)
    public String editUsersAddresses(@RequestParam Long id, Model model) {
        model.addAttribute("usersAddresses", addressSetAsSelectList(id));
        return "editAddresses";
    }

    @GetMapping(Mappings.EDIT_ADDRESS)
    public String editUserAddress(@RequestParam Long addressId, Model model) {
        model.addAttribute("addressToBeEdited", addressService.findAddressById(addressId));
        return "editAddress";
    }

    @PostMapping(Mappings.EDIT_ADDRESS)
    public String processEditUserAddress(@Valid @ModelAttribute("addressToBeEdited") Address addressData,
                                         BindingResult result) {
        if (result.hasErrors()) {
            return "editAddress";
        }
        Address addressFromDatabase = addressService.findAddressById(addressData.getId());
        addressFromDatabase.setCityName(addressData.getCityName());
        addressFromDatabase.setStreetName(addressData.getStreetName());
        addressFromDatabase.setZipCode(addressData.getZipCode());
        Address address = addressService.updateAddress(addressFromDatabase);
        logger.info("Address edited ->" + addressData);
        return "redirect:/clientsTable";
    }

    @GetMapping(Mappings.EDIT_MAIN_ADDRESS)
    public String editUsersMainAddress(@RequestParam Long id, Model model) {

        model.addAttribute("usersAddresses", addressSetAsSelectList(id));
        return "editMainAddress";
    }

    @PostMapping(Mappings.EDIT_MAIN_ADDRESS)
    public String processEditUsersMainAddress(@RequestParam Long addressId, @RequestParam() Long id) {
        Client clientFromDatabase = clientService.findClientById(id);
        Address addressFromDatabase = addressService.findAddressById(addressId);
        clientFromDatabase.setMainAddress(addressFromDatabase);
        clientService.updateClient(clientFromDatabase);
        logger.info("Main address for " + clientFromDatabase + " was changed to ->" + addressFromDatabase);
        return "redirect:/clientsTable";
    }

    private Map<Long, String> addressSetAsSelectList(Long id) {
        return addressService.getAllClientAddresses(clientService.findClientById(id))
                .stream()
                .collect(Collectors.toMap(Address::getId,
                        (address) -> address.getCityName() + ", " + address.getStreetName()));

    }

}
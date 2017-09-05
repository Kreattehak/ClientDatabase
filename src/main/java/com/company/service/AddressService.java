package com.company.service;

import com.company.model.Address;
import com.company.model.Client;

import java.util.Set;

public interface AddressService {

    Address updateAddress(Address newAddress);

    Address findAddressById(Long id);

    void deleteAddress(Address address);

    Set<Address> getAllClientAddresses(Client client);

}

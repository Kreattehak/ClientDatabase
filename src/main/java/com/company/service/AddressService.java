package com.company.service;

import com.company.model.Address;

public interface AddressService {

    Address updateAddress(Address newAddress);

    Address findAddressById(Long id);

    void deleteAddress(Address address);

}

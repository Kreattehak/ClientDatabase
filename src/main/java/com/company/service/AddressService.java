package com.company.service;

import com.company.model.Address;
import com.company.model.Client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AddressService {

    Address updateAddress(Address editedAddress, HttpServletRequest request);

    void updateMainAddress(Long addressId, Long clientId, HttpServletRequest request);

    Address findAddressById(Long addressId, HttpServletRequest request);

    Set<Address> findAllAddresses();

    void deleteAddress(Long addressId, HttpServletRequest request);

    Set<Address> getAllClientAddresses(Client client);

    Address saveAddress(Address newAddress, Long clientId, HttpServletRequest request);

    Map<Long, String> getAllClientAddressesAsMap(Long clientId, HttpServletRequest request);

    Map<Long, String> getAllClientAddressesWithoutMainAddressAsMap(Long clientId, HttpServletRequest request);

    Address[] getAllClientAddressesAsArray(Long clientId, HttpServletRequest request);
}

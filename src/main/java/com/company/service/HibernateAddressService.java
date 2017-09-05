package com.company.service;

import com.company.dao.AddressDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class HibernateAddressService implements AddressService {

    @InjectLogger("com.company.service.AddressServiceImpl")
    private static Logger logger;

    @Autowired
    private AddressDao addressDao;

    @Override
    @Transactional(readOnly = false)
    public Address updateAddress(Address address) {
        logger.debug("Address with id" + address.getId() + " was edited.");
        addressDao.update(address);
        return address;
    }

    @Override
    public Address findAddressById(Long id) {
        return addressDao.findById(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteAddress(Address address) {
        addressDao.delete(address);
        logger.debug("Address with id" + address.getId() + " was deleted.");
    }

    @Override
    public Set<Address> getAllClientAddresses(Client client) {
        return client.getAddress();
    }
}
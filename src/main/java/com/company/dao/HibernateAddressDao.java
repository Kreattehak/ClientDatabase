package com.company.dao;

import com.company.model.Address;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateAddressDao extends AbstractDao<Address, Long> implements AddressDao {

    //further implementations

}
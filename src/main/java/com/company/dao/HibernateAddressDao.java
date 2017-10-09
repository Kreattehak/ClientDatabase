package com.company.dao;

import com.company.model.Address;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class HibernateAddressDao extends AbstractDao<Address, Long> implements AddressDao {

    @Autowired
    public HibernateAddressDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    //further implementations

}
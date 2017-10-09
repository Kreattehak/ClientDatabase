package com.company;

import com.company.controller.*;
import com.company.dao.AbstractDaoTest;
import com.company.model.ClientTest;
import com.company.service.HibernateAddressServiceTest;
import com.company.service.HibernateClientServiceTest;
import com.company.util.LoggerInjectorTest;
import com.company.util.MappingsTest;
import com.company.util.WebDataResolverAndCreatorTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoggerInjectorTest.class,
        WebDataResolverAndCreatorTest.class,
        MappingsTest.class,
//        FastTests.class,
        ClientTest.class,
        AbstractDaoTest.class,
        HibernateClientServiceTest.class,
        HibernateAddressServiceTest.class,
        HomeControllerTest.class,
        ClientControllerTest.class,
        ClientControllerIntegrationTest.class,
        AddressControllerTest.class,
        AddressControllerIntegrationTest.class,
        ClientRestControllerTest.class,
        ClientRestControllerIntegrationTest.class,
        AddressRestControllerTest.class,
        AddressRestControllerIntegrationTest.class
})
public class JUnitTestSuite {
    //intentionally empty - JUnitTestSuite is sufficient
}


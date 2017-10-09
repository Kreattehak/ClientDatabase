package com.company.dao;

import com.company.configuration.AppConfiguration;
import com.company.configuration.HibernateConfigurationForTests;
import com.company.model.Address;
import com.company.model.Client;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static com.company.Constants.*;
import static com.company.service.HibernateAddressServiceTest.checkAddressFieldsEquality;
import static com.company.service.HibernateClientServiceTest.checkClientFieldsEquality;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HibernateConfigurationForTests.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class AbstractDaoTest {

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private AddressDao addressDao;

    private Address testAddress;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
    }

    @After
    public void tearDown() throws Exception {
//        clientDao.findAll().forEach(clientDao::delete);
//        addressDao.findAll().forEach(addressDao::delete);
        System.out.println(clientDao.findAll());
        System.out.println(addressDao.findAll());
        testClient = null;
        testAddress = null;
    }

    @Test
    public void shouldFindClientInDatabase() {
        Client clientFromDatabase = clientDao.save(testClient);

        assertThat(clientDao.findById(clientFromDatabase.getId()),
                is(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME)));
    }

    @Test
    public void shouldFindAddressInDatabase() {
        Address addressFromDatabase = addressDao.save(testAddress);

        assertThat(addressDao.findById(addressFromDatabase.getId()), is(checkAddressFieldsEquality(
                ADDRESS_STREET_NAME,ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE)));
    }

    @Test
    public void shouldReturnAllClientsFromDatabase() {
        clientDao.save(testClient);
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        clientDao.save(anotherTestClient);

        Collection<Client> clients = clientDao.findAll();

        assertThat(clients, hasSize(2));
        assertThat(clients, allOf(
                hasItem(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME)),
                hasItem(checkClientFieldsEquality(ANOTHER_CLIENT_FIRST_NAME,
                        ANOTHER_CLIENT_LAST_NAME))));
    }

    @Test
    public void shouldReturnAllAddressesFromDatabase() {
        addressDao.save(testAddress);
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        addressDao.save(anotherTestAddress);

        Collection<Address> addresses = addressDao.findAll();

        assertThat(addresses, hasSize(2));
        assertThat(addresses, allOf(
                hasItem(checkAddressFieldsEquality(
                        ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE)),
                hasItem(checkAddressFieldsEquality(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE))));
    }

    @Test
    public void shouldAddClientToDatabase() {
        Client clientFromDatabase = clientDao.save(testClient);

        assertThat(clientFromDatabase.getId(), notNullValue());
        assertThat(clientDao.findAll(),
                hasItem(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME)));
    }

    @Test
    public void shouldAddAddressToDatabase() {
        Address addressFromDatabase = addressDao.save(testAddress);

        assertThat(addressFromDatabase.getId(), notNullValue());
        assertThat(addressDao.findAll(),
                hasItem(checkAddressFieldsEquality(ADDRESS_STREET_NAME,
                        ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE)));
    }

    @Test
    public void shouldDeleteClientFromDatabase() {
        clientDao.save(testClient);
        clientDao.delete(testClient);

        Collection<Client> clients = clientDao.findAll();

        assertThat(clients, empty());
    }

    @Test
    public void shouldDeleteAddressFromDatabase() {
        addressDao.save(testAddress);
        addressDao.delete(testAddress);

        Collection<Address> addresses = addressDao.findAll();

        assertThat(addresses, empty());
    }

    @Test
    public void shouldUpdateClientWithGivenData() {
        Client clientFromDatabase = clientDao.save(testClient);
        testClient.setFirstName(ANOTHER_CLIENT_FIRST_NAME);
        testClient.setLastName(ANOTHER_CLIENT_LAST_NAME);

        Client updatedClient = clientDao.update(testClient);

        assertEquals(clientFromDatabase.getId(), updatedClient.getId());
        assertThat(clientDao.findById(testClient.getId()),
                is(checkClientFieldsEquality(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME)));
    }

    @Test
    public void shouldUpdateAddressWithGivenData() {
        Address addressFromDatabase = addressDao.save(testAddress);
        testAddress.setStreetName(ANOTHER_ADDRESS_STREET_NAME);
        testAddress.setCityName(ANOTHER_ADDRESS_CITY_NAME);
        testAddress.setZipCode(ANOTHER_ADDRESS_ZIP_CODE);
        Address updatedAddress = addressDao.update(testAddress);

        assertEquals(addressFromDatabase.getId(), updatedAddress.getId());
        assertThat(addressDao.findById(testAddress.getId()),
                is(checkAddressFieldsEquality(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE)));
    }

    @Test
    public void shouldNotFindClientOrAddressWithUnregisteredId() {
        assertThat(clientDao.findById(0L), nullValue());
        assertThat(addressDao.findById(0L), nullValue());
    }
}
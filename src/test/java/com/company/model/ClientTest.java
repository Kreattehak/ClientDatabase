package com.company.model;

import org.hamcrest.number.OrderingComparison;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

import static com.company.Constants.ADDRESS_CITY_NAME;
import static com.company.Constants.ADDRESS_STREET_NAME;
import static com.company.Constants.ADDRESS_ZIP_CODE;
import static com.company.Constants.CLIENT_FIRST_NAME;
import static com.company.Constants.CLIENT_LAST_NAME;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public class ClientTest {

    private Client testClient;

    @Before
    public void setUp() throws Exception {
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
    }

    @After
    public void tearDown() throws Exception {
        testClient = null;
    }

    @Test
    public void shouldAddAddress() throws Exception {
        Address testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient.addAddress(testAddress);

        assertThat(testClient.getAddress(), hasSize(1));
        assertThat(testClient.getAddress(), hasItem(testAddress));
    }

    @Test
    public void shouldSetFirstAddedAddressAsMainAddress() throws Exception {
        Address testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient.addAddress(testAddress);

        assertThat(testClient.getMainAddress(), equalTo(testAddress));
    }

    @Test
    public void shouldCreateClientWithDateOfRegistration() {
        LocalDate.now();

        assertThat(testClient.getDateOfRegistration(), OrderingComparison.lessThan(new Date()));
    }
}
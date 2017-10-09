package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.AddressDao;
import com.company.dao.ClientDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static com.company.Constants.*;
import static com.company.controller.AddressController.ADDRESS_TO_BE_EDITED;
import static com.company.controller.AddressController.CLIENT_ADDRESSES;
import static com.company.controller.AddressController.NEW_ADDRESS;
import static com.company.service.HibernateAddressServiceTest.DAEM;
import static com.company.service.HibernateAddressServiceTest.DANREM;
import static com.company.service.HibernateAddressServiceTest.FAEM;
import static com.company.service.HibernateAddressServiceTest.UAEM;
import static com.company.service.HibernateAddressServiceTest.UMAEM;
import static com.company.service.HibernateAddressServiceTest.checkAddressFieldsEqualityWithClient;
import static com.company.service.HibernateClientServiceTest.FCEM;
import static com.company.util.Mappings.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class AddressControllerIntegrationTest {

    @Autowired
    private AddressService addressService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private Client testClient;
    private Address testAddress;
    private Set<Address> allAddressesCurrrentlyInDatabase;

    @Before
    public void setUp() throws Exception {
        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @After
    public void tearDown() throws Exception {
//        addressService.findAllAddresses().forEach(
//                address -> addressService.deleteAddress(address.getId(), address.getClient().getId()));
        mockMvc = null;
        testAddress = null;
        testClient = null;
    }

    @Test
    public void shouldAddAddressToDatabaseFromWebPageForm() throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);
        allAddressesCurrrentlyInDatabase = addressService.findAllAddresses();

        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, testAddress.getZipCode())
                .param(CLIENT_ID, clientFromDatabase.getId().toString()));

        Set<Address> allAddressesAfterPersist = addressService.findAllAddresses();
        allAddressesAfterPersist.removeAll(allAddressesCurrrentlyInDatabase);

        Address addressFromDatabase = allAddressesAfterPersist.iterator().next();

        assertThat(allAddressesAfterPersist, hasSize(1));
        assertThat(addressFromDatabase, is(
                checkAddressFieldsEqualityWithClient(
                        ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, clientFromDatabase)));
        //Compiler can't infer type
        assertThat(clientDao.findById(clientFromDatabase.getId()).getAddress(),
                Matchers.<Set<Address>>allOf(hasSize(1), hasItem(addressFromDatabase)));
    }

    @Test
    public void shouldValidateStreetNameAndNotAddAddressToDatabase() throws Exception {
        tryToAddAddress(INVALID_TO_SHORT_INPUT, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, STREET_NAME);
    }

    @Test
    public void shouldValidateCityNameAndNotAddAddressToDatabase() throws Exception {
        tryToAddAddress(ADDRESS_STREET_NAME, INVALID_TO_SHORT_INPUT, ADDRESS_ZIP_CODE, CITY_NAME);
    }

    @Test
    public void shouldValidateZipCodeAndNotAddAddressToDatabase() throws Exception {
        tryToAddAddress(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, INVALID_TO_SHORT_INPUT, ZIP_CODE);
    }

    @Test
    public void shouldNotAddAddressToDatabaseWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                post(ADD_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .param(STREET_NAME, testAddress.getStreetName())
                        .param(CITY_NAME, testAddress.getCityName())
                        .param(ZIP_CODE, testAddress.getZipCode())
                        .param(CLIENT_ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldAddClientAddressesMapToModel() throws Exception {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        saveClientToDatabase(testClient);
        saveAddressToDatabase(testAddress);
        saveAddressToDatabase(anotherTestAddress);

        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherTestAddress.setClient(testClient);

        mockMvc.perform(get(EDIT_ADDRESSES)
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(view().name(extractViewName(EDIT_ADDRESSES)))
                .andExpect(model().attribute(CLIENT_ADDRESSES, allOf(
                        hasEntry(equalTo(testAddress.getId()),
                                equalTo(addressValueInMap(testAddress))),
                        hasEntry(equalTo(anotherTestAddress.getId()),
                                equalTo(addressValueInMap(anotherTestAddress))))));
    }

    @Test
    public void shouldNotAddClientAddressesMapToModelWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                get(EDIT_ADDRESSES)
                        .param(CLIENT_ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldAddClientAddressToWebForm() throws Exception {
        saveClientToDatabase(testClient);
        saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        mockMvc.perform(get(EDIT_ADDRESS)
                .param(ADDRESS_ID, testAddress.getId().toString()))
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)))
                .andExpect(model().attribute(ADDRESS_TO_BE_EDITED,
                        is(checkAddressFieldsEqualityWithClient(ADDRESS_STREET_NAME,
                                ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, testClient))));
    }

    @Test
    public void shouldNotAddClientAddressToWebFormWhenAddressWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(get(EDIT_ADDRESS)
                .param(ADDRESS_ID, ID_NOT_FOUND_VALUE_STRING), FAEM, addressService);
    }

    @Test
    public void shouldEditAddressFromDatabase() throws Exception {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, addressFromDatabase.getId().toString())
                .param(STREET_NAME, anotherTestAddress.getStreetName())
                .param(CITY_NAME, anotherTestAddress.getCityName())
                .param(ZIP_CODE, anotherTestAddress.getZipCode()));

        assertThat(addressDao.findById(addressFromDatabase.getId()),
                is(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, clientFromDatabase)));
    }

    @Test
    public void shouldValidateStreetNameAndNotEditAddressFromDatabase() throws Exception {
        tryToEditAddress(INVALID_TO_SHORT_INPUT, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, STREET_NAME);
    }

    @Test
    public void shouldValidateCityNameAndNotEditAddressFromDatabase() throws Exception {
        tryToEditAddress(ANOTHER_ADDRESS_STREET_NAME, INVALID_TO_SHORT_INPUT, ANOTHER_ADDRESS_ZIP_CODE, CITY_NAME);
    }

    @Test
    public void shouldValidateZipCodeAndNotEditAddressFromDatabase() throws Exception {
        tryToEditAddress(ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, INVALID_TO_SHORT_INPUT, ZIP_CODE);
    }

    @Test
    public void shouldNotEditAddressInDatabaseWhenIdWasNull() throws Exception {
        tryToPerformActionButExceptionWasThrown(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, ADDRESS_STREET_NAME)
                .param(CITY_NAME, ADDRESS_CITY_NAME)
                .param(ZIP_CODE, ADDRESS_ZIP_CODE), UAEM, addressService);
    }

    @Test
    public void shouldNotEditAddressInDatabaseWhenAddressWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, ID_NOT_FOUND_VALUE_STRING)
                .param(STREET_NAME, ADDRESS_STREET_NAME)
                .param(CITY_NAME, ADDRESS_CITY_NAME)
                .param(ZIP_CODE, ADDRESS_ZIP_CODE), UAEM, addressService);
    }

    @Test
    public void shouldAddClientAddressesWithoutMainAddressMapToModel() throws Exception {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        saveClientToDatabase(testClient);
        saveAddressToDatabase(testAddress);
        saveAddressToDatabase(anotherTestAddress);

        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherTestAddress.setClient(testClient);

        mockMvc.perform(get(EDIT_MAIN_ADDRESS)
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(view().name(extractViewName(EDIT_MAIN_ADDRESS)))
                .andExpect(model().attribute(CLIENT_ADDRESSES, allOf(
                        not(hasEntry(equalTo(testAddress.getId()),
                                equalTo(addressValueInMap(testAddress)))),
                        hasEntry(equalTo(anotherTestAddress.getId()),
                                equalTo(addressValueInMap(anotherTestAddress))))));
    }

    @Test
    public void shouldNotAddClientAddressesWithoutMainAddressMapToModelWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                get(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldEditMainAddress() throws Exception {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        Address anotherAddressFromDatabase = saveAddressToDatabase(anotherTestAddress);
        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherAddressFromDatabase.setClient(testClient);

        mockMvc.perform(put(EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ADDRESS_ID, anotherAddressFromDatabase.getId().toString())
                .param(CLIENT_ID, clientFromDatabase.getId().toString()));

        assertThat(clientDao.findById(clientFromDatabase.getId()).getMainAddress(),
                is(checkAddressFieldsEqualityWithClient(
                        ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));
    }

    @Test
    public void shouldNotProcessEditMainAddressWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                put(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, ID_NOT_FOUND_VALUE_STRING)
                        .param(ADDRESS_ID, ID_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldNotProcessEditMainAddressWhenAddressToChangeIsAlsoMainAddress() throws Exception {
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        Client clientFromDatabase = clientDao.save(testClient);
        tryToPerformActionButExceptionWasThrown(
                put(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, clientFromDatabase.getId().toString())
                        .param(ADDRESS_ID, ID_NOT_FOUND_VALUE_STRING), UMAEM, addressService);
    }

    @Test
    public void shouldNotProcessEditMainAddressWhenAddressWasNotFound() throws Exception {
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        Client clientFromDatabase = clientDao.save(testClient);
        tryToPerformActionButExceptionWasThrown(
                put(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, clientFromDatabase.getId().toString())
                        .param(ADDRESS_ID, ID_NOT_FOUND_VALUE_STRING), FAEM, addressService);
    }

    @Test
    public void shouldDeleteAddressFromDatabaseWhenItsNotMainAddress() throws Exception {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        Address anotherAddressFromDatabase = saveAddressToDatabase(anotherTestAddress);
        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherAddressFromDatabase.setClient(testClient);

        mockMvc.perform(get(REMOVE_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .header(REFERER_HEADER, REFERER_HEADER_VALUE + clientFromDatabase.getId())
                .param(ADDRESS_ID, anotherAddressFromDatabase.getId().toString()));

        assertThat(clientDao.findById(clientFromDatabase.getId()).getAddress(),
                Matchers.<Set<Address>>allOf(hasSize(1), hasItem(addressFromDatabase)));
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenRefererHeaderWasNull() throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .param(ADDRESS_ID, addressFromDatabase.getId().toString()), DANREM, addressService);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .header(REFERER_HEADER, REFERER_HEADER_VALUE + ID_VALUE_STRING)
                        .param(ADDRESS_ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenAddressWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .header(REFERER_HEADER, REFERER_HEADER_VALUE + ID_VALUE_STRING)
                        .param(ADDRESS_ID, ID_VALUE_STRING), FAEM, addressService);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenItsMainAddress() throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .header(REFERER_HEADER, REFERER_HEADER_VALUE + clientFromDatabase.getId())
                        .param(ADDRESS_ID, addressFromDatabase.getId().toString()), DAEM, addressService);
        ;
    }

    @Test
    public void shouldDeleteAddressFromDatabaseWhenClientWasDeleted() throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        mockMvc.perform(get(REMOVE_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(CLIENT_ID, clientFromDatabase.getId().toString()));

        assertThat(addressDao.findById(testAddress.getId()), nullValue());

    }

    private Client saveClientToDatabase(Client client) {
        return clientDao.save(client);
    }

    private Address saveAddressToDatabase(Address address) {
        return addressDao.save(address);
    }

    private String addressValueInMap(Address address) {
        return address.getCityName() + ", " + address.getStreetName();
    }

    private void tryToAddAddress(String streetName, String cityName, String zipCode, String fieldWithError) throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);

        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, streetName)
                .param(CITY_NAME, cityName)
                .param(ZIP_CODE, zipCode)
                .param(CLIENT_ID, clientFromDatabase.getId().toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_ADDRESS)))
                .andExpect(model().attributeHasFieldErrors(NEW_ADDRESS, fieldWithError));
    }

    private void tryToEditAddress(String streetName, String cityName, String zipCode, String fieldWithError)
            throws Exception {
        Client clientFromDatabase = saveClientToDatabase(testClient);
        Address addressFromDatabase = saveAddressToDatabase(testAddress);
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);

        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, addressFromDatabase.getId().toString())
                .param(STREET_NAME, streetName)
                .param(CITY_NAME, cityName)
                .param(ZIP_CODE, zipCode))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)))
                .andExpect(model().attributeHasFieldErrors(ADDRESS_TO_BE_EDITED, fieldWithError));

        assertThat(addressDao.findById(addressFromDatabase.getId()),
                is(checkAddressFieldsEqualityWithClient(ADDRESS_STREET_NAME,
                        ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, clientFromDatabase)));
    }

    //TODO: REWORK AFTER HTTP STATUS CHANGE
    private void tryToPerformActionButExceptionWasThrown(MockHttpServletRequestBuilder builder,
                                                         String message, Object target) throws Exception {
        ReflectionTestUtils.setField(target, message, STRING_TO_TEST_EQUALITY);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ERROR_PAGE)))
                .andExpect(model().attribute(ERROR_MESSAGE, STRING_TO_TEST_EQUALITY))
                .andExpect(model().attribute(HTTP_STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value()));
    }

}

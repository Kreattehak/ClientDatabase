package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.AddressDao;
import com.company.dao.ClientDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.company.util.LocalizedMessages;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
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
import static com.company.service.HibernateAddressServiceTest.*;
import static com.company.service.HibernateClientServiceTest.FCEM;
import static com.company.util.LocalizedMessagesTest.getErrorMessage;
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
    @Autowired
    private LocalizedMessages localizedMessages;

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
        addressService = null;
        clientService = null;
        clientDao = null;
        addressDao = null;
        webApplicationContext = null;
        localizedMessages = null;

        mockMvc = null;
        testClient = null;
        testAddress = null;
        allAddressesCurrrentlyInDatabase = null;
    }

    @Test
    public void shouldAddAddressToDatabaseFromWebPageForm() throws Exception {
        Client clientFromDatabase = clientDao.save(testClient);
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
        assertThat(addressFromDatabase, is(checkAddressFieldsEqualityWithClient(
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
    public void shouldThrowExceptionWhenTryingToAddTheSameAddress() throws Exception {
        saveClientWithAddress();
        tryToPerformActionButExceptionWasThrown(
                post(ADD_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .param(STREET_NAME, testAddress.getStreetName())
                        .param(CITY_NAME, testAddress.getCityName())
                        .param(ZIP_CODE, testAddress.getZipCode())
                        .param(CLIENT_ID, testClient.getId().toString()), SAEM, addressService);
    }

    @Test
    public void shouldAddClientAddressesMapToModel() throws Exception {
        Address anotherTestAddress = saveClientWithTwoAddresses();

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
        saveClientWithAddress();

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
        saveClientWithAddress();

        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, testAddress.getId().toString())
                .param(STREET_NAME, ANOTHER_ADDRESS_STREET_NAME)
                .param(CITY_NAME, ANOTHER_ADDRESS_CITY_NAME)
                .param(ZIP_CODE, ANOTHER_ADDRESS_ZIP_CODE));

        assertThat(addressDao.findById(testAddress.getId()),
                is(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));
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
    public void shouldNotEditAddressInDatabaseWithTheSameData() throws Exception {
        saveClientWithAddress();
        tryToPerformActionButExceptionWasThrown(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, testAddress.getId().toString())
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
                .param(ZIP_CODE, ADDRESS_ZIP_CODE), FAEM, addressService);
    }

    @Test
    public void shouldAddClientAddressesWithoutMainAddressMapToModel() throws Exception {
        Address anotherTestAddress = saveClientWithTwoAddresses();

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
        Address anotherTestAddress = saveClientWithTwoAddresses();

        mockMvc.perform(put(EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ADDRESS_ID, anotherTestAddress.getId().toString())
                .param(CLIENT_ID, testClient.getId().toString()));

        assertThat(clientDao.findById(testClient.getId()).getMainAddress(),
                is(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));
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
        saveClientWithAddress();

        tryToPerformActionButExceptionWasThrown(
                put(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, testClient.getId().toString())
                        .param(ADDRESS_ID, testAddress.getId().toString()), UMAEM, addressService);
    }

    @Test
    public void shouldNotProcessEditMainAddressWhenAddressWasNotFound() throws Exception {
        saveClientWithAddress();

        tryToPerformActionButExceptionWasThrown(
                put(EDIT_MAIN_ADDRESS)
                        .param(CLIENT_ID, testClient.getId().toString())
                        .param(ADDRESS_ID, ID_NOT_FOUND_VALUE_STRING), FAEM, addressService);
    }

    @Test
    public void shouldDeleteAddressFromDatabaseWhenItsNotMainAddress() throws Exception {
        Address anotherTestAddress = saveClientWithTwoAddresses();

        mockMvc.perform(get(REMOVE_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .header(REFERER_HEADER, REFERER_HEADER_VALUE + testClient.getId())
                .param(ADDRESS_ID, anotherTestAddress.getId().toString()));

        assertThat(clientDao.findById(testClient.getId()).getAddress(),
                Matchers.<Set<Address>>allOf(hasSize(1), hasItem(is(checkAddressFieldsEqualityWithClient(
                        ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, testClient)))));
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenRefererHeaderWasNull() throws Exception {
        saveClientWithAddress();

        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .param(ADDRESS_ID, testAddress.getId().toString()), DANREM, addressService);
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
        saveClientWithAddress();

        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .header(REFERER_HEADER, REFERER_HEADER_VALUE + testClient.getId())
                        .param(ADDRESS_ID, ID_VALUE_STRING), FAEM, addressService);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenItsMainAddress() throws Exception {
        saveClientWithAddress();

        tryToPerformActionButExceptionWasThrown(
                get(REMOVE_ADDRESS)
                        .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                        .header(REFERER_HEADER, REFERER_HEADER_VALUE + testClient.getId())
                        .param(ADDRESS_ID, testAddress.getId().toString()), DAEM, addressService);
        ;
    }

    @Test
    public void shouldDeleteAddressFromDatabaseWhenClientWasDeleted() throws Exception {
        saveClientWithAddress();

        mockMvc.perform(get(REMOVE_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(CLIENT_ID, testClient.getId().toString()));

        assertThat(addressDao.findById(testAddress.getId()), nullValue());

    }

    public Address saveClientWithTwoAddresses() {
        Address anotherTestAddress = new Address(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        clientDao.save(testClient);
        addressDao.save(testAddress);
        addressDao.save(anotherTestAddress);

        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherTestAddress.setClient(testClient);

        return anotherTestAddress;
    }

    private void saveClientWithAddress() {
        testClient.addAddress(testAddress);
        testAddress.setClient(testClient);
        clientDao.save(testClient);
    }

    private String addressValueInMap(Address address) {
        return address.getCityName() + ", " + address.getStreetName();
    }

    private void tryToAddAddress(String streetName, String cityName, String zipCode, String fieldWithError) throws Exception {
        Client clientFromDatabase = clientDao.save(testClient);

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
        Client clientFromDatabase = clientDao.save(testClient);
        Address addressFromDatabase = addressDao.save(testAddress);
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

    private void tryToPerformActionButExceptionWasThrown(MockHttpServletRequestBuilder builder,
                                                         String message, Object target) throws Exception {
        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ERROR_PAGE)))
                .andExpect(model().attribute(ERROR_MESSAGE,
                        getErrorMessage(localizedMessages, message, target)));
    }

}

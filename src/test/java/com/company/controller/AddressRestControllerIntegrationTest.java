package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.AddressDao;
import com.company.dao.ClientDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.service.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static com.company.controller.AddressRestControllerTest.ANF;
import static com.company.controller.AddressRestControllerTest.ASE;
import static com.company.controller.AddressRestControllerTest.ASR;
import static com.company.controller.AddressRestControllerTest.MASE;
import static com.company.service.HibernateAddressServiceTest.*;
import static com.company.service.HibernateClientServiceTest.FCEM;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Transactional
public class AddressRestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AddressRestController addressRestController;

    private MockMvc mockMvc;
    private Client testClient;
    private Address testAddress;

    @Before
    public void setUp() throws Exception {
        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        objectMapper = null;
        clientDao = null;
        addressDao = null;
        clientService = null;
        addressService = null;
        webApplicationContext = null;
        addressRestController = null;

        mockMvc = null;
        testClient = null;
        testAddress = null;
    }

    @Test
    public void shouldReturnClientAddressesArray() throws Exception {
        Address anotherTestAddress = saveClientWithTwoAddresses();

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_ALL_ADDRESSES)
                .param(ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(testAddress.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", equalTo(anotherTestAddress.getId().intValue())));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(get(REST_API_PREFIX + REST_GET_ALL_ADDRESSES)
                .param(ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldEditAddressInDatabase() throws Exception {
        ReflectionTestUtils.setField(addressRestController, ASE, STRING_TO_TEST_EQUALITY);
        saveClientWithAddress();

        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        anotherTestAddress.setId(testAddress.getId());
        String data = objectMapper.writeValueAsString(anotherTestAddress);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        assertThat(addressDao.findById(testAddress.getId()),
                is(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));
    }

    @Test
    public void shouldThrowExceptionWhenAddressIdWasNotFoundWhileEditingAddress() throws Exception {
        testAddress.setId(ID_NOT_FOUND);
        String data = objectMapper.writeValueAsString(testAddress);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), FAEM, addressService);
    }

    @Test
    public void shouldThrowExceptionWhenAddressIdWasNullWhileEditingAddress() throws Exception {
        String data = objectMapper.writeValueAsString(testAddress);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), UAEM, addressService);
    }

    @Test
    public void shouldValidateStreetNameAndNotUpdateAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToEditAddress(INVALID_TO_SHORT_INPUT, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
    }

    @Test
    public void shouldValidateCityNameAndNotUpdateAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToEditAddress(ADDRESS_STREET_NAME, INVALID_TO_SHORT_INPUT, ADDRESS_ZIP_CODE);
    }

    @Test
    public void shouldValidateZipCodeAndNotUpdateAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToEditAddress(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, INVALID_TO_SHORT_INPUT);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToUpdateAddressWithTheSameAddress() throws Exception {
        saveClientWithAddress();
        String data = objectMapper.writeValueAsString(testAddress);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), UAEM, addressService);
    }

    @Test
    public void shouldPerformSaveAddressAction() throws Exception {
        String data = objectMapper.writeValueAsString(testAddress);
        clientDao.save(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .param(ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", greaterThan(0)));

        Set<Address> addresses = addressService.findAllAddresses();

        assertThat(addressService.findAllAddresses(), hasSize(1));
        assertThat(addresses.iterator().next(), is(checkAddressFieldsEqualityWithClient(
                ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, testClient)));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFoundWhileAddingAddress() throws Exception {
        String data = objectMapper.writeValueAsString(testAddress);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .param(ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToAddTheSameAddress() throws Exception {
        saveClientWithAddress();

        String data = objectMapper.writeValueAsString(testAddress);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .param(ID, testClient.getId().toString()), SAEM, addressService);
    }

    @Test
    public void shouldValidateStreetNameAndNotAddAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToAddAddress(INVALID_TO_SHORT_INPUT, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
    }

    @Test
    public void shouldValidateCityNameAndNotAddAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToAddAddress(ADDRESS_STREET_NAME, INVALID_TO_SHORT_INPUT, ADDRESS_ZIP_CODE);
    }

    @Test
    public void shouldValidateZipCodeAndNotAddAddressInDatabase() throws Exception {
        validateFieldsWhenTryingToAddAddress(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, INVALID_TO_SHORT_INPUT);
    }

    @Test
    public void shouldDeleteAddress() throws Exception {
        ReflectionTestUtils.setField(addressRestController, ASR, STRING_TO_TEST_EQUALITY);

        Address anotherTestAddress = saveClientWithTwoAddresses();

        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(anotherTestAddress.getId());
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        mockMvc.perform(post(REST_API_PREFIX + REST_DELETE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .header(REFERER_HEADER, REST_REFERER_HEADER_VALUE + testClient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        Set<Address> addresses = addressService.findAllAddresses();

        assertThat(addressService.findAllAddresses(), hasSize(1));
        assertThat(addresses.iterator().next(), is(checkAddressFieldsEqualityWithClient(
                ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, testClient)));
    }

    @Test
    public void shouldNotDeleteAddressWhenRefererHeaderWasEmpty() throws Exception {
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(testAddress.getId());
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_DELETE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), DANREM, addressService);
    }

    @Test
    public void shouldNotDeleteAddressWhenItsMainAddress() throws Exception {
        saveClientWithAddress();
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(testAddress.getId());
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_DELETE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .header(REFERER_HEADER, REST_REFERER_HEADER_VALUE + testClient.getId()), DAEM, addressService);
    }

    @Test
    public void shouldNotDeleteAddressWhenAddressWasNotFound() throws Exception {
        saveClientWithAddress();
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(ANOTHER_ID_VALUE);
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_DELETE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .header(REFERER_HEADER, REST_REFERER_HEADER_VALUE + testClient.getId()), FAEM, addressService);
    }

    @Test
    public void shouldChangeMainAddress() throws Exception {
        Address anotherTestAddress = saveClientWithTwoAddresses();

        ReflectionTestUtils.setField(addressRestController, MASE, STRING_TO_TEST_EQUALITY);
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(anotherTestAddress.getId());
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        mockMvc.perform(put(REST_API_PREFIX + REST_EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        assertThat(clientDao.findById(testClient.getId()).getMainAddress(), is(checkAddressFieldsEqualityWithClient(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFoundWhileEditingMainAddress() throws Exception {
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(ID_VALUE);
        params.setClientId(ID_VALUE);
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), FCEM, clientService);
    }

    @Test
    public void shouldThrowExceptionWhenTryingToChangeMainAddressToTheSameAddress() throws Exception {
        saveClientWithAddress();

        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(testAddress.getId());
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), UMAEM, addressService);
    }

    @Test
    public void shouldThrowExceptionAddressIdWasNotFoundWhileEditingMainAddress() throws Exception {
        saveClientWithAddress();

        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(ID_NOT_FOUND);
        params.setClientId(testClient.getId());
        String data = objectMapper.writeValueAsString(params);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), FAEM, addressService);
    }

    private void validateFieldsWhenTryingToEditAddress(String streetName, String cityName,
                                                       String zipCode) throws Exception {
        ReflectionTestUtils.setField(addressRestController, ANF, STRING_TO_TEST_EQUALITY);
        Address address = addressDao.save(testAddress);
        testAddress.setStreetName(streetName);
        testAddress.setCityName(cityName);
        testAddress.setZipCode(zipCode);
        String data = objectMapper.writeValueAsString(testAddress);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));
    }

    private void validateFieldsWhenTryingToAddAddress(String streetName, String cityName, String zipCode)
            throws Exception {
        clientDao.save(testClient);
        testAddress.setStreetName(streetName);
        testAddress.setCityName(cityName);
        testAddress.setZipCode(zipCode);
        String data = objectMapper.writeValueAsString(testAddress);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(ID, testClient.getId().toString())
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(ID_NOT_FOUND.intValue())));
    }

    private Address saveClientWithTwoAddresses() {
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

    //TODO: REWORK AFTER HTTP STATUS CHANGE
    private void tryToPerformActionButExceptionWasThrown(MockHttpServletRequestBuilder builder,
                                                         String message, Object target) throws Exception {
        ReflectionTestUtils.setField(target, message, STRING_TO_TEST_EQUALITY);

        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorMessage", equalTo(STRING_TO_TEST_EQUALITY)))
                .andExpect(jsonPath("$.httpStatus",
                        equalTo(String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()))));
    }
}

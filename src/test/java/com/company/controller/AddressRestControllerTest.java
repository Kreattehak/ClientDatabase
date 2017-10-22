package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import com.company.util.LocalizedMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static com.company.Constants.*;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
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
public class AddressRestControllerTest {

    static final String ASR = "addressSuccessfullyRemoved";
    static final String ANF = "addressNotFound";
    static final String ASE = "addressSuccessfullyEdited";
    static final String MASE = "mainAddressSuccessfullyEdited";

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private LocalizedMessages localizedMessagesMock;
    @Mock
    private AddressService addressServiceMock;

    @InjectMocks
    private AddressRestController addressRestController;

    private MockMvc mockMvc;
    private Address testAddress;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(addressRestController)
                .build();

        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testAddress.setId(ID_VALUE);
        testClient.setId(ID_VALUE);
    }

    @After
    public void tearDown() throws Exception {
        objectMapper = null;
        localizedMessagesMock = null;
        addressServiceMock = null;
        addressRestController = null;
        mockMvc = null;
        testAddress = null;
        testClient = null;
    }

    @Test
    public void shouldPerformReturnClientAddressesArrayAction() throws Exception {
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        anotherTestAddress.setId(ANOTHER_ID_VALUE);
        Address[] addresses = {testAddress, anotherTestAddress};

        when(addressServiceMock.getAllClientAddressesAsArray(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(addresses);

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_ALL_ADDRESSES)
                .param(ID, ID_VALUE_STRING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(addresses.length)));

        verify(addressServiceMock).getAllClientAddressesAsArray(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformEditAddressAction() throws Exception {
        String data = objectMapper.writeValueAsString(testAddress);

        when(addressServiceMock.updateAddress(any(Address.class), any(HttpServletRequest.class)))
                .thenReturn(testAddress);
        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        verify(addressServiceMock).updateAddress(any(Address.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateStreetNameAndNotEditAddressInDatabaseAction() throws Exception {
        testAddress.setStreetName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        tryToEditAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateCityNameAndNotEditAddressInDatabaseAction() throws Exception {
        testAddress.setCityName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        tryToEditAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateZipCodeAndNotEditAddressInDatabaseAction() throws Exception {
        testAddress.setZipCode(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        tryToEditAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformSaveAddressAction() throws Exception {
        String data = objectMapper.writeValueAsString(testAddress);

        when(addressServiceMock.saveAddress(any(Address.class),
                anyLong(), any(HttpServletRequest.class))).thenReturn(testAddress);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .param(ID, ID_VALUE_STRING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(testAddress.getId().intValue())));

        verify(addressServiceMock).saveAddress(any(Address.class), anyLong(),
                any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateStreetNameAndNotAddAddressToDatabaseAction() throws Exception {
        testAddress.setStreetName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        tryToAddAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateCityNameAndNotAddAddressToDatabaseAction() throws Exception {
        testAddress.setCityName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        tryToAddAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateZipCodeAndNotAddAddressToDatabaseAction() throws Exception {
        testAddress.setZipCode(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testAddress);

        tryToAddAddressWithNotValidData(data);

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformDeleteAddressAction() throws Exception {
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(ID_VALUE);
        params.setClientId(ID_VALUE);
        String data = objectMapper.writeValueAsString(params);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        mockMvc.perform(post(REST_API_PREFIX + REST_DELETE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        verify(addressServiceMock).deleteAddress(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformChangeMainAddressAction() throws Exception {
        AddressRestController.Params params = new AddressRestController.Params();
        params.setAddressId(ID_VALUE);
        params.setClientId(ID_VALUE);
        String data = objectMapper.writeValueAsString(params);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        mockMvc.perform(put(REST_API_PREFIX + REST_EDIT_MAIN_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        verify(addressServiceMock).updateMainAddress(anyLong(), anyLong(),
                any(HttpServletRequest.class));
    }

    private void tryToEditAddressWithNotValidData(String data) throws Exception {
        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));
    }

    private void tryToAddAddressWithNotValidData(String data) throws Exception {
        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data)
                .param(ID, ID_VALUE_STRING))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(ID_NOT_FOUND.intValue())));
    }
}
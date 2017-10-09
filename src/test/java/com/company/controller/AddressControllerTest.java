package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.configuration.HibernateConfigurationForTests;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.AddressService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.company.Constants.*;
import static com.company.controller.AddressController.ADDRESS_TO_BE_EDITED;
import static com.company.controller.AddressController.CLIENT_ADDRESSES;
import static com.company.controller.AddressController.NEW_ADDRESS;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class AddressControllerTest {

    @Mock
    private AddressService addressServiceMock;

    @InjectMocks
    private AddressController addressController;

    private MockMvc mockMvc;
    private Address testAddress;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(RESOLVER_PREFIX);
        viewResolver.setSuffix(RESOLVER_SUFFIX);
        mockMvc = MockMvcBuilders.standaloneSetup(addressController)
                .setViewResolvers(viewResolver)
                .build();

        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testAddress.setId(ID_VALUE);
        testClient.setId(ID_VALUE);
    }

    @After
    public void tearDown() throws Exception {
        mockMvc = null;
        testAddress = null;
        testClient = null;
    }

    @Test
    public void shouldPerformAddEmptyNewAddressToModelAction() throws Exception {
        mockMvc.perform(get(ADD_ADDRESS))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(ADD_ADDRESS)))
                .andExpect(model().attribute(NEW_ADDRESS, allOf(notNullValue(), isA(Address.class))));
    }

    @Test
    public void shouldPerformAddClientToDatabaseAction() throws Exception {
        when(addressServiceMock.saveAddress(
                any(Address.class), anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testAddress);

        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, testAddress.getZipCode())
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(addressServiceMock).saveAddress(any(Address.class), anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateStreetNameAndNotAddAddressToDatabaseAction() throws Exception {
        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, INVALID_TO_SHORT_INPUT)
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, testAddress.getZipCode())
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateCityNameAndNotAddAddressToDatabaseAction() throws Exception {
        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, INVALID_TO_SHORT_INPUT)
                .param(ZIP_CODE, testAddress.getZipCode())
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateZipCodeAndNotAddAddressToDatabaseAction() throws Exception {
        mockMvc.perform(post(ADD_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, INVALID_TO_SHORT_INPUT)
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformAddClientAddressesAsMapToModelInEditAddressesAction() throws Exception {
        Map<Long, String> addresses = new HashMap<>();
        addresses.put(testAddress.getId(), testAddress.toString());

        when(addressServiceMock.getAllClientAddressesAsMap(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(addresses);

        mockMvc.perform(get(EDIT_ADDRESSES)
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(EDIT_ADDRESSES)))
                .andExpect(model().attribute(CLIENT_ADDRESSES,
                        hasEntry(equalTo(testAddress.getId()), equalTo(testAddress.toString()))));
    }

    @Test
    public void shouldPerformAddAddressFromDatabaseToModelAction() throws Exception {
        Map<Long, String> addresses = new HashMap<>();
        addresses.put(testAddress.getId(), testAddress.toString());

        when(addressServiceMock.findAddressById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testAddress);

        mockMvc.perform(get(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ADDRESS_ID, ID_VALUE_STRING))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)))
                .andExpect(model().attribute(ADDRESS_TO_BE_EDITED, equalTo(testAddress)));

        verify(addressServiceMock).findAddressById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformEditAddressFromDatabaseAction() throws Exception {
        when(addressServiceMock.updateAddress(any(Address.class), any(HttpServletRequest.class))).thenReturn(testAddress);

        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, testAddress.getZipCode()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(addressServiceMock).updateAddress(any(Address.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateStreetNameAndNotEditAddressFromDatabaseAction() throws Exception {
        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, INVALID_TO_SHORT_INPUT)
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, testAddress.getZipCode()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateCityNameAndNotEditAddressFromDatabaseAction() throws Exception {
        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, INVALID_TO_SHORT_INPUT)
                .param(ZIP_CODE, testAddress.getZipCode()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformValidateZipCodeAndNotEditAddressFromDatabaseAction() throws Exception {
        mockMvc.perform(put(EDIT_ADDRESS)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(STREET_NAME, testAddress.getStreetName())
                .param(CITY_NAME, testAddress.getCityName())
                .param(ZIP_CODE, INVALID_TO_SHORT_INPUT))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_ADDRESS)));

        verifyZeroInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformAddClientAddressesAsMapToModelInEditMainAddressAction() throws Exception {
        Map<Long, String> addresses = new HashMap<>();
        addresses.put(testAddress.getId(), testAddress.toString());

        when(addressServiceMock.getAllClientAddressesWithoutMainAddressAsMap(
                anyLong(), any(HttpServletRequest.class)))
                .thenReturn(addresses);

        mockMvc.perform(get(EDIT_MAIN_ADDRESS)
                .param(CLIENT_ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(EDIT_MAIN_ADDRESS)))
                .andExpect(model().attribute(CLIENT_ADDRESSES,
                        hasEntry(equalTo(testAddress.getId()), equalTo(testAddress.toString()))));

        verify(addressServiceMock).getAllClientAddressesWithoutMainAddressAsMap(
                anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformEditMainAddressAction() throws Exception {
        mockMvc.perform(put(EDIT_MAIN_ADDRESS)
                .param(CLIENT_ID, ID_VALUE_STRING)
                .param(ADDRESS_ID, ID_VALUE_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(addressServiceMock).updateMainAddress(anyLong(), anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }

    @Test
    public void shouldPerformRemoveAddressAction() throws Exception {
        mockMvc.perform(get(REMOVE_ADDRESS)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(ADDRESS_ID, ID_VALUE_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(addressServiceMock).deleteAddress(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressServiceMock);
    }
}
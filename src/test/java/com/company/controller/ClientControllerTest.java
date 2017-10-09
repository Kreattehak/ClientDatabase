package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.model.Client;
import com.company.service.ClientService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.company.Constants.*;
import static com.company.controller.ClientController.CLIENTS;
import static com.company.controller.ClientController.CLIENT_TO_BE_EDITED;
import static com.company.controller.ClientController.NEW_CLIENT;
import static com.company.controller.ClientController.WITH_CLIENT_ID_GET_PARAMETER;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
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
public class ClientControllerTest {

    @Mock
    private ClientService clientServiceMock;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(RESOLVER_PREFIX);
        viewResolver.setSuffix(RESOLVER_SUFFIX);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setViewResolvers(viewResolver)
                .build();

        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testClient.setId(ID_VALUE);
    }

    @After
    public void tearDown() throws Exception {
        mockMvc = null;
        testClient = null;
    }

    @Test
    public void shouldPerformAddClientListToModelAction() throws Exception {
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        anotherTestClient.setId(ANOTHER_ID_VALUE);
        List<Client> clients = Arrays.asList(testClient, anotherTestClient);

        when(clientServiceMock.getAllClients()).thenReturn(clients);

        mockMvc.perform(get(TABLE_OF_CLIENTS))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(TABLE_OF_CLIENTS)))
                .andExpect(model().attribute(CLIENTS, hasSize(clients.size())));

        verify(clientServiceMock).getAllClients();
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformAdNewClientToModelAction() throws Exception {
        mockMvc.perform(get(ADD_CLIENT))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(ADD_CLIENT)))
                .andExpect(model().attribute(NEW_CLIENT, allOf(notNullValue(), isA(Client.class))));
    }

    @Test
    public void shouldPerformAddClientToDatabaseAction() throws Exception {
        when(clientServiceMock.saveClient(any(Client.class), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, testClient.getLastName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(clientServiceMock).saveClient(any(Client.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateFirstNameAndNotAddClientToDatabaseAction() throws Exception {
        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, INVALID_TO_SHORT_INPUT)
                .param(LAST_NAME, testClient.getLastName()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_CLIENT)));

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateLastNameAndNotAddClientToDatabaseAction() throws Exception {
        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, INVALID_TO_SHORT_INPUT))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_CLIENT)));

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformRedirectToNewAddressFormAfterClientWasAddedAndClientWantToAddNewAddressAction()
            throws Exception {
        when(clientServiceMock.saveClient(any(Client.class), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, testClient.getLastName())
                .param(SHOULD_ADD_ADDRESS, Boolean.TRUE.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + ADD_ADDRESS +
                        WITH_CLIENT_ID_GET_PARAMETER + testClient.getId()))
                .andExpect(redirectedUrl(ADD_ADDRESS + WITH_CLIENT_ID_GET_PARAMETER
                        + testClient.getId()));

        verify(clientServiceMock).saveClient(any(Client.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformDeleteClientAction() throws Exception {
        mockMvc.perform(get(REMOVE_CLIENT)
                .param(CLIENT_ID, ID_VALUE_STRING))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(clientServiceMock).deleteClient(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldFillUpFormWithClientDataFromDatabase() throws Exception {
        when(clientServiceMock.findClientByIdAndCleanUnnecessaryData(
                anyLong(), any(HttpServletRequest.class))).thenReturn(testClient);

        mockMvc.perform(get(EDIT_CLIENT)
                .param(CLIENT_ID, ID_VALUE_STRING))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(EDIT_CLIENT)))
                .andExpect(model().attribute(CLIENT_TO_BE_EDITED, equalTo(testClient)));

        verify(clientServiceMock).findClientByIdAndCleanUnnecessaryData(
                anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformEditClientAction() throws Exception {
        when(clientServiceMock.updateClient(any(Client.class), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(put(EDIT_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(ID, ID_VALUE_STRING)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, testClient.getLastName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));

        verify(clientServiceMock).updateClient(any(Client.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateFirstNameAndNotEditClientInDatabaseAction() throws Exception {
        mockMvc.perform(put(EDIT_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, INVALID_TO_SHORT_INPUT)
                .param(LAST_NAME, testClient.getLastName()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_CLIENT)));

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateLastNameAndNotEditClientInDatabaseAction() throws Exception {
        mockMvc.perform(put(EDIT_CLIENT)
                .contentType(APPLICATION_FORM_URLENCODED_VALUE)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, INVALID_TO_SHORT_INPUT))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_CLIENT)));

        verifyZeroInteractions(clientServiceMock);
    }
}
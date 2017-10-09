package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.configuration.HibernateConfigurationForTests;
import com.company.model.Client;
import com.company.service.ClientService;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletRequest;

import static com.company.Constants.*;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
public class ClientRestControllerTest {

    static final String CSR = "clientSuccessfullyRemoved";
    static final String CNF = "clientNotFound";
    static final String CSE = "clientSuccessfullyEdited";

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ClientService clientServiceMock;

    @InjectMocks
    private ClientRestController clientRestController;

    private MockMvc mockMvc;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientRestController)
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
    public void shouldPerformReturnClientArrayAction() throws Exception {
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        anotherTestClient.setId(ANOTHER_ID_VALUE);
        Client[] clients = {testClient, anotherTestClient};
        when(clientServiceMock.getAllClientsAsArray()).thenReturn(clients);

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_ALL_CLIENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(clients.length)));

        verify(clientServiceMock).getAllClientsAsArray();
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldReturnClientAction() throws Exception {
        when(clientServiceMock.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_CLIENT)
                .param(ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testClient.getId().intValue())));

        verify(clientServiceMock).findClientById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformDeleteClientAction() throws Exception {
        ReflectionTestUtils.setField(clientRestController, CSR, STRING_TO_TEST_EQUALITY);
        String data = objectMapper.writeValueAsString(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_DELETE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        verify(clientServiceMock).deleteClient(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformEditClientAction() throws Exception {
        ReflectionTestUtils.setField(clientRestController, CSE, STRING_TO_TEST_EQUALITY);
        String data = objectMapper.writeValueAsString(testClient);

        when(clientServiceMock.updateClient(any(Client.class), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));

        verify(clientServiceMock).updateClient(any(Client.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateFirstNameAndNotEditClientInDatabaseAction() throws Exception {
        ReflectionTestUtils.setField(clientRestController, CNF, STRING_TO_TEST_EQUALITY);
        testClient.setFirstName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testClient);

        tryToEditClientWithNotValidData(data);

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateLastNameAndNotEditClientInDatabaseAction() throws Exception {
        ReflectionTestUtils.setField(clientRestController, CNF, STRING_TO_TEST_EQUALITY);
        testClient.setLastName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testClient);

        tryToEditClientWithNotValidData(data);

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformSaveClientAction() throws Exception {
        String data = objectMapper.writeValueAsString(testClient);

        when(clientServiceMock.saveClient(any(Client.class), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", equalTo(testClient.getId().intValue())));


        verify(clientServiceMock).saveClient(any(Client.class), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateFirstNameAndNotAddClientToDatabaseAction() throws Exception {
        testClient.setFirstName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testClient);

        tryToAddClientWithNotValidData(data);

        verifyZeroInteractions(clientServiceMock);
    }

    @Test
    public void shouldPerformValidateLastNameAndNotAddClientToDatabaseAction() throws Exception {
        testClient.setLastName(INVALID_TO_SHORT_INPUT);
        String data = objectMapper.writeValueAsString(testClient);

        tryToAddClientWithNotValidData(data);

        verifyZeroInteractions(clientServiceMock);
    }

    private void tryToEditClientWithNotValidData(String data) throws Exception {
        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(STRING_TO_TEST_EQUALITY)));
    }

    private void tryToAddClientWithNotValidData(String data) throws Exception {
        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(ID_NOT_FOUND.intValue())));
    }
}
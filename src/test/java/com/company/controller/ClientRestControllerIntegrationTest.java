package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.LocalizedMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static com.company.Constants.*;
import static com.company.controller.ClientRestControllerTest.CNF;
import static com.company.controller.ClientRestControllerTest.CSE;
import static com.company.controller.ClientRestControllerTest.CSR;
import static com.company.service.HibernateClientServiceTest.DCEM;
import static com.company.service.HibernateClientServiceTest.FCEM;
import static com.company.service.HibernateClientServiceTest.UCEM;
import static com.company.service.HibernateClientServiceTest.checkClientFieldsEquality;
import static com.company.util.LocalizedMessagesTest.getErrorMessage;
import static com.company.util.Mappings.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
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
public class ClientRestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ClientService clientService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ClientRestController clientRestController;
    @Autowired
    private LocalizedMessages localizedMessages;

    private MockMvc mockMvc;
    private Client testClient;

    @Before
    public void setUp() throws Exception {
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        objectMapper = null;
        clientDao = null;
        clientService = null;
        webApplicationContext = null;
        clientRestController = null;
        localizedMessages = null;
        mockMvc = null;
        testClient = null;
    }

    @Test
    public void shouldReturnClientArray() throws Exception {
        Client anotherClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        clientDao.save(testClient);
        clientDao.save(anotherClient);

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_ALL_CLIENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", equalTo(testClient.getId().intValue())))
                .andExpect(jsonPath("$.[1].id", equalTo(anotherClient.getId().intValue())));
    }

    @Test
    public void shouldReturnClient() throws Exception {
        clientDao.save(testClient);

        mockMvc.perform(get(REST_API_PREFIX + REST_GET_CLIENT)
                .param(ID, testClient.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testClient.getId().intValue())));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(get(REST_API_PREFIX + REST_GET_CLIENT)
                .param(ID, ID_NOT_FOUND_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldDeleteClientFromDatabase() throws Exception {
        clientDao.save(testClient);
        String data = objectMapper.writeValueAsString(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_DELETE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        equalTo(getErrorMessage(localizedMessages, CSR, clientRestController))));

        assertThat(clientService.getAllClients(), hasSize(0));
    }

    @Test
    public void shouldNotDeleteClientFromDatabaseWhenClientWasNotFound() throws Exception {
        testClient.setId(ID_NOT_FOUND);
        String data = objectMapper.writeValueAsString(testClient);

        tryToPerformActionButExceptionWasThrown(post(REST_API_PREFIX + REST_DELETE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), DCEM, clientService);
    }

    @Test
    public void shouldEditClientInDatabase() throws Exception {
        clientDao.save(testClient);

        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        anotherTestClient.setId(testClient.getId());
        String data = objectMapper.writeValueAsString(anotherTestClient);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",
                        equalTo(getErrorMessage(localizedMessages, CSE, clientRestController))));

        assertThat(clientService.getAllClients(), Matchers.<List<Client>>allOf(
                hasSize(1), hasItem(checkClientFieldsEquality(
                        ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME))));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFoundWhileEditingClient() throws Exception {
        testClient.setId(ID_NOT_FOUND);
        String data = objectMapper.writeValueAsString(testClient);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), FCEM, clientService);
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNullWhileEditingClient() throws Exception {
        String data = objectMapper.writeValueAsString(testClient);

        tryToPerformActionButExceptionWasThrown(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data), UCEM, clientService);
    }

    @Test
    public void shouldValidateFirstNameAndNotUpdateClientInDatabase() throws Exception {
        validateFieldsWhenTryingToEditClient(INVALID_TO_SHORT_INPUT, CLIENT_LAST_NAME);
    }

    @Test
    public void shouldValidateLastNameAndNotUpdateClientInDatabase() throws Exception {
        validateFieldsWhenTryingToEditClient(CLIENT_LAST_NAME, INVALID_TO_SHORT_INPUT);
    }

    @Test
    public void shouldSaveClientToDatabase() throws Exception {
        String data = objectMapper.writeValueAsString(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", greaterThan(0)));

        assertThat(clientService.getAllClients(), Matchers.<List<Client>>allOf(
                hasSize(1), hasItem(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME))));
    }

    @Test
    public void shouldValidateFirstNameAndNotAddClientInDatabase() throws Exception {
        validateFieldsWhenTryingToAddClient(INVALID_TO_SHORT_INPUT, CLIENT_LAST_NAME);
    }

    @Test
    public void shouldValidateLastNameAndNotAddClientInDatabase() throws Exception {
        validateFieldsWhenTryingToAddClient(CLIENT_LAST_NAME, INVALID_TO_SHORT_INPUT);
    }

    private void validateFieldsWhenTryingToEditClient(String firstName, String lastName)
            throws Exception {
        Client client = clientDao.save(testClient);
        testClient.setFirstName(firstName);
        testClient.setLastName(lastName);
        String data = objectMapper.writeValueAsString(testClient);

        mockMvc.perform(put(REST_API_PREFIX + REST_UPDATE_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$",
                        equalTo(getErrorMessage(localizedMessages, CNF, clientRestController))));
    }

    private void validateFieldsWhenTryingToAddClient(String firstName, String lastName)
            throws Exception {
        testClient.setFirstName(firstName);
        testClient.setLastName(lastName);
        String data = objectMapper.writeValueAsString(testClient);

        mockMvc.perform(post(REST_API_PREFIX + REST_SAVE_NEW_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .content(data))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$", equalTo(ID_NOT_FOUND.intValue())));
    }

    private void tryToPerformActionButExceptionWasThrown(
            MockHttpServletRequestBuilder builder, String message, Object target) throws Exception {
        mockMvc.perform(builder)
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errorMessage",
                        equalTo(getErrorMessage(localizedMessages, message, target))));
    }
}

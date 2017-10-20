package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.ClientDao;
import com.company.model.Client;
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

import java.util.List;

import static com.company.Constants.*;
import static com.company.controller.ClientController.CLIENTS;
import static com.company.controller.ClientController.CLIENT_TO_BE_EDITED;
import static com.company.controller.ClientController.NEW_CLIENT;
import static com.company.service.HibernateClientServiceTest.DCEM;
import static com.company.service.HibernateClientServiceTest.FCEM;
import static com.company.service.HibernateClientServiceTest.UCEM;
import static com.company.service.HibernateClientServiceTest.checkClientFieldsEquality;
import static com.company.util.LocalizedMessagesTest.getErrorMessage;
import static com.company.util.Mappings.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
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
public class ClientControllerIntegrationTest {

    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ClientService clientService;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private LocalizedMessages localizedMessages;

    private MockMvc mockMvc;
    private Client testClient;
    private List<Client> allClientsCurrrentlyInDatabase;

    @Before
    public void setUp() throws Exception {
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        clientDao = null;
        clientService = null;
        webApplicationContext = null;
        localizedMessages = null;
        mockMvc = null;
        testClient = null;
        allClientsCurrrentlyInDatabase = null;
    }

    @Test
    public void shouldAddClientListToModel() throws Exception {
        Client anotherClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        clientDao.save(testClient);
        clientDao.save(anotherClient);

        mockMvc.perform(get(TABLE_OF_CLIENTS))
                .andExpect(view().name(extractViewName(TABLE_OF_CLIENTS)))
                .andExpect(model().attribute(CLIENTS, allOf(
                        hasItem(is(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME))),
                        hasItem(is(checkClientFieldsEquality(ANOTHER_CLIENT_FIRST_NAME,
                                ANOTHER_CLIENT_LAST_NAME))))));
    }

    @Test
    public void shouldAddClientToDatabaseFromWebPageForm() throws Exception {
        allClientsCurrrentlyInDatabase = clientService.getAllClients();

        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(FIRST_NAME, testClient.getFirstName())
                .param(LAST_NAME, testClient.getLastName()));

        List<Client> allClientsAfterPersist = clientService.getAllClients();
        allClientsAfterPersist.removeAll(allClientsCurrrentlyInDatabase);

        Client clientFromDatabase = allClientsAfterPersist.get(0);

        assertThat(allClientsAfterPersist, hasSize(1));
        assertThat(clientFromDatabase,
                is(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME)));
    }

    @Test
    public void shouldValidateFirstNameAndNotAddClientToDatabase() throws Exception {
        validateFieldsWhenTryingToAddClient(INVALID_TO_SHORT_INPUT, CLIENT_FIRST_NAME, FIRST_NAME);
    }

    @Test
    public void shouldValidateLastNameAndNotAddClientToDatabase() throws Exception {
        validateFieldsWhenTryingToAddClient(CLIENT_FIRST_NAME, INVALID_TO_SHORT_INPUT, LAST_NAME);
    }

    @Test
    public void shouldDeleteClientFromDatabase() throws Exception {
        clientDao.save(testClient);

        List<Client> allClientsAfterPersist = clientService.getAllClients();
        Client clientFromDatabase = allClientsAfterPersist.get(0);

        mockMvc.perform(get(REMOVE_CLIENT)
                .param(CLIENT_ID, clientFromDatabase.getId().toString()));

        assertThat(clientService.getAllClients(), empty());
    }

    @Test
    public void shouldNotDeleteClientFromDatabaseWhenClientWasNotFound() throws Exception {
        clientDao.save(testClient);

        mockMvc.perform(get(REMOVE_CLIENT)
                .param(CLIENT_ID, ID_NOT_FOUND_VALUE_STRING))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ERROR_PAGE)))
                .andExpect(model().attribute(
                        ERROR_MESSAGE, getErrorMessage(localizedMessages, DCEM, clientService)));

        assertThat(clientService.getAllClients(), Matchers.<List<Client>>allOf(
                hasSize(1),
                hasItem(checkClientFieldsEquality(CLIENT_FIRST_NAME, CLIENT_LAST_NAME))));
    }

    @Test
    public void shouldUpdateClient() throws Exception {
        clientDao.save(testClient);

        allClientsCurrrentlyInDatabase = clientService.getAllClients();
        Client clientFromDatabase = allClientsCurrrentlyInDatabase.get(0);

        mockMvc.perform(put(EDIT_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(ID, clientFromDatabase.getId().toString())
                .param(FIRST_NAME, ANOTHER_CLIENT_FIRST_NAME)
                .param(LAST_NAME, ANOTHER_CLIENT_LAST_NAME));

        List<Client> allClientsAfterEdit = clientService.getAllClients();
        Client editedClient = allClientsAfterEdit.get(0);

        assertThat(allClientsAfterEdit, hasSize(1));
        assertThat(editedClient, is(checkClientFieldsEquality(
                ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME)));
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFoundWhileEditingClient() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                get(EDIT_CLIENT)
                        .param(CLIENT_ID, ID_VALUE_STRING), FCEM, clientService);
    }

    @Test
    public void shouldNotUpdateClientWhenIdWasNull() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                put(EDIT_CLIENT)
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .param(FIRST_NAME, ANOTHER_CLIENT_FIRST_NAME)
                        .param(LAST_NAME, ANOTHER_CLIENT_LAST_NAME), UCEM, clientService);
    }

    @Test
    public void shouldNotUpdateClientWhenClientWasNotFound() throws Exception {
        tryToPerformActionButExceptionWasThrown(
                put(EDIT_CLIENT)
                        .contentType(APPLICATION_JSON_UTF8_VALUE)
                        .param(ID, ID_NOT_FOUND_VALUE_STRING)
                        .param(FIRST_NAME, ANOTHER_CLIENT_FIRST_NAME)
                        .param(LAST_NAME, ANOTHER_CLIENT_LAST_NAME), FCEM, clientService);
    }

    @Test
    public void shouldValidateFirstNameAndNotUpdateClientInDatabase() throws Exception {
        validateFieldsWhenTryingToEditClient(INVALID_TO_SHORT_INPUT, CLIENT_LAST_NAME, FIRST_NAME);
    }

    @Test
    public void shouldValidateLastNameAndNotUpdateClientInDatabase() throws Exception {
        validateFieldsWhenTryingToEditClient(CLIENT_FIRST_NAME, INVALID_TO_SHORT_INPUT, LAST_NAME);
    }

    private void validateFieldsWhenTryingToEditClient(String firstName, String lastName, String whatToValid)
            throws Exception {
        Client client = clientDao.save(testClient);

        mockMvc.perform(put(EDIT_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(ID, client.getId().toString())
                .param(FIRST_NAME, firstName)
                .param(LAST_NAME, lastName))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(EDIT_CLIENT)))
                .andExpect(model().attributeHasFieldErrors(CLIENT_TO_BE_EDITED, whatToValid));
    }

    private void validateFieldsWhenTryingToAddClient(String firstName, String lastName, String whatToValid)
            throws Exception {
        mockMvc.perform(post(ADD_CLIENT)
                .contentType(APPLICATION_JSON_UTF8_VALUE)
                .param(FIRST_NAME, firstName)
                .param(LAST_NAME, lastName))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name(extractViewName(ADD_CLIENT)))
                .andExpect(model().attributeHasFieldErrors(NEW_CLIENT, whatToValid));
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

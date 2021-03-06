package com.company.service;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.ClientDao;
import com.company.model.Client;
import com.company.util.LocalizedMessages;
import com.company.util.ProcessUserRequestException;
import com.company.util.WebDataResolverAndCreator;
import org.hamcrest.Matcher;
import org.hamcrest.number.OrderingComparison;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.company.Constants.*;
import static com.company.util.Mappings.ID_NOT_FOUND;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class HibernateClientServiceTest {

    public static final String UCEM = "updateClientExceptionMessage";
    public static final String DCEM = "deleteClientExceptionMessage";
    public static final String FCEM = "findClientExceptionMessage";

    @Mock
    private ClientDao clientDaoMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private WebDataResolverAndCreator webDataResolverAndCreatorMock;
    @Mock
    private LocalizedMessages localizedMessagesMock;

    @InjectMocks
    private HibernateClientService clientService;

    private Client testClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testClient.setId(ID_VALUE);
    }

    @After
    public void tearDown() {
        clientDaoMock = null;
        requestMock = null;
        webDataResolverAndCreatorMock = null;
        localizedMessagesMock = null;
        clientService = null;
        testClient = null;
    }

    @Test
    public void shouldFindClientById() {
        when(clientDaoMock.findById(anyLong())).thenReturn(testClient);

        clientService.findClientById(anyLong(), requestMock);

        verify(clientDaoMock).findById(anyLong());
        verifyNoMoreInteractions(clientDaoMock);
    }

    @Test
    public void shouldThrowExceptionWhenClientWasNotFoundById() {
        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        clientService.findClientById(ID_NOT_FOUND, requestMock);
    }

    @Test
    public void shouldFindClientByIdAndCleanUnnecessaryData() {
        when(clientDaoMock.findById(anyLong())).thenReturn(testClient);

        clientService.findClientByIdAndCleanUnnecessaryData(anyLong(), requestMock);

        verify(clientDaoMock).findById(anyLong());
        verify(webDataResolverAndCreatorMock).cleanClientData(any(Client.class));
        verifyNoMoreInteractions(clientDaoMock);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldThrowExceptionWhenClientIdWasNotFoundWhileEditingClient() {
        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        clientService.findClientByIdAndCleanUnnecessaryData(ID_NOT_FOUND, requestMock);
    }

    @Test
    public void shouldReturnAllClientsFromDatabase() {
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        List<Client> clients = Arrays.asList(testClient, anotherTestClient);

        when(clientDaoMock.findAll()).thenReturn(clients);

        clientService.getAllClients();

        verify(clientDaoMock).findAll();
        verifyNoMoreInteractions(clientDaoMock);
    }

    @Test
    public void shouldReturnAllClientsFromDatabaseAsArray() {
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        List<Client> clients = Arrays.asList(testClient, anotherTestClient);

        when(clientDaoMock.findAll()).thenReturn(clients);

        clientService.getAllClientsAsArray();

        verify(clientDaoMock).findAll();
        verifyNoMoreInteractions(clientDaoMock);
    }

    @Test
    public void shouldAddClientToDatabase() {
        when(clientDaoMock.save(any(Client.class))).thenReturn(testClient);

        clientService.saveClient(testClient, requestMock);

        verify(clientDaoMock).save(any(Client.class));
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(clientDaoMock);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldDeleteClientFromDatabase() {
        when(clientDaoMock.findById(anyLong())).thenReturn(testClient);

        clientService.deleteClient(anyLong(), requestMock);

        verify(clientDaoMock).findById(anyLong());
        verify(clientDaoMock).delete(any(Client.class));
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(clientDaoMock);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldThrowExceptionWhenClientWasNotFoundWhileDeletingClient() {
        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);
        when(clientDaoMock.findById(anyLong())).thenReturn(null);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        clientService.deleteClient(testClient.getId(), requestMock);
    }

    @Test
    public void shouldUpdateClientInDatabaseWithGivenData() {
        Client anotherTestClient = new Client(ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME);
        anotherTestClient.setId(ANOTHER_ID_VALUE);

        when(clientDaoMock.update(any(Client.class))).thenReturn(testClient);
        when(clientDaoMock.findById(anyLong())).thenReturn(testClient);

        clientService.updateClient(anotherTestClient, requestMock);

        assertThat(testClient, is(checkClientFieldsEquality(
                ANOTHER_CLIENT_FIRST_NAME, ANOTHER_CLIENT_LAST_NAME)));

        verify(clientDaoMock).findById(anyLong());
        verify(clientDaoMock).update(any(Client.class));
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(clientDaoMock);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldThrowExceptionWhenIdWasNullWhileUpdatingClient() {
        testClient.setId(null);

        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        clientService.updateClient(testClient, requestMock);
    }

    @Test
    public void shouldThrowExceptionWhenClientWasNotFoundWhileUpdatingClient() {
        when(localizedMessagesMock.getMessage(anyString())).thenReturn(STRING_TO_TEST_EQUALITY);
        when(clientDaoMock.findById(anyLong())).thenReturn(null);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        clientService.updateClient(testClient, requestMock);
    }

    public static Matcher<Client> checkClientFieldsEquality(String firstName, String lastName) {
        return allOf(
                hasProperty(FIRST_NAME, is(firstName)),
                hasProperty(LAST_NAME, is(lastName)),
                hasProperty(ADDRESS, empty()),
                hasProperty(MAIN_ADDRESS, nullValue()),
                hasProperty(DATE_OF_REGISTRATION, OrderingComparison.lessThan(new Date())));
    }
}
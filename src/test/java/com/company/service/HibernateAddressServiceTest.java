package com.company.service;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.dao.AddressDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.util.ProcessUserRequestException;
import com.company.util.WebDataResolverAndCreator;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.company.Constants.*;
import static com.company.util.Mappings.ID_NOT_FOUND;
import static com.company.util.Mappings.REFERER_HEADER;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class HibernateAddressServiceTest {

    public static final String FAEM = "findAddressExceptionMessage";
    public static final String SAEM = "saveAddressExceptionMessage";
    public static final String DAEM = "deleteAddressExceptionMessage";
    public static final String UAEM = "updateAddressExceptionMessage";
    public static final String UMAEM = "updateMainAddressExceptionMessage";
    public static final String DANREM = "deleteAddressNoRefererExceptionMessage";

    @Mock
    private AddressDao addressDao;
    @Mock
    private ClientService clientService;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private WebDataResolverAndCreator webDataResolverAndCreatorMock;

    @InjectMocks
    private HibernateAddressService addressService;

    private Address testAddress;
    private Client testClient;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        testClient.setId(ID_VALUE);
        testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testAddress.setId(ID_VALUE);
    }

    @After
    public void tearDown() {
        addressDao = null;
        clientService = null;
        requestMock = null;
        webDataResolverAndCreatorMock = null;
        addressService = null;
        testAddress = null;
        testClient = null;

    }

    @Test
    public void shouldFindAddressById() throws Exception {
        when(addressDao.findById(anyLong())).thenReturn(testAddress);

        addressService.findAddressById(anyLong(), requestMock);

        verify(addressDao).findById(anyLong());
        verifyNoMoreInteractions(addressDao);
    }

    @Test
    public void shouldThrowExceptionWhenAddressWasNotFoundById() {
        ReflectionTestUtils.setField(addressService, FAEM, STRING_TO_TEST_EQUALITY);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.findAddressById(ID_NOT_FOUND, requestMock);
    }

    @Test
    public void shouldReturnAllAddressesFromDatabase() throws Exception {
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        //due to equal() implementation addresses need to have different ids, to fetch them to set
        anotherTestAddress.setId(ANOTHER_ID_VALUE);
        List<Address> addressesList = Arrays.asList(testAddress, anotherTestAddress);

        when(addressDao.findAll()).thenReturn(addressesList);

        Set<Address> addresses = addressService.findAllAddresses();

        assertThat(addresses, hasSize(addressesList.size()));
        assertThat(addresses, allOf(
                hasItem(checkAddressFieldsEquality(ADDRESS_STREET_NAME,
                        ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE)),
                hasItem(checkAddressFieldsEquality(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE))));

        verify(addressDao).findAll();
        verifyNoMoreInteractions(addressDao);
    }

    @Test
    public void shouldReturnAllClientAddresses() throws Exception {
        addClientWithTwoAddresses();

        assertThat(addressService.getAllClientAddresses(testClient), Matchers.<Set<Address>>allOf(
                hasSize(2),
                hasItem(checkAddressFieldsEqualityWithClient(
                        ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE, testClient)),
                hasItem(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                        ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient))));
    }

    @Test
    public void shouldReturnAllClientAddressesAsArray() throws Exception {
        Address anotherTestAddress = addClientWithTwoAddresses();

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        Address[] addresses = addressService.getAllClientAddressesAsArray(
                testClient.getId(), requestMock);

        assertThat(addresses, arrayWithSize(2));
        assertTrue(Arrays.equals(addresses, new Address[]{testAddress, anotherTestAddress}));

        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientService);
    }

    @Test
    public void shouldReturnAllClientAddressesAsMap() throws Exception {
        Address anotherTestAddress = addClientWithTwoAddresses();

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        Map<Long, String> addresses = addressService.getAllClientAddressesAsMap(
                testClient.getId(), requestMock);

        assertThat(addresses.size(), equalTo(2));
        assertThat(addresses, allOf(
                hasEntry(ID_VALUE,
                        testAddress.getCityName() + ", " + testAddress.getStreetName()),
                hasEntry(ANOTHER_ID_VALUE,
                        anotherTestAddress.getCityName() + ", " + anotherTestAddress.getStreetName())));

        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientService);
    }

    @Test
    public void shouldReturnAllClientAddressesAsMapButWithoutMainAddress() throws Exception {
        Address anotherTestAddress = addClientWithTwoAddresses();

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        Map<Long, String> addresses = addressService.getAllClientAddressesWithoutMainAddressAsMap(
                testClient.getId(), requestMock);

        assertThat(addresses.size(), equalTo(1));
        assertThat(addresses, hasEntry(ANOTHER_ID_VALUE,
                anotherTestAddress.getCityName() + ", " + anotherTestAddress.getStreetName()));

        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(clientService);
    }

    @Test
    public void shouldAddAddressToDatabase() throws Exception {
        when(addressDao.save(any(Address.class))).thenReturn(testAddress);
        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        addressService.saveAddress(testAddress, testClient.getId(), requestMock);

        assertThat(testClient.getAddress(), hasItem(testAddress));
        assertThat(testAddress, hasProperty(CLIENT, equalTo(testClient)));

        verify(addressDao).save(any(Address.class));
        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(addressDao);
        verifyNoMoreInteractions(clientService);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldNotAddTwoSameAddressesForOneClient() throws Exception {
        ReflectionTestUtils.setField(addressService, SAEM, STRING_TO_TEST_EQUALITY);
        testClient.addAddress(testAddress);

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.saveAddress(testAddress, testClient.getId(), requestMock);
    }

    @Test
    public void shouldAddFirstAddressToClientAsMainAddress() throws Exception {
        when(addressDao.save(any(Address.class))).thenReturn(testAddress);
        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        addressService.saveAddress(testAddress, testClient.getId(), requestMock);

        assertThat(testClient.getMainAddress(), equalTo(testAddress));

        verify(addressDao).save(any(Address.class));
        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verifyNoMoreInteractions(addressDao);
        verifyNoMoreInteractions(clientService);
    }

    @Test
    public void shouldDeleteAddressFromDatabaseWhenItsNotMainAddress() throws Exception {
        Address anotherTestAddress = addClientWithTwoAddresses();

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);
        when(addressDao.findById(anyLong())).thenReturn(anotherTestAddress);
        when(requestMock.getHeader(REFERER_HEADER))
                .thenReturn(REFERER_HEADER_VALUE + testClient.getId());

        addressService.deleteAddress(anotherTestAddress.getId(), requestMock);

        verify(addressDao).delete(any(Address.class));
        verify(addressDao).findById(anyLong());
        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verify(webDataResolverAndCreatorMock).fetchClientIdFromRequest(requestMock);
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(addressDao);
        verifyNoMoreInteractions(clientService);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenRefererHeaderWasNotPresent() throws Exception {
        ReflectionTestUtils.setField(addressService, DANREM, STRING_TO_TEST_EQUALITY);

        when(requestMock.getHeader(REFERER_HEADER)).thenReturn(null);
        when(webDataResolverAndCreatorMock.fetchClientIdFromRequest(requestMock))
                .thenReturn(ID_NOT_FOUND);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.deleteAddress(testAddress.getId(), requestMock);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenItIsMainAddress() throws Exception {
        ReflectionTestUtils.setField(addressService, DAEM, STRING_TO_TEST_EQUALITY);
        testClient.addAddress(testAddress);

        when(requestMock.getHeader(REFERER_HEADER))
                .thenReturn(REFERER_HEADER_VALUE + testClient.getId());
        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.deleteAddress(testAddress.getId(), requestMock);
    }

    @Test
    public void shouldNotDeleteAddressFromDatabaseWhenAddressToDeleteWasNotFound() throws Exception {
        ReflectionTestUtils.setField(addressService, FAEM, STRING_TO_TEST_EQUALITY);
        testClient.addAddress(testAddress);

        when(requestMock.getHeader(REFERER_HEADER))
                .thenReturn(REFERER_HEADER_VALUE + testClient.getId());
        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);
        when(addressDao.findById(anyLong())).thenReturn(null);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.deleteAddress(ANOTHER_ID_VALUE, requestMock);
    }

    @Test
    public void shouldUpdateAddressWithGivenData() throws Exception {
        testAddress.setClient(testClient);
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);
        anotherTestAddress.setId(ID_VALUE);


        when(addressDao.findById(anyLong())).thenReturn(testAddress);

        addressService.updateAddress(anotherTestAddress, requestMock);

        assertThat(testAddress, is(checkAddressFieldsEqualityWithClient(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE, testClient)));

        verify(addressDao).findById(anyLong());
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(addressDao);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldNotUpdateAddressWhenAddressFromFormDoesNotHaveId() throws Exception {
        ReflectionTestUtils.setField(addressService, UAEM, STRING_TO_TEST_EQUALITY);
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.updateAddress(anotherTestAddress, requestMock);
    }

    @Test
    public void shouldNotUpdateAddressWhenAddressDoesNotExist() throws Exception {
        ReflectionTestUtils.setField(addressService, UAEM, STRING_TO_TEST_EQUALITY);
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);

        when(addressDao.findById(anyLong())).thenReturn(null);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.updateAddress(anotherTestAddress, requestMock);
    }

    @Test
    public void shouldNotUpdateAddressWithTheSameData() throws Exception {
        ReflectionTestUtils.setField(addressService, UAEM, STRING_TO_TEST_EQUALITY);
        testAddress.setClient(testClient);
        testClient.addAddress(testAddress);

        when(addressDao.findById(anyLong())).thenReturn(testAddress);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.updateAddress(testAddress, requestMock);
    }

    @Test
    public void shouldUpdateMainAddress() throws Exception {
        Address anotherTestAddress = addClientWithTwoAddresses();

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);
        when(addressDao.findById(anyLong())).thenReturn(anotherTestAddress);

        addressService.updateMainAddress(anotherTestAddress.getId(), testClient.getId(), requestMock);

        assertThat(testClient.getMainAddress(), is(checkAddressFieldsEqualityWithClient(
                ANOTHER_ADDRESS_STREET_NAME, ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE,
                testClient)));

        verify(addressDao).findById(anyLong());
        verify(clientService).findClientById(anyLong(), any(HttpServletRequest.class));
        verify(webDataResolverAndCreatorMock).getUserData(requestMock);
        verifyNoMoreInteractions(addressDao);
        verifyNoMoreInteractions(clientService);
        verifyNoMoreInteractions(webDataResolverAndCreatorMock);
    }

    @Test
    public void shouldNotUpdateMainAddressToMainAddress() throws Exception {
        ReflectionTestUtils.setField(addressService, UMAEM, STRING_TO_TEST_EQUALITY);
        testClient.addAddress(testAddress);

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.updateMainAddress(testAddress.getId(), testClient.getId(), requestMock);
    }

    @Test
    public void shouldNotUpdateMainAddressWhenAddressDoesNotExist() throws Exception {
        ReflectionTestUtils.setField(addressService, UMAEM, STRING_TO_TEST_EQUALITY);
        testClient.addAddress(testAddress);

        when(clientService.findClientById(anyLong(), any(HttpServletRequest.class)))
                .thenReturn(testClient);
        when(addressDao.findById(anyLong())).thenReturn(null);

        expectedException.expect(ProcessUserRequestException.class);
        expectedException.expectMessage(STRING_TO_TEST_EQUALITY);
        addressService.updateMainAddress(testAddress.getId(), testClient.getId(), requestMock);
    }

    private Address addClientWithTwoAddresses() {
        Address anotherTestAddress = new Address(ANOTHER_ADDRESS_STREET_NAME,
                ANOTHER_ADDRESS_CITY_NAME, ANOTHER_ADDRESS_ZIP_CODE);

        anotherTestAddress.setId(ANOTHER_ID_VALUE);

        testClient.addAddress(testAddress);
        testClient.addAddress(anotherTestAddress);
        testAddress.setClient(testClient);
        anotherTestAddress.setClient(testClient);

        return anotherTestAddress;
    }

    public static Matcher<Address> checkAddressFieldsEquality(String streetName,
                                                              String cityName, String zipCode) {
        return allOf(
                hasProperty(STREET_NAME, is(streetName)),
                hasProperty(CITY_NAME, is(cityName)),
                hasProperty(ZIP_CODE, is(zipCode)),
                hasProperty(CLIENT, nullValue()));
    }

    public static Matcher<Address> checkAddressFieldsEqualityWithClient(
            String streetName, String cityName, String zipCode, Client client) {
        return allOf(
                hasProperty(STREET_NAME, is(streetName)),
                hasProperty(CITY_NAME, is(cityName)),
                hasProperty(ZIP_CODE, is(zipCode)),
                hasProperty(CLIENT, is(equalTo(client))));
    }
}
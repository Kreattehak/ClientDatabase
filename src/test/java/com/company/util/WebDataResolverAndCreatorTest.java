package com.company.util;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import com.company.model.Address;
import com.company.model.Client;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.company.Constants.*;
import static com.company.util.Mappings.COOKIE_NAME;
import static com.company.util.Mappings.LOGGED_USER_HEADER;
import static com.company.util.Mappings.REFERER_HEADER;
import static com.company.util.WebDataResolverAndCreator.ALERT_MESSAGE_UNRECOGNIZED_USER;
import static com.company.util.WebDataResolverAndCreator.FORWARDED_HEADER;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class WebDataResolverAndCreatorTest {

    private final String RANDOM_IP = "10:10:10:25";

    @Autowired
    private WebDataResolverAndCreator webDataResolverAndCreator;

    @Mock
    private HttpServletRequest requestMock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        webDataResolverAndCreator = null;
        requestMock = null;
    }

    @Test
    public void shouldCleanUpClientDataForWebForm() {
        Client testClient = new Client(CLIENT_FIRST_NAME, CLIENT_LAST_NAME);
        Address testAddress = new Address(ADDRESS_STREET_NAME, ADDRESS_CITY_NAME, ADDRESS_ZIP_CODE);
        testClient.addAddress(testAddress);

        webDataResolverAndCreator.cleanClientData(testClient);

        assertThat(testClient, cleanClient());
    }

    @Test
    public void shouldReturnLoggedUserNameFromHeaderSendByFrontEndApp() throws Exception {
        String userName = "someuser";
        when(requestMock.getHeader(LOGGED_USER_HEADER)).thenReturn(userName);

        assertThat(webDataResolverAndCreator.getLoggedUserName(requestMock), equalTo(userName));

        verify(requestMock).getHeader(LOGGED_USER_HEADER);
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldReturnLoggedUserNameFromCookie() throws Exception {
        String userName = "someuser";
        Cookie cookie = new Cookie(COOKIE_NAME, TEST_JWT);
        when(requestMock.getHeader(LOGGED_USER_HEADER)).thenReturn(null);
        when(requestMock.getCookies()).thenReturn(new Cookie[]{cookie});

        assertThat(webDataResolverAndCreator.getLoggedUserName(requestMock), equalTo(userName));

        verify(requestMock).getHeader(LOGGED_USER_HEADER);
        verify(requestMock).getCookies();
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldReturnWarningWhenNotLoggedUserWasTryingToMakeAChange() throws Exception {
        assertThat(webDataResolverAndCreator.getLoggedUserName(requestMock),
                equalTo(ALERT_MESSAGE_UNRECOGNIZED_USER));

        verify(requestMock).getHeader(LOGGED_USER_HEADER);
        verify(requestMock).getCookies();
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldReturnClientIpWhenXForwardedForHeaderIsPresent() {
        when(requestMock.getHeader(FORWARDED_HEADER)).thenReturn(RANDOM_IP);

        assertThat(webDataResolverAndCreator.getUserIp(requestMock), equalTo(RANDOM_IP));

        verify(requestMock).getHeader(FORWARDED_HEADER);
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldReturnClientIpWhenXForwardedForHeaderIsNotPresent() {
        when(requestMock.getHeader(FORWARDED_HEADER)).thenReturn(null);
        when(requestMock.getRemoteAddr()).thenReturn(RANDOM_IP);

        assertThat(webDataResolverAndCreator.getUserIp(requestMock), equalTo(RANDOM_IP));

        verify(requestMock).getHeader(FORWARDED_HEADER);
        verify(requestMock).getRemoteAddr();
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldFetchClientIdFromMvcRequest() throws Exception {
        when(requestMock.getHeader(REFERER_HEADER)).thenReturn(REFERER_HEADER_VALUE + ID_VALUE);

        assertThat(webDataResolverAndCreator.fetchClientIdFromRequest(requestMock), equalTo(ID_VALUE));

        verify(requestMock).getHeader(REFERER_HEADER);
        verifyNoMoreInteractions(requestMock);
    }

    @Test
    public void shouldFetchClientIdFromRestRequest() throws Exception {
        when(requestMock.getHeader(REFERER_HEADER)).thenReturn(REST_REFERER_HEADER_VALUE + ID_VALUE);

        assertThat(webDataResolverAndCreator.fetchClientIdFromRequest(requestMock), equalTo(ID_VALUE));

        verify(requestMock).getHeader(REFERER_HEADER);
        verifyNoMoreInteractions(requestMock);
    }

    public static Matcher<Client> cleanClient() {
        return allOf(
                hasProperty(FIRST_NAME, is(CLIENT_FIRST_NAME)),
                hasProperty(LAST_NAME, is(CLIENT_LAST_NAME)),
                hasProperty(ADDRESS, nullValue()),
                hasProperty(MAIN_ADDRESS, nullValue()),
                hasProperty(DATE_OF_REGISTRATION, nullValue()));
    }
}
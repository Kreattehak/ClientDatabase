package com.company.configuration.security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.company.util.Mappings.COOKIE_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class AuthenticationSuccessHandlerTest {

    @Mock
    private UserDetailsService userDetailsServiceMock;
    @Mock
    private JwtTokenUtil jwtTokenUtilMock;
    @Mock
    private DeviceResolver deviceResolverMock;
    @Mock
    private HttpServletRequest requestMock;
    @Mock
    private HttpServletResponse responseMock;
    @Mock
    private Authentication authenticationMock;

    @InjectMocks
    private AuthenticationSuccessHandler authenticator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        userDetailsServiceMock = null;
        jwtTokenUtilMock = null;
        deviceResolverMock = null;
        authenticator = null;
    }

    @Test
    public void shouldAddTokenToCookie() throws Exception {
        String token = "SOME TOKEN";
        ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);
        when(jwtTokenUtilMock.generateToken(any(), any())).thenReturn(token);

        authenticator.onAuthenticationSuccess(requestMock, responseMock, authenticationMock);

        verify(responseMock).addCookie(captor.capture());
        Cookie cookie = captor.getValue();

        assertThat(cookie.getName(), equalTo(COOKIE_NAME));
        assertThat(cookie.getValue(), equalTo(token));
    }
}
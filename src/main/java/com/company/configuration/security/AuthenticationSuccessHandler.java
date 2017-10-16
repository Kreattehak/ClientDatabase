package com.company.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.company.util.Mappings.COOKIE_NAME;

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private DeviceResolver deviceResolver;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        //Once more get User
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        final Device device = deviceResolver.resolveDevice(request);
        final String token = jwtTokenUtil.generateToken(userDetails, device);

        // Add a session cookie
        Cookie sessionCookie = new Cookie(COOKIE_NAME, token);
        response.addCookie(sessionCookie);

        clearAuthenticationAttributes(request);

        // Call the original impl
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

package com.company.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
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

@Component
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        final String token = jwtTokenUtil.generateToken(userDetails, null);//TODO: Device

        // Add a session cookie
        Cookie sessionCookie = new Cookie("currentUser", token);
        response.addCookie(sessionCookie);

        System.out.println(sessionCookie.getValue());

        clearAuthenticationAttributes(request);

        // call the original impl
        super.onAuthenticationSuccess(request, response, authentication);
    }
}

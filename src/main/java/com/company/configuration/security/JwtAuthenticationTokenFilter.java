package com.company.configuration.security;

import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.company.util.Mappings.COOKIE_NAME;
import static com.company.util.Mappings.LOGIN_LOGGER_NAME;

/**
 * JwtAuthenticationTokenFilter only validates token from cookie or from request header,
 * part where user login and gets authenticated is done on AuthenticationRestController by
 * default spring implementation.
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @InjectLogger(LOGIN_LOGGER_NAME)
    private static Logger logger;

    @Value("${jwt.header}")
    private String tokenHeader;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationTokenFilter(UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authToken = request.getHeader(this.tokenHeader);

        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ((COOKIE_NAME).equals(cookie.getName())) {
                        authToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        String username = jwtTokenUtil.getUsernameFromToken(authToken);


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Checking authentication for user " + username + " " + request.getRequestURL());

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                logger.info("Authenticated user " + username + ", setting security context"
                        + " " + request.getRequestURL());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}

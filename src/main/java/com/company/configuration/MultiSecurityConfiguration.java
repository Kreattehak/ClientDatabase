package com.company.configuration;

import com.company.configuration.security.AuthenticationSuccessHandler;
import com.company.configuration.security.JwtAuthenticationEntryPoint;
import com.company.configuration.security.JwtAuthenticationTokenFilter;
import com.company.configuration.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.LinkedHashMap;

import static com.company.util.Mappings.*;
import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableWebSecurity
public class MultiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] PERMITTED_URLS = {
            REST_AUTHORIZATION + ANY_SUBPATH, REST_API_PREFIX + REST_GET_ALL_CLIENTS,
            SLASH, TABLE_OF_CLIENTS, RESOURCES + ANY_SUBPATH, FAVICON, ABOUT_AUTHOR_PAGE, BLANK_PAGE
    };
    private static final int BCRYPT_STRENGTH = 12;

    private final AuthenticationSuccessHandler authSuccessHandler;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    public MultiSecurityConfiguration(AuthenticationSuccessHandler authSuccessHandler,
                                      UserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil,
                                      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.authSuccessHandler = authSuccessHandler;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationTokenFilter(userDetailsService, jwtTokenUtil);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(delegatingAuthenticationEntryPoint()).and()
                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                .antMatchers(LOGIN_PAGE).permitAll()
                .antMatchers(OPTIONS, ANY_SUBPATH).permitAll()
                .anyRequest().hasRole("USER")
                .and()
                .formLogin().loginPage(LOGIN_PAGE)
                .failureHandler(getAuthFailureHandler()).successHandler(authSuccessHandler)
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout=true")
                .deleteCookies(COOKIE_NAME, "JSESSIONID").invalidateHttpSession(true);

        // Custom JWT based security filter
        httpSecurity.addFilterBefore(
                authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity.headers().cacheControl();
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity
                .ignoring()
                .antMatchers(PERMITTED_URLS);
    }

    @Bean
    SimpleUrlAuthenticationFailureHandler getAuthFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler(LOGIN_PAGE + "?error=true");
    }

    @Bean
    public DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
        LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> entryPoints = new LinkedHashMap<>();
        entryPoints.put(new AntPathRequestMatcher(REST_API_PREFIX + ANY_SUBPATH), jwtAuthenticationEntryPoint);
        DelegatingAuthenticationEntryPoint defaultEntryPoint = new DelegatingAuthenticationEntryPoint(entryPoints);
        defaultEntryPoint.setDefaultEntryPoint(new LoginUrlAuthenticationEntryPoint(LOGIN_PAGE));
        return defaultEntryPoint;
    }
}
package com.company.configuration;

import com.company.configuration.security.AuthenticationSuccessHandler;
import com.company.configuration.security.JwtAuthenticationEntryPoint;
import com.company.configuration.security.JwtAuthenticationTokenFilter;
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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.company.util.Mappings.*;
import static org.springframework.http.HttpMethod.OPTIONS;

@Configuration
@EnableWebSecurity
public class MultiSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final String[] PERMITTED_URLS = {
            REST_AUTHORIZATION + ANY_SUBPATH, REST_API_PREFIX + REST_GET_ALL_CLIENTS,
            SLASH, TABLE_OF_CLIENTS, FAVICON
    };

    private static final int BCRYPT_STRENGTH = 12;

    @Autowired
    private SimpleUrlAuthenticationFailureHandler authFailureHandler;

    @Autowired
    private AuthenticationSuccessHandler authSuccessHandler;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private UserDetailsService userDetailsService;

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
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // we don't need CSRF because our token is invulnerable
                .csrf().disable()

//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                // don't create session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                .authorizeRequests()
                .antMatchers(LOGIN_PAGE).permitAll()
                .antMatchers(OPTIONS, ANY_SUBPATH).permitAll()
                .anyRequest().hasRole("USER")
                .and()
                .formLogin().failureHandler(authFailureHandler).loginPage(LOGIN_PAGE)
                .successHandler(authSuccessHandler)
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/login?logout")
                .deleteCookies("currentUser", "JSESSIONID").invalidateHttpSession(true);

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

        SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/login");
        handler.setDefaultFailureUrl("/login");
        //handler.setUseForward( true );

        return handler;
    }
}
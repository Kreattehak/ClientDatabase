package com.company.configuration;

import com.company.configuration.security.JwtAuthenticationEntryPoint;
import com.company.configuration.security.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.company.util.Mappings.ANY_SUBPATH;
import static com.company.util.Mappings.LOGIN_PAGE;
import static com.company.util.Mappings.REST_API_PREFIX;
import static com.company.util.Mappings.REST_GET_ALL_CLIENTS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final int BCRYPT_STRENGTH = 12;
    private static final String AUTHORIZATION = "/auth";

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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH);
    }

    @Bean
    public JwtAuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                // we don't need CSRF because our token is invulnerable
//                .csrf().disable()
//
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//
//                // don't create session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//
//                .authorizeRequests()
//                .antMatchers(REST_API_PREFIX + AUTHORIZATION + ANY_SUBPATH).permitAll()
//                .antMatchers(LOGIN_PAGE + ANY_SUBPATH).permitAll()
//                .antMatchers(REST_API_PREFIX + REST_GET_ALL_CLIENTS).permitAll()
//                .antMatchers(HttpMethod.OPTIONS, ANY_SUBPATH).permitAll()
//                .anyRequest().authenticated();
//
//        // Custom JWT based security filter
//        httpSecurity
//                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
//
//        // disable page caching
//        httpSecurity.headers().cacheControl();
    }
}
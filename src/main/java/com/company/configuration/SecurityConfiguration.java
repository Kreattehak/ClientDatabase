package com.company.configuration;

import com.company.configuration.security.JwtAuthenticationEntryPoint;
import com.company.configuration.security.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
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
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

import static com.company.util.Mappings.ANY_SUBPATH;
import static com.company.util.Mappings.LOGIN_PAGE;
import static com.company.util.Mappings.REST_API_PREFIX;
import static com.company.util.Mappings.REST_GET_ALL_CLIENTS;
import static org.springframework.http.HttpMethod.OPTIONS;

@EnableWebSecurity
public class SecurityConfiguration {

    @Configuration
    @Order(2)
    public static class JWTSecurityAdapter extends WebSecurityConfigurerAdapter {
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
            httpSecurity
                    // we don't need CSRF because our token is invulnerable
                    .csrf().disable()

                    .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()

                    // don't create session
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                    .authorizeRequests()
                    .antMatchers(REST_API_PREFIX + AUTHORIZATION + ANY_SUBPATH).permitAll()
                    .antMatchers(LOGIN_PAGE + ANY_SUBPATH).permitAll()
                    .antMatchers(REST_API_PREFIX + REST_GET_ALL_CLIENTS).permitAll()
                    .antMatchers(OPTIONS, ANY_SUBPATH).permitAll()
                    .anyRequest().authenticated();

            // Custom JWT based security filter
            httpSecurity
                    .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

            // disable page caching
            httpSecurity.headers().cacheControl();
        }
    }

    @Configuration
    @Order(1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        public static final String[] PERMITED_URLS = {"/resources/**", "/aboutUs", "/blank", "/"
                , "/login", "/logout", "/api/**", "/getClient", "/hello"};

        @Autowired
        private DataSource dataSource;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            CharacterEncodingFilter filter = new CharacterEncodingFilter();
            filter.setEncoding("UTF-8");
            filter.setForceEncoding(true);
            http.addFilterBefore(filter, CsrfFilter.class);

            http.authorizeRequests()
                    .antMatchers(PERMITED_URLS).permitAll()
                    .antMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .and()
                    .csrf();
        }
    }

}
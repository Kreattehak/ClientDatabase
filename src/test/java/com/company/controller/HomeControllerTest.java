package com.company.controller;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static com.company.controller.HomeController.ABOUT_US_PAGE;
import static com.company.controller.HomeController.BLANK_PAGE;
import static com.company.controller.HomeController.DEFAULT_PAGE;
import static com.company.controller.HomeController.LOGIN_PAGE;
import static com.company.util.Mappings.REDIRECT;
import static com.company.util.Mappings.RESOLVER_PREFIX;
import static com.company.util.Mappings.RESOLVER_SUFFIX;
import static com.company.util.Mappings.TABLE_OF_CLIENTS;
import static com.company.util.Mappings.extractViewName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class HomeControllerTest {

    @Autowired
    private HomeController homeController;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix(RESOLVER_PREFIX);
        viewResolver.setSuffix(RESOLVER_SUFFIX);
        mockMvc = MockMvcBuilders.standaloneSetup(homeController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @After
    public void tearDown() throws Exception {
        mockMvc = null;
    }

    @Test
    public void defaultPageShouldRedirectToClientsTable() throws Exception {
        mockMvc.perform(get(DEFAULT_PAGE))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));
    }

    @Test
    public void shouldMapToBlankPage() throws Exception {
        performRequestForHomeController(BLANK_PAGE);
    }

    @Test
    public void shouldMapToAboutUsPage() throws Exception {
        performRequestForHomeController(ABOUT_US_PAGE);
    }

    @Test
    public void shouldMapToLoginPage() throws Exception {
        performRequestForHomeController(LOGIN_PAGE);
    }

    private void performRequestForHomeController(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(url)));
    }
}
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

import static com.company.util.Mappings.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        homeController = null;
        mockMvc = null;
    }

    @Test
    public void defaultPageShouldRedirectToClientsTable() throws Exception {
        mockMvc.perform(get(SLASH))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(REDIRECT + TABLE_OF_CLIENTS))
                .andExpect(redirectedUrl(TABLE_OF_CLIENTS));
    }

    @Test
    public void shouldMapToBlankPage() throws Exception {
        performRequestForHomeController(BLANK_PAGE);
    }

    @Test
    public void shouldMapToAboutAuthorPage() throws Exception {
        performRequestForHomeController(ABOUT_AUTHOR_PAGE);
    }

    private void performRequestForHomeController(String url) throws Exception {
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(view().name(extractViewName(url)));
    }
}
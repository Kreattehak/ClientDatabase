package com.company.util;

import com.company.configuration.AppConfiguration;
import com.company.configuration.AppTestConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppTestConfig.class, AppConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class LocalizedMessagesTest {

    @Autowired
    private LocalizedMessages localizedMessages;

    @Test
    public void shouldReturnLocalizedMessage() throws Exception {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("PL"));
        String plMessage = localizedMessages.getMessage("exception.findClient");

        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String enMessage = localizedMessages.getMessage("exception.findClient");

        assertThat(plMessage, not(equalTo(enMessage)));
        assertThat(plMessage, containsString("klient"));
        assertThat(enMessage, containsString("client"));
    }

    public static String getErrorMessage(LocalizedMessages localizedMessages, String fieldName, Object targetObject) {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        return localizedMessages.getMessage(
                (String) ReflectionTestUtils.getField(targetObject, fieldName));
    }

}
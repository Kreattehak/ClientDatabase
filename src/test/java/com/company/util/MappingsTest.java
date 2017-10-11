package com.company.util;

import org.junit.Test;

import static com.company.util.Mappings.ONE_GET_PARAM;
import static com.company.util.Mappings.TWO_GET_PARAMS;
import static com.company.util.Mappings.extractGetParamValues;
import static com.company.util.Mappings.extractViewName;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItemInArray;
import static org.junit.Assert.assertThat;

public class MappingsTest {

    @Test
    public void shouldExtractViewName() throws Exception {
        String redirect = "redirect:/admin/getSomething";
        String forward = "forward:/admin/getSomething";
        String someUrl = "/admin/getSomething";
        String viewName = "getSomething";

        assertThat(extractViewName(redirect), equalTo(viewName));
        assertThat(extractViewName(forward), equalTo(viewName));
        assertThat(extractViewName(someUrl), equalTo(viewName));
    }

    @Test
    public void shouldExtractGetParamValuesWhenRequestWasMadeFromMVCApp() {
        String urlWithGetParam = "http://localhost:8080/admin/data?clientId=15";
        String urlWithTwoGetParams = "http://localhost:8080/admin/data?isNewClient=15&account=isEnabled";
        String getParam = "15";
        String anotherGetParam = "isEnabled";
        int quantityOfExtractedParams = 2;

        String[] extractedGetParams = extractGetParamValues(urlWithTwoGetParams, TWO_GET_PARAMS);

        assertThat(extractedGetParams, allOf(
                arrayWithSize(quantityOfExtractedParams),
                hasItemInArray(getParam),
                hasItemInArray(anotherGetParam)));
        assertThat(extractGetParamValues(urlWithGetParam, ONE_GET_PARAM),
                hasItemInArray(equalTo(getParam)));
    }

    @Test
    public void shouldExtractGetParamValuesWhenRequestWasMadeFromFrontEndApp() {
        String urlWithParam = "http://localhost:4200/clients/details/24";
        String urlWithTwoParams = "http://localhost:4200/clients/details/24/data/25";
        String param = "24";
        String anotherParam = "25";
        int quantityOfExtractedParams = 2;

        String[] extractedGetParams = extractGetParamValues(urlWithTwoParams, TWO_GET_PARAMS);

        assertThat(extractedGetParams, allOf(
                arrayWithSize(quantityOfExtractedParams),
                hasItemInArray(param),
                hasItemInArray(anotherParam)));
        assertThat(extractGetParamValues(urlWithParam, ONE_GET_PARAM),
                hasItemInArray(equalTo(param)));
    }
}
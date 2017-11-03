package com.company;

import static com.company.util.Mappings.ID_NOT_FOUND;

public class Constants {

    public static final String CLIENT_FIRST_NAME = "Danny";
    public static final String CLIENT_LAST_NAME = "Pepito";
    public static final String ANOTHER_CLIENT_FIRST_NAME = "Joa";
    public static final String ANOTHER_CLIENT_LAST_NAME = "Black";

    public static final String ADDRESS_STREET_NAME = "SomeStreet";
    public static final String ADDRESS_CITY_NAME  = "SomeCity";
    public static final String ADDRESS_ZIP_CODE = "11-111";
    public static final String ANOTHER_ADDRESS_STREET_NAME = "AnotherStreet";
    public static final String ANOTHER_ADDRESS_CITY_NAME  = "AnotherCity";
    public static final String ANOTHER_ADDRESS_ZIP_CODE= "99-999";

    public static final String REFERER_HEADER_VALUE = "http://localhost:8080/admin/removeAddressFromClient?clientId=";
    public static final String REST_REFERER_HEADER_VALUE = "http://localhost:4200/clients/details/";

    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ADDRESS = "address";
    public static final String MAIN_ADDRESS = "mainAddress";
    public static final String DATE_OF_REGISTRATION = "dateOfRegistration";
    public static final String CLIENT_ID = "clientId";
    public static final String STREET_NAME = "streetName";
    public static final String CITY_NAME = "cityName";
    public static final String ZIP_CODE = "zipCode";
    public static final String CLIENT = "client";
    public static final String ADDRESS_ID = "addressId";
    public static final String ID = "id";
    public static final String SHOULD_ADD_ADDRESS = "shouldAddAddress";

    public static final Long ID_VALUE = 10L;
    public static final String ID_VALUE_STRING = ID_VALUE.toString();
    public static final Long ANOTHER_ID_VALUE = 11L;
    public static final String ANOTHER_ID_VALUE_STRING = ANOTHER_ID_VALUE.toString();
    public static final String ID_NOT_FOUND_VALUE_STRING = ID_NOT_FOUND.toString();

    public static final String INVALID_TO_SHORT_INPUT = "00";
    public static final String TEST_DB_URL_PROPERTY_NAME = "test.hibernate.connection.url";
    public static final String STRING_TO_TEST_EQUALITY = "1a2b3c4d5e6";

    //JWT without expiration date
    public static final String TEST_JWT = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzb21ldXNlciIsIm" +
            "F1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6MTUwODY4MjQ2NjI4OH0.l2TJQ-avQINPpgUd85RZKvmpJg59L" +
            "WELazzj2Z3aNPEBzyQyB3R3aCWwJLOYLJC5ZNhsWW3J9os8U3Qo6w_F4A";
}

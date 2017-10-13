package com.company.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mappings {

    public static final String REDIRECT = "redirect:";
    public static final String SLASH = "/";
    public static final String ANY_SUBPATH = "/**";
    public static final String RESOURCES = "/resources";
    public static final String ERROR_PAGE = "/error";

    private static final String ADMIN_PREFIX = "/admin";
    public static final String REST_API_PREFIX = "/api";

    public static final String REST_AUTHORIZATION = REST_API_PREFIX + "/auth";
    public static final String REST_AUTHORIZATION_REFRESH = REST_API_PREFIX + "/refresh";

    public static final String LOGIN_PAGE = "/login";
    public static final String TABLE_OF_CLIENTS = "/clientsTable";
    public static final String ADD_CLIENT = ADMIN_PREFIX + "/addClient";
    public static final String REMOVE_CLIENT = ADMIN_PREFIX + "/removeClient";
    public static final String EDIT_CLIENT = ADMIN_PREFIX + "/editClient";

    public static final String ADD_ADDRESS = ADMIN_PREFIX + "/addAddress";
    public static final String EDIT_ADDRESSES = ADMIN_PREFIX + "/editAddresses";
    public static final String EDIT_ADDRESS = ADMIN_PREFIX + "/editAddress";
    public static final String EDIT_MAIN_ADDRESS = ADMIN_PREFIX + "/editMainAddress";
    public static final String REMOVE_ADDRESS_FROM_CLIENT = ADMIN_PREFIX + "/removeAddressFromClient";
    public static final String REMOVE_ADDRESS = ADMIN_PREFIX + "/removeAddress";

    public static final String REST_GET_ALL_CLIENTS = "/getAllClients";
    public static final String REST_GET_CLIENT = ADMIN_PREFIX + "/getClient";
    public static final String REST_DELETE_CLIENT = ADMIN_PREFIX + "/deleteClient";
    public static final String REST_UPDATE_CLIENT = ADMIN_PREFIX + "/updateClient";
    public static final String REST_SAVE_NEW_CLIENT = ADMIN_PREFIX + "/saveNewClient";

    public static final String REST_GET_ALL_ADDRESSES = ADMIN_PREFIX + "/getAllAddresses";
    public static final String REST_UPDATE_ADDRESS = ADMIN_PREFIX + "/updateAddress";
    public static final String REST_SAVE_NEW_ADDRESS = ADMIN_PREFIX + "/saveNewAddress";
    public static final String REST_DELETE_ADDRESS = ADMIN_PREFIX + "/deleteAddress";
    public static final String REST_EDIT_MAIN_ADDRESS = ADMIN_PREFIX + "/editMainAddress";

    public static final String BLANK_PAGE = "/blank";
    public static final String ABOUT_US_PAGE = "/aboutUs";

    public static final String RESOLVER_PREFIX = "/WEB-INF/views/";
    public static final String RESOLVER_SUFFIX = ".jsp";
    public static final String FAVICON = "/favicon.ico";

    public static final String DEFAULT_COMPONENT_SCAN_PACKAGE = "com.company";
    public static final String ADDRESS_SERVICE_LOGGER_NAME =
            DEFAULT_COMPONENT_SCAN_PACKAGE + ".service.HibernateAddressService";
    public static final String CLIENT_SERVICE_LOGGER_NAME =
            DEFAULT_COMPONENT_SCAN_PACKAGE + ".service.HibernateClientService";
    public static final String LOGIN_LOGGER_NAME =
            DEFAULT_COMPONENT_SCAN_PACKAGE + ".controller.JwtAuthenticationTokenFilter";

    static int ONE_GET_PARAM = 1;
    static int TWO_GET_PARAMS = 2;
    public static final String REFERER_HEADER = "referer";

    public static final String DEFAULT_ENCODING_VALUE = "UTF-8";
    public static final Long ID_NOT_FOUND = -1L;

    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String HTTP_STATUS = "httpStatus";

    public static final String LOGGED_USER_HEADER = "Logged-User";
    public static final String COOKIE_NAME = "currentUser";

    private Mappings() {
    }

    public static String extractViewName(String viewName) {
        String[] properViewName = viewName.split("/");
        return properViewName[properViewName.length - 1];
    }

    static String[] extractGetParamValues(String url, int quantityOfParams) {
        Pattern p = Pattern.compile("(\\?|&)([^=]+)=([^&]+)");
        Matcher m = p.matcher(url);
        String params[] = new String[quantityOfParams];
        int i = 0;
        while (m.find()) {
            params[i] = m.group(3);
            i++;
        }
        //when request was made from angular front end app
        if (i == 0) {
            m.usePattern(Pattern.compile("(/\\w+)(/)(\\d+)"));
            while (m.find()) {
                params[i] = m.group(3);
                i++;
            }
        }
        return params;
    }
}








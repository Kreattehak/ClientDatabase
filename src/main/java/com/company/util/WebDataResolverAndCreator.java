package com.company.util;

import com.company.model.Client;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static com.company.util.Mappings.ID_NOT_FOUND;
import static com.company.util.Mappings.ONE_GET_PARAM;
import static com.company.util.Mappings.REFERER_HEADER;
import static com.company.util.Mappings.extractGetParamValues;

/**
 * Reduces quantity of data send with post request each time client
 * wants to change one of stored client entities.
 * Also provides user name and ip for logs.
 */

public class WebDataResolverAndCreator {

    static final String FORWARDED_HEADER = "X-FORWARDED-FOR";
    static final String LOGGED_USER_HEADER = "Logged-User";
    static final String ALERT_MESSAGE_UNRECOGNIZED_USER = "CHECK WEBSITE SECURITY, UNRECOGNIZED USER!";

    private WebDataResolverAndCreator() {
    }

    public static void cleanClientData(Client client) {
        client.setAddress(null);
        client.setMainAddress(null);
        client.setDateOfRegistration(null);
    }

    public static String getUserData(HttpServletRequest request) {
        return "User " + getLoggedUserName(request) + " with ip: " + getUserIp(request);
    }

    static String getLoggedUserName(HttpServletRequest request) {
        String userName = request.getHeader(LOGGED_USER_HEADER);
        return userName != null ? userName : ALERT_MESSAGE_UNRECOGNIZED_USER;
    }

    static String getUserIp(HttpServletRequest request) {
        String remoteAddress = "";

        if (request != null) {
            remoteAddress = request.getHeader(FORWARDED_HEADER);
            if (remoteAddress == null || "".equals(remoteAddress)) {
                remoteAddress = request.getRemoteAddr();
            }
        }

        return remoteAddress;
    }

    public static Long fetchClientIdFromRequest(HttpServletRequest request) {
        String data = request.getHeader(REFERER_HEADER);
        if (data == null) {
            return ID_NOT_FOUND;
        }
        return Long.parseLong(extractGetParamValues(data, ONE_GET_PARAM)[0]);
    }

}

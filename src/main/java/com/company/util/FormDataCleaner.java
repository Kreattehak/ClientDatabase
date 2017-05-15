package com.company.util;

import com.company.model.Client;

/**
 * Reduces quantity of data send with post request each time client
 * wants to change one of stored entities
 */

public class FormDataCleaner {

    private FormDataCleaner() {
    }

    public static void cleanClientData(Client client) {
        client.setAddress(null);
        client.setMainAddress(null);
        client.setDateOfRegistration(null);
    }

}

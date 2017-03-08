package com.company.repository;

import com.company.model.Client;

import java.util.List;
import java.util.stream.Stream;

public interface ClientRepo {

    List<Client> getAllClients();

    List<Client> fullFillDatabase();
}

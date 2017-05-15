package com.company;

import com.company.dao.ClientDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MyApplication {

    @InjectLogger("com.company.MyApplication")
    private static Logger logger;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDao clientDao;

    private Client clientToPersist;

    public void performDbTasks() {

        Set<Address> address = new HashSet<>();
        Address address1 = new Address("Miła", "Katowice", "40-400");

        clientToPersist = new Client("Styrkeriusz", "Nazwisko");
        address1.setClient(clientToPersist);
        address.add(address1);
        clientToPersist.setAddress(address);

//        clientService.saveClient(clientToPersist);


        List<Client> clients = clientService.findAllClients();
        clients.stream()
                .sorted(Comparator.comparing(Client::getId, Comparator.reverseOrder()))
                .forEach(System.out::println);
        clients.stream()
                .flatMap(client -> client.getAddress().stream())
                .sorted(Comparator.comparing(Address::getId, Comparator.reverseOrder()))
                .forEach(System.out::println);
    }

    public void getClient() {
        System.out.println(clientService.findClientById(1L));
    }

    public void letsTest() {
        Client client = clientService.findClientById(48L);
        System.out.println(client);
    }

}
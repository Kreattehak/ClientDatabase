package com.company;

import com.company.configuration.security.repository.UserRepository;
import com.company.configuration.security.repository.UserRepositoryImpl;
import com.company.dao.ClientDao;
import com.company.model.Address;
import com.company.model.Client;
import com.company.model.security.User;
import com.company.service.ClientService;
import com.company.util.InjectLogger;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class MyApplication {

    @InjectLogger("com.company.MyApplication")
    private static Logger logger;

//    @Autowired
//    private ClientService clientService;
//
//    @Autowired
//    private ClientDao clientDao;

    @Autowired
    private UserRepository userRepository;

    private Client clientToPersist;

    public void performDbTasks() {
        User user = userRepository.findByUsername("admin");
        System.out.println(user);
    }


}
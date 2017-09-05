package com.company.configuration.security.repository;

import com.company.model.security.User;

public interface UserRepository{
    User findByUsername(String username);
}
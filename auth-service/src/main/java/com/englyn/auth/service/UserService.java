package com.englyn.auth.service;

public interface UserService {

    // Custom method to check if a user exists by username
    String getUserToken(String username) throws RuntimeException;
}


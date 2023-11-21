package com.englyn.auth.service;

import com.englyn.auth.exception.AuthenticationFailedException;

public interface UserService {

    // Custom method to check if a user exists by username
    String getUserToken(String username) throws AuthenticationFailedException;
}


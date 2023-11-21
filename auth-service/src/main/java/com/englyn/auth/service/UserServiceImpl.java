package com.englyn.auth.service;

import com.englyn.auth.entity.UserEntity;
import com.englyn.auth.exception.AuthenticationFailedException;
import com.englyn.auth.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getUserToken(String username) throws AuthenticationFailedException {
        UserEntity user =  userRepository.findByUsername(username);
        if (null == user)
            throw new AuthenticationFailedException(username + " not found.");
        return user.getToken();
    }
}


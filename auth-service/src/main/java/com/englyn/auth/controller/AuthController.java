package com.englyn.auth.controller;

import com.englyn.auth.exception.AuthenticationFailedException;
import com.englyn.auth.model.AuthenticationResponse;
import com.englyn.auth.model.Credentials;
import com.englyn.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody Credentials credentials) {
        // Authenticate user and generate token
        try{
            String token = userService.getUserToken(credentials.getUsername());
            return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);
        } catch (AuthenticationFailedException afe){
            afe.printStackTrace();
            return new ResponseEntity<>(new AuthenticationResponse(null), HttpStatus.UNAUTHORIZED);
        }
    }

}

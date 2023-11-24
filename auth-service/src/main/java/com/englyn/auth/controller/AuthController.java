package com.englyn.auth.controller;

import com.englyn.auth.model.AuthenticationResponse;
import com.englyn.auth.model.Credentials;
import com.englyn.auth.model.GameLaunchRequest;
import com.englyn.auth.model.GameResponse;
import com.englyn.auth.service.GameLaunchService;
import com.englyn.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private GameLaunchService gameLaunchService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Credentials credentials) {
        // Authenticate user and generate token
        try{
            String token = userService.getUserToken(credentials.getUser());
            return new ResponseEntity<>(new AuthenticationResponse(token), HttpStatus.OK);
        } catch (Exception afe){
            afe.printStackTrace();
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/launch")
    public ResponseEntity<GameResponse> launchGame(@RequestBody GameLaunchRequest request) {
        GameResponse gameResponse = gameLaunchService.launchGame(request);
        return new ResponseEntity<>(gameResponse, gameResponse.getStatus().equalsIgnoreCase("OK") ? HttpStatus.OK: HttpStatus.BAD_REQUEST);
    }

}

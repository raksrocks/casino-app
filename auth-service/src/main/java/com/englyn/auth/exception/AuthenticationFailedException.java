package com.englyn.auth.exception;

public class AuthenticationFailedException extends RuntimeException{

    public AuthenticationFailedException(String message){
        super(message);
    }
}

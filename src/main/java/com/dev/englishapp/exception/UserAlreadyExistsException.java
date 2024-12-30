package com.dev.englishapp.exception;


import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }

}

package com.dev.englishapp.exception;

public class SessionNotFoundException extends RuntimeException{

    public SessionNotFoundException(String message){
        super(message);
    }
}

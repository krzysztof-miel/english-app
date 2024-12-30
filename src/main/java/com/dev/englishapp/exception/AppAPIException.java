package com.dev.englishapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class AppAPIException extends RuntimeException{

    @Getter
    private HttpStatus status;
    private String message;

    public AppAPIException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public AppAPIException(String message ,HttpStatus status, String message1) {
        super(message);
        this.status = status;
        this.message = message1;
    }

    @Override
    public String getMessage() {
        return message;
    }

}

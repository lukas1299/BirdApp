package com.birdapp.BirdApp.exception;

public class UserAlreadyExistsException extends Exception{
    private final String message;

    public UserAlreadyExistsException(String message) {

        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

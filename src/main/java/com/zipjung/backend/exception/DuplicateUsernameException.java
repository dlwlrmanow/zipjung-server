package com.zipjung.backend.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException(String message) {
        super(message);
    }

    public DuplicateUsernameException(Exception ex){
        super(ex);
    }
}

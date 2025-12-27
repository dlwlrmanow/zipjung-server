package com.zipjung.backend.exception;

public class AlreadyExistDataException extends RuntimeException {
    public AlreadyExistDataException(String message) {
        super(message);
    }
}

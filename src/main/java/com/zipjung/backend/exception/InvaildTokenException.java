package com.zipjung.backend.exception;

public class InvaildTokenException extends RuntimeException {
    public InvaildTokenException(String message) {
        super(message);
    }
}

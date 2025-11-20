package com.zipjung.backend.exception;

public class TodoDBException extends RuntimeException {
    public TodoDBException(String message) {
        super(message);
    }
}
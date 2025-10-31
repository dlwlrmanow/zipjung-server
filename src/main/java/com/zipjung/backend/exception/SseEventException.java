package com.zipjung.backend.exception;

public class SseEventException extends RuntimeException {
    public SseEventException(String message) {
        super(message);
    }
}

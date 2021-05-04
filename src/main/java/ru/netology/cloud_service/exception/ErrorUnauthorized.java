package ru.netology.cloud_service.exception;

public class ErrorUnauthorized extends RuntimeException {
    public ErrorUnauthorized(String message) {
        super(message);
    }
}

package ru.netology.cloud_service.exception;

public class ErrorBadCredentials extends RuntimeException {
    public ErrorBadCredentials(String message) {
        super(message);
    }
}

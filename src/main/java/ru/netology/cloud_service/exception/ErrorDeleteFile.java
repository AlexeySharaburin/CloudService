package ru.netology.cloud_service.exception;

public class ErrorDeleteFile extends RuntimeException {
    public ErrorDeleteFile(String message) {
        super(message);
    }
}

package ru.netology.cloud_service.exception;

public class ErrorUploadFile extends RuntimeException {
    public ErrorUploadFile(String message) {
        super(message);
    }
}

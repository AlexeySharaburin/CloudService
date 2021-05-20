package ru.netology.cloud_service.exception;

public class ErrorDownloadFile extends RuntimeException {
    public ErrorDownloadFile(String message) {
        super(message);
    }
}

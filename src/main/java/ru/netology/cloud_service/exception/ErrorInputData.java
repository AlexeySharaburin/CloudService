package ru.netology.cloud_service.exception;

public class ErrorInputData extends RuntimeException {
    public ErrorInputData(String message) {
        super(message);
    }
}

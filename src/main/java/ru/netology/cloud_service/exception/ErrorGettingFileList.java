package ru.netology.cloud_service.exception;

public class ErrorGettingFileList extends RuntimeException {
    public ErrorGettingFileList(String message) {
        super(message);
    }
}

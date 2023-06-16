package ru.practicum.shareit.exception;

public class BadInputDataException extends RuntimeException {
    public BadInputDataException(String message) {
        super("Bad request exception. " + message);
    }
}
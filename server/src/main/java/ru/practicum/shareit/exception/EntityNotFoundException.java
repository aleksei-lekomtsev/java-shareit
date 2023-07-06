package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class entityClass, String message) {
        super(String.format("Entity with type=%s doesn't exist. %s", entityClass.getSimpleName(), message));
    }
}
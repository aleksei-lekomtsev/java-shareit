package ru.practicum.shareit.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(Class entityClass, String message) {
        super("Не найдена сущность с типом: " + entityClass.getSimpleName() + ". " + message);
    }
}
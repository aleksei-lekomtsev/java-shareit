package ru.practicum.shareit.storage;

import java.util.Optional;

public interface Storage<T> {
    T create(T entity);

    T update(T entity);

    Optional<T> findById(Integer id);

    void delete(Integer id);
}

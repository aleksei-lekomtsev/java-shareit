package ru.practicum.shareit.user;

import ru.practicum.shareit.storage.Storage;

import java.util.Collection;


public interface UserStorage extends Storage<User> {
    Collection<User> findAll();
}

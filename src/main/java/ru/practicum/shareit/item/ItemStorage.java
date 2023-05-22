package ru.practicum.shareit.item;

import ru.practicum.shareit.storage.Storage;

import java.util.Collection;

public interface ItemStorage extends Storage<Item> {
    Collection<Item> findAll(Integer userId);

    Collection<Item> search(String text);
}

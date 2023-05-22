package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    public Collection<Item> findAll(Integer userId) {
        return itemStorage.findAll(userId);
    }

    public Item create(Item entity) {
        userStorage.findById(entity.getOwnerId()).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", entity.getOwnerId());
                    throw new EntityNotFoundException(User.class,
                            "Пользователь с id=" + entity.getOwnerId() + " не существует.");
                });
        return itemStorage.create(entity);
    }

    public Item update(Item entity) {
        userStorage.findById(entity.getOwnerId()).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", entity.getOwnerId());
                    throw new EntityNotFoundException(User.class,
                            "Пользователь с id=" + entity.getOwnerId() + " не существует.");
                });
        return itemStorage.update(entity);
    }

    public Item findById(Integer id) {
        return itemStorage.findById(id).orElseThrow(
                () -> {
                    log.warn("Item with id={} not exist", id);
                    throw new EntityNotFoundException(Item.class, "Вещь с id=" + id + " не существует.");
                });
    }

    public void delete(Integer id) {
        itemStorage.delete(id);
    }

    public Collection<Item> search(String text) {
        return itemStorage.search(text);
    }
}

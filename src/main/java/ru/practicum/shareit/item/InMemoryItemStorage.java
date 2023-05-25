package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.checkForNull;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private static Integer            id    = 0;
    private final  Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item create(Item entity) {
        checkForNull(entity);
        entity.setId(++id);
        items.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Item update(Item entity) {
        checkForNull(entity);
        if (!items.containsKey(entity.getId())) {
            log.error("Вещь с id: " + entity.getId() + " не найдена.");
            throw new EntityNotFoundException(Item.class, "Вещь с id: " + id + " не найдена.");
        }
        if (!Objects.equals(items.get(entity.getId()).getOwnerId(), entity.getOwnerId())) {
            log.error("Смена владельца не поддерживается.");
            throw new EntityNotFoundException(Item.class, "Смена владельца не поддерживается.");
        }
        if (entity.getName() == null) {
            entity.setName(items.get(entity.getId()).getName());
        }
        if (entity.getDescription() == null) {
            entity.setDescription(items.get(entity.getId()).getDescription());
        }
        if (entity.getAvailable() == null) {
            entity.setAvailable(items.get(entity.getId()).getAvailable());
        }

        items.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Collection<Item> findAll(Integer userId) {
        return items
                .values()
                .stream()
                .filter(i -> i.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void delete(Integer id) {
        if (!items.containsKey(id)) {
            log.warn("Вещь с id: " + id + " не найдена.");
            throw new EntityNotFoundException(Item.class, "Вещь с id: " + id + " не найдена.");
        }
        items.remove(id);
    }

    @Override
    public Collection<Item> search(String text) {
        return items
                .values()
                .stream()
                .filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        i.getAvailable().equals(Boolean.TRUE) && !text.isEmpty())
                .collect(Collectors.toList());
    }
}
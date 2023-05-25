package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static ru.practicum.shareit.util.Util.checkForNull;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static Integer            id    = 0;
    private final  Map<Integer, User> users = new HashMap<>();

    private boolean doesEmailExist(User user) {
        return users
                .values()
                .stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()) &&
                        !Objects.equals(u.getId(), user.getId()));
    }

    @Override
    public User create(User entity) {
        checkForNull(entity);
        if (doesEmailExist(entity)) {
            log.error("Пользователь с email: " + entity.getEmail() + " уже существует");
            throw new RuntimeException("Пользователь с email: " + entity.getEmail() + " уже существует");
        }
        entity.setId(++id);
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public User update(User entity) {
        checkForNull(entity);
        if (!users.containsKey(entity.getId())) {
            log.error("Пользователь с id: " + entity.getId() + " не найден.");
            throw new EntityNotFoundException(User.class, "Пользователь с id: " + id + " не найден.");
        }
        if (doesEmailExist(entity)) {
            log.error("Пользователь с email: " + entity.getEmail() + " уже существует");
            throw new RuntimeException("Пользователь с email: " + entity.getEmail() + " уже существует");
        }
        if (entity.getName() == null) {
            entity.setName(users.get(entity.getId()).getName());
        }
        if (entity.getEmail() == null) {
            entity.setEmail(users.get(entity.getId()).getEmail());
        }
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Integer id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(User.class, "Пользователь с id: " + id + " не найден.");
        }
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Integer id) {
        if (!users.containsKey(id)) {
            log.warn("Пользователь с id: " + id + " не найден.");
            throw new EntityNotFoundException(User.class, "Пользователь с id: " + id + " не найден.");
        }

        users.remove(id);
    }
}
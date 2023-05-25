package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User entity) {
        return userStorage.create(entity);
    }

    public User update(User entity) {
        return userStorage.update(entity);
    }

    public User findById(Integer id) {
        return userStorage.findById(id).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", id);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + id + " не существует.");
                });
    }

    public void delete(Integer id) {
        userStorage.delete(id);
    }
}

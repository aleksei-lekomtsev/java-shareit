package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.Collection;

import static ru.practicum.shareit.util.Util.checkForNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    private final UserRepository repository;

    private boolean doesEmailExist(User user) {
        return !repository
                .findByEmailAndIdNot(user.getEmail(), user.getId())
                .isEmpty();
    }

    public Collection<User> findAll() {
        return repository.findAll();
    }

    public User create(User entity) {
        checkForNull(entity);
        if (doesEmailExist(entity)) {
            log.error("Пользователь с email: " + entity.getEmail() + " уже существует");
            throw new RuntimeException("Пользователь с email: " + entity.getEmail() + " уже существует");
        }
        return repository.save(entity);
    }

    public User update(User entity) {
        checkForNull(entity);
        User user = repository.findById(entity.getId()).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", entity.getId());
                    throw new EntityNotFoundException(User.class, "User with id=" + entity.getId() + " doesn't exist.");
                });
        if (doesEmailExist(entity)) {
            log.error("Пользователь с email: " + entity.getEmail() + " уже существует");
            throw new RuntimeException("Пользователь с email: " + entity.getEmail() + " уже существует");
        }
        if (entity.getName() == null) {
            entity.setName(user.getName());
        }
        if (entity.getEmail() == null) {
            entity.setEmail(user.getEmail());
        }
        return repository.save(entity);
    }

    public User findById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", id);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + id + " не существует.");
                });
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

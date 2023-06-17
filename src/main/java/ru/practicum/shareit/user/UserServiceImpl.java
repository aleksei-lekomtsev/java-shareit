package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

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

    public Collection<UserDto> findAll() {
        return repository
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(UserDto dto) {
        User entity = UserMapper.toUser(dto);
        checkForNull(entity);
        if (doesEmailExist(entity)) {
            log.error("Пользователь с email: " + entity.getEmail() + " уже существует");
            throw new RuntimeException("Пользователь с email: " + entity.getEmail() + " уже существует");
        }
        return UserMapper.toUserDto(repository.save(entity));
    }

    public UserDto update(UserDto dto) {
        User entity = UserMapper.toUser(dto);
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

        // > Проверки точно нужны?
        // Если я правильно все понимаю...
        // Когда происходит update в UserDto entity может прийти новый description
        // а старый name не прийти, т.е. в базе должен обновиться description, а name остаться старый
        // Подобной проверкой я это учитываю и беру из Entity(user) старое значение name, чтобы
        // при вызове save метода UserRepository класса поля объекта аргумента для save были заполнены
        if (entity.getName() == null) {
            entity.setName(user.getName());
        }
        if (entity.getEmail() == null) {
            entity.setEmail(user.getEmail());
        }
        return UserMapper.toUserDto(repository.save(entity));
    }

    public UserDto findById(Long id) {
        return UserMapper.toUserDto(repository.findById(id).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", id);
                    throw new EntityNotFoundException(User.class, "Пользователь с id=" + id + " не существует.");
                }));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

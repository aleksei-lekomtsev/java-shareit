package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.checkForNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private boolean doesEmailExist(UserDto dto) {
        return !repository
                .findByEmailAndIdNot(dto.getEmail(), dto.getId())
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
        checkForNull(dto);
        if (doesEmailExist(dto)) {
            log.error("Пользователь с email: {} уже существует.", dto.getEmail());
            throw new RuntimeException(String.format("Пользователь с email: %s  уже существует.", dto.getEmail()));
        }
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(dto)));
    }

    @Transactional
    public UserDto update(UserDto dto) {
        checkForNull(dto);
        User user = repository.findById(dto.getId()).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", dto.getId());
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", dto.getId()));
                });
        if (doesEmailExist(dto)) {
            log.error("Пользователь с email: {} уже существует.", dto.getEmail());
            throw new RuntimeException(String.format("Пользователь с email: %s  уже существует.", dto.getEmail()));
        }

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        return UserMapper.toUserDto(user);
    }

    public UserDto findById(Long id) {
        return UserMapper.toUserDto(repository.findById(id).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", id);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", id));
                }));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
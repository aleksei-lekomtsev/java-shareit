package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EntityNotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.Util.checkForNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository      repository;
    private final UserMapper userMapper;

    private boolean doesEmailExist(UserDto dto) {
        return !repository
                .findByEmailAndIdNot(dto.getEmail(), dto.getId())
                .isEmpty();
    }

    @Transactional
    @Override
    public UserDto create(UserDto dto) {
        checkForNull(dto);
        if (doesEmailExist(dto)) {
            log.error("Пользователь с email: {} уже существует.", dto.getEmail());
            throw new RuntimeException(String.format("Пользователь с email: %s  уже существует.", dto.getEmail()));
        }
        return userMapper.toDto(repository.save(userMapper.toUser(dto)));
    }

    @Transactional
    @Override
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
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findById(Long id) {
        return userMapper.toDto(repository.findById(id).orElseThrow(
                () -> {
                    log.warn("User with id={} not exist", id);
                    throw new EntityNotFoundException(User.class,
                            String.format("Entity with id=%d doesn't exist.", id));
                }));
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<UserDto> findAll() {
        return repository
                .findAll()
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
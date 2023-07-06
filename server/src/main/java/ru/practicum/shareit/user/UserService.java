package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    Collection<UserDto> findAll();

    UserDto create(UserDto dto);

    UserDto update(UserDto dto);

    UserDto findById(Long id);

    void delete(Long id);
}

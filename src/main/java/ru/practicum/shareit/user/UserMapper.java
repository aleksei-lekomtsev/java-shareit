package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

public class UserMapper {

    public static UserCreateDto toUserDto(User user) {
        return new UserCreateDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public static User toUser(UserCreateDto user) {
        User result =  new User();
        result.setName(user.getName());
        result.setEmail(user.getEmail());
        return result;
    }

    public static User toUser(UserUpdateDto user) {
        User result =  new User();

        if (user.getId() != null) {
            result.setId(user.getId());
        }

        if (user.getName() != null) {
            result.setName(user.getName());
        }

        if (user.getEmail() != null) {
            result.setEmail(user.getEmail());
        }
        return result;
    }
}

package ru.practicum.shareit.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;


@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }

    public User toUser(UserDto user) {
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

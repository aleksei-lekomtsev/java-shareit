package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserUserDtoMapper {
    User toUser(UserDto userDto);

    UserDto toDto(User user);
}

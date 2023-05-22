package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import javax.validation.Valid;
import java.util.Collection;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserCreateDto> getUsers() {
        return userService
                .findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreateDto createUser(@RequestBody @Valid UserCreateDto user) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(user)));
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserCreateDto updateUser(@PathVariable Integer userId, @RequestBody UserUpdateDto user) {
        user.setId(userId);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(user)));
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserCreateDto getUserById(@PathVariable Integer userId) {
        return UserMapper.toUserDto(userService.findById(userId));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Integer userId) {
        userService.delete(userId);
    }
}

package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getUsersWhenEmpty() {
        Collection<UserDto> all = userService.findAll();

        assertTrue(all.isEmpty());
    }

    @Test
    void getUsers() {
        List<UserDto> expected = List.of(new UserDto());
        Mockito
                .when(userService.findAll())
                .thenReturn(expected);

        Collection<UserDto> all = userController.getUsers();

        assertEquals(expected, all);
    }
}
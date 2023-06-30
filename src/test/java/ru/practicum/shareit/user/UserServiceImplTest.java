package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(
                userService,
                "userMapper",
                userMapper
        );
    }

    @Test
    void findById() {
        Long           id       = 0L;
        Optional<User> expected = Optional.of(new User());
        when(repository.findById(id)).thenReturn(expected);

        UserDto actual = userService.findById(id);

        assertEquals(userMapper.toDto(expected.get()), actual);
    }

    @Test
    void findByIdNotFoundUser() {
        Long id = 0L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void create() {
        User user = new User();
        when(repository.save(user)).thenReturn(user);

        UserDto actual = userService.create(userMapper.toDto(user));

        assertEquals(userMapper.toDto(user), actual);
        verify(repository).save(user);
    }

    @Test
    void createEmailAlreadyExists() {
        User user = new User();
        user.setId(0L);
        user.setEmail("email@mail.ru");
        when(repository.findByEmailAndIdNot(user.getEmail(), user.getId()))
                .thenReturn(List.of(new User(1L, "name", "email@mail.ru")));

        assertThrows(RuntimeException.class, () -> userService.create(userMapper.toDto(user)));
    }

    @Test
    void update() {
        Long id   = 0L;
        User user = new User();
        user.setId(id);
        Optional<User> userOptional = Optional.of(user);
        UserDto        expected     = new UserDto();
        expected.setName("name");
        expected.setEmail("email@mail.ru");
        expected.setId(id);
        when(repository.findById(id)).thenReturn(userOptional);

        UserDto actual = userService.update(expected);

        assertEquals(expected, actual);
    }

    @Test
    void updateEmailAlreadyExists() {
        Long id   = 0L;
        User user = new User();
        user.setId(id);
        Optional<User> userOptional = Optional.of(user);
        UserDto        expected     = new UserDto();
        expected.setName("name");
        expected.setEmail("email@mail.ru");
        expected.setId(id);
        when(repository.findById(id)).thenReturn(userOptional);
        when(repository.findByEmailAndIdNot(expected.getEmail(), expected.getId()))
                .thenReturn(List.of(new User(1L, "name", "email@mail.ru")));

        assertThrows(RuntimeException.class, () -> userService.update(expected));
    }

    @Test
    void updateNotFoundUser() {
        Long    id       = 0L;
        UserDto expected = new UserDto();
        expected.setName("name");
        expected.setEmail("email@mail.ru");
        expected.setId(id);
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.update(expected));
    }

    @Test
    void findAll() {
        Long userId = 0L;
        User user   = new User();
        user.setId(userId);
        List<User> expected = List.of(user);
        when(repository.findAll()).thenReturn(expected);

        Collection<UserDto> actual = userService.findAll();

        assertEquals(expected
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList()), actual);
    }

    @Test
    void delete() {
        userService.delete(0L);
        verify(repository).deleteById(0L);
    }
}

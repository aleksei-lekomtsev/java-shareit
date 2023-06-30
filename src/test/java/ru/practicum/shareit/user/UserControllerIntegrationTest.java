package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;


@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createUser() {
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("email@mail.ru");
        when(userService.create(user)).thenReturn(user);

        String result = mockMvc
                .perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);

    }

    @SneakyThrows
    @Test
    void updateUser() {
        long    id   = 0L;
        UserDto user = new UserDto();
        user.setId(id);
        user.setName("name");
        user.setEmail("email@mail.ru");

        when(userService.update(user)).thenReturn(user);

        String result = mockMvc
                .perform(patch("/users/{userId}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), result);
    }

    @SneakyThrows
    @Test
    void updateUserFailInvalidEmail() {
        long    id   = 0L;
        UserDto user = new UserDto();
        user.setName("name");
        user.setEmail("email.ru");

        mockMvc
                .perform(patch("/users/{userId}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).update(user);
    }

    @SneakyThrows
    @Test
    void getUserById() {
        long id = 0L;
        mockMvc
                .perform(get("/users/{userId}", id))
                .andExpect(status().isOk());

        verify(userService).findById(id);
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        long id = 0L;
        mockMvc
                .perform(delete("/users/{userId}", id))
                .andExpect(status().isOk());

        verify(userService).delete(id);
    }
}
package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@WebMvcTest(controllers = {ItemController.class, UserController.class})
class ItemControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private UserService userService;


    @SneakyThrows
    @Test
    void createItem() {
        Long    userId  = 0L;
        ItemDto itemDto = new ItemDto();
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc
                .perform(post("/items")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        Long    id      = 0L;
        Long    userId  = 0L;
        ItemDto itemDto = new ItemDto();
        itemDto.setId(id);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        when(itemService.update(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc
                .perform(patch("/items/{itemId}", id)
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/items/{itemId}", id).header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).findById(userId, id);
    }

    @SneakyThrows
    @Test
    void getItems() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/items", id)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemService).findAll(userId);
    }

    @SneakyThrows
    @Test
    public void deleteItem() {
        long id = 0L;
        mockMvc
                .perform(delete("/items/{itemId}", id))
                .andExpect(status().isOk());

        verify(itemService).delete(id);
    }

    @SneakyThrows
    @Test
    void search() {
        Long id     = 0L;
        mockMvc
                .perform(get("/items/search", id))
                .andExpect(status().isOk());

        verify(itemService).search(null);
    }
}

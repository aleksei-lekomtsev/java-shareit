package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@WebMvcTest(controllers = {ItemRequestController.class, UserController.class})
class ItemRequestControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createItemRequest() {
        Long           userId         = 0L;
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("expected");
        when(itemRequestService.create(eq(userId), eq(itemRequestDto), anyLong())).thenReturn(itemRequestDto);

        String result = mockMvc
                .perform(post("/requests")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void findById() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/requests/{requestId}", id).header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemRequestService).findById(userId, id);
    }

    @SneakyThrows
    @Test
    void getItemsRequests() {
        Long userId = 0L;
        mockMvc
                .perform(get("/requests/all").header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(itemRequestService).findAll(userId, 0, 10);
    }
}
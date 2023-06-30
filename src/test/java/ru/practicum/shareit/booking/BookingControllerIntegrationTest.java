package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Util.X_SHARER_USER_ID;


@WebMvcTest(controllers = {BookingController.class, UserController.class})
class BookingControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void createBooking() {
        Long       userId     = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.now().plusDays(1L));
        bookingDto.setEnd(LocalDateTime.MAX);
        when(bookingService.create(userId, bookingDto)).thenReturn(bookingDto);

        String result = mockMvc
                .perform(post("/bookings")
                        .header(X_SHARER_USER_ID, userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void updateBooking() {
        Long       userId     = 0L;
        Long       id  = 0L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(id);
        bookingDto.setStart(LocalDateTime.now().plusDays(1L));
        bookingDto.setEnd(LocalDateTime.MAX);
        when(bookingService.update(userId, id, true)).thenReturn(bookingDto);

        String result = mockMvc
                .perform(patch("/bookings/{bookingId}", id)
                        .header(X_SHARER_USER_ID, userId)
                        .param("approved", "true")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getBookingById() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/bookings/{bookingId}", id).header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findById(userId, id);
    }

    @SneakyThrows
    @Test
    void getBookings() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/bookings", id)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findAll(userId, "ALL", 0, 10, false);
    }

    @SneakyThrows
    @Test
    void getBookingsForOwner() {
        Long userId = 0L;
        Long id     = 0L;
        mockMvc
                .perform(get("/bookings/owner", id)
                        .header(X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());

        verify(bookingService).findAll(userId, "ALL", 0, 10, true);
    }
}
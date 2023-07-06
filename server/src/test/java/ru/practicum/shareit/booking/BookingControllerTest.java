package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    @Test
    void getBookingsWhenEmpty() {
        Collection<BookingDto> all = bookingService.findAll(0L, "ALL", 0, 10, false);

        assertTrue(all.isEmpty());
    }

    @Test
    void getBookingsForOwner() {
        Collection<BookingDto> all = bookingService.findAll(0L, "ALL", 0, 10, true);

        assertTrue(all.isEmpty());
    }

    @Test
    void getBookings() {
        List<BookingDto> expected = List.of(new BookingDto());
        Mockito
                .when(bookingService.findAll(0L, "ALL", 0, 10, false))
                .thenReturn(expected);

        Collection<BookingDto> all = bookingController.getBookings(0L, "ALL", 0, 10);

        assertEquals(expected, all);
    }

    @Test
    void getBookingById() {
        BookingDto expected = new BookingDto();
        Mockito
                .when(bookingService.findById(0L, 0L))
                .thenReturn(expected);

        BookingDto actual = bookingController.getBookingById(0L, 0L);

        assertEquals(expected, actual);
    }
}
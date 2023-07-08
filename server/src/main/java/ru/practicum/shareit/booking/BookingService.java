package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDto entity);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long id);

    List<BookingDto> findAll(Long bookerId, String state, int from, int size, Boolean owner);
}

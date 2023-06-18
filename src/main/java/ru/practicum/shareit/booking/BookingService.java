package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {
    BookingDto create(Long userId, BookingDto entity);

    BookingDto update(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long id);

    Collection<BookingDto> findAll(Long bookerId, String state);

    Collection<BookingDto> findAllForOwner(Long ownerId, String state);
}

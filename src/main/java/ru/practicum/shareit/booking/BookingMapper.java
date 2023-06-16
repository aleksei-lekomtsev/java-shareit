package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


public class BookingMapper {
    public static Booking toBooking(BookingDto dto, Item item, User user, Status status) {
        return new Booking(
                dto.getId(),
                dto.getStart(),
                dto.getEnd(),
                item,
                user,
                status
        );
    }

    public static BookingDto toDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd(),
                false,
                booking.getStatus().name(),
                booking.getBooker(),
                booking.getItem()
        );
    }
}

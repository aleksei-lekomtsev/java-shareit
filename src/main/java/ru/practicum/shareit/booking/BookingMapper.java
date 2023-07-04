package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;


@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "item", target = "item")
    @Mapping(source = "booker", target = "booker")
    @Mapping(source = "status", target = "status")
    Booking toBooking(BookingDto dto, Item item, User booker, Status status);

    @Mapping(source = "booking.id", target = "id")
    @Mapping(source = "booking.item.id", target = "itemId")
    BookingDto toDto(Booking booking);
}

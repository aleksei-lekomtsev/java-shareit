package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                lastBooking == null ?
                        null :
                        new LastNextBookingDto(lastBooking.getId(), lastBooking.getBooker().getId()),
                nextBooking == null ?
                        null :
                        new LastNextBookingDto(nextBooking.getId(), nextBooking.getBooker().getId()),
                comments
                );
    }

    public Item toItem(ItemDto item, User user) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user
        );
    }
}

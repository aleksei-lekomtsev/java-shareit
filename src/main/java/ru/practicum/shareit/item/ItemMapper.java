package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;


import java.util.List;


@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(source = "item.id", target = "id")
    @Mapping(source = "item.description", target = "description")
    @Mapping(source = "item.owner.id", target = "ownerId")
    @Mapping(source = "lastBooking.id", target = "lastBooking.id")
    @Mapping(source = "lastBooking.booker.id", target = "lastBooking.bookerId")
    @Mapping(source = "nextBooking.id", target = "nextBooking.id")
    @Mapping(source = "nextBooking.booker.id", target = "nextBooking.bookerId")
    @Mapping(source = "item.itemRequest.id", target = "requestId")
    ItemDto toItemDto(Item item, Booking lastBooking, Booking nextBooking, List<CommentDto> comments);

    @Mapping(source = "dto.id", target = "id")
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.description", target = "description")
    @Mapping(source = "user", target = "owner")
    Item toItem(ItemDto dto, User user, ItemRequest itemRequest);
}
